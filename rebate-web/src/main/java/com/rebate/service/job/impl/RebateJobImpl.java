package com.rebate.service.job.impl;

import com.google.common.base.Joiner;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.common.util.rebate.RebateRuleUtil;
import com.rebate.dao.*;
import com.rebate.domain.*;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.*;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.job.RebateJob;
import com.rebate.service.userinfo.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("rebateJob")
public class RebateJobImpl implements RebateJob {
    private static final Logger LOG = LoggerFactory.getLogger(RebateJobImpl.class);

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("rebateDetailDao")
    @Autowired(required = true)
    private RebateDetailDao rebateDetailDao;

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @Qualifier("incomeDetailDao")
    @Autowired(required = true)
    private IncomeDetailDao incomeDetailDao;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Qualifier("agentRelationDao")
    @Autowired(required = true)
    private AgentRelationDao agentRelationDao;

    @Qualifier("productCouponDao")
    @Autowired(required = true)
    private ProductCouponDao productCouponDao;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Override
    public void freshProducts() {
        int page = 1;
        int pageSize = 100;
        ProductQuery productQuery = new ProductQuery();
        productQuery.setStartRow((page - 1) * pageSize);
        productQuery.setPageSize(pageSize);
        productQuery.setStatus(EProductStatus.PASS.getCode());

        List<Product> products = productDao.findProducts(productQuery);

        while (products.size() > 0) {
            LOG.error("[商品更新任务]加载第" + page + "页{}条记录！", products.size());

            for (Product product : products) {
                //查询优惠券信息
                ProductCoupon productCouponQuery = new ProductCoupon();
                productCouponQuery.setProductId(product.getProductId());
                ProductCoupon productCoupon = productCouponDao.findById(productCouponQuery);

                if (null != productCoupon) {
                    //清理掉没有优惠券转链接的信息
                    String coupontPromotionLink = jdSdkManager.getPromotionCouponCode(productCoupon.getProductId(), productCoupon.getCouponLink(), "");
                    if (StringUtils.isBlank(coupontPromotionLink)) {
                        LOG.error("[商品更新任务]删除没有优惠券活动链接的商品,productId;{}", productCoupon.getProductId());
                        Product productUpdate = new Product();
                        productUpdate.setProductId(productCoupon.getProductId());
                        productUpdate.setStatus(EProductStatus.DELETE.getCode());
                        productDao.update(productUpdate);

                        ProductCoupon productCouponUpdate = new ProductCoupon();
                        productCouponUpdate.setProductId(productCoupon.getProductId());
                        productCouponUpdate.setStatus(EProductStatus.DELETE.getCode());
                        productCouponDao.update(productCouponUpdate);
                    }
                }

                Date now = new Date();
                if (product.getEndDate().before(now)) {
                    //清理掉活动过期的商品信息
                    Product productUpdate = new Product();
                    productUpdate.setProductId(productCoupon.getProductId());
                    productUpdate.setStatus(EProductStatus.DELETE.getCode());
                    productDao.update(productUpdate);

                    ProductCoupon productCouponUpdate = new ProductCoupon();
                    productCouponUpdate.setProductId(productCoupon.getProductId());
                    productCouponUpdate.setStatus(EProductStatus.DELETE.getCode());
                    productCouponDao.update(productCouponUpdate);
                }

                //重新更新商品信息
                Product mediaProduct = jdSdkManager.getMediaProduct(product.getProductId());
                if (null != mediaProduct) {
                    productDao.update(mediaProduct);
                }

            }

            page++;
            productQuery.setStartRow((page - 1) * pageSize);
            products = productDao.findProducts(productQuery);
        }
    }

    @Override
    public void importMediaOrder() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        int pageSize = 10;
        //获取近30天订单进行更新
        for (int days = 0; days < 30; days++) {
            int page = 1;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_WEEK, -days);
            String queryTime = format.format(calendar.getTime());

            List<RebateDetail> rebateDetails = jdSdkManager.getRebateDetails(queryTime, page, pageSize);
            while (rebateDetails.size() > 0) {
                LOG.error("[联盟订单导入任务]加载[" + queryTime + "]第" + page + "页{}条订单明细记录！", rebateDetails.size());
                for (RebateDetail rebateDetail : rebateDetails) {
                    try{

                        Double userCommission = 0.0;

                        if (StringUtils.isNotBlank(rebateDetail.getSubUnionId())) {
                            //查询用户信息
                            UserInfo userInfoQuery = new UserInfo();
                            userInfoQuery.setSubUnionId(rebateDetail.getSubUnionId());
                            UserInfo userInfo = userInfoDao.findUserInfoBySubUnionId(userInfoQuery);
                            if (null != userInfo) {
                                if (EAgent.FIRST_AGENT.getCode() == userInfo.getAgent()) {
                                    //如果为代理模式一，则根据给上级代理进行分佣
                                    userCommission = addFirstAgentIncomeDetail(rebateDetail);
                                } else if (EAgent.SECOND_AGENT.getCode() == userInfo.getAgent()) {
                                    //代理模式二
                                    userCommission = addSecondAgentIncomeDetail(rebateDetail, userInfo);
                                }else{
                                    //普通返利用户
                                    addGeneralRebateUserIncomeDetail(rebateDetail);
                                }
                            }
                        }

                        //设置订单明细中的用户返利佣金
                        rebateDetail.setUserCommission(userCommission);

                        //查询是否已存在订单明细
                        RebateDetailQuery rebateDetailQuery = new RebateDetailQuery();
                        rebateDetailQuery.setOrderId(rebateDetail.getOrderId());
                        RebateDetail existsRebateDetail = rebateDetailDao.queryRebateDetailByOrderId(rebateDetailQuery);

                        if (null == existsRebateDetail) {
                            //插入明细
                            rebateDetailDao.insert(rebateDetail);
                        } else {
                            //更新明细状态
                            rebateDetailDao.update(rebateDetail);
                        }
                    }catch (Exception e){
                        LOG.error("subUnionId:{},openId:{}",rebateDetail.getSubUnionId(),rebateDetail.getOpenId());
                    }
                }

                page++;
                rebateDetails = jdSdkManager.getRebateDetails(queryTime, page, pageSize);
            }
        }
    }

    /**
     * 普通返利用户插入收入明细
     *
     * @param rebateDetail
     * @return
     */
    private Double addGeneralRebateUserIncomeDetail(RebateDetail rebateDetail) {

        Product productQuery = new Product();
        productQuery.setProductId(rebateDetail.getProductId());
        productQuery.setStatus(EProductStatus.PASS.getCode());
        Product product = productDao.findById(productQuery);
        if(null!=product && product.getIsRebate()==EProudctRebateType.NOT_REBATE.getCode()){
            return 0.0;
        }

        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getGeneralRebateUserPlatRatio());

        //给返利用户返佣金
        Double userCommission = new BigDecimal(rebateDetail.getCommission() + "").subtract(new BigDecimal(platCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        addIncomeDetail(rebateDetail, EIncomeType.GENERAL_ORDER_REBATE.getCode(), rebateDetail.getOpenId(), userCommission);

        return userCommission;
    }

    /**
     * 代理模式二插入收入明细
     *
     * @param rebateDetail
     * @return
     */
    private Double addSecondAgentIncomeDetail(RebateDetail rebateDetail, UserInfo userInfo) {
        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getSencondAgentPlatRatio());

        if(jDProperty.isWhiteAgent(rebateDetail.getSubUnionId())){
            //如果为白名单，平台不抽成
            platCommission = 0.0;
        }

        Double agentCommission = 0.0;
        if (StringUtils.isNotBlank(userInfo.getRecommendAccount())) {
            //给推荐的代理用户根据比例分配佣金
            agentCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getSencondAgentRatio());
            addIncomeDetail(rebateDetail, EIncomeType.SECOND_AGENT_REBATE.getCode(), userInfo.getRecommendAccount(), agentCommission);
        }

        //给返利用户返佣金
        Double userCommission = new BigDecimal(rebateDetail.getCommission() + "").subtract(new BigDecimal(platCommission + "")).subtract(new BigDecimal(agentCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        addIncomeDetail(rebateDetail, EIncomeType.SECOND_ORDER_REBATE.getCode(), rebateDetail.getOpenId(), userCommission);

        return userCommission;
    }

    /**
     * 代理模式一插入收入明细
     *
     * @param rebateDetail
     * @return
     */
    private Double addFirstAgentIncomeDetail(RebateDetail rebateDetail) {
        Double resultCommission = null;

        //根据子联盟id查询代理关系
        AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
        agentRelationQuery.setAgentSubUnionId(rebateDetail.getSubUnionId());
        AgentRelation agentRelation = agentRelationDao.findByAgentSubUnionId(agentRelationQuery);

        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getFirstAgentPlatRatio());

        if(jDProperty.isWhiteAgent(rebateDetail.getSubUnionId())){
            //如果为白名单，平台不抽成
            platCommission = 0.0;
        }

        Double commission = new BigDecimal(rebateDetail.getCommission() + "").subtract(new BigDecimal(platCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        //如果当前用户为二级代理，则给一级代理进行分佣，所分佣金为平台抽成后的
        if (null != agentRelation && StringUtils.isNotBlank(agentRelation.getParentAgentSubUnionId())) {
            //二级代理用户根据比例获取佣金
            Double agentCommission = commission * agentRelation.getCommissionRatio();
            agentCommission = new BigDecimal(agentCommission).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();//精确2位小数

            //给上一级代理即一级代理进行分佣
            Double parentAgentCommission = new BigDecimal(commission + "").subtract(new BigDecimal(agentCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            //代理用户,根据比例获取佣金
            addIncomeDetail(rebateDetail, EIncomeType.FIRST_ORDER_REBATE.getCode(), rebateDetail.getOpenId(), agentCommission);

            //给上级代理进行分佣
            String parentAgentOpenId = "";
            UserInfo parentAgentQuery = new UserInfo();
            parentAgentQuery.setSubUnionId(agentRelation.getParentAgentSubUnionId());
            UserInfo parentAgentUserInfo = userInfoDao.findUserInfoBySubUnionId(parentAgentQuery);
            if (null != parentAgentUserInfo) {
                parentAgentOpenId = parentAgentUserInfo.getSubUnionId();
                addIncomeDetail(rebateDetail, EIncomeType.FIRST_AGENT_REBATE.getCode(), parentAgentOpenId, parentAgentCommission);
            }
            resultCommission = agentCommission;
        } else {
            //没有上级代理，则直接返佣
            addIncomeDetail(rebateDetail, EIncomeType.FIRST_ORDER_REBATE.getCode(), rebateDetail.getOpenId(), commission);
            resultCommission = commission;

        }

        return resultCommission;
    }

    /**
     * 添加收入明细
     *
     * @param rebateDetail
     * @param type
     * @param openId
     * @param income
     */
    private void addIncomeDetail(RebateDetail rebateDetail, int type, String openId, Double income) {
        Long referenceId = rebateDetail.getId();
        if (StringUtils.isNotBlank(rebateDetail.getSubUnionId()) && ERebateDetailStatus.SETTLEMENT.getCode() == rebateDetail.getStatus()) {
            IncomeDetailQuery incomeDetailQuery = new IncomeDetailQuery();
            incomeDetailQuery.setOpenId(openId);
            incomeDetailQuery.setReferenceId(referenceId);
            if (null == incomeDetailDao.findIncomeDetail(incomeDetailQuery)) {
                //插入收支记录
                IncomeDetail incomeDetail = new IncomeDetail();
                incomeDetail.setOpenId(openId);
                incomeDetail.setReferenceId(referenceId);
                incomeDetail.setIncome(income);
                incomeDetail.setStatus(0);
                incomeDetail.setDealt(0);
                incomeDetail.setType(type);
                incomeDetailDao.insert(incomeDetail);

                //更新用户提现余额
                userInfoService.updateUserCommission(openId);
            }
        }

    }


    @Override
    public void importAllJdMediaProducts() {
        int page = 1;
        int pageSize = 50;
        List<Long> skuList = JdMediaProductGrapUtil.grabProducts(page, pageSize);
        List<Product> products = jdSdkManager.getMediaProducts(Joiner.on(",").join(skuList));
        while (products.size() > 0) {
            LOG.error("[JD联盟全量导入任务]商品导入任务!page:" + page + ",size:" + products.size());
            for (Product product : products) {
                product.setCouponType(EProudctCouponType.GENERAL.getCode());

                //计算返佣规则确定商品是否返佣
                if (RebateRuleUtil.isRebate(product.getCommissionWl(), false)) {
                    product.setIsRebate(EProudctRebateType.REBATE.getCode());
                } else {
                    product.setIsRebate(EProudctRebateType.NOT_REBATE.getCode());
                }

                if (null == productDao.findById(product)) {
                    productDao.insert(product);
                } else {

                    //存在则更新
                    productDao.update(product);
                }
            }
            page++;
            skuList = JdMediaProductGrapUtil.grabProducts(page, pageSize);
            products = jdSdkManager.getMediaProducts(Joiner.on(",").join(skuList));
        }

    }

    @Override
    public void importMediaThemeProducts() {
        int page = 1;
        int pageSize = 1000;
        List<Product> products = jdSdkManager.getMediaThemeProducts(page, pageSize);
        while (products.size() > 0) {
            LOG.error("[importMediaThemeProducts]商品导入任务!page:" + page + ",size:" + products.size());
            for (Product product : products) {
                if (null == productDao.findById(product)) {
                    productDao.insert(product);
                } else {

                    //存在则更新
                    productDao.update(product);
                }
            }
            page++;
            products = jdSdkManager.getMediaThemeProducts(page, pageSize);
        }

    }

    @Override
    public void importCouponProducts() {
        int page = 1;
        int pageSize = 20;
        List<ProductCoupon> list = jdSdkManager.getMediaCoupons(page, pageSize);
        while (list.size() > 0) {
            LOG.error("[importCouponProducts]page:" + page + ",list:" + list.size());
            List<Long> skuList = new ArrayList<>();
            for (ProductCoupon productCoupon : list) {
                if (null == productCouponDao.findById(productCoupon)) {
                    productCouponDao.insert(productCoupon);
                } else {
                    productCouponDao.update(productCoupon);
                }
                skuList.add(productCoupon.getProductId());
            }

            List<Product> products = jdSdkManager.getMediaProducts(Joiner.on(",").join(skuList));

            for (Product product : products) {
                product.setCouponType(EProudctCouponType.COUPON.getCode());

                if (null == productDao.findById(product)) {
                    product.setIsRebate(EProudctRebateType.NOT_REBATE.getCode());
                    productDao.insert(product);
                } else {
                    productDao.update(product);
                }
            }
            page++;
            list = jdSdkManager.getMediaCoupons(page, pageSize);
        }

    }


}

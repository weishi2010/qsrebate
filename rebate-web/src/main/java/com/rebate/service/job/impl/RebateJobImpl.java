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
                    //查询用户信息
                    UserInfo userInfoQuery = new UserInfo();
                    userInfoQuery.setSubUnionId(rebateDetail.getSubUnionId());
                    UserInfo userInfo = userInfoDao.findUserInfoBySubUnionId(userInfoQuery);

                    Double userCommission = RebateRuleUtil.getJDUserCommission(rebateDetail.getCommission());
                    Double agentCommission = null;
                    Double parentAgentCommission = null;

                    //根据子联盟id查询代理关系
                    AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
                    agentRelationQuery.setAgentSubUnionId(rebateDetail.getSubUnionId());
                    AgentRelation agentRelation = agentRelationDao.findByAgentSubUnionId(agentRelationQuery);
                    //如果存在上一级代理，则给上一级代理分成
                    if (null != agentRelation && StringUtils.isNotBlank(agentRelation.getParentAgentSubUnionId())) {
                        //代理用户,根据比例获取佣金
                        agentCommission = userCommission * agentRelation.getCommissionRatio();
                        agentCommission = new BigDecimal(agentCommission).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();//精确2位小数

                        //给上级代理进行分佣
                        parentAgentCommission = userCommission - agentCommission;
                        parentAgentCommission = new BigDecimal(parentAgentCommission).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();//精确2位小数

                    }

                    //查询是否已存在订单明细
                    RebateDetailQuery rebateDetailQuery = new RebateDetailQuery();
                    rebateDetailQuery.setOrderId(rebateDetail.getOrderId());
                    RebateDetail existsRebateDetail = rebateDetailDao.queryRebateDetailByOrderId(rebateDetailQuery);

                    if (null == existsRebateDetail) {
                        //查询商品，如果为不可返佣商品则返佣金额设置为0
                        Product productQuery = new Product();
                        productQuery.setProductId(rebateDetail.getProductId());
                        Product product = productDao.findById(productQuery);

                        //如果为普通用户则根据商品返利标识进行确认是否进行返利
                        if (null != product && EProudctRebateType.NOT_REBATE.getCode() == product.getIsRebate()
                                && userInfo.getAgent() == EAgent.NOT_AGENT.getCode()) {
                            rebateDetail.setUserCommission(0.0);
                        }else if(null!=agentCommission){
                            //二级代理用户按二级代理比例进行计算
                            rebateDetail.setUserCommission(agentCommission);
                        }else{
                            //一级代理、普通返利用户则按平台抽成后的佣金进行返佣
                            rebateDetail.setUserCommission(userCommission);
                        }

                        //如果是白名单则不进行抽成，进行使用联盟返还的佣金
                        if(jDProperty.isWhiteAgent(userInfo.getSubUnionId())){
                            rebateDetail.setUserCommission(rebateDetail.getCommission());
                        }

                        //插入明细
                        rebateDetailDao.insert(rebateDetail);

                    } else {

                        //更新明细状态
                        rebateDetailDao.update(rebateDetail);
                    }

                    //对有子联盟ID且未结算过的订单明细进行用户返佣收入更新
                    if (StringUtils.isNotBlank(rebateDetail.getSubUnionId()) && ERebateDetailStatus.SETTLEMENT.getCode() == rebateDetail.getStatus()) {
                        userIncomeUpdate(rebateDetail, userInfo, agentRelation, agentCommission, parentAgentCommission);
                    }

                }
                page++;
                rebateDetails = jdSdkManager.getRebateDetails(queryTime, page, pageSize);
            }
        }
    }

    /**
     * 更新用户收入
     *
     * @param rebateDetail
     * @param userInfo
     */
    private void userIncomeUpdate(RebateDetail rebateDetail, UserInfo userInfo, AgentRelation agentRelation, Double agentCommission, Double parentAgentCommission) {
        if (rebateDetail.getUserCommission() <= 0) {
            return;
        }

        //如果存在上一级代理，则给上一级代理分成
        if (null != agentRelation && StringUtils.isNotBlank(agentRelation.getParentAgentSubUnionId())) {
            //代理用户,根据比例获取佣金
            addIncomeDetail(rebateDetail.getId(), EIncomeType.ORDER_REBATE.getCode(), rebateDetail.getOpenId(), agentCommission);

            //给上级代理进行分佣
            String parentAgentOpenId = "";
            UserInfo parentAgentQuery = new UserInfo();
            parentAgentQuery.setSubUnionId(agentRelation.getParentAgentSubUnionId());
            UserInfo parentAgentUserInfo = userInfoDao.findUserInfoBySubUnionId(parentAgentQuery);
            if (null != parentAgentUserInfo) {
                parentAgentOpenId = parentAgentUserInfo.getSubUnionId();
                addIncomeDetail(rebateDetail.getId(), EIncomeType.AGENT_REBATE.getCode(), parentAgentOpenId, parentAgentCommission);
            }
        } else {
            //不存在代理关系的，则按普通返利用户进行返佣金
            addIncomeDetail(rebateDetail.getId(), EIncomeType.ORDER_REBATE.getCode(), rebateDetail.getOpenId(), rebateDetail.getUserCommission());
        }


        //更新用户提现余额
        if (null != userInfo) {
            userInfoService.updateUserCommission(rebateDetail.getOpenId());
        }

    }

    /**
     * 添加收入明细
     *
     * @param referenceId
     * @param type
     * @param openId
     * @param income
     */
    private void addIncomeDetail(Long referenceId, int type, String openId, Double income) {
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

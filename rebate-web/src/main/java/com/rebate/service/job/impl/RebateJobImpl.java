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
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.job.RebateJob;
import com.rebate.service.product.ProductCouponService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
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

    @Qualifier("recommendUserInfoDao")
    @Autowired(required = true)
    private RecommendUserInfoDao recommendUserInfoDao;

    @Qualifier("productCouponService")
    @Autowired(required = true)
    private ProductCouponService productCouponService;


    @Qualifier("wxService")
    @Autowired(required = false)
    private WxService wxService;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = false)
    private WxAccessTokenService wxAccessTokenService;

    @Qualifier("userSummaryDao")
    @Autowired
    private UserSummaryDao userSummaryDao;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Override
    public void refreshUserClick() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            List<UserInfo> list = userInfoDao.findAllUsers(new UserInfo());
            LOG.error("[点击数入库任务]加载第{}条用户记录！", list.size());


            for (UserInfo userInfo : list) {
                for (int days = 0; days < 20; days++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_WEEK, -days);
                    Date day = calendar.getTime();
                    Long clickCount = shortUrlManager.getJDUnionUrlClick(userInfo.getSubUnionId(), day);
                    if (clickCount > 0) {

                        UserSummary userSummary = new UserSummary();
                        userSummary.setSubUnionId(userInfo.getSubUnionId());
                        userSummary.setOpDate(sdf.parse(sdf.format(day)));
                        userSummary.setClickCount(clickCount.intValue());
                        if (null == userSummaryDao.findUserSummary(userSummary)) {
                            userSummaryDao.insert(userSummary);
                        } else {
                            userSummaryDao.update(userSummary);

                        }
                    }

                    //全站点击更新
                    String qsSubUnionId = "ALL";
                    UserSummary userSummary = new UserSummary();
                    userSummary.setSubUnionId(qsSubUnionId);
                    userSummary.setOpDate(sdf.parse(sdf.format(day)));
                    userSummary.setClickCount(shortUrlManager.getJDUnionUrlClick(qsSubUnionId, day).intValue());
                    if (null == userSummaryDao.findUserSummary(userSummary)) {
                        userSummaryDao.insert(userSummary);
                    } else {
                        userSummaryDao.update(userSummary);

                    }
                }

            }


        } catch (ParseException e) {
            LOG.error("refreshUserClick error!", e);
        }

    }

    @Override
    public void refreshUserInfo() {
        List<UserInfo> list = userInfoDao.findAllUsers(new UserInfo());
        for (UserInfo userInfo : list) {
            WxUserInfo wxUserInfo = wxService.getWxApiUserInfo(wxAccessTokenService.getApiAccessToken().getAccessToken(), userInfo.getOpenId());
            if (null != wxUserInfo) {
                //更新昵称
                userInfo.setNickName(wxUserInfo.getNickname());
                userInfoDao.update(userInfo);
            }
        }
    }

    @Override
    public void refreshProducts() {
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
    public void refreshDaxueCouponProductsCache() {
        String subUnionId = jDProperty.getApiSubUnionId();
        int page = 1;
        int pageSize = 100;
        ProductCouponQuery productCouponQuery = new ProductCouponQuery();
        productCouponQuery.setStartRow((page - 1) * pageSize);
        productCouponQuery.setPageSize(pageSize);
        productCouponQuery.setStatus(EProductStatus.PASS.getCode());

        List<ProductCoupon> productCoupons = productCouponDao.findProductCoupons(productCouponQuery);

        while (productCoupons.size() > 0) {
            LOG.error("[掌上大学优惠券商品缓存刷新任务]加载第" + page + "页{}条记录！", productCoupons.size());

            for (ProductCoupon productCoupon : productCoupons) {
                Long productId = productCoupon.getProductId();
                //清理掉没有优惠券转链接的信息
                String coupontPromotionLink = jdSdkManager.getPromotionCouponCode(productCoupon.getProductId(), productCoupon.getCouponLink(), subUnionId);
                if (StringUtils.isNotBlank(coupontPromotionLink)) {

                    //查询商品信息
                    Product productQuery = new Product();
                    productQuery.setProductId(productId);
                    Product product = productDao.findById(productQuery);
                    //保健一级分类先暂时不推送
                    if (null != product && 9192 == product.getFirstCategory()) {
                        continue;
                    }

                    //方案一：记录到数据库表中
                    DaxueProduct daxueProduct = new DaxueProduct();
                    daxueProduct.setProductId(productId);
                    daxueProduct.setPromotionUrl(coupontPromotionLink);
                    if (null == productDao.findDaxueProductById(daxueProduct)) {
                        productDao.insertDaxueProduct(daxueProduct);
                    } else {
                        productDao.updateDaxueProduct(daxueProduct);
                    }


                    //方案二：记录到缓存中
                    //构造vo
                    ProductVo productVo = new ProductVo(product);

                    productVo.setPromotionUrl(coupontPromotionLink);
                    productVo.setPromotionShortUrl(coupontPromotionLink);
                    productVo.setProductCoupon(productCoupon);

                    if (productVo.getCouponPrice() <= 10) {
                        //添加到9.9商品缓存id列表中
                        productCouponService.addSecskillProductListCache(productVo);
                    } else {
                        //添加到内购券缓存id列表中
                        productCouponService.addProductCouponListCache(productVo);
                    }
                    //存在则更新到单条缓存
                    productCouponService.addProductVoCache(productVo);

                } else {
                    //方案一
                    // 从数据库中清除掉SKU
                    productDao.deleteDaxueProductByProductId(productId);

                    //方案二
                    //券链接为空说明已失效，删除单条缓存
                    productCouponService.cleanProductVoCache(productCoupon.getProductId());
                    if (productCoupon.getCouponPrice() <= 10) {
                        //从id列表缓存中删除
                        productCouponService.cleanSecskillProductListCache(productCoupon.getProductId());
                    } else {
                        //从id列表缓存中删除
                        productCouponService.cleanProductCouponListCache(productCoupon.getProductId());
                    }

                }
            }

            page++;
            productCouponQuery.setStartRow((page - 1) * pageSize);
            productCoupons = productCouponDao.findProductCoupons(productCouponQuery);
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
                    try {

                        if (StringUtils.isNotBlank(rebateDetail.getSubUnionId())) {
                            //查询用户信息
                            UserInfo userInfoQuery = new UserInfo();
                            userInfoQuery.setSubUnionId(rebateDetail.getSubUnionId());
                            UserInfo userInfo = userInfoDao.findUserInfoBySubUnionId(userInfoQuery);
                            if (null != userInfo) {
                                if (EAgent.FIRST_AGENT.getCode() == userInfo.getAgent()) {
                                    //如果为代理模式一，则根据给上级代理进行分佣
                                    rebateDetail = addFirstAgentIncomeDetail(rebateDetail);
                                } else if (EAgent.SECOND_AGENT.getCode() == userInfo.getAgent()) {
                                    //代理模式二
                                    rebateDetail = addSecondAgentIncomeDetail(rebateDetail);
                                } else {
                                    //普通返利用户
                                    rebateDetail = addGeneralRebateUserIncomeDetail(rebateDetail, userInfo);
                                }
                            } else {
                                rebateDetail.setUserCommission(0.0);
                            }
                        } else {
                            rebateDetail.setUserCommission(0.0);
                        }


                        if (null == rebateDetail.getAgentCommission()) {
                            rebateDetail.setAgentCommission(0.0);
                        }

                        //查询是否已存在订单明细
                        RebateDetailQuery rebateDetailQuery = new RebateDetailQuery();
                        rebateDetailQuery.setOrderId(rebateDetail.getOrderId());
                        rebateDetailQuery.setProductId(rebateDetail.getProductId());
                        RebateDetail existsRebateDetail = rebateDetailDao.queryRebateDetailByOrderId(rebateDetailQuery);

                        if (null == existsRebateDetail) {
                            //插入明细
                            rebateDetailDao.insert(rebateDetail);
                        } else {
                            //更新明细状态
                            rebateDetailDao.update(rebateDetail);
                        }
                    } catch (Exception e) {
                        LOG.error("subUnionId:{}", rebateDetail.getSubUnionId(), e);
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
    private RebateDetail addGeneralRebateUserIncomeDetail(RebateDetail rebateDetail, UserInfo userInfo) {

        Product productQuery = new Product();
        productQuery.setProductId(rebateDetail.getProductId());
        productQuery.setStatus(EProductStatus.PASS.getCode());
        Product product = productDao.findById(productQuery);
        if (null != product && product.getIsRebate() == EProudctRebateType.NOT_REBATE.getCode()) {
            rebateDetail.setUserCommission(0.0);
            return rebateDetail;
        }
        Double agentCommission = 0.0;
        Double platCommission = null;

        RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
        recommendUserInfoQuery.setOpenId(userInfo.getOpenId());
        RecommendUserInfo existsRecommendUserInfo = recommendUserInfoDao.findRecommendUserInfo(recommendUserInfoQuery);
        if (null != existsRecommendUserInfo && StringUtils.isNotBlank(existsRecommendUserInfo.getRecommendAccount())) {
            //代理模式2的分成
            //平台抽成佣金
            platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getSencondAgentPlatRatio());


            //给推荐的代理用户根据比例分配佣金
            agentCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getSencondAgentRatio());
            addIncomeDetail(rebateDetail, EIncomeType.SECOND_AGENT_REBATE.getCode(), userInfo.getRecommendAccount(), agentCommission);
        } else {
            //平台抽成佣金
            platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getGeneralRebateUserPlatRatio());
        }

        //给返利用户返佣金
        Double userCommission = new BigDecimal(rebateDetail.getCommission() + "").subtract(new BigDecimal(platCommission + "")).subtract(new BigDecimal(agentCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        addIncomeDetail(rebateDetail, EIncomeType.GENERAL_ORDER_REBATE.getCode(), rebateDetail.getOpenId(), userCommission);


        rebateDetail.setPlatformRatio(jDProperty.getSencondAgentPlatRatio());
        rebateDetail.setUserCommission(userCommission);
        rebateDetail.setAgentCommission(agentCommission);//给推荐用户分佣金时记录到明细中
        return rebateDetail;
    }

    /**
     * 代理模式二插入收入明细
     *
     * @param rebateDetail
     * @return
     */
    private RebateDetail addSecondAgentIncomeDetail(RebateDetail rebateDetail) {
        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getSencondAgentPlatRatio());

        if (jDProperty.isWhiteAgent(rebateDetail.getSubUnionId())) {
            //如果为白名单，平台不抽成
            platCommission = 0.0;
        }

        //给返利用户返佣金
        Double userCommission = new BigDecimal(rebateDetail.getCommission() + "").subtract(new BigDecimal(platCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        addIncomeDetail(rebateDetail, EIncomeType.SECOND_ORDER_REBATE.getCode(), rebateDetail.getOpenId(), userCommission);


        rebateDetail.setPlatformRatio(jDProperty.getSencondAgentPlatRatio());
        rebateDetail.setUserCommission(userCommission);
        rebateDetail.setAgentCommission(0.0);

        return rebateDetail;
    }

    /**
     * 代理模式一插入收入明细
     *
     * @param rebateDetail
     * @return
     */
    private RebateDetail addFirstAgentIncomeDetail(RebateDetail rebateDetail) {
        Double resultCommission = null;

        //根据子联盟id查询代理关系
        AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
        agentRelationQuery.setAgentSubUnionId(rebateDetail.getSubUnionId());
        AgentRelation agentRelation = agentRelationDao.findByAgentSubUnionId(agentRelationQuery);

        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(rebateDetail.getCommission(), jDProperty.getFirstAgentPlatRatio());

        if (jDProperty.isWhiteAgent(rebateDetail.getSubUnionId())) {
            //如果为白名单，平台不抽成
            platCommission = 0.0;
        }

        Double commission = new BigDecimal(rebateDetail.getCommission() + "").subtract(new BigDecimal(platCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Double agentCommission = 0.0;
        //如果当前用户为二级代理，则给一级代理进行分佣，所分佣金为平台抽成后的
        if (null != agentRelation && StringUtils.isNotBlank(agentRelation.getParentAgentSubUnionId())) {
            //二级代理用户根据比例获取佣金
            agentCommission = commission * agentRelation.getCommissionRatio();
            agentCommission = new BigDecimal(agentCommission + "").setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();//精确2位小数

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

        rebateDetail.setPlatformRatio(jDProperty.getFirstAgentPlatRatio());
        rebateDetail.setUserCommission(resultCommission);
        rebateDetail.setAgentCommission(agentCommission);
        return rebateDetail;
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

package com.rebate.manager.jd.impl;

import com.google.common.base.Joiner;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.mall.ProductWrapService.ProductBase;
import com.jd.open.api.sdk.request.cps.*;
import com.jd.open.api.sdk.request.mall.NewWareBaseproductGetRequest;
import com.jd.open.api.sdk.response.cps.*;
import com.jd.open.api.sdk.response.mall.NewWareBaseproductGetResponse;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.CouponUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.common.util.rebate.RebateRuleUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.AgentRelationDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.en.*;
import com.rebate.domain.jd.JDConfig;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.AgentRelationQuery;
import com.rebate.manager.jd.JdSdkManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Component("jdSdkManager")
public class JdSdkManagerImpl implements JdSdkManager {
    private static final Logger LOG = LoggerFactory.getLogger(JdSdkManagerImpl.class);

    @Qualifier("jdConfig")
    @Autowired(required = true)
    private JDConfig jdConfig;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;


    @Qualifier("agentRelationDao")
    @Autowired(required = true)
    private AgentRelationDao agentRelationDao;

    private static final TypeReference<List<Map>> mapTypeReference = new TypeReference<List<Map>>() {
    };

    @Override
    public Product getMediaProduct(Long skuId) {
        Product product = null;
        List<Product> list = getMediaProducts(skuId.toString());
        if (null != list && list.size() > 0) {
            product = list.get(0);
        }
        return product;
    }

    @Override
    public Double getQSCommission(UserInfo userInfo,Product product){
        Double userCommission = 0.0;
        if(null==userInfo){
            return userCommission;
        }

        Double commission = product.getCommissionWl();
        if(product.getCouponPrice()>0){
            //如果有券后价则为内购券商品，按券后价计算预计返佣
            commission = product.getCouponPrice()*product.getCommissionRatioWl();
            commission = new BigDecimal(commission + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        }

        if (EAgent.FIRST_AGENT.getCode() == userInfo.getAgent()) {
            //代理模式一佣金计算
            userCommission = computeFirstAgentCommission(userInfo.getSubUnionId(),commission);
        } else if (EAgent.SECOND_AGENT.getCode() == userInfo.getAgent()) {
            //代理模式二佣金计算
            userCommission = computeSecondAgentCommission(userInfo.getSubUnionId(),commission);
        }else{
            if(null!=product && null!=product.getIsRebate() && product.getIsRebate()==EProudctRebateType.NOT_REBATE.getCode()){
                userCommission = 0.0;
            }else{
                //普通返利用户
                userCommission = computeGeneralRebateUserCommission(userInfo,commission);
            }
        }

        //JD联盟返回的佣金还要扣除10%的服务费
        userCommission = userCommission*.9;
        userCommission = new BigDecimal(userCommission + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return userCommission;
    }

    /**
     * 普通返利用户佣金计算
     * @return
     */
    private Double computeGeneralRebateUserCommission(UserInfo userInfo,Double commission){

        Double agentCommission = 0.0;
        Double platCommission = null;

        if (StringUtils.isNotBlank(userInfo.getRecommendAccount())) {
            //代理模式2的分成
            //平台抽成佣金
            platCommission = RebateRuleUtil.computeCommission(commission, jDProperty.getSencondAgentPlatRatio());

            //给推荐的代理用户根据比例分配佣金
            agentCommission = RebateRuleUtil.computeCommission(commission, jDProperty.getSencondAgentRatio());
        }else{
            //平台抽成佣金
            platCommission = RebateRuleUtil.computeCommission(commission, jDProperty.getGeneralRebateUserPlatRatio());

            agentCommission = 0.0;
        }

        LOG.error("computeGeneralRebateUserCommission,commission:"+commission+",platCommission:"+platCommission+",agentCommission:"+agentCommission);
        //给返利用户返佣金
        Double userCommission = new BigDecimal(commission + "").subtract(new BigDecimal(platCommission + "")).subtract(new BigDecimal(agentCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return userCommission;
    }

    /**
     * 代理模式二佣金计算
     */
    private Double computeSecondAgentCommission(String recommmendSubUnionId,Double commission) {
        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(commission, jDProperty.getSencondAgentPlatRatio());

        if(jDProperty.isWhiteAgent(recommmendSubUnionId)){
            //如果为白名单，平台不抽成
            platCommission = 0.0;
        }

        //给返利用户返佣金
        Double userCommission = new BigDecimal(commission + "").subtract(new BigDecimal(platCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        LOG.error("computeFirstAgentCommission,commission:"+userCommission+",platCommission:"+platCommission);

        return userCommission;
    }

    /**
     * 代理模式一佣金计算
     */
    private Double computeFirstAgentCommission(String subUnionId,Double commission) {
        Double resultCommission = null;

        //根据子联盟id查询代理关系
        AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
        agentRelationQuery.setAgentSubUnionId(subUnionId);
        AgentRelation agentRelation = agentRelationDao.findByAgentSubUnionId(agentRelationQuery);

        //平台抽成佣金
        Double platCommission = RebateRuleUtil.computeCommission(commission, jDProperty.getFirstAgentPlatRatio());
        if(jDProperty.isWhiteAgent(subUnionId)){
            //如果为白名单，平台不抽成
            platCommission = 0.0;
        }

        Double subCommission = new BigDecimal(commission + "").subtract(new BigDecimal(platCommission + "")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        //如果当前用户为二级代理，则给一级代理进行分佣，所分佣金为平台抽成后的
        if (null != agentRelation && StringUtils.isNotBlank(agentRelation.getParentAgentSubUnionId())) {
            //二级代理用户根据比例获取佣金
            Double agentCommission = subCommission * agentRelation.getCommissionRatio();
            agentCommission = new BigDecimal(agentCommission+"").setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();//精确2位小数
            resultCommission = agentCommission;
        } else {
            resultCommission = subCommission;

        }
        LOG.error("computeFirstAgentCommission,commission:"+resultCommission+",platCommission:"+platCommission);

        return resultCommission;
    }


    @Override
    public List<Product> getMediaProducts(String skuIds) {
        List<Product> list = new ArrayList<>();
        String json = getGetpromotioninfoResult(skuIds);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && resultObj.getBoolean("sucessed")) {
            String resultJson = resultObj.get("result").toString();
            List<Map> mapList = JsonUtil.fromJson(resultJson, mapTypeReference);
            if (null != mapList) {
                for (Map map : mapList) {
                    Product product = new Product();
                    product.setProductId(Long.parseLong(map.get("skuId").toString()));
                    product.setCommissionRatioWl(Double.parseDouble(map.get("commisionRatioWl").toString()) / 100);
                    //初始化BigDecimal时要通过字符串进行初始化创建，否则会丢失精度
                    product.setCommissionRatioWl(new BigDecimal(product.getCommissionRatioWl()+"").setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setCommissionRatioPc(Double.parseDouble(map.get("commisionRatioPc").toString()) / 100);
                    product.setCommissionRatioPc(new BigDecimal(product.getCommissionRatioPc()+"").setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("goodsName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setMaterialUrl(map.get("materialUrl").toString());
                    product.setSourcePlatform(EProductSource.JD.getCode());//来源JD
                    product.setOriginalPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setUnitPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setWlUnitPrice(Double.parseDouble(map.get("wlUnitPrice").toString()));
                    product.setStartDate(new Date(Long.parseLong(map.get("startDate").toString())));
                    product.setEndDate(new Date(Long.parseLong(map.get("endDate").toString())));
                    //轻松返平台获取佣金
                    Double qsCommissionWl = getCommissionWl(product.getCommissionRatioWl(), product.getOriginalPrice());//移动端
                    Double qsCommissionPc = getCommissionPc(product.getCommissionRatioPc(), product.getOriginalPrice());//PC端

                    product.setCommissionPc(qsCommissionPc);//联盟给平台的移动返还佣金
                    product.setCommissionWl(qsCommissionWl);//联盟给平台的PC返还佣金
                    product.setUserCommission(0.0);//平台返还用户佣金
                    product.setShopId(Long.parseLong(map.get("shopId").toString()));
                    product.setDistribution(1);
                    product.setProductType(1);
                    product.setCouponPrice(0.0);
                    product.setFreePost(EProductFreePost.NOT_FREE_POST.getCode());//默认为不包邮
                    product.setStock(0);
                    product.setStatus(EProductStatus.PASS.getCode());
                    product.setSortWeight(0);
                    product.setFirstCategoryName("");
                    product.setSecondCategoryName("");
                    product.setThirdCategoryName("");
                    product.setFirstCategory(1);
                    product.setSecondCategory(2);
                    product.setThirdCategory(3);

                    list.add(product);
                }

                //获取商品基础信息
                List<ProductBase> mediaProductBaseInfos = getProductBaseInfos(skuIds);
                Map productMap = new HashMap();
                for (ProductBase productBase : mediaProductBaseInfos) {
                    productMap.put(productBase.getSkuId(), productBase);
                }
                //设置分类信息
                for (Product product : list) {
                    ProductBase productBase = (ProductBase) productMap.get(product.getProductId());
                    if (null != productBase) {
                        String[] categoryArray = productBase.getCategory().split(";");
                        product.setFirstCategory(Integer.parseInt(categoryArray[0]));
                        product.setSecondCategory(Integer.parseInt(categoryArray[1]));
                        product.setThirdCategory(Integer.parseInt(categoryArray[2]));
                    }
                }

            }
        }
        return list;
    }


    @Override
    public PaginatedArrayList<ProductCoupon> getMediaCoupons(int page, int pageSize) {
        PaginatedArrayList<ProductCoupon> list = new PaginatedArrayList<>(page, pageSize);

        String json = getGetCouponGoodsResult(page, pageSize);

        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && resultObj.getInt("resultCode") == 200) {
            int total = resultObj.getInt("total");
            list.setTotalItem(total);

            String resultJson = resultObj.get("data").toString();
            List<Map> mapList = JsonUtil.fromJson(resultJson, mapTypeReference);
            if (null != mapList) {
                for (Map map : mapList) {
                    ProductCoupon coupon = new ProductCoupon();
                    coupon.setProductId(Long.parseLong(map.get("skuId").toString()));
                    coupon.setCouponNote(map.get("couponNote").toString());
                    coupon.setStartDate(new Date(Long.parseLong(map.get("startTime").toString())));
                    coupon.setEndDate(new Date(Long.parseLong(map.get("endTime").toString())));
                    coupon.setCouponTab(Integer.parseInt(map.get("couponTab").toString()));
                    coupon.setSourcePlatform(EProductSource.JD.getCode());
                    coupon.setDiscount(CouponUtil.getCouponDiscount(coupon.getCouponNote()));
                    coupon.setQuota(CouponUtil.getCouponQuota(coupon.getCouponNote()));
                    coupon.setYn(0);
                    coupon.setNum(0);
                    coupon.setRemainNum(0);
                    coupon.setCouponLink("");
                    coupon.setCouponPrice(0.0);
                    coupon.setStatus(0);
                    coupon.setPlatform(0);
                    list.add(coupon);
                }
            }
        }
        return list;
    }

    @Override
    public PaginatedArrayList<Product> getMediaCouponProducts(int page, int pageSize) {
        PaginatedArrayList<Product> list = new PaginatedArrayList<>(page, pageSize);

        String json = getGetCouponGoodsResult(page, pageSize);

        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && resultObj.getInt("resultCode") == 200) {
            int total = resultObj.getInt("total");
            list.setTotalItem(total);

            String resultJson = resultObj.get("data").toString();
            List<Map> mapList = JsonUtil.fromJson(resultJson, mapTypeReference);
            if (null != mapList) {
                for (Map map : mapList) {
                    Product product = new Product();
                    product.setProductId(Long.parseLong(map.get("skuId").toString()));
                    product.setCommissionRatioWl(Double.parseDouble(map.get("commisionRatioWl").toString()) / 100);
                    product.setCommissionRatioWl(new BigDecimal(product.getCommissionRatioWl()+"").setScale(4, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setCommissionRatioPc(Double.parseDouble(map.get("commisionRatioPc").toString()) / 100);
                    product.setCommissionRatioPc(new BigDecimal(product.getCommissionRatioPc()+"").setScale(4, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("skuName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setOriginalPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    //轻松返平台获取佣金
                    Double qsCommissionWl = getCommissionWl(product.getCommissionRatioWl(), product.getOriginalPrice());//移动端
                    Double qsCommissionPc = getCommissionPc(product.getCommissionRatioPc(), product.getOriginalPrice());//PC端

                    product.setCommissionPc(qsCommissionPc);//联盟给平台的移动返还佣金
                    product.setCommissionWl(qsCommissionWl);//联盟给平台的PC返还佣金
                    product.setUserCommission(0.0);//平台返还用户佣金，初始为0

                    product.setDistribution(1);
                    product.setProductType(1);
                    product.setStock(0);
                    product.setStatus(0);
                    product.setFirstCategoryName("");
                    product.setSecondCategoryName("");
                    product.setThirdCategoryName("");
                    product.setFirstCategory(1);
                    product.setSecondCategory(2);
                    product.setThirdCategory(3);

                    list.add(product);
                }
                //获取sku列表
                List<Long> skuIdList = new ArrayList<>();
                for (Product product:list){
                    skuIdList.add(product.getProductId());
                }

                //获取商品基础信息
                List<ProductBase> mediaProductBaseInfos = getProductBaseInfos(Joiner.on(",").join(skuIdList));
                Map productMap = new HashMap();
                for (ProductBase productBase : mediaProductBaseInfos) {
                    productMap.put(productBase.getSkuId(), productBase);
                }
                //设置分类信息
                for (Product product : list) {
                    ProductBase productBase = (ProductBase) productMap.get(product.getProductId());
                    if (null != productBase) {
                        String[] categoryArray = productBase.getCategory().split(";");
                        product.setFirstCategory(Integer.parseInt(categoryArray[0]));
                        product.setSecondCategory(Integer.parseInt(categoryArray[1]));
                        product.setThirdCategory(Integer.parseInt(categoryArray[2]));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public PaginatedArrayList<Product> getMediaThemeProducts(int page, int pageSize) {
        PaginatedArrayList<Product> list = new PaginatedArrayList<>(page, pageSize);

        String json = getGetCouponGoodsResult(page, pageSize);

        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && resultObj.getInt("resultCode") == 200) {
            int total = resultObj.getInt("total");
            list.setTotalItem(total);

            String resultJson = resultObj.get("data").toString();
            List<Map> mapList = JsonUtil.fromJson(resultJson, mapTypeReference);
            if (null != mapList) {
                for (Map map : mapList) {
                    Product product = new Product();
                    product.setProductId(Long.parseLong(map.get("skuId").toString()));
                    product.setCommissionRatioWl(Double.parseDouble(map.get("pcCommissionShare").toString()) / 100);
                    product.setCommissionRatioWl(new BigDecimal(product.getCommissionRatioWl()+"").setScale(4, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setCommissionRatioPc(Double.parseDouble(map.get("wlCommissionShare").toString()) / 100);
                    product.setCommissionRatioPc(new BigDecimal(product.getCommissionRatioPc()+"").setScale(4, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("skuName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setOriginalPrice(Double.parseDouble(map.get("wlPrice").toString()));//获取移动端价格
                    //轻松返平台获取佣金
                    Double qsCommissionWl = getCommissionWl(product.getCommissionRatioWl(), product.getOriginalPrice());//移动端
                    Double qsCommissionPc = getCommissionPc(product.getCommissionRatioPc(), product.getOriginalPrice());//PC端

                    product.setCommissionPc(qsCommissionPc);//联盟给平台的移动返还佣金
                    product.setCommissionWl(qsCommissionWl);//联盟给平台的PC返还佣金
                    product.setUserCommission(0.0);//平台返还用户佣金，初始为0

                    if (RebateRuleUtil.isRebate(product.getCommissionWl(), false)) {
                        product.setIsRebate(EProudctRebateType.REBATE.getCode());
                    } else {
                        product.setIsRebate(EProudctRebateType.NOT_REBATE.getCode());
                    }
                    product.setStartDate(new Date(Long.parseLong(map.get("startTime").toString())));
                    product.setEndDate(new Date(Long.parseLong(map.get("endTime").toString())));
                    product.setWlUnitPrice(Double.parseDouble(map.get("wlPrice").toString()));//获取移动端价格
                    product.setUnitPrice(Double.parseDouble(map.get("pcPrice").toString()));
                    product.setCouponType(EProudctCouponType.GENERAL.getCode());
                    product.setSourcePlatform(EProductSource.JD.getCode());
                    product.setMaterialUrl(map.get("skuUrl").toString());
                    if (map.containsKey("shopId")) {
                        product.setShopId(Long.parseLong(map.get("shopId").toString()));

                    } else {
                        product.setShopId(0l);
                    }
                    product.setSortWeight(0);
                    product.setDistribution(1);
                    product.setProductType(1);
                    product.setStock(0);
                    product.setStatus(0);
                    product.setFirstCategoryName("");
                    product.setSecondCategoryName("");
                    product.setThirdCategoryName("");
                    product.setFirstCategory(1);
                    product.setSecondCategory(2);
                    product.setThirdCategory(3);

                    list.add(product);
                }

                //获取sku列表
                List<Long> skuIdList = new ArrayList<>();
                for (Product product:list){
                    skuIdList.add(product.getProductId());
                }

                //获取商品基础信息
                List<ProductBase> mediaProductBaseInfos = getProductBaseInfos(Joiner.on(",").join(skuIdList));
                Map productMap = new HashMap();
                for (ProductBase productBase : mediaProductBaseInfos) {
                    productMap.put(productBase.getSkuId(), productBase);
                }
                //设置分类信息
                for (Product product : list) {
                    ProductBase productBase = (ProductBase) productMap.get(product.getProductId());
                    if (null != productBase) {
                        String[] categoryArray = productBase.getCategory().split(";");
                        product.setFirstCategory(Integer.parseInt(categoryArray[0]));
                        product.setSecondCategory(Integer.parseInt(categoryArray[1]));
                        product.setThirdCategory(Integer.parseInt(categoryArray[2]));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<RebateDetail> getRebateDetails(String queryTime, int page, int pageSize) {
        List<RebateDetail> list = new ArrayList<>();
        String json = getQueryImportOrdersResult(queryTime, page, pageSize);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && 1 == resultObj.getInt("success") && resultObj.containsKey("data")) {
            JSONArray data = resultObj.getJSONArray("data");
            Iterator it = data.iterator();

            while (it.hasNext()) {
                JSONObject orderObj = (JSONObject) it.next();

                JSONArray skuArray = orderObj.getJSONArray("skus");
                Iterator skuIterator = skuArray.iterator();
                while (skuIterator.hasNext()) {
                    JSONObject skuObj = (JSONObject) skuIterator.next();

                    RebateDetail detail = new RebateDetail();
                    if (orderObj.containsKey("orderTime")) {
                        detail.setSubmitDate(new Date(orderObj.getLong("orderTime")));
                    } else {
                        //未完成的订单，订单时间完成时间设置为一个超大的时间
                        detail.setSubmitDate(new Date(3505625155l));
                    }

                    if (orderObj.containsKey("finishTime")) {
                        detail.setFinishDate(new Date(orderObj.getLong("finishTime")));
                    } else {
                        //未完成的订单，订单时间完成时间设置为一个超大的时间
                        detail.setFinishDate(new Date(3505625155l));
                    }
                    if (orderObj.containsKey("subUnionId")) {
                        detail.setOpenId(orderObj.getString("subUnionId"));
                    }
                    detail.setStatus(orderObj.getInt("balance"));
                    detail.setOrderStatus(orderObj.getInt("yn"));
                    detail.setOrderId(orderObj.getLong("orderId"));

                    detail.setProductId(skuObj.getLong("skuId"));
                    detail.setRebateRatio(skuObj.getDouble("subsidyRate"));
                    detail.setCommissionRatio(skuObj.getDouble("commissionRate"));
                    detail.setProductCount(skuObj.getInt("skuNum"));
                    detail.setProductName(skuObj.getString("skuName"));

                    List<Product> mediaProducts = getMediaProducts(detail.getProductId().toString());
                    if (null != mediaProducts && mediaProducts.size() > 0) {
                        detail.setImgUrl(mediaProducts.get(0).getImgUrl());
                    } else {
                        detail.setImgUrl("");
                    }

                    detail.setPrice(skuObj.getDouble("cosPrice"));
                    detail.setUnionId("");
                    if (skuObj.containsKey("subUnionId")) {
                        detail.setSubUnionId(skuObj.getString("subUnionId"));
                    } else {
                        detail.setSubUnionId("");
                    }
                    detail.setCommission(skuObj.getDouble("commission"));
                    detail.setPositionId(orderObj.getString("positionId"));
                    detail.setPlatformRatio(RebateRuleUtil.PLATFORM_COMMISSION_RATIO * detail.getCommissionRatio());//用户订单明细看到的分成比例为平台抽成比例*联盟返佣比例
                    detail.setPlatformRatio(new BigDecimal(detail.getPlatformRatio()+"").setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());//精确2位小数

                    list.add(detail);
                }

            }
        }
        return list;
    }

    @Override
    public String getLongPromotinUrl(String itemUrl, String subUnionId) {

        String url = itemUrl;
        String json = getServicePromotionCode(itemUrl, subUnionId);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && 0 == Integer.parseInt(resultObj.getString("resultCode"))) {
            url = resultObj.getString("url");
        }
        return url;
    }

    @Override
    public String getShortPromotinUrl(Long skuId, String subUnionId) {
        String url = "";
        int proCont = EJDProCont.PRODUCT_URL.getCode();
        String json = getServicePromotionWxsqCode(skuId.toString(),proCont, subUnionId);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && 0 == Integer.parseInt(resultObj.getString("resultCode"))) {
            JSONObject urlListObj = (JSONObject) resultObj.get("urlList");
            url = urlListObj.getString(skuId.toString());
        }
        return url;
    }

    @Override
    public String getSalesActivityPromotinUrl(String salesUrl, String subUnionId) {
        String url = "";
        int proCont = EJDProCont.PRODUCT_URL.getCode();
        String json = getServicePromotionWxsqCode(salesUrl,proCont, subUnionId);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && 0 == Integer.parseInt(resultObj.getString("resultCode"))) {
            JSONObject urlListObj = (JSONObject) resultObj.get("urlList");
            url = urlListObj.getString(salesUrl);
        }
        return url;
    }

    @Override
    public String getPromotionCouponCode(Long skuId, String couponUrl, String subUnionId) {
        String url = "";
        String json = getServicePromotionCouponCode(skuId.toString(), couponUrl, subUnionId);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && 0 == Integer.parseInt(resultObj.getString("resultCode"))) {
            JSONObject urlListObj = (JSONObject) resultObj.get("urlList");
            url = urlListObj.getString(couponUrl.trim() + "," + skuId);
        }
        return url;
    }

    @Override
    public ProductBase getProductBaseInfo(Long skuId) {
        ProductBase productBase = null;
        List<ProductBase> list = getProductBaseInfos(skuId.toString());
        if (null != list && list.size() > 0) {
            productBase = list.get(0);
        }
        return productBase;
    }

    @Override
    public List<ProductBase> getProductBaseInfos(String skuIds) {
        List<ProductBase> list = new ArrayList<>();
        try {
            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            NewWareBaseproductGetRequest request = new NewWareBaseproductGetRequest();

            request.setIds(skuIds);
            request.setBasefields("category");

            NewWareBaseproductGetResponse response = client.execute(request);
            if(null!=response.getListproductbaseResult()){
                list = response.getListproductbaseResult();
            }

        } catch (Exception e) {
            LOG.error("[获取商品信息]调用异常!skuIds:{}", skuIds);
        }
        return list;
    }

    /**
     * 获取推广商品信息
     *
     * @param skuIds
     * @return
     */
    private String getGetpromotioninfoResult(String skuIds) {
        String json = "";
        try {

            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            ServicePromotionGoodsInfoRequest request = new ServicePromotionGoodsInfoRequest();

            request.setSkuIds(skuIds);

            ServicePromotionGoodsInfoResponse response = client.execute(request);

            json = response.getGetpromotioninfoResult();
        } catch (Exception e) {
            LOG.error("[获取推广商品详细信息]调用异常!skuids:" + skuIds);
        }
        return json;
    }

    /**
     * 获取自定义推广长链接
     *
     * @param itemUrl
     * @param subUnionId
     * @return
     */
    private String getServicePromotionCode(String itemUrl, String subUnionId) {
        String json = "";

        try {
            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());


            ServicePromotionGetcodeRequest request = new ServicePromotionGetcodeRequest();

            request.setPromotionType(jdConfig.getPromotionType());
            request.setMaterialId(itemUrl);
            request.setUnionId(jdConfig.getUnionId());
            request.setSubUnionId(subUnionId);
            request.setChannel(jdConfig.getChannel());
            request.setWebId(jdConfig.getWebId());
            request.setAdttype(jdConfig.getAdtType());

            ServicePromotionGetcodeResponse response = client.execute(request);

            json = response.getQueryjsResult();
        } catch (Exception e) {
            LOG.error("[获取推广链接]调用异常!itemUrl:{},subUnionId:{}", itemUrl, subUnionId);
        }
        return json;
    }


    private String getServicePromotionCouponCode(String skuIds, String couponUrl, String subUnionId) {
        String json = "";

        try {
            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            ServicePromotionCouponGetCodeBySubUnionIdRequest request = new ServicePromotionCouponGetCodeBySubUnionIdRequest();

            request.setCouponUrl(couponUrl);
            request.setMaterialIds(skuIds);
            request.setSubUnionId(subUnionId);

            ServicePromotionCouponGetCodeBySubUnionIdResponse response = client.execute(request);

            json = response.getGetcodebysubunionidResult();

        } catch (Exception e) {
            LOG.error("[获取推广链接]调用异常!skuIds:{},subUnionId:{},couponUrl:" + couponUrl, skuIds, subUnionId);
            throw new RuntimeException("[获取推广链接]调用异常!");
        }
        return json;
    }

    /**
     * 获取自定义推广链接
     * 1.单品推广短链接 2.活动推广链接
     * @param materialIds
     * @param proCont
     * @return
     */
    private String getServicePromotionWxsqCode(String materialIds,int proCont, String subUnionId) {
        String json = "";

        try {
            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            ServicePromotionWxsqGetCodeBySubUnionIdRequest request = new ServicePromotionWxsqGetCodeBySubUnionIdRequest();

            request.setProCont(proCont);
            request.setMaterialIds(materialIds);
            request.setSubUnionId(subUnionId);

            ServicePromotionWxsqGetCodeBySubUnionIdResponse response = client.execute(request);

            json = response.getGetcodebysubunionidResult();
        } catch (Exception e) {
            LOG.error("[获取推广短链接]调用异常!materialIds:{},subUnionId:{}", materialIds, subUnionId);
        }
        return json;
    }


    /**
     * 获取优惠商品
     *
     * @param page
     * @param pageSize
     * @return
     */
    private String getGetCouponGoodsResult(int page, int pageSize) {
        String json = "";
        try {

            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            UnionThemeGoodsServiceQueryCouponGoodsRequest request = new UnionThemeGoodsServiceQueryCouponGoodsRequest();

            request.setFrom(page);
            request.setPageSize(pageSize);

            UnionThemeGoodsServiceQueryCouponGoodsResponse response = client.execute(request);

            json = response.getQueryCouponGoodsResult();
        } catch (Exception e) {
            LOG.error("[获取优惠商品]调用异常!page:" + page);
        }
        return json;
    }

    /**
     * 查询引入订单
     * success：接口调用是否成功（1：成功，0：失败）;
     * msg: 接口调用失败success为0时的信息描述;
     * hasMore：是否还有数据(true：还有数据 false:已查询完毕，没有数据了);
     *
     * @return
     */
    private String getQueryImportOrdersResult(String queryTime, int page, int pageSize) {
        String json = "";
        try {

            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            UnionServiceQueryImportOrdersRequest request = new UnionServiceQueryImportOrdersRequest();

            request.setUnionId(jdConfig.getUnionId());
            request.setTime(queryTime);
            request.setPageIndex(page);
            request.setPageSize(pageSize);

            UnionServiceQueryImportOrdersResponse response = client.execute(request);
            json = response.getQueryImportOrdersResult();
        } catch (Exception e) {
            LOG.error("[查询引入订单]调用异常!", e);
        }
        return json;
    }


    /**
     * 获取移动返利佣金
     */
    private Double getCommissionWl(Double commissionRatioWl, Double originalPrice) {
        if (null != commissionRatioWl && null != originalPrice) {
            return new BigDecimal(commissionRatioWl * originalPrice+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.0;
    }

    /**
     * 获取PC返利佣金
     */
    private Double getCommissionPc(Double commissionRatioPc, Double originalPrice) {
        if (null != commissionRatioPc && null != originalPrice) {
            return new BigDecimal(commissionRatioPc * originalPrice+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.0;
    }
}

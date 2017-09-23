package com.rebate.manager.jd.impl;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.cps.*;
import com.jd.open.api.sdk.response.cps.*;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.common.util.rebate.RebateRuleUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.en.EProductSource;
import com.rebate.domain.jd.JDConfig;
import com.rebate.manager.jd.JdSdkManager;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component("jdSdkManager")
public class JdSdkManagerImpl implements JdSdkManager {
    private static final Logger LOG = LoggerFactory.getLogger(JdSdkManagerImpl.class);

    @Qualifier("jdConfig")
    @Autowired(required = true)
    private JDConfig jdConfig;


    private static final TypeReference<List<Map>> mapTypeReference = new TypeReference<List<Map>>() {
    };

    @Override
    public List<Product> getMediaProducts(String skuIds) {
        List<Product> list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                    product.setCommissionRatioWl(new BigDecimal(product.getCommissionRatioWl()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setCommissionRatioPc(Double.parseDouble(map.get("commisionRatioPc").toString()) / 100);
                    product.setCommissionRatioPc(new BigDecimal(product.getCommissionRatioPc()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("goodsName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setMaterialUrl(map.get("materialUrl").toString());
                    product.setSourcePlatform(EProductSource.JD.getCode());//来源JD
                    product.setOriginalPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setUnitPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setWlUnitPrice(Double.parseDouble(map.get("wlUnitPrice").toString()));
                    product.setStartDate(new Date(Long.parseLong(map.get("startDate").toString())));
                    product.setEndDate(new Date(Long.parseLong(map.get("endDate").toString())));
                    product.setCommissionPc(product.getOriginalPrice() * product.getCommissionRatioPc());
                    product.setCommissionWl(product.getOriginalPrice() * product.getCommissionRatioWl());
                    product.setShopId(Long.parseLong(map.get("shopId").toString()));
                    product.setDistribution(1);
                    product.setProductType(1);
                    product.setStock(0);
                    product.setStatus(0);
                    product.setSortWeight(0);
                    product.setFirstCategoryName("");
                    product.setSecondCategoryName("");
                    product.setThirdCategoryName("");
                    product.setFirstCategory(1);
                    product.setSecondCategory(2);
                    product.setThirdCategory(3);

                    //抓取商品分类
                    product = JdMediaProductGrapUtil.grapCategory(product);
                    list.add(product);
                }
            }
        }
        return list;
    }

    @Override
    public PaginatedArrayList<Product> getMediaCouponProducts(int page, int pageSize) {
        PaginatedArrayList<Product> list = new PaginatedArrayList<>(page, pageSize);

        String json = getGetpromotioninfoResult(page, pageSize);

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
                    product.setCommissionRatioWl(new BigDecimal(product.getCommissionRatioWl()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setCommissionRatioPc(Double.parseDouble(map.get("commisionRatioPc").toString()) / 100);
                    product.setCommissionRatioPc(new BigDecimal(product.getCommissionRatioPc()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("skuName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setOriginalPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setCommissionPc(product.getOriginalPrice() * product.getCommissionRatioPc());
                    product.setCommissionWl(product.getOriginalPrice() * product.getCommissionRatioWl());
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

                    //抓取商品分类
                    product = JdMediaProductGrapUtil.grapCategory(product);
                    list.add(product);
                }
            }
        }
        return list;
    }

    @Override
    public PaginatedArrayList<Product> getMediaThemeProducts(int page, int pageSize) {
        PaginatedArrayList<Product> list = new PaginatedArrayList<>(page, pageSize);

        String json = getGetpromotioninfoResult(page, pageSize);

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
                    product.setCommissionRatioWl(new BigDecimal(product.getCommissionRatioWl()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setCommissionRatioPc(Double.parseDouble(map.get("wlCommissionShare").toString()) / 100);
                    product.setCommissionRatioPc(new BigDecimal(product.getCommissionRatioPc()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("skuName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setOriginalPrice(Double.parseDouble(map.get("wlPrice").toString()));//获取移动端价格
                    product.setCommissionPc(product.getOriginalPrice() * product.getCommissionRatioPc());
                    product.setCommissionWl(product.getOriginalPrice() * product.getCommissionRatioWl());
                    product.setCommissionPc(new BigDecimal(product.getCommissionPc()).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
                    product.setCommissionWl(new BigDecimal(product.getCommissionWl()).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());

                    if (RebateRuleUtil.isRebate(product.getCommissionWl(), false)) {
                        product.setIsRebate(1);
                    }

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

                    //抓取商品分类
                    product = JdMediaProductGrapUtil.grapCategory(product);
                    list.add(product);
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
                    detail.setPrice(skuObj.getDouble("cosPrice"));
                    detail.setUnionId("");
                    if(skuObj.containsKey("subUnionId")){
                        detail.setSubUnionId(skuObj.getString("subUnionId"));
                    }else{
                        detail.setSubUnionId("");
                    }
                    detail.setCommission(skuObj.getDouble("commission"));
                    detail.setPositionId(orderObj.getString("positionId"));
                    detail.setPlatformRatio(RebateRuleUtil.PLATFORM_USER_COMMISSION_RATIO);//平台抽成比例
                    detail.setUserCommission(RebateRuleUtil.getJDUserCommission(detail.getCommission()));//用户返佣金额
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
        String json = getServicePromotionWxsqCode(skuId, subUnionId);
        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && 0 == Integer.parseInt(resultObj.getString("resultCode"))) {
            JSONObject urlListObj = (JSONObject) resultObj.get("urlList");
            url = urlListObj.getString(skuId.toString());
        }
        return url;
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


    /**
     * 获取自定义推广短链接
     *
     * @param skuId
     * @param subUnionId
     * @return
     */
    private String getServicePromotionWxsqCode(Long skuId, String subUnionId) {
        String json = "";

        try {
            JdClient client = new DefaultJdClient(jdConfig.getApiUrl(), jdConfig.getAccessToken(), jdConfig.getAppKey(), jdConfig.getAppSecret());

            ServicePromotionWxsqGetCodeBySubUnionIdRequest request = new ServicePromotionWxsqGetCodeBySubUnionIdRequest();

            request.setProCont(1);
            request.setMaterialIds(skuId.toString());
            request.setSubUnionId(subUnionId);

            ServicePromotionWxsqGetCodeBySubUnionIdResponse response = client.execute(request);

            json = response.getGetcodebysubunionidResult();
        } catch (Exception e) {
            LOG.error("[获取推广短链接]调用异常!skuId:{},subUnionId:{}", skuId, subUnionId);
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
    private String getGetpromotioninfoResult(int page, int pageSize) {
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
}

package com.rebate.manager.jd.impl;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.cps.ServicePromotionGoodsInfoRequest;
import com.jd.open.api.sdk.request.cps.UnionServiceQueryImportOrdersRequest;
import com.jd.open.api.sdk.request.cps.UnionThemeGoodsServiceQueryCouponGoodsRequest;
import com.jd.open.api.sdk.response.cps.ServicePromotionGoodsInfoResponse;
import com.jd.open.api.sdk.response.cps.UnionServiceQueryImportOrdersResponse;
import com.jd.open.api.sdk.response.cps.UnionThemeGoodsServiceQueryCouponGoodsResponse;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.manager.jd.JdSdkManager;
import net.sf.json.JSONObject;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 {
 "access_token": "873049e9-749c-479e-9647-7e91e13eabcf",
 "code": 0,
 "expires_in": 31535999,
 "refresh_token": "c97d6656-9d02-40d5-a59d-2ac26fa093e4",
 "time": "1504537477085",
 "token_type": "bearer",
 "uid": "5730215537",
 "user_nick": "bestcuitao"
 }
 */
@Component("jdSdkManager")
public class JdSdkManagerImpl implements JdSdkManager {
    private static final Logger LOG = LoggerFactory.getLogger(JdSdkManagerImpl.class);

    /**
     * https://oauth.jd.com/oauth/authorize?response_type=code&client_id=BC2C0FCDA61E45DBE36E95D51E29543C&redirect_uri=http://www.jingcuhui.com&state=213
     https://oauth.jd.com/oauth/token?grant_type=authorization_code&client_id=BC2C0FCDA61E45DBE36E95D51E29543C&redirect_uri=http://www.jingcuhui.com&code=yjEswj&state=213&client_secret=d253bb1c493344c5aa337ff917cfd46b
     */
    private static final String appKey = "BC2C0FCDA61E45DBE36E95D51E29543C";
    private static final String appSecret = "d253bb1c493344c5aa337ff917cfd46b";
    private static final String accessToken = "873049e9-749c-479e-9647-7e91e13eabcf";
    private static final String apiUrl = "https://api.jd.com/routerjson";
    private static final Long unionId = 1000062434l;//黔ICP备15015084号-2


    private static final TypeReference<List<Map>> mapTypeReference = new TypeReference<List<Map>>() {
    };

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
                    product.setCommissionRatio(Double.parseDouble(map.get("commisionRatioWl").toString()) / 100);
                    product.setCommissionRatio(new BigDecimal(product.getCommissionRatio()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("goodsName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setOriginalPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setCommission(product.getOriginalPrice() * product.getCommissionRatio());
                    product.setCommission(new BigDecimal(product.getCommission()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
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
    public PaginatedArrayList<Product> getMediaCouponProducts(int page, int pageSize) {
        PaginatedArrayList<Product> list = new PaginatedArrayList<>(page,pageSize);

        String json = getGetpromotioninfoResult(page,pageSize);

        JSONObject resultObj = JsonUtil.fromJson(json, JSONObject.class);
        if (null != resultObj && resultObj.getInt("resultCode")==200) {
            int total = resultObj.getInt("total");
            list.setTotalItem(total);

            String resultJson = resultObj.get("data").toString();
            List<Map> mapList = JsonUtil.fromJson(resultJson, mapTypeReference);
            if (null != mapList) {
                for (Map map : mapList) {
                    Product product = new Product();
                    product.setProductId(Long.parseLong(map.get("skuId").toString()));
                    product.setCommissionRatio(Double.parseDouble(map.get("commision").toString()) / 100);
                    product.setCommissionRatio(new BigDecimal(product.getCommissionRatio()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                    product.setName(map.get("skuName").toString());
                    product.setImgUrl(map.get("imgUrl").toString());
                    product.setOriginalPrice(Double.parseDouble(map.get("unitPrice").toString()));
                    product.setCommission(product.getOriginalPrice() * product.getCommissionRatio());
                    product.setCommission(new BigDecimal(product.getCommission()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
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
        if (null != resultObj && 1==resultObj.getInt("success")) {
            String resultJson = resultObj.getString("result");
        }
        System.out.println("json:" + json);
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

            JdClient client = new DefaultJdClient(apiUrl, accessToken, appKey, appSecret);

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
     * 获取优惠商品
     * @param page
     * @param pageSize
     * @return
     */
    private String getGetpromotioninfoResult(int page ,int pageSize) {
        String json = "";
        try {

            JdClient client=new DefaultJdClient(apiUrl,accessToken,appKey,appSecret);

            UnionThemeGoodsServiceQueryCouponGoodsRequest request=new UnionThemeGoodsServiceQueryCouponGoodsRequest();

            request.setFrom( page );
            request.setPageSize( pageSize );

            UnionThemeGoodsServiceQueryCouponGoodsResponse response=client.execute(request);

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
     * @return
     */
    private String getQueryImportOrdersResult(String queryTime, int page, int pageSize) {
        String json = "";
        try {

            JdClient client = new DefaultJdClient(apiUrl, accessToken, appKey, appSecret);

            UnionServiceQueryImportOrdersRequest request = new UnionServiceQueryImportOrdersRequest();

            request.setUnionId(unionId);
            request.setTime(queryTime);
            request.setPageIndex(page);
            request.setPageSize(pageSize);

            UnionServiceQueryImportOrdersResponse response = client.execute(request);
            json = response.getQueryImportOrdersResult();
        } catch (Exception e) {
            LOG.error("[查询引入订单]调用异常!",e);
        }
        return json;
    }
}
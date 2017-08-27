package com.rebate.manager.jd.impl;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.cps.ServicePromotionGoodsInfoRequest;
import com.jd.open.api.sdk.response.cps.ServicePromotionGoodsInfoResponse;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.domain.Product;
import com.rebate.manager.jd.JdSdkManager;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {
 * "access_token": "b721dff3-cdf3-4e69-9245-aaf4c2e2e2e4",
 * "code": 0,
 * "expires_in": 31535999,
 * "refresh_token": "88c6a481-9f68-4df8-952c-c9769ef05c13",
 * "time": "1503844477453",
 * "token_type": "bearer",
 * "uid": "2298807957",
 * "user_nick": "yitong0524"
 * }
 */
@Component("jdSdkManager")
public class JdSdkManagerImpl implements JdSdkManager {
    private static final Logger LOG = LoggerFactory.getLogger(JdSdkManagerImpl.class);

    private static final String appKey = "58647A717120C93F2B509C79EE554B5C";
    private static final String appSecret = "0490015803c14051bf4d5763e7cb3a06 ";
    private static final String accessToken = "b721dff3-cdf3-4e69-9245-aaf4c2e2e2e4";
    private static final String apiUrl = "https://api.jd.com/routerjson";

    private static final TypeReference<List<Map>> mapTypeReference = new TypeReference<List<Map>>() {
    };

    @Override
    public List<Product> getMediaProduct(String skuIds) {
        List<Product> list = new ArrayList<>();

        String json = getGetpromotioninfoResult(skuIds);
        Map resultMap = JsonUtil.fromJson(json, Map.class);
        if (null != resultMap && Boolean.parseBoolean(resultMap.get("sucessed").toString())) {
            List<Map> mapList = JsonUtil.fromJson(resultMap.get("result").toString(),mapTypeReference);
            if (null != resultMap){
                for (Map map :mapList){
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
        return null;
    }

    /**
     * 调用接口获取参数
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

            return response.getGetpromotioninfoResult();
        } catch (Exception e) {
            LOG.error("[获取推广商品详细信息]调用异常!skuids:" + skuIds);
        }
        return json;
    }
}

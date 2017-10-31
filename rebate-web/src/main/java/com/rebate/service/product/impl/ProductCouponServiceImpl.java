package com.rebate.service.product.impl;

import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RedisUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.vo.ProductVo;
import com.rebate.service.product.ProductCouponService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

@Service("productCouponService")
public class ProductCouponServiceImpl implements ProductCouponService {

    @Qualifier("redisUtil")
    @Autowired(required = false)
    private RedisUtil redisUtil;

    @Override
    public void addProductVoCache(ProductVo productVo) {
        if (null != productVo) {
            String key = RedisKey.JD_PRODUCT_VO.getPrefix(""+productVo.getProductId());
            redisUtil.set(key, JsonUtil.toJson(productVo));
        }
    }

    @Override
    public ProductVo getProductVoCache(Long productId) {
        ProductVo productVo = null;

        String key = RedisKey.JD_PRODUCT_VO.getPrefix(""+productId);
        String json = redisUtil.get(key);
        if (StringUtils.isNotBlank(json)) {
            productVo = JsonUtil.fromJson(json, ProductVo.class);
        }
        return productVo;
    }

    @Override
    public void cleanProductVoCache(Long productId) {
        if (null != productId) {
            String key = RedisKey.JD_PRODUCT_VO.getPrefix(""+productId);
            redisUtil.del(key);
        }
    }

    @Override
    public void addProductCouponListCache(ProductVo productVo) {
        if (null != productVo) {
            String key = RedisKey.JD_PRODUCT_COUPONS.getPrefix("");
            redisUtil.zadd(key, productVo.getProductId().toString(), Double.parseDouble("" + productVo.getSortWeight()));
        }
    }

    @Override
    public void cleanProductCouponListCache(Long productId) {
        if (null != productId) {
            String key = RedisKey.JD_PRODUCT_COUPONS.getPrefix("");
            redisUtil.zrem(key, productId.toString());
        }
    }

    @Override
    public PaginatedArrayList<Long> getProductCouponList(int page, int pageSize) {
        PaginatedArrayList<Long> list = new PaginatedArrayList<>();

        String key = RedisKey.JD_PRODUCT_COUPONS.getPrefix("");
        int start = (page - 1) * pageSize;
        int end = start + pageSize;

        list.setTotalItem(redisUtil.zcard(key).intValue());

        Set<String> productCouponSet = redisUtil.zrevrange(key, start, end);
        if (null != productCouponSet && productCouponSet.size() > 0) {
            Iterator it = productCouponSet.iterator();
            while (it.hasNext()) {
                list.add(Long.parseLong((String) it.next()));
            }
        }

        return list;
    }
}

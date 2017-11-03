package com.rebate.service.product.impl;

import com.jd.data.redis.RedisUtils;
import com.jd.data.redis.connection.RedisAccessException;
import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.vo.ProductVo;
import com.rebate.service.product.ProductCouponService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

@Service("productCouponService")
public class ProductCouponServiceImpl implements ProductCouponService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCouponServiceImpl.class);

    @Qualifier("redisUtil")
    @Autowired(required = false)
    private RedisUtils redisUtil;

    @Override
    public void addProductVoCache(ProductVo productVo) {
        if (null != productVo) {
            String key = RedisKey.JD_PRODUCT_VO.getPrefix("" + productVo.getProductId());
            try {
                redisUtil.set(key, JsonUtil.toJson(productVo));
            } catch (RedisAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ProductVo getProductVoCache(Long productId) {
        ProductVo productVo = null;

        String key = RedisKey.JD_PRODUCT_VO.getPrefix("" + productId);
        String json = null;
        try {
            json = redisUtil.get(key);
        } catch (RedisAccessException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            productVo = JsonUtil.fromJson(json, ProductVo.class);
        }
        return productVo;
    }

    @Override
    public void cleanProductVoCache(Long productId) {
        if (null != productId) {
            String key = RedisKey.JD_PRODUCT_VO.getPrefix("" + productId);
            try {
                redisUtil.del(key);
            } catch (RedisAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addProductCouponListCache(ProductVo productVo) {
        if (null != productVo) {
            String key = RedisKey.JD_COUPON_PRODUCT.getPrefix("");
            try {
                redisUtil.zadd(key, Double.parseDouble("" + productVo.getSortWeight()), productVo.getProductId().toString());
            } catch (RedisAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cleanProductCouponListCache(Long productId) {
        if (null != productId) {
            String key = RedisKey.JD_COUPON_PRODUCT.getPrefix("");
            try {
                redisUtil.zrem(key, productId.toString());
            } catch (RedisAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addSecskillProductListCache(ProductVo productVo) {
        if (null != productVo) {
            String key = RedisKey.JD_SECSKILL_PRODUCTS.getPrefix("");
            try {
                redisUtil.zadd(key, Double.parseDouble("" + productVo.getSortWeight()), productVo.getProductId().toString());
            } catch (RedisAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cleanSecskillProductListCache(Long productId) {
        if (null != productId) {
            String key = RedisKey.JD_SECSKILL_PRODUCTS.getPrefix("");
            try {
                redisUtil.zrem(key, productId.toString());
            } catch (RedisAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PaginatedArrayList<Long> getProductCouponList(int page, int pageSize) {
        PaginatedArrayList<Long> list = new PaginatedArrayList<>();
        try {

            String key = RedisKey.JD_COUPON_PRODUCT.getPrefix("");
            int start = (page - 1) * pageSize;
            int end = start + pageSize - 1;

            list.setTotalItem(redisUtil.zcard(key).intValue());

            Set<String> productCouponSet = redisUtil.zrevrange(key, start, end);
            if (null != productCouponSet && productCouponSet.size() > 0) {
                Iterator it = productCouponSet.iterator();
                while (it.hasNext()) {
                    list.add(Long.parseLong((String) it.next()));
                }
            }
        } catch (Exception e) {
            LOG.error("getProductCouponList error!", e);
        }
        return list;
    }

    @Override
    public PaginatedArrayList<Long> findSecSkillProducts(int page, int pageSize) {
        PaginatedArrayList<Long> list = new PaginatedArrayList<>();
        try {
            String key = RedisKey.JD_SECSKILL_PRODUCTS.getPrefix("");
            int start = (page - 1) * pageSize;
            int end = start + pageSize - 1;

            list.setTotalItem(redisUtil.zcard(key).intValue());

            Set<String> productCouponSet = redisUtil.zrevrange(key, start, end);
            if (null != productCouponSet && productCouponSet.size() > 0) {
                Iterator it = productCouponSet.iterator();
                while (it.hasNext()) {
                    list.add(Long.parseLong((String) it.next()));
                }
            }
        } catch (Exception e) {
            LOG.error("findSecSkillProducts error!", e);
        }
        return list;
    }
}

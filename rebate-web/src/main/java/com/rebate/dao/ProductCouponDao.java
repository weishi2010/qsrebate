package com.rebate.dao;

import com.rebate.domain.ProductCoupon;

public interface ProductCouponDao {
    /**
     * 插入
     * @param productCoupon
     */
    void insert(ProductCoupon productCoupon);

    /**
     * 更新
     * @param productCoupon
     */
    void update(ProductCoupon productCoupon);

    /**
     * 删除
     * @param skuId
     */
    void deleteByProductId(Long skuId);

    /**
     * 根据id查询商品优惠券信息
     * @param productCoupon
     * @return
     */
    ProductCoupon findById(ProductCoupon productCoupon);
}

package com.rebate.dao;

import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.query.ProductCouponQuery;
import com.rebate.domain.query.ProductQuery;

import java.util.List;

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


    /**
     * 查询列表
     * @param productCouponQuery
     * @return
     */
    List<ProductCoupon> findProductCoupons(ProductCouponQuery productCouponQuery);

    /**
     * 查询总数
     * @param productCouponQuery
     * @return
     */
    int findProductCouponsCount(ProductCouponQuery productCouponQuery);
}

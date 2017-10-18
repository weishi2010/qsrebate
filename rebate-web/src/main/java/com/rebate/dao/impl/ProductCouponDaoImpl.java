package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.ProductCouponDao;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.query.ProductCouponQuery;

import java.util.List;

public class ProductCouponDaoImpl extends BaseDao implements ProductCouponDao {
    @Override
    public void insert(ProductCoupon productCoupon) {
        insert("ProductCoupon.insert", productCoupon);
    }

    @Override
    public void update(ProductCoupon productCoupon) {
        update("ProductCoupon.update",productCoupon);
    }

    @Override
    public void deleteByProductId(Long skuId) {
        delete("ProductCoupon.deleteByProductId",skuId);
    }

    @Override
    public ProductCoupon findById(ProductCoupon productCoupon) {
        return (ProductCoupon)queryForObject("ProductCoupon.findById",productCoupon);
    }

    @Override
    public List<ProductCoupon> findProductCoupons(ProductCouponQuery productCouponQuery) {
        return queryForList("ProductCoupon.findProductCoupons",productCouponQuery);
    }

    @Override
    public int findProductCouponsCount(ProductCouponQuery productCouponQuery) {
        return (int)queryForObject("ProductCouponQuery.findProductCouponsCount",productCouponQuery);
    }
}

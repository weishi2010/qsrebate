package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.ProductCouponDao;
import com.rebate.domain.ProductCoupon;

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
    public ProductCoupon findById(ProductCoupon productCoupon) {
        return (ProductCoupon)queryForObject("ProductCoupon.findById",productCoupon);
    }
}

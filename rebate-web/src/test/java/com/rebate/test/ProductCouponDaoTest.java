package com.rebate.test;


import com.google.common.base.Joiner;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.en.EProudctCouponType;
import com.rebate.domain.en.EProudctRebateType;
import com.rebate.manager.jd.JdSdkManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class ProductCouponDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ProductCouponDao productCouponDao;

    @Autowired
    private JdSdkManager jdSdkManager;

    @Autowired
    private ProductDao productDao;

    @Test
    public void testGetMediaCouponProducts() {
        List<ProductCoupon> list = jdSdkManager.getMediaCoupons(1, 10);
        System.out.println("list:" + JsonUtil.toJson(list));
        List<Long> skuList = new ArrayList<>();
        for (ProductCoupon productCoupon : list) {
            if (null == productCouponDao.findById(productCoupon)) {
                productCouponDao.insert(productCoupon);
            }else{
                productCouponDao.update(productCoupon);
            }
            skuList.add(productCoupon.getProductId());
        }

        List<Product> products = jdSdkManager.getMediaProducts(Joiner.on(",").join(skuList));

        for (Product product : products) {
            product.setCouponType(EProudctCouponType.COUPON.getCode());

            if (null == productDao.findById(product)) {
                product.setIsRebate(EProudctRebateType.NOT_REBATE.getCode());
                productDao.insert(product);
            } else {
                productDao.update(product);
            }
        }



    }
}

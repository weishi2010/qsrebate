package com.rebate.test;


import com.google.common.base.Joiner;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.en.EProudctCouponType;
import com.rebate.domain.en.EProudctRebateType;
import com.rebate.domain.query.ProductCouponQuery;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.job.impl.RebateJobImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class ProductCouponDaoTest extends AbstractJUnit4SpringContextTests {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCouponDaoTest.class);

    @Autowired
    private ProductCouponDao productCouponDao;

    @Autowired
    private ProductDao productDao;

    @Test
    public void updateCouponPrice() {
        int page=1;
        int pageSize=20;
        ProductCouponQuery productCouponQuery = new ProductCouponQuery();
        productCouponQuery.setPageSize(pageSize);
        productCouponQuery.setStartRow((page-1)*pageSize);
        List<ProductCoupon> list = productCouponDao.findProductCoupons(productCouponQuery);
        while(list.size()>0){
            for(ProductCoupon coupon:list){
                Product product = new Product();
                product.setProductId(coupon.getProductId());
                product.setCouponPrice(coupon.getCouponPrice());
                productDao.update(product);
            }
            LOG.error("[updateCouponPrice]page:"+page+",list:" + list.size());
            page++;
            productCouponQuery.setStartRow((page-1)*pageSize);
            list = productCouponDao.findProductCoupons(productCouponQuery);
        }
    }
}

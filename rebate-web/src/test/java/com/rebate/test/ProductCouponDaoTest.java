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
    private JdSdkManager jdSdkManager;

    @Autowired
    private ProductDao productDao;

    @Test
    public void testGetMediaCouponProducts() {
        int page=1;
        int pageSize=20;
        List<ProductCoupon> list = jdSdkManager.getMediaCoupons(page, pageSize);
        while(list.size()>0){
            List<Long> skus = new ArrayList<>();
            for(ProductCoupon coupon:list){
                skus.add(coupon.getProductId());
            }
            LOG.error("[importCouponProducts]page:"+page+",list:" + skus);
            page++;
            list = jdSdkManager.getMediaCoupons(page, pageSize);
        }



    }
}

package com.rebate.test;


import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;
import com.rebate.manager.jd.JdSdkManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class ProductDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private JdSdkManager jdSdkManager;

    @Test
    public void testInsert() {
        List<Product> list = jdSdkManager.getMediaProducts("1615786");

        for (Product product : list) {
            if (null == productDao.findById(product)) {
                product.setIsRebate(1);
                productDao.insert(product);
            } else {
                productDao.update(product);
            }
        }
    }

    @Test
    public void testGetProducts() {
        ProductQuery query = new ProductQuery();
        query.setPageSize(10);
        query.setThirdCategory(1195);
        List list = productDao.findProducts(query);
        System.out.println("list:" + JsonUtil.toJson(list));
    }

    @Test
    public void testGetMediaProducts() {
        List list = jdSdkManager.getMediaProducts("1615786");
        System.out.println("list:" + JsonUtil.toJson(list));
    }

    @Test
    public void testGetMediaCouponProducts() {
        List list = jdSdkManager.getMediaCouponProducts(1, 10);
        System.out.println("list:" + JsonUtil.toJson(list));
    }

    @Test
    public void testGetMediaThemeProducts() {
        List list = jdSdkManager.getMediaThemeProducts(1, 10);
        System.out.println("list:" + JsonUtil.toJson(list));
    }

    @Test
    public void testGetPromotinUrl() {
        String url = jdSdkManager.getShortPromotinUrl(4586850l, "weishi2010");
        System.out.println("url:" + url);
    }
}

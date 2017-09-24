package com.rebate.test;


import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
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
        ProductQuery productQuery = new ProductQuery();
        productQuery.setIndex(3);
        productQuery.setPageSize(10);
        PaginatedArrayList<ProductVo> products = new PaginatedArrayList<ProductVo>(productQuery.getIndex(), productQuery.getPageSize());

        int totalItem = productDao.findProductsCount(productQuery);
        if (totalItem > 0) {
            products.setTotalItem(totalItem);
            productQuery.setStartRow(products.getStartRow());
            System.out.println(products.getStartRow());
            if(productQuery.getIndex()<=products.getTotalPage()) {
                List<Product> list = productDao.findProducts(productQuery);
                System.out.println("list:" + JsonUtil.toJson(list));
            }
        }

    }

    @Test
    public void testGetMediaProducts() {
        List<Product> list = jdSdkManager.getMediaProducts("1615786");
        System.out.println("list:" + JsonUtil.toJson(list));
    }

    @Test
    public void testGetMediaCouponProducts() {
        List<ProductCoupon> list = jdSdkManager.getMediaCoupons(1, 10);
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

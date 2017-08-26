package com.rebate.test;


import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class ProductDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ProductDao productDao;

    @Test
    public void testInsert() {
        int pageSize = 100;
        for(int page=1;page<1000;page++){

            List<Product> list = JdMediaProductGrapUtil.grabProducts(page, pageSize);
            for (Product product : list) {
                product.setDistribution(1);
                product.setProductType(1);
                product.setStock(100);
                product.setStatus(0);
                product.setFirstCategoryName("");
                product.setSecondCategoryName("");
                product.setThirdCategoryName("");
                product.setFirstCategory(1);
                product.setSecondCategory(2);
                product.setThirdCategory(3);

                product = JdMediaProductGrapUtil.grapCategory(product);
                if (null == productDao.findById(product)) {
                    productDao.insert(product);
                }
            }
        }
//        Product product = new Product();
//        product.setProductId(111l);
//        product.setName("test商品");
//        product.setCommissionRatio(1.0);
//        product.setDistribution(1);
//        product.setOriginalPrice(123132.0);
//        product.setProductType(1);
//        product.setStock(100);
//        product.setStatus(0);
//        product.setFirstCategoryName("一级分类 ");
//        product.setSecondCategoryName("二级分类 ");
//        product.setThirdCategoryName("三级分类 ");
//        product.setFirstCategory(1);
//        product.setSecondCategory(2);
//        product.setThirdCategory(3);
//        if (null == productDao.findById(product)) {
//            productDao.insert(product);
//        }
    }

    @Test
    public void testGetProducts(){
        ProductQuery query = new ProductQuery();
        query.setPageSize(10);
        query.setThirdCategory(1195);
        List list = productDao.findProducts(query);
        System.out.println("list:"+JsonUtil.toJson(list));
    }


}

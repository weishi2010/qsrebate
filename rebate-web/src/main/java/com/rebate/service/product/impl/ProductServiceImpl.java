package com.rebate.service.product.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final static String IMG_SIZE = "s150x150_jfs";
    private final static String DEFAULT_IMG_SIZE = "jfs";

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Qualifier("categoryDao")
    @Autowired(required = true)
    private CategoryDao categoryDao;

    @Override
    public List<Category> findByActiveCategories(CategoryQuery qategoryQuery){
        return categoryDao.findByActiveCategories(qategoryQuery);
    }

    @Override
    public PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery) {
        PaginatedArrayList<ProductVo> products = new PaginatedArrayList<ProductVo>(productQuery.getIndex(), productQuery.getPageSize());
        try {
            int totalItem = productDao.findProductsCount(productQuery);
            if (totalItem > 0) {
                products.setTotalItem(totalItem);
                productQuery.setStartRow(products.getStartRow());
                List<Product> list = productDao.findProducts(productQuery);
                for (Product product : list) {
                    ProductVo vo = new ProductVo(product);
                    vo.setImgUrl(product.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));
                    products.add(vo);
                }
            }

        } catch (Exception e) {
            LOG.error("findProductList error!productQuery:"+ JsonUtil.toJson(productQuery),e);
        }
        return products;
    }
}

package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;

import java.util.List;

/**
 * Created by weishi on 2017/7/13.
 */
public class ProductDaoImpl extends BaseDao implements ProductDao {
    @Override
    public void insert(Product product) {
        insert("Product.insert",product);
    }

    @Override
    public void update(Product product) {
        update("Product.update",product);
    }

    @Override
    public void deleteByProductId(Long skuId) {
        delete("Product.deleteByProductId",skuId);
    }

    @Override
    public Product findById(Product product) {
        return (Product)queryForObject("Product.findById",product);
    }

    @Override
    public List<Product> findProducts(ProductQuery productQuery) {
        return queryForList("Product.findProducts",productQuery);
    }

    @Override
    public int findProductsCount(ProductQuery productQuery) {
        return (Integer)queryForObject("Product.findProductsCount",productQuery);
    }
}

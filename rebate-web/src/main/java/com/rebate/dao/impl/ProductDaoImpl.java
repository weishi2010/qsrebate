package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.DaxueProduct;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;

import java.util.List;

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

    @Override
    public int batchResetProductSortWeight(ProductQuery productQuery) {
        return update("Product.batchResetProductSortWeight",productQuery);
    }

    @Override
    public void insertDaxueProduct(DaxueProduct daxueProduct) {
        insert("Product.insertDaxueProduct",daxueProduct);
    }

    @Override
    public void deleteDaxueProductByProductId(Long productId) {
        delete("Product.deleteDaxueProductByProductId",productId);
    }

    @Override
    public void updateDaxueProduct(DaxueProduct daxueProduct) {
        update("Product.updateDaxueProduct",daxueProduct);
    }

    @Override
    public DaxueProduct findDaxueProductById(Product product) {
        return (DaxueProduct)queryForObject("Product.findDaxueProductById",product);
    }

    @Override
    public List<DaxueProduct> findDaxueProducts(ProductQuery productQuery) {
        return queryForList("Product.findDaxueProducts",productQuery);
    }

    @Override
    public int findDaxueProductsCount(ProductQuery productQuery) {
        return (int)queryForObject("Product.findDaxueProductsCount",productQuery);
    }

    @Override
    public long findMaxExtSortWeight(ProductQuery productQuery) {
        return (long)queryForObject("Product.findMaxExtSortWeight",productQuery);

    }
}

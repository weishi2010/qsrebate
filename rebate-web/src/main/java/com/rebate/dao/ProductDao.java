package com.rebate.dao;

import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;

import java.util.List;

/**
 * Created by weishi on 2017/7/13.
 */
public interface ProductDao {
    /**
     * 插入
     * @param product
     */
    void insert(Product product);

    /**
     * 更新
     * @param product
     */
    void update(Product product);


    /**
     * 删除
     * @param skuId
     */
    void deleteByProductId(Long skuId);

    /**
     * 根据id查询商品
     * @param product
     * @return
     */
    Product findById(Product product);

    /**
     * 查询列表
     * @param productQuery
     * @return
     */
    List<Product> findProducts(ProductQuery productQuery);

    /**
     * 查询总数
     * @param productQuery
     * @return
     */
    int findProductsCount(ProductQuery productQuery);

    int batchResetProductSortWeight(ProductQuery productQuery);

}

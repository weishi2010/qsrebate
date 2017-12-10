package com.rebate.dao;

import com.rebate.domain.DaxueProduct;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;

import java.util.List;

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
    //------------------------掌上大学-------------------------------

    /**
     * 插入
     * @param daxueProduct
     */
    void insertDaxueProduct(DaxueProduct daxueProduct);

    /**
     * 删除
     * @param productId
     */
    void deleteDaxueProductByProductId(Long productId);

    /**
     * 更新
     * @param daxueProduct
     */
    void updateDaxueProduct(DaxueProduct daxueProduct);



    /**
     * 根据id查询掌上大学商品
     * @param product
     * @return
     */
    Product findDaxueProductById(Product product);
    /**
     * 查询掌上大学商品列表
     * @param productQuery
     * @return
     */
    List<DaxueProduct> findDaxueProducts(ProductQuery productQuery);
    /**
     * 查询掌上大学商品总数
     * @param productQuery
     * @return
     */
    int findDaxueProductsCount(ProductQuery productQuery);

    /**
     * 获取最大排序值
     * @param productQuery
     * @return
     */
    long findMaxExtSortWeight(ProductQuery productQuery);

}

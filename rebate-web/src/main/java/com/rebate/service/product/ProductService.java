package com.rebate.service.product;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.RecommendCategory;
import com.rebate.domain.UserInfo;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ApiProductVo;
import com.rebate.domain.vo.ProductVo;

import java.util.List;

public interface ProductService {

    /**
     * 更新
     * @param product
     */
    void update(Product product);

    /**
     * 导入商品
     * @param productIds 商品id列表
     */
    void importProducts(String productIds);

    /**
     * 导入优惠券商品
     * @param couponMapList
     * @param couponType 优惠券类型
     * @param extSortWeight 扩展排序值
     */
    void importCouponProducts(List<ProductCoupon> couponMapList,Integer couponType);

    /**
     * 查询有效分类
     * @param recommendCategory
     * @return
     */
    List<RecommendCategory> findByRecommendCategories(RecommendCategory recommendCategory);
    /**
     * 查询列表
     * @param productQuery
     * @return
     */
    PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery, UserInfo userInfo);

    /**
     * 根据skuId查询
     * @param skuId
     * @return
     */
    ProductVo findProduct(Long skuId);

    /**
     * 根据skuId查询商品基础信息
     * @param skuId
     * @return
     */
    Product findProductBaseInfo(Long skuId);

    /**
     * 获取内购券商品列表
     * @param subUnionId
     * @param page
     * @param pageSize
     * @return
     */
    PaginatedArrayList<ApiProductVo> findCouponProducts(String subUnionId, int page, int pageSize);

    /**
     * 获取9.9商品列表
     * @param subUnionId
     * @param page
     * @param pageSize
     * @return
     */
    PaginatedArrayList<ApiProductVo> findSecSkillProducts(String subUnionId, int page, int pageSize);

    PaginatedArrayList<ApiProductVo> findDaxueProductList(ProductQuery productQuery);

    /**
     * 批量置顶
     * @return
     */
    long batchResetProductSortWeight(ProductQuery productQuery);

    String getCouponPromotionUrl(Long skuId,String subUnionId);

    /**
     * 更新商品扩展排序字段
     * @param products
     */
    void updateProductExtSortWeight(List<Product> products);

}

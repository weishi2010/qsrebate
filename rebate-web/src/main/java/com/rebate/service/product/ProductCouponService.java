package com.rebate.service.product;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.vo.ProductVo;

public interface ProductCouponService {
    /**
     * 添加单条缓存
     * @param productVo
     */
    void addProductVoCache(ProductVo productVo);

    /**
     * 清除缓存
     * @param productId
     */
    void cleanProductVoCache(Long productId);

    /**
     * 添加缓存
     * @param productVo
     */
    void addProductCouponListCache(ProductVo productVo);

    /**
     * 获取缓存
     * @param productId
     * @return
     */
    ProductVo getProductVoCache(Long productId);

    /**
     * 清理缓存
     * @param productId
     */
    void cleanProductCouponListCache(Long productId);

    void addSecskillProductListCache(ProductVo productVo);

    void cleanSecskillProductListCache(Long productId);

    /**
     * 获取优惠券商品列表
     * @param page
     * @param pageSize
     * @return
     */
    PaginatedArrayList<Long> getProductCouponList(int page,int pageSize);

    /**
     * 获取9.9商品列表
     * @param page
     * @param pageSize
     * @return
     */
    PaginatedArrayList<Long> findSecSkillProducts(int page,int pageSize);

}

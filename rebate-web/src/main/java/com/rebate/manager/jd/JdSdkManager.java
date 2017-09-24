package com.rebate.manager.jd;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.RebateDetail;

import java.util.List;

/**
 * JD SDK
 */
public interface JdSdkManager {
    /**
     * 根据sku获取商品
     *
     * @param skuIds
     * @return
     */
    List<Product> getMediaProducts(String skuIds);

    PaginatedArrayList<ProductCoupon> getMediaCoupons(int page, int pageSize);

    /**
     * 获取优惠商品
     *
     * @param page
     * @param pageSize
     * @return
     */
    List<Product> getMediaCouponProducts(int page, int pageSize);

    /**
     * 获取爆款商品
     *
     * @param page
     * @param pageSize
     * @return
     */
    PaginatedArrayList<Product> getMediaThemeProducts(int page, int pageSize);

    /**
     * 获取推广长链接
     *
     * @param itemUrl
     * @param subUnionId
     * @return
     */
    String getLongPromotinUrl(String itemUrl, String subUnionId);

    /**
     * 获取推广短链接
     *
     * @param skuId
     * @param subUnionId
     * @return
     */
    String getShortPromotinUrl(Long skuId, String subUnionId);

    /**
     * 查询返佣明细
     * @param queryTime
     * @param page
     * @param pageSize
     * @return
     */

    List<RebateDetail> getRebateDetails(String queryTime, int page, int pageSize);
}

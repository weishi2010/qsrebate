package com.rebate.manager.jd;

import com.jd.open.api.sdk.domain.mall.ProductWrapService.ProductBase;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.UserInfo;

import java.util.List;

/**
 * JD SDK
 */
public interface JdSdkManager {
    /**
     * 根据sku列表获取商品列表
     *
     * @param skuIds
     * @return
     */
    List<Product> getMediaProducts(String skuIds);

    /**
     * 根据sku获取商品
     *
     * @param skuId
     * @return
     */
    Product getMediaProduct(Long skuId);

    /**
     * 获取商品信息
     *
     * @param skuIds
     * @return
     */
    List<ProductBase> getProductBaseInfos(String skuIds);

    /**
     * 获取商品信息
     *
     * @param skuId
     * @return
     */
    ProductBase getProductBaseInfo(Long skuId);

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
     * 获取优惠券二转一链接
     *
     * @param skuId
     * @param couponUrl
     * @param subUnionId
     * @return
     */
    String getPromotionCouponCode(Long skuId, String couponUrl, String subUnionId);

    /**
     * 获取单品推广短链接
     *
     * @param skuId
     * @param subUnionId
     * @return
     */
    String getShortPromotinUrl(Long skuId, String subUnionId);

    /**
     * 获取活动推广链接
     * @param salesUrl
     * @param subUnionId
     * @return
     */
    String getSalesActivityPromotinUrl(String salesUrl, String subUnionId);

    /**
     * 获取业绩订单明细
     * @param queryTime
     * @param page
     * @param pageSize
     * @return
     */
    List<RebateDetail> getCommissionRebateDetails(String queryTime, int page, int pageSize);
    /**
     * 查询返佣明细
     *
     * @param queryTime
     * @param page
     * @param pageSize
     * @return
     */

    List<RebateDetail> getRebateDetails(String queryTime, int page, int pageSize);

    /**
     * 获取佣金
     * @param userInfo
     * @param product
     * @return
     */
    Double getQSCommission(UserInfo userInfo, Product product);
}

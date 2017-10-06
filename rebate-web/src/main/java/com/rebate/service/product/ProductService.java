package com.rebate.service.product;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.RecommendCategory;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;

import java.util.List;

public interface ProductService {

    /**
     * 导入商品
     * @param productIds 商品id列表
     */
    void importProducts(String productIds);

    /**
     * 导入优惠券商品
     * @param couponMapList
     */
    void importCouponProducts(List<ProductCoupon> couponMapList);

    /**
     * 查询有效分类
     * @param recommendCategory
     * @return
     */
    public List<RecommendCategory> findByRecommendCategories(RecommendCategory recommendCategory);
    /**
     * 查询列表
     * @param productQuery
     * @return
     */
    PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery);

    /**
     * 根据skuId查询
     * @param skuId
     * @param openId
     * @return
     */
    ProductVo findProduct(Long skuId,String openId);

}

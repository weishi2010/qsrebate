package com.rebate.service.product;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.common.web.result.Result;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;

import java.util.List;

public interface ProductService {

    /**
     * 导入商品
     * @param productIds 商品id列表
     * @param productCouponType 商品优惠类型
     */
    void importProducts(String productIds,int productCouponType);

    /**
     * 查询有效分类
     * @param qategoryQuery
     * @return
     */
    List<Category> findByActiveCategories(CategoryQuery qategoryQuery);

    /**
     * 查询列表
     * @param productQuery
     * @param openId
     * @return
     */
    PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery,String openId);

    /**
     * 根据skuId查询
     * @param skuId
     * @param openId
     * @return
     */
    ProductVo findProduct(Long skuId,String openId);

}

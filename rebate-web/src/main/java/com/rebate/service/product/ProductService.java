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
     * 查询有效分类
     * @param qategoryQuery
     * @return
     */
    List<Category> findByActiveCategories(CategoryQuery qategoryQuery);

    /**
     * 查询列表
     * @param productQuery
     * @return
     */
    PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery);
}

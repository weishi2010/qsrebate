package com.rebate.dao;

import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Product;
import com.rebate.domain.RecommendCategory;
import com.rebate.domain.query.ProductQuery;

import java.util.List;

/**
 * Created by weishi on 2017/7/13.
 */
public interface CategoryDao {
    /**
     * 插入
     * @param category
     */
    void insert(Category category);

    /**
     * 根据三级分类查询
     * @param categoryQuery
     * @return
     */
    Category findByThirdCategory(CategoryQuery categoryQuery);

    /**
     * 查询列表
     * @param categoryQuery
     * @return
     */
    List<Category> findByThirdCategories(CategoryQuery categoryQuery);

    /**
     * 查询推荐分类归类列表
     * @param recommendCategory
     * @return
     */
    List<RecommendCategory> findByRecommendCategories(RecommendCategory recommendCategory);


    /**
     * 全列表
     * @return
     */
    List<Category> selectAllCategory();
}

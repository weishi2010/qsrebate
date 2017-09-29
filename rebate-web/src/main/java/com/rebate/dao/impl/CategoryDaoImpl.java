package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.CategoryDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.RecommendCategory;

import java.util.List;

public class CategoryDaoImpl extends BaseDao implements CategoryDao {
    @Override
    public void insert(Category category) {
        insert("Category.insert",category);
    }

    @Override
    public Category findByThirdCategory(CategoryQuery categoryQuery) {
        return (Category)queryForObject("Category.findByThirdCategory",categoryQuery);
    }

    @Override
    public List<Category> findByThirdCategories(CategoryQuery categoryQuery) {
        return queryForList("Category.findByThirdCategories",categoryQuery);
    }

    @Override
    public List<RecommendCategory> findByRecommendCategories(RecommendCategory recommendCategory) {
        return queryForList("Category.findByRecommendCategories",recommendCategory);
    }

}

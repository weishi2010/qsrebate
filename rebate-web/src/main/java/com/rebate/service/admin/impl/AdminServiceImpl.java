package com.rebate.service.admin.impl;

import com.rebate.dao.CategoryDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryInfo;
import com.rebate.service.admin.AdminService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Qualifier("categoryDao")
    @Autowired(required = true)
    private CategoryDao categoryDao;

    @Override
    public List<CategoryInfo> getFirstCategory() {
        List<CategoryInfo> list = new ArrayList<>();
        List<Category> allCategoryList = categoryDao.selectAllCategory();
        Map<Integer ,String> categoryMap = new HashMap();

        for (Category category : allCategoryList) {
            categoryMap.put(category.getFirstCategory(), category.getFirstCategoryName());
        }

        for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setId(entry.getKey());
            if(StringUtils.isNotBlank(entry.getValue())){
                categoryInfo.setName(entry.getValue());
            }else{
                categoryInfo.setName(""+entry.getKey());
            }
            list.add(categoryInfo);
        }

        return list;
    }

    @Override
    public List<CategoryInfo> getSecondCategory(Integer categoryId) {
        List<CategoryInfo> list = new ArrayList<>();

        List<Category> allCategoryList = categoryDao.selectAllCategory();
        Map<Integer ,String> categoryMap = new HashMap();

        for (Category category : allCategoryList) {
            if (categoryId.equals(category.getFirstCategory())) {
                categoryMap.put(category.getSecondCategory(), category.getSecondCategoryName());
            }
        }

        for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setId(entry.getKey());
            if(StringUtils.isNotBlank(entry.getValue())){
                categoryInfo.setName(entry.getValue());
            }else{
                categoryInfo.setName(""+entry.getKey());
            }
            list.add(categoryInfo);
        }

        return list;
    }

    @Override
    public List<CategoryInfo> getThirdCategory(Integer categoryId) {
        List<CategoryInfo> list = new ArrayList<>();

        List<Category> allCategoryList = categoryDao.selectAllCategory();
        Map<Integer ,String> categoryMap = new HashMap();

        for (Category category : allCategoryList) {
            if (categoryId.equals(category.getSecondCategory())) {
                categoryMap.put(category.getThirdCategory(), category.getThirdCategoryName());
            }
        }

        for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setId(entry.getKey());
            if(StringUtils.isNotBlank(entry.getValue())){
                categoryInfo.setName(entry.getValue());
            }else{
                categoryInfo.setName(""+entry.getKey());
            }
            list.add(categoryInfo);
        }

        return list;
    }
}

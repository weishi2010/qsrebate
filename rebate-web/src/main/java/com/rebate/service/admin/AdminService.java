package com.rebate.service.admin;

import com.rebate.domain.CategoryInfo;

import java.util.List;

public interface AdminService {

    List<CategoryInfo> getFirstCategory();

    List<CategoryInfo> getSecondCategory(Integer categoryId);

    List<CategoryInfo> getThirdCategory(Integer categoryId);
}

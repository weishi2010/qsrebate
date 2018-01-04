package com.rebate.service.admin;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.CategoryInfo;
import com.rebate.domain.IncomeDetail;
import com.rebate.domain.query.IncomeDetailQuery;

import java.util.List;

public interface AdminService {

    List<CategoryInfo> getFirstCategory();

    List<CategoryInfo> getSecondCategory(Integer categoryId);

    List<CategoryInfo> getThirdCategory(Integer categoryId);

    PaginatedArrayList<IncomeDetail> getIncomeDetails(IncomeDetailQuery incomeDetailQuery);
}

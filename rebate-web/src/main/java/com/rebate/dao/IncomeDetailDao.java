package com.rebate.dao;

import com.rebate.domain.IncomeDetail;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.IncomeDetailQuery;

public interface IncomeDetailDao {
    /**
     * 插入
     * @param incomeDetail
     * @return
     */
    long insert(IncomeDetail incomeDetail);

    /**
     * 根据openId referenceId查询
     * @param incomeDetailQuery
     * @return
     */
    IncomeDetail findIncomeDetail(IncomeDetailQuery incomeDetailQuery);

    /**
     * 根据类型查询统计
     * @param incomeDetailQuery
     * @return
     */
    Double findIncomeStatistisByType(IncomeDetailQuery incomeDetailQuery);

}

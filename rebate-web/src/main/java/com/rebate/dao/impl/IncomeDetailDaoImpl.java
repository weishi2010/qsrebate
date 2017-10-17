package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.IncomeDetailDao;
import com.rebate.domain.IncomeDetail;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.IncomeDetailQuery;

/**
 * @author weishi
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2017/10/17 22:18  weishi 新建代码
 * =========================== 维护日志 ===========================
 */
public class IncomeDetailDaoImpl extends BaseDao implements IncomeDetailDao {
    @Override
    public long insert(IncomeDetail incomeDetail) {
        return (long) insert("IncomeDetail.insert", incomeDetail);
    }

    @Override
    public IncomeDetail findIncomeDetail(IncomeDetailQuery incomeDetailQuery) {
        return (IncomeDetail) queryForObject("IncomeDetail.findIncomeDetail", incomeDetailQuery);
    }

    @Override
    public Double findIncomeStatistisByType(IncomeDetailQuery incomeDetailQuery) {
        return (Double) queryForObject("IncomeDetail.findIncomeStatistisByType", incomeDetailQuery);
    }
}

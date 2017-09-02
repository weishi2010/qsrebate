package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.CommissionDao;
import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;

public class CommissionDaoImpl extends BaseDao implements CommissionDao {
    @Override
    public long insert(Commission commission) {
        return (Long)insert("Commission.insert",commission);
    }

    @Override
    public long updateTotalCommission(Commission commission) {
        return update("Commission.updateTotalCommission",commission);
    }

    @Override
    public Commission findCommissionByOpenId(Commission commission) {
        return (Commission)queryForObject("Commission.findCommissionByOpenId",commission);
    }
}

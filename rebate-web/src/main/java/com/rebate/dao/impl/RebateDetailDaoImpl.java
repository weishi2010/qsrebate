package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.RebateDetailQuery;

import java.util.List;

public class RebateDetailDaoImpl extends BaseDao implements RebateDetailDao {
    @Override
    public long insert(RebateDetail rebateDetail) {
        return (Long) insert("RebateDetail.insert", rebateDetail);
    }

    @Override
    public int findCountByOpenId(RebateDetailQuery rebateDetailQuery) {
        return (int) queryForObject("RebateDetail.findCountByOpenId", rebateDetailQuery);
    }

    @Override
    public List<RebateDetail> findListByOpenId(RebateDetailQuery rebateDetailQuery) {
        return (List<RebateDetail>) queryForList("RebateDetail.findListByOpenId", rebateDetailQuery);
    }
}

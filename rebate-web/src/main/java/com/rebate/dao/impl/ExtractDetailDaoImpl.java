package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.ExtractDetailDao;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.query.ExtractDetailQuery;

import java.util.List;

public class ExtractDetailDaoImpl extends BaseDao implements ExtractDetailDao {
    @Override
    public long insert(ExtractDetail extractDetail) {
        return (Long)insert("ExtractDetail.insert",extractDetail);
    }

    @Override
    public void updateExtractDetail(ExtractDetail extractDetail) {
        update("ExtractDetail.updateExtractDetail",extractDetail);
    }

    @Override
    public int findExtractDetailCount(ExtractDetailQuery extractDetailQuery) {
        return (int)queryForObject("ExtractDetail.findExtractDetailCount",extractDetailQuery);
    }

    @Override
    public List<ExtractDetail> findExtractDetailList(ExtractDetailQuery extractDetailQuery) {
        return queryForList("ExtractDetail.findExtractDetailList",extractDetailQuery);
    }

    @Override
    public Double findUserTotalExtract(ExtractDetailQuery extractDetailQuery) {
        return (Double)queryForObject("ExtractDetail.findUserTotalExtract",extractDetailQuery);

    }
}

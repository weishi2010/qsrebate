package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.OrderSummary;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.OrderSummaryQuery;
import com.rebate.domain.query.RebateDetailQuery;

import java.util.List;

public class RebateDetailDaoImpl extends BaseDao implements RebateDetailDao {
    @Override
    public long insert(RebateDetail rebateDetail) {
        return (Long) insert("RebateDetail.insert", rebateDetail);
    }

    @Override
    public void update(RebateDetail rebateDetail) {
        update("RebateDetail.update", rebateDetail);
    }

    @Override
    public RebateDetail queryRebateDetailByOrderId(RebateDetailQuery rebateDetailQuery) {
        return (RebateDetail) queryForObject("RebateDetail.queryRebateDetailByOrderId", rebateDetailQuery);
    }

    @Override
    public int findCount(RebateDetailQuery rebateDetailQuery) {
        return (int) queryForObject("RebateDetail.findCount", rebateDetailQuery);
    }

    @Override
    public Double findUserTotalCommission(RebateDetailQuery rebateDetailQuery) {
        return (Double) queryForObject("RebateDetail.findUserTotalCommission", rebateDetailQuery);
    }

    @Override
    public List<RebateDetail> findList(RebateDetailQuery rebateDetailQuery) {
        return (List<RebateDetail>) queryForList("RebateDetail.findList", rebateDetailQuery);
    }

    @Override
    public List<OrderSummary> getOrderSummaryBySubUnionId(OrderSummary orderSummary) {
        return (List<OrderSummary>) queryForList("RebateDetail.getOrderSummaryBySubUnionId", orderSummary);
    }

    @Override
    public OrderSummary getAllOrderSummaryByDate(OrderSummaryQuery orderSummaryQuery) {
        return (OrderSummary) queryForObject("RebateDetail.getAllOrderSummaryByDate", orderSummaryQuery);
    }

    @Override
    public OrderSummary getRecommendUserOrderSummaryByOpenId(RebateDetailQuery rebateDetailQuery) {
        return (OrderSummary) queryForObject("RebateDetail.getRecommendUserOrderSummaryByOpenId", rebateDetailQuery);
    }
}

package com.rebate.dao;

import com.rebate.domain.OrderSummary;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.OrderSummaryQuery;
import com.rebate.domain.query.RebateDetailQuery;

import java.util.List;

/**
 * Created by weishi on 2017/7/15.
 */
public interface RebateDetailDao {
    /**
     * 插入
     *
     * @param rebateDetail
     * @return
     */
    long insert(RebateDetail rebateDetail);

    /**
     * 更新
     *
     * @param rebateDetail
     * @return
     */
    void update(RebateDetail rebateDetail);

    /**
     * 根据订单号查询返利明细
     * @param rebateDetailQuery
     * @return
     */
    RebateDetail queryRebateDetailByOrderId(RebateDetailQuery rebateDetailQuery);

    /**
     * 根据openid查询总数
     *
     * @param rebateDetailQuery
     * @return
     */
    int findCountBySubUnionId(RebateDetailQuery rebateDetailQuery);

    /**
     * 查询用户总佣金余额
     * @param rebateDetailQuery
     * @return
     */
    Double findUserTotalCommission(RebateDetailQuery rebateDetailQuery);

    /**
     * 根据id查询列表
     *
     * @param rebateDetailQuery
     * @return
     */
    List<RebateDetail> findListBySubUnionId(RebateDetailQuery rebateDetailQuery);

    /**
     * 订单统计
     * @param orderSummary
     * @return
     */
    List<OrderSummary> getOrderSummaryBySubUnionId(OrderSummary orderSummary);

    /**
     * 根据时间查询全部统计
     * @param orderSummaryQuery
     * @return
     */
    OrderSummary getAllOrderSummaryByDate(OrderSummaryQuery orderSummaryQuery);

    /**
     * 根据时间查询推荐用户带来的订单统计
     * @param rebateDetailQuery
     * @return
     */
    OrderSummary getRecommendUserOrderSummaryByOpenId(RebateDetailQuery rebateDetailQuery);
}

package com.rebate.service.order;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.OrderSummary;
import com.rebate.domain.query.OrderSummaryQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.RebateDetailVo;

import java.util.List;

public interface RebateDetailService {

    /**
     * 查询列表
     * @param rebateDetailQuery
     * @return
     */
    PaginatedArrayList<RebateDetailVo> findRebateDetailList(RebateDetailQuery rebateDetailQuery);

    /**
     * 代理模式一：查询下级代理订单明细
     * @param query
     * @return
     */
    PaginatedArrayList<RebateDetailVo> findFirstAgentSonRebateDetailList(RebateDetailQuery query);

    /**
     * 代理模式二：查询推广粉丝订单明细
     * @param query
     * @return
     */
     PaginatedArrayList<RebateDetailVo> findSecondAgentRecommendRebateDetailList(RebateDetailQuery query);

    /**
     * 订单统计
     * @param orderSummary
     * @return
     */
    PaginatedArrayList<OrderSummary> getOrderSummaryBySubUnionId(OrderSummary orderSummary);

    /**
     * 代理模式一获取子代理订单汇总
     * @param subUnionId
     * @return
     */
    PaginatedArrayList<OrderSummary> getFirstAgentSonOrderSummary(String subUnionId);

    /**
     * 代理模式二获取粉丝订单汇总
     * @param openId
     * @return
     */
    PaginatedArrayList<OrderSummary> getSecondAgentRecommendUserOrderSummary(String openId);

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

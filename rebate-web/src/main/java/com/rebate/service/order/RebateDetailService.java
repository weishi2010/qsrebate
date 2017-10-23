package com.rebate.service.order;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.OrderSummary;
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
     * 订单统计
     * @param orderSummary
     * @return
     */
    PaginatedArrayList<OrderSummary> getOrderSummaryBySubUnionId(OrderSummary orderSummary);
}

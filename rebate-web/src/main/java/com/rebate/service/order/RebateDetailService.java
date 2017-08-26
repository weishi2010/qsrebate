package com.rebate.service.order;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.RebateDetailVo;

public interface RebateDetailService {

    /**
     * 查询列表
     * @param rebateDetailQuery
     * @return
     */
    PaginatedArrayList<RebateDetailVo> findRebateDetailList(RebateDetailQuery rebateDetailQuery);
}

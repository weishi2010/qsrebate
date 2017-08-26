package com.rebate.service.extract;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.vo.ExtractDetailVo;

public interface ExtractDetailService {
    /**
     * 提现
     *
     * @param extractDetail
     * @return 返回错误码
     */
    int extract(ExtractDetail extractDetail);

    /**
     * 查询列表
     *
     * @param extractDetailQuery
     * @return
     */
    PaginatedArrayList<ExtractDetailVo> findExtractDetailList(ExtractDetailQuery extractDetailQuery);
}

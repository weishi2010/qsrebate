package com.rebate.domain.query;

import com.rebate.domain.ExtractDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 提现明细查询对象
 */
public class ExtractDetailQuery extends ExtractDetail {
    /**
     * 开始时间
     */
    @Getter
    @Setter
    private Date beginDate;
    /**
     * 结束时间
     */
    @Getter
    @Setter
    private Date endDate;
}

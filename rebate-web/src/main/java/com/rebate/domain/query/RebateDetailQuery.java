package com.rebate.domain.query;

import com.rebate.domain.RebateDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 返利明细查询对象
 */
public class RebateDetailQuery extends RebateDetail {
    @Getter
    @Setter
    private Date startDate;

    @Getter
    @Setter
    private Date endDate;
}

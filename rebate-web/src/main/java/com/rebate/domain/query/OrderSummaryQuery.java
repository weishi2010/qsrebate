package com.rebate.domain.query;

import com.rebate.domain.OrderSummary;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class OrderSummaryQuery extends OrderSummary {

    @Getter
    @Setter
    private Date startDate;

    @Getter
    @Setter
    private Date endDate;
}

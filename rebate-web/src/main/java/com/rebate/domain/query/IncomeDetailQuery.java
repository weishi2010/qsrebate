package com.rebate.domain.query;

import com.rebate.domain.IncomeDetail;
import lombok.Getter;
import lombok.Setter;

public class IncomeDetailQuery extends IncomeDetail {
    @Getter
    @Setter
    private String typeList;
}

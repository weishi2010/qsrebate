package com.rebate.domain.vo;

import com.rebate.domain.ExtractDetail;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class ExtractDetailVo extends ExtractDetail {
    public ExtractDetailVo(ExtractDetail extractDetail) {
        BeanUtils.copyProperties(extractDetail, this);
    }

    /**
     * 获取显示方案
     * @return
     */
    public String getExtractStatusShow(){
        return EExtractStatus.getStatusShow(super.getStatus());
    }

}

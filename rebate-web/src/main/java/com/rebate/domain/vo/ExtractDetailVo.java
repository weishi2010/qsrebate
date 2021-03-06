package com.rebate.domain.vo;

import com.rebate.domain.ExtractDetail;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtractDetailVo extends ExtractDetail {
    @Getter
    @Setter
    private String nickName;

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

    /**
     * 获取显示方案
     * @return
     */
    public String getExtractDateShow(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(this.getExtractDate());
    }
}

package com.rebate.domain.query;

import com.rebate.domain.Activity;
import lombok.Getter;
import lombok.Setter;

/**
 * 活动信息查询
 */
public class ActivityQuery extends Activity{
    @Getter
    @Setter
    private String statusList;
}

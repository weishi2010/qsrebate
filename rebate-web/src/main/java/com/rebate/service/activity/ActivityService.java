package com.rebate.service.activity;

import com.rebate.domain.Activity;
import com.rebate.domain.query.ActivityQuery;

import java.util.List;

public interface ActivityService {
    /**
     * 活动导入
     */
    void importActivity( List<Activity> activityListList );

    /**
     * 查询活动列表
     * @param activityQuery
     * @return
     */
    List<Activity> getActivityTopList(ActivityQuery activityQuery);
}

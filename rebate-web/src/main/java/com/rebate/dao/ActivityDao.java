package com.rebate.dao;

import com.rebate.domain.Activity;
import com.rebate.domain.query.ActivityQuery;

import java.util.List;

public interface ActivityDao {
    /**
     * 插入
     * @param activity
     */
    void insert(Activity activity);

    /**
     * 更新
     * @param activity
     */
    void update(Activity activity);

    /**
     * 根据标题和链接查询活动
     * @param activityQuery
     * @return
     */
    Activity findActivity(ActivityQuery activityQuery);

    /**
     * 根据条件查询活动列表
     * @param activityQuery
     * @return
     */
    List<Activity> findActivityList(ActivityQuery activityQuery);

}

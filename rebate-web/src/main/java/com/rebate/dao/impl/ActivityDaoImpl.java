package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.ActivityDao;
import com.rebate.domain.Activity;
import com.rebate.domain.query.ActivityQuery;

import java.util.List;

public class ActivityDaoImpl extends BaseDao implements ActivityDao {
    @Override
    public void insert(Activity activity) {
        insert("Activity.insert",activity);
    }
    @Override
    public void update(Activity activity) {
        update("Activity.update",activity);
    }

    @Override
    public Activity findActivity(ActivityQuery activityQuery) {
        return (Activity)queryForObject("Activity.findActivity",activityQuery);
    }

    @Override
    public List<Activity> findActivityList(ActivityQuery activityQuery) {
        return (List<Activity>)queryForList("Activity.findActivityList",activityQuery);
    }
}

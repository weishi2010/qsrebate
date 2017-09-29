package com.rebate.service.activity.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.dao.ActivityDao;
import com.rebate.domain.Activity;
import com.rebate.domain.query.ActivityQuery;
import com.rebate.service.activity.ActivityService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Qualifier("activityDao")
    @Autowired(required = true)
    private ActivityDao activityDao;

    @Override
    public void importActivity(List<Activity> activityListList) {
        for (Activity activity : activityListList) {
            try{
                if(null==activity.getRemark()){
                    activity.setRemark("");
                }
                if(null==activity.getStatus()){
                    activity.setStatus(0);
                }
                ActivityQuery activityQuery = new ActivityQuery();
                activityQuery.setTitle(activity.getTitle());
                activityQuery.setActivityLink(activity.getActivityLink());
                if (null == activityDao.findActivity(activityQuery)) {
                    activityDao.insert(activity);
                }
            }catch (Exception e){
                LOG.error("importActivity error!activity:"+ JsonUtil.toJson(activity),e);
            }

        }
    }

    @Override
    public List<Activity> getActivityTopList(ActivityQuery activityQuery) {
        return activityDao.findActivityList(activityQuery);
    }

}

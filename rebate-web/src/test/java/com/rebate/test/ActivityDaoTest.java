package com.rebate.test;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.ActivityDao;
import com.rebate.dao.CategoryDao;
import com.rebate.domain.Activity;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.en.EActivityStatus;
import com.rebate.domain.query.ActivityQuery;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class ActivityDaoTest extends AbstractJUnit4SpringContextTests {

    private final static TypeReference<List<Map>> mapListTypeReference = new TypeReference<List<Map>>() {
    };

    @Qualifier("activityDao")
    @Autowired(required = true)
    private ActivityDao activityDao;

    @Test
    public void testInsert() {
        for(int i=0;i<20;i++){

            Activity activity = new Activity();
            activity.setActivityLink("http://m.qingsongfan.com");
            activity.setBeginTime(new Date());
            activity.setEndTime(new Date());
            activity.setBenefit("满200减100");
            activity.setImgUrl("http://img.qingsongfan.com");
            activity.setTitle("活动测试"+i);
            activity.setRemark("备注");
            activity.setStatus(1);
            System.out.println("json:"+JsonUtil.toJson(activity));
            ActivityQuery activityQuery = new ActivityQuery();
            activityQuery.setTitle(activity.getTitle());
            activityQuery.setActivityLink(activity.getActivityLink());
            if(null==activityDao.findActivity(activityQuery)){
                activityDao.insert(activity);
            }
        }

    }
    @Test
    public void testList(){
        ActivityQuery activityQuery= new ActivityQuery();
        activityQuery.setStatusList(EActivityStatus.DEFAULT.getCode()+","+EActivityStatus.PASS.getCode());
        activityQuery.setPageSize(20);
        List list = activityDao.findActivityList(activityQuery);

        System.out.println("list:"+JsonUtil.toJson(list));
    }



}
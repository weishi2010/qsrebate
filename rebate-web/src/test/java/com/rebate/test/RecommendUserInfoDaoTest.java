package com.rebate.test;


import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.RecommendUserInfoDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.RecommendUserInfo;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.ESequence;
import com.rebate.domain.en.ESubUnionIdPrefix;
import com.rebate.domain.query.RecommendUserInfoQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class RecommendUserInfoDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private RecommendUserInfoDao recommendUserInfoDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Qualifier("sequenceUtil")
    @Autowired(required = true)
    private SequenceUtil sequenceUtil;

    @Test
    public void insert() {
        RecommendUserInfo recommendUserInfo = new RecommendUserInfo();
        recommendUserInfo.setOpenId("a");
        recommendUserInfo.setRecommendAccount("weishi2010");
        recommendUserInfo.setStatus(0);

        RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
        recommendUserInfoQuery.setRecommendAccount("weishi2010");
        RecommendUserInfo existsRecommendUserInfo = recommendUserInfoDao.findRecommendUserInfo(recommendUserInfoQuery);
        if (null == existsRecommendUserInfo) {
            recommendUserInfoDao.insert(recommendUserInfo);
        }else{
            recommendUserInfoDao.update(recommendUserInfo);
        }
    }

    @Test
    public void testFindCount(){
        SimpleDateFormat formatStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat formatEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

        Calendar calendar = Calendar.getInstance();
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = formatStart.parse(formatStart.format(calendar.getTime()));

            calendar.setTime(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            endDate = formatEnd.parse(formatEnd.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
        recommendUserInfoQuery.setRecommendAccount("weishi2010");
        recommendUserInfoQuery.setStartDate(startDate);
        recommendUserInfoQuery.setEndDate(endDate);
        int count = recommendUserInfoDao.findRecommendUserCount(recommendUserInfoQuery);
        System.out.println("count:"+count);
    }
}

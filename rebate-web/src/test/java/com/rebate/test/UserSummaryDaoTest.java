package com.rebate.test;


import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.UserInfoDao;
import com.rebate.dao.UserSummaryDao;
import com.rebate.domain.UserInfo;
import com.rebate.domain.UserSummary;
import com.rebate.domain.en.ESequence;
import com.rebate.domain.en.ESubUnionIdPrefix;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class UserSummaryDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private UserSummaryDao userSummaryDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @Test
    public void insert() {
        UserSummary userSummary = new UserSummary();
        userSummary.setSubUnionId("JD100001251");
        try {
            userSummary.setOpDate(sdf.parse(sdf.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        userSummary.setClickCount(1);
        if (null == userSummaryDao.findUserSummary(userSummary)) {
            userSummaryDao.insert(userSummary);
        } else {
            userSummaryDao.update(userSummary);

        }
    }

    @Test
    public void incrUserClick() {
        UserSummary userSummary = new UserSummary();
        userSummary.setSubUnionId("JD100001251");
        try {
            userSummary.setOpDate(sdf.parse(sdf.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null == userSummaryDao.findUserSummary(userSummary)) {
            userSummary.setClickCount(1);
            userSummaryDao.insert(userSummary);
        }else{
            userSummaryDao.incrUserClick(userSummary);
        }
    }

}

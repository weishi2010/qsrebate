package com.rebate.test;


import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CommentDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Comment;
import com.rebate.domain.UserInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class UserInfoDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private UserInfoDao userInfoDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void insert() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone("13710788876");
        userInfo.setNickName("迷你猴子");
        userInfo.setOpenId("weishi2010");
        userInfo.setStatus(0);
        userInfo.setEmail("weishi@163.com");
        userInfo.setWxImage("http://11");
        UserInfo existsUserInfo = userInfoDao.findLoginUserInfo(userInfo);
        if (null == existsUserInfo) {
            userInfoDao.insert(userInfo);
        }else{
            System.out.println("existsUserInfo:"+JsonUtil.toJson(existsUserInfo));
        }
    }


}

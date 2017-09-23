package com.rebate.test;


import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CommentDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Comment;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.ESequence;
import com.rebate.domain.en.ESubUnionIdPrefix;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Qualifier("sequenceUtil")
    @Autowired(required = true)
    private SequenceUtil sequenceUtil;

    @Test
    public void insert() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone("13710788876");
        userInfo.setNickName("迷你猴子");
        userInfo.setOpenId("weishi2010");
        userInfo.setStatus(0);
        userInfo.setEmail("weishi@163.com");
        userInfo.setWxImage("http://11");
        //生成子联盟ID
        String subUnionId = ESubUnionIdPrefix.getSubUnionId(ESubUnionIdPrefix.JD.getCode(),sequenceUtil.get(ESequence.SUB_UNION_ID.getSequenceName()));
        userInfo.setSubUnionId(subUnionId);
        userInfo.setRecommendAccount("weishi2010");
        UserInfo existsUserInfo = userInfoDao.findLoginUserInfo(userInfo);
        if (null == existsUserInfo) {
            userInfoDao.insert(userInfo);
        }else{
            System.out.println("existsUserInfo:"+JsonUtil.toJson(existsUserInfo));
        }
    }


    public static void main(String[] args){
    String userInfoJson = "{\"id\":3,\"openId\":\"oIAUmv8x60aC5B7FrxVy8Z9-imyY\",\"wxImage\":\"http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqpJdBmwaEgG5QfqPAyxvUBtEtPmeoIP9Zmmxkic8EJvKUXj9FjUAoMwnvb0KySL7NMPJXc6Kic4lOQ/0\",\"phone\":\"\",\"nickName\":\"wsh\",\"email\":\"\",\"status\":0,\"subUnionId\":\"JD100000251\",\"recommendAccount\":\"\",\"created\":\"2017-09-23 16:26:57\",\"modified\":\"2017-09-23 16:26:57\"}";
    UserInfo userInfo = JsonUtil.fromJson(userInfoJson,UserInfo.class);
    System.out.println(userInfo.getOpenId());
    }
}

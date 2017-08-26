package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.UserInfo;

/**
 * Created by weishi on 2017/7/15.
 */
public class UserInfoDaoImpl extends BaseDao implements UserInfoDao {
    @Override
    public long insert(UserInfo userInfo) {
        return (Long)insert("UserInfo.insert",userInfo);
    }

    @Override
    public UserInfo findLoginUserInfo(UserInfo userInfo) {
        return (UserInfo) queryForObject("UserInfo.findLoginUserInfo",userInfo);
    }
}

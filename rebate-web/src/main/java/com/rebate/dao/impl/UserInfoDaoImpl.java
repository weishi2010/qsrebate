package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.UserInfo;
import com.rebate.domain.query.UserInfoQuery;

public class UserInfoDaoImpl extends BaseDao implements UserInfoDao {
    @Override
    public long insert(UserInfo userInfo) {
        return (Long)insert("UserInfo.insert",userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        update("UserInfo.update",userInfo);
    }

    @Override
    public UserInfo findLoginUserInfo(UserInfo userInfo) {
        return (UserInfo) queryForObject("UserInfo.findLoginUserInfo",userInfo);
    }

    @Override
    public UserInfo findUserInfoBySubUnionId(UserInfo userInfo) {
        return (UserInfo) queryForObject("UserInfo.findUserInfoBySubUnionId",userInfo);
    }

    @Override
    public int findRecommendUserCount(UserInfoQuery userInfoQuery) {
        return (int) queryForObject("UserInfo.findRecommendUserCount",userInfoQuery);
    }
}

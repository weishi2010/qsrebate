package com.rebate.dao;

import com.rebate.domain.UserInfo;

/**
 * Created by weishi on 2017/7/15.
 */
public interface UserInfoDao {
    /**
     * 插入
     * @param userInfo
     * @return
     */
    long insert(UserInfo userInfo);

    /**
     * 根据openId查询
     * @param userInfo
     * @return
     */
    UserInfo findLoginUserInfo(UserInfo userInfo);

    /**
     * 根据子联盟ID查询
     * @param userInfo
     * @return
     */
    UserInfo findUserInfoBySubUnionId(UserInfo userInfo);
}

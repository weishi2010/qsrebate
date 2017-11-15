package com.rebate.dao;

import com.rebate.domain.UserInfo;
import com.rebate.domain.query.UserInfoQuery;

import java.util.List;

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
     * 更新
     * @param userInfo
     */
    void update(UserInfo userInfo);

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

    /**
     * 按时间查询推荐过来的粉丝用户
     * @param userInfoQuery
     * @return
     */
    int findRecommendUserCount(UserInfoQuery userInfoQuery);

    int findUserCount(UserInfoQuery userInfoQuery);

    List<UserInfo> findAllUsers(UserInfoQuery userInfoQuery);
}

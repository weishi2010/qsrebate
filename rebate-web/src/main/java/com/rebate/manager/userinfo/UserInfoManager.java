package com.rebate.manager.userinfo;

import com.rebate.domain.UserInfo;
import com.rebate.domain.query.UserInfoQuery;

import java.util.List;

public interface UserInfoManager {
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

    /**
     * 查询用户总条数
     * @param userInfoQuery
     * @return
     */
    int findUserCount(UserInfoQuery userInfoQuery);

    /**
     * 查询用户列表
     * @param userInfoQuery
     * @return
     */
    List<UserInfo> findAllUsers(UserInfoQuery userInfoQuery);

    /**
     * 是否白名单
     * @param subUnionId
     * @return
     */
    boolean isWhiteAgent(String subUnionId);

    /**
     * 添加代理白名单
     * @param subUnionId
     */
    void addWhiteAgent(String subUnionId);

    /**
     * 取消代理白名单
     * @param subUnionId
     */
    void cancelWhiteAgent(String subUnionId);

}

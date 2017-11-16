package com.rebate.service.userinfo;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;
import com.rebate.domain.query.RecommendUserInfoQuery;
import com.rebate.domain.query.UserInfoQuery;
import com.rebate.domain.vo.UserInfoVo;

import java.util.List;

public interface UserInfoService {

    /**
     * 是否白名单代理
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

    public void sysRecommendUser();

    /**
     * 更新代理状态
     * @param openId
     * @param agent
     */
    void updateUserInfoAgent(String openId,String recommendOpenId,Integer agent);


    /**
     * 更新用户提现余额
     *
     * @param openId
     */
    void updateUserCommission(String openId);

    /**
     * 注册用户
     * @param openId
     * @param angent
     * @param isAward 是否进行注册奖励
     * @return
     */
    UserInfo registUserInfo(String openId,String recommendOpenId, Integer angent,boolean isAward);

    /**
     * 查询用户信息
     *
     * @param openId
     * @return
     */
    UserInfo getUserInfo(String openId);

    /**
     * 查询用户佣金
     *
     * @param openId
     * @return
     */
    Commission getUserCommission(String openId);

    /**
     * 按时间查询推荐过来的粉丝用户
     * @param recommendUserInfoQuery
     * @return
     */
    int findRecommendUserCount(RecommendUserInfoQuery recommendUserInfoQuery);

    /**
     * 查询用户列表
     * @param userInfoQuery
     * @return
     */
    PaginatedArrayList<UserInfoVo> getUserList(UserInfoQuery userInfoQuery);
}

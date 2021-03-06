package com.rebate.service.userinfo;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.query.AgentRelationQuery;
import com.rebate.domain.query.RecommendUserInfoQuery;
import com.rebate.domain.query.UserInfoQuery;
import com.rebate.domain.vo.AgentRelationVo;
import com.rebate.domain.vo.UserInfoVo;

import java.util.List;

public interface UserInfoService {

    void synWxUserInfo(String openId);

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

    /**
     * 清除缓存
     * @param openId
     */
    void cleanUserInfoCache(String openId);

    public void sysRecommendUser();

    /**
     * 代理模式一(多级代理)：更新二级代理比例
     * @param id
     * @param commissionRatio
     */
    void updateSecondAgentCommissionRate(String userByParentId,Long id,Double commissionRatio);

    /**
     * 代理模式一(多级代理)：添加下级代理
     * @param agentSubUnionId
     * @param secondAgentSubUnionId
     */
    int addSecondAgent(String agentSubUnionId,String secondAgentSubUnionId,Double commissionRatio);

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
     * 获取用户提现金额
     * @param openId
     * @return
     */
    Double getUserExtractCommission(String openId);

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

    /**
     * 代理模式一:查询二级代理列表
     * @param agentRelationQuery
     * @return
     */
    PaginatedArrayList<AgentRelationVo> getAgentUserByParentId(AgentRelationQuery agentRelationQuery);
}

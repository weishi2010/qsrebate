package com.rebate.service.userinfo.impl;

import com.google.common.base.Joiner;
import com.jd.data.redis.RedisUtils;
import com.rebate.common.cache.RedisKey;
import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.*;
import com.rebate.domain.Commission;
import com.rebate.domain.RecommendUserInfo;
import com.rebate.domain.UserInfo;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.AgentRelationQuery;
import com.rebate.domain.query.IncomeDetailQuery;
import com.rebate.domain.query.RecommendUserInfoQuery;
import com.rebate.domain.query.UserInfoQuery;
import com.rebate.domain.vo.AgentRelationVo;
import com.rebate.domain.vo.UserInfoVo;
import com.rebate.domain.whitelist.WhiteUserInfo;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.manager.userinfo.UserInfoManager;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
    private static final Logger LOG = LoggerFactory.getLogger(UserInfoServiceImpl.class);


    @Qualifier("commissionDao")
    @Autowired(required = true)
    private CommissionDao commissionDao;

    @Qualifier("incomeDetailDao")
    @Autowired(required = true)
    private IncomeDetailDao incomeDetailDao;

    @Qualifier("userInfoManager")
    @Autowired(required = true)
    private UserInfoManager userInfoManager;

    @Qualifier("wxService")
    @Autowired(required = false)
    private WxService wxService;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = false)
    private WxAccessTokenService wxAccessTokenService;

    @Qualifier("sequenceUtil")
    @Autowired(required = true)
    private SequenceUtil sequenceUtil;


    @Qualifier("recommendUserInfoDao")
    @Autowired(required = true)
    private RecommendUserInfoDao recommendUserInfoDao;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Qualifier("agentRelationDao")
    @Autowired(required = true)
    private AgentRelationDao agentRelationDao;

    @Override
    public void synWxUserInfo(String openId) {
        WxUserInfo wxUserInfo = wxService.getWxApiUserInfo(wxAccessTokenService.getApiAccessToken().getAccessToken(), openId);
        if (null != wxUserInfo) {
            LOG.error("synWxUserInfo wxUserInfo:" + JsonUtil.toJson(wxUserInfo));

            try {
                UserInfo userInfoQuery = new UserInfo();
                userInfoQuery.setOpenId(openId);
                UserInfo userInfo = userInfoManager.findLoginUserInfo(userInfoQuery);
                LOG.error("synWxUserInfo userInfo:" + JsonUtil.toJson(userInfo));
                if (null != userInfo) {
                    //更新昵称
                    userInfo.setNickName(wxUserInfo.getNickname());
                    userInfo.setWxImage(wxUserInfo.getHeadimgurl());
                    userInfoManager.update(userInfo);
                }
            } catch (Exception e) {
                LOG.error("synWxUserInfo error!wxUserInfo:" + JsonUtil.toJson(wxUserInfo), e);
            }
        }
    }

    @Override
    public void addWhiteAgent(String subUnionId) {
        userInfoManager.addWhiteAgent(subUnionId);
    }

    @Override
    public void cancelWhiteAgent(String subUnionId) {
        userInfoManager.cancelWhiteAgent(subUnionId);
    }

    @Override
    public void sysRecommendUser() {

        UserInfoQuery userInfoQuery = new UserInfoQuery();
        userInfoQuery.setStartRow(0);
        userInfoQuery.setPageSize(100000);
        List<UserInfo> list = userInfoManager.findAllUsers(userInfoQuery);
        for (UserInfo userInfo : list) {
            if (StringUtils.isNotBlank(userInfo.getRecommendAccount()) && !userInfo.getRecommendAccount().equalsIgnoreCase(userInfo.getOpenId())) {
                RecommendUserInfo recommendUserInfo = new RecommendUserInfo();
                recommendUserInfo.setOpenId(userInfo.getOpenId());
                recommendUserInfo.setRecommendAccount(userInfo.getRecommendAccount());
                recommendUserInfo.setStatus(0);

                RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
                recommendUserInfoQuery.setRecommendAccount(userInfo.getRecommendAccount());
                recommendUserInfoQuery.setOpenId(userInfo.getOpenId());
                RecommendUserInfo existsRecommendUserInfo = recommendUserInfoDao.findRecommendUserInfo(recommendUserInfoQuery);
                if (null == existsRecommendUserInfo) {
                    recommendUserInfoDao.insert(recommendUserInfo);
                } else {
                    recommendUserInfoDao.update(recommendUserInfo);
                }
            }
        }

    }

    @Override
    public void updateSecondAgentCommissionRate(String agentUserByParentId, Long id, Double commissionRatio) {
        AgentRelation agentRelation = new AgentRelation();
        agentRelation.setId(id);
        agentRelation.setParentAgentSubUnionId(agentUserByParentId);
        agentRelation.setCommissionRatio(commissionRatio);
        agentRelationDao.update(agentRelation);
    }

    @Override
    public int addSecondAgent(String agentSubUnionId, String secondAgentSubUnionId, Double commissionRatio) {
        int code = EAgentResultCode.SUCCESS.getCode();
        try {
            UserInfo userInfoQuery = new UserInfo();
            userInfoQuery.setSubUnionId(secondAgentSubUnionId);
            UserInfo userInfo = userInfoManager.findUserInfoBySubUnionId(userInfoQuery);
            if (null == userInfo) {
                return EAgentResultCode.NOT_EXISTS_USER.getCode();
            }

            //查询此二级代理用户是否已存在，不存在则再进行添加
            AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
            agentRelationQuery.setAgentSubUnionId(secondAgentSubUnionId);
            if (null == agentRelationDao.findByAgentSubUnionId(agentRelationQuery)) {
                AgentRelation agentRelation = new AgentRelation();
                agentRelation.setParentAgentSubUnionId(agentSubUnionId);
                agentRelation.setAgentSubUnionId(secondAgentSubUnionId);
                agentRelation.setStatus(0);
                agentRelation.setCommissionRatio(commissionRatio);
                agentRelationDao.insert(agentRelation);
            } else {
                code = EAgentResultCode.EXISTS.getCode();
            }
        } catch (Exception e) {
            code = EAgentResultCode.ERROR.getCode();
            LOG.error("addSecondAgent error!agentSubUnionId:" + agentSubUnionId + ",secondAgentSubUnionId:" + secondAgentSubUnionId, e);
        }
        return code;
    }

    @Override
    public UserInfo registUserInfo(String openId, String recommendOpenId, Integer angent, boolean isAward) {
        UserInfo userInfo = null;
        WxUserInfo wxUserInfo = wxService.getWxApiUserInfo(wxAccessTokenService.getApiAccessToken().getAccessToken(), openId);
        if (null != wxUserInfo) {

            userInfo = new UserInfo();
            userInfo.setPhone("");
            userInfo.setNickName(wxUserInfo.getNickname());
            userInfo.setOpenId(wxUserInfo.getOpenid());
            userInfo.setStatus(0);
            userInfo.setEmail("");
            userInfo.setAgent(angent);
            userInfo.setWxImage(wxUserInfo.getHeadimgurl());
            String subUnionId = ESubUnionIdPrefix.getSubUnionId(ESubUnionIdPrefix.JD.getCode(), sequenceUtil.get(ESequence.SUB_UNION_ID.getSequenceName()));
            userInfo.setSubUnionId(subUnionId);

            //如果推荐人为自己，则不设置推荐人
            if (StringUtils.isNotBlank(recommendOpenId) && !recommendOpenId.equalsIgnoreCase(openId)) {
                userInfo.setRecommendAccount(recommendOpenId);
                //添加推荐记录，即拉粉记录
                addRecommendUser(openId, recommendOpenId);
            } else {
                userInfo.setRecommendAccount("");
            }

            try {

                userInfoManager.insert(userInfo);
            } catch (Exception e) {
                userInfo.setNickName(wxUserInfo.getOpenid());
                userInfoManager.insert(userInfo);
            }

//            IncomeDetailQuery incomeDetailQuery = new IncomeDetailQuery();
//            incomeDetailQuery.setOpenId(wxUserInfo.getOpenid());
//            incomeDetailQuery.setType(EIncomeType.REGIST.getCode());
//            //未获得奖励的则给予用户奖励10元
//            if (isAward && null == incomeDetailDao.findIncomeDetail(incomeDetailQuery)) {
//                //注册成功用户给用户发放10元提现金额插入支出记录
//                IncomeDetail incomeDetail = new IncomeDetail();
//                incomeDetail.setOpenId(openId);
//                incomeDetail.setReferenceId(userInfo.getId());
//                incomeDetail.setIncome(jDProperty.getNewRegisterAward());
//                incomeDetail.setStatus(0);
//                incomeDetail.setDealt(0);
//                incomeDetail.setType(EIncomeType.REGIST.getCode());
//                incomeDetailDao.insert(incomeDetail);
//            }

        }

        return userInfo;
    }

    @Override
    public void updateUserInfoAgent(String openId, String recommendOpenId, Integer agent) {
        UserInfo userInfo = new UserInfo();
        userInfo.setOpenId(openId);
        userInfo.setAgent(agent);
        //如果推荐人为自己，则不设置推荐人
        if (StringUtils.isNotBlank(recommendOpenId) && !recommendOpenId.equalsIgnoreCase(openId)) {
            userInfo.setRecommendAccount(recommendOpenId);
            boolean flag = addRecommendUser(openId, recommendOpenId);
        }
        userInfoManager.update(userInfo);

    }

    @Override
    public UserInfo getUserInfo(String openId) {
        //从缓存获取用户信息
        UserInfo userInfo = userInfoManager.getUserInfoCache(openId);
        if (null != userInfo) {
            LOG.error("[getUserInfo]userInfo cache:" + JsonUtil.toJson(userInfo));
            return userInfo;
        }

        //从db查询
        UserInfo userInfoQuery = new UserInfo();
        userInfoQuery.setOpenId(openId);
        userInfo = userInfoManager.findLoginUserInfo(userInfoQuery);

        //从wx接口实时获取昵称
        WxUserInfo wxUserInfo = wxService.getWxApiUserInfo(wxAccessTokenService.getApiAccessToken().getAccessToken(), openId);
        if (null != wxUserInfo) {
            userInfo.setNickName(wxUserInfo.getNickname());
        }

        //设置缓存
        if (null != userInfo) {
            userInfoManager.setUserInfoCache(userInfo);
        }
        return userInfo;
    }

    @Override
    public Commission getUserCommission(String openId) {
        Commission query = new Commission();
        query.setOpenId(openId);
        Commission commission = commissionDao.findCommissionByOpenId(query);
        if (null == commission) {
            commission = new Commission();
            commission.setTotalCommission(0.0);
        }
        return commission;
    }


    @Override
    public Double getUserExtractCommission(String openId) {
        IncomeDetailQuery incomeDetailQuery = new IncomeDetailQuery();
        incomeDetailQuery.setOpenId(openId);
        incomeDetailQuery.setTypeList(EIncomeType.EXTRACT.getCode() + "");
        Double payment = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if(null!=payment){
            payment = new BigDecimal(payment+"").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();//保留一位小数
        }else{
            payment = 0.0;
        }
        return payment;
    }

    @Override
    public int findRecommendUserCount(RecommendUserInfoQuery recommendUserInfoQuery) {
        return recommendUserInfoDao.findRecommendUserCount(recommendUserInfoQuery);
    }

    @Override
    public PaginatedArrayList<UserInfoVo> getUserList(UserInfoQuery userInfoQuery) {
        PaginatedArrayList<UserInfoVo> result = new PaginatedArrayList<>(userInfoQuery.getIndex(), userInfoQuery.getPageSize());
        int totalItem = userInfoManager.findUserCount(userInfoQuery);
        if (totalItem > 0) {
            result.setTotalItem(totalItem);
            userInfoQuery.setStartRow(result.getStartRow());
            List<UserInfo> list = userInfoManager.findAllUsers(userInfoQuery);
            for (UserInfo userInfo : list) {
                UserInfoVo userInfoVo = new UserInfoVo(userInfo);
                userInfoVo.setWhiteAgent(userInfoManager.isWhiteAgent(userInfoVo.getSubUnionId()));
                userInfoVo.setSui(DESUtil.qsEncrypt(jDProperty.getEncryptKey(), userInfoVo.getSubUnionId(), "UTF-8"));
                //获取用户缓存，从缓存中获取最新昵称
                UserInfo userInfoCache = getUserInfo(userInfo.getOpenId());
                if (null != userInfoCache) {
                    userInfoVo.setNickName(userInfoCache.getNickName());
                }
                result.add(userInfoVo);
            }
        }
        return result;
    }

    @Override
    public PaginatedArrayList<AgentRelationVo> getAgentUserByParentId(AgentRelationQuery agentRelationQuery) {
        PaginatedArrayList<AgentRelationVo> result = new PaginatedArrayList<>(agentRelationQuery.getIndex(), agentRelationQuery.getPageSize());
        int totalItem = agentRelationDao.findCountByParentId(agentRelationQuery);
        if (totalItem > 0) {
            result.setTotalItem(totalItem);
            agentRelationQuery.setStartRow(result.getStartRow());
            List<AgentRelation> list = agentRelationDao.findByParentId(agentRelationQuery);
            for (AgentRelation agentRelation : list) {
                AgentRelationVo agentRelationVo = new AgentRelationVo(agentRelation);
                //查询用户信息
                UserInfo userInfoQuery = new UserInfo();
                userInfoQuery.setSubUnionId(agentRelation.getAgentSubUnionId());
                UserInfo userInfo = userInfoManager.findUserInfoBySubUnionId(userInfoQuery);
                agentRelationVo.setNickName(userInfo.getNickName());
                agentRelationVo.setImgUrl(userInfo.getWxImage());

                result.add(agentRelationVo);
            }
        }
        return result;
    }

    /**
     * 更新用户提现余额
     *
     * @param openId
     */
    @Override
    public void updateUserCommission(String openId) {
        //计算收入、支出
        IncomeDetailQuery incomeDetailQuery = new IncomeDetailQuery();
        incomeDetailQuery.setOpenId(openId);
        List<Integer> codes = new ArrayList<>();
        codes.add(EIncomeType.REGIST.getCode());
        codes.add(EIncomeType.FIRST_ORDER_REBATE.getCode());
        codes.add(EIncomeType.FIRST_AGENT_REBATE.getCode());
        codes.add(EIncomeType.SECOND_AGENT_REBATE.getCode());
        codes.add(EIncomeType.SECOND_ORDER_REBATE.getCode());
        codes.add(EIncomeType.GENERAL_ORDER_REBATE.getCode());
        incomeDetailQuery.setTypeList(Joiner.on(",").join(codes));
        Double income = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if (null == income) {
            income = 0.0;
        }

        incomeDetailQuery.setTypeList(EIncomeType.EXTRACT.getCode() + "");
        Double payment = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if (null == payment) {
            payment = 0.0;
        }

        Double totalCommission = income - payment;//收入减支出=余额
        totalCommission = new BigDecimal(totalCommission+"").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();//保留一位小数

        //更新用户提现余额
        Commission commission = new Commission();
        commission.setOpenId(openId);
        commission.setTotalCommission(totalCommission);
        Commission userCommission = commissionDao.findCommissionByOpenId(commission);

        if (null == userCommission) {
            commission.setStatus(0);
            commissionDao.insert(commission);
        } else {
            userCommission.setTotalCommission(totalCommission);
            commissionDao.updateTotalCommission(commission);

        }
    }
    //------------------------------private methods----------------------------------------

    /**
     * 添加推荐记录
     *
     * @param openId
     * @param recommendOpenId
     */
    private boolean addRecommendUser(String openId, String recommendOpenId) {
        boolean flag = false;
        if (StringUtils.isNotBlank(recommendOpenId) && !recommendOpenId.equalsIgnoreCase(openId)) {
            RecommendUserInfo recommendUserInfo = new RecommendUserInfo();
            recommendUserInfo.setOpenId(openId);
            recommendUserInfo.setRecommendAccount(recommendOpenId);
            recommendUserInfo.setStatus(0);

            RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
            recommendUserInfoQuery.setRecommendAccount(recommendOpenId);
            recommendUserInfoQuery.setOpenId(openId);
            RecommendUserInfo existsRecommendUserInfo = recommendUserInfoDao.findRecommendUserInfo(recommendUserInfoQuery);
            if (null == existsRecommendUserInfo) {
                recommendUserInfoDao.insert(recommendUserInfo);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 更新推荐记录
     *
     * @param openId
     * @param recommendOpenId
     */
    private void updateRecommendUser(String openId, String recommendOpenId) {
        if (StringUtils.isNotBlank(recommendOpenId) && !recommendOpenId.equalsIgnoreCase(openId)) {
            RecommendUserInfo recommendUserInfo = new RecommendUserInfo();
            recommendUserInfo.setOpenId(openId);
            recommendUserInfo.setRecommendAccount(recommendOpenId);
            recommendUserInfoDao.update(recommendUserInfo);
        }
    }

}

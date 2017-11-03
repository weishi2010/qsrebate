package com.rebate.service.userinfo.impl;

import com.jd.data.redis.RedisUtils;
import com.rebate.common.cache.RedisKey;
import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CommissionDao;
import com.rebate.dao.IncomeDetailDao;
import com.rebate.dao.RecommendUserInfoDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Commission;
import com.rebate.domain.RecommendUserInfo;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EIncomeType;
import com.rebate.domain.en.ESequence;
import com.rebate.domain.en.ESubUnionIdPrefix;
import com.rebate.domain.query.IncomeDetailQuery;
import com.rebate.domain.query.RecommendUserInfoQuery;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Qualifier("redisUtil")
    @Autowired(required = false)
    private RedisUtils redisUtil;

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

    @Override
    public void sysRecommendUser() {

        List<UserInfo> list = userInfoDao.findAllUsers(new UserInfo());
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

                userInfoDao.insert(userInfo);
            } catch (Exception e) {
                userInfo.setNickName(wxUserInfo.getOpenid());
                userInfoDao.insert(userInfo);
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
            updateRecommendUser(openId, recommendOpenId);
        } else {
            userInfo.setRecommendAccount("");
        }

        userInfoDao.update(userInfo);
    }

    @Override
    public UserInfo getUserInfo(String openId) {
        //从缓存获取用户信息
//        UserInfo userInfo = getUserInfoCache(openId);
//        if (null != userInfo) {
//            LOG.error("[getUserInfo]userInfo cache:" + JsonUtil.toJson(userInfo));
//            return userInfo;
//        }

        //从db查询
        UserInfo userInfoQuery = new UserInfo();
        userInfoQuery.setOpenId(openId);
        UserInfo userInfo = userInfoDao.findLoginUserInfo(userInfoQuery);

        //设置缓存
        if (null != userInfo) {
            setUserInfoCache(userInfo);
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
    public int findRecommendUserCount(RecommendUserInfoQuery recommendUserInfoQuery) {
        return recommendUserInfoDao.findRecommendUserCount(recommendUserInfoQuery);
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
        incomeDetailQuery.setTypeList(EIncomeType.REGIST.getCode() + ","
                + EIncomeType.FIRST_ORDER_REBATE.getCode() + "," + EIncomeType.FIRST_AGENT_REBATE.getCode()
                + EIncomeType.SECOND_ORDER_REBATE.getCode() + "," + EIncomeType.SECOND_AGENT_REBATE.getCode()
                + EIncomeType.GENERAL_ORDER_REBATE.getCode());
        Double income = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if (null == income) {
            income = 0.0;
        }

        incomeDetailQuery.setTypeList(EIncomeType.EXTRACT.getCode() + "");
        Double payment = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if (null == payment) {
            payment = 0.0;
        }

        Double totalCommission = income - payment;//收入减少余额
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
    private void addRecommendUser(String openId, String recommendOpenId) {
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
            }
        }
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

    /**
     * 查询用户缓存
     *
     * @param openId
     */
    private UserInfo getUserInfoCache(String openId) {
        UserInfo userInfo = null;
        try {
            //设置到缓存
            String json = redisUtil.get(RedisKey.USER_INFO.getPrefix(openId));
            if (StringUtils.isNotBlank(json)) {
                userInfo = JsonUtil.fromJson(json, UserInfo.class);
            }
        } catch (Exception e) {
            LOG.error("getUserInfoCache error!openId:{}", openId);
        }
        return userInfo;
    }

    /**
     * 设置缓存
     *
     * @param userInfo
     */
    private void setUserInfoCache(UserInfo userInfo) {
        try {
            //设置到缓存
            redisUtil.set(RedisKey.USER_INFO.getPrefix(userInfo.getOpenId()), RedisKey.USER_INFO.getTimeout(), JsonUtil.toJson(userInfo));
        } catch (Exception e) {
            LOG.error("setUserInfoCache error!userInfo:{}", JsonUtil.toJson(userInfo));
        }
    }
}

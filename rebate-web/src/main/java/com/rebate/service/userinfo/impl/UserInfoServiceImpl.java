package com.rebate.service.userinfo.impl;

import com.rebate.common.cache.RedisKey;
import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RedisUtil;
import com.rebate.dao.CommissionDao;
import com.rebate.dao.IncomeDetailDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Commission;
import com.rebate.domain.IncomeDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EAgent;
import com.rebate.domain.en.EIncomeType;
import com.rebate.domain.en.ESequence;
import com.rebate.domain.en.ESubUnionIdPrefix;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.IncomeDetailQuery;
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
    private RedisUtil redisUtil;

    @Qualifier("wxService")
    @Autowired(required = false)
    private WxService wxService;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = false)
    private WxAccessTokenService wxAccessTokenService;


    @Qualifier("sequenceUtil")
    @Autowired(required = true)
    private SequenceUtil sequenceUtil;


    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Override
    public UserInfo registUserInfo(String openId, Integer angent, boolean isAward) {
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
            userInfo.setRecommendAccount("");
            try{

                userInfoDao.insert(userInfo);
            }catch (Exception e){
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
    public void updateUserInfoAgent(String openId,Integer agent){
        UserInfo  userInfo = new UserInfo();
        userInfo.setOpenId(openId);
        userInfo.setAgent(agent);
        userInfoDao.update(userInfo);
    }

    @Override
    public UserInfo getUserInfo(String openId) {
        //从缓存获取用户信息
        UserInfo userInfo = getUserInfoCache(openId);
        if (null != userInfo) {
            LOG.error("[getUserInfo]userInfo cache:" + JsonUtil.toJson(userInfo));
            return userInfo;
        }

        //从db查询
        UserInfo userInfoQuery = new UserInfo();
        userInfoQuery.setOpenId(openId);
        userInfo = userInfoDao.findLoginUserInfo(userInfoQuery);

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
                + EIncomeType.FIRST_ORDER_REBATE.getCode()+ "," + EIncomeType.FIRST_AGENT_REBATE.getCode()
                + EIncomeType.SECOND_ORDER_REBATE.getCode()+ "," + EIncomeType.SECOND_AGENT_REBATE.getCode()
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
            redisUtil.set(RedisKey.USER_INFO.getPrefix(userInfo.getOpenId()), JsonUtil.toJson(userInfo), RedisKey.USER_INFO.getTimeout());
        } catch (Exception e) {
            LOG.error("setUserInfoCache error!userInfo:{}", JsonUtil.toJson(userInfo));
        }
    }
}

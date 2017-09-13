package com.rebate.service.userinfo.impl;

import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RedisUtil;
import com.rebate.dao.CommissionDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;
import com.rebate.service.userinfo.UserInfoService;
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

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Qualifier("redisUtil")
    @Autowired(required = false)
    private RedisUtil redisUtil;

    @Override
    public void registUserInfo(UserInfo userInfo) {
        userInfoDao.insert(userInfo);
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

    //------------------------------private methods----------------------------------------

    /**
     * 查询用户缓存
     *
     * @param openId
     */
    private UserInfo getUserInfoCache(String openId) {
        UserInfo userInfo = null;
        //设置到缓存
        String json = redisUtil.get(RedisKey.USER_INFO.getPrefix(openId));
        if (StringUtils.isNotBlank(json)) {
            userInfo = JsonUtil.fromJson(json, UserInfo.class);
        }
        return userInfo;
    }

    /**
     * 设置缓存
     *
     * @param userInfo
     */
    private void setUserInfoCache(UserInfo userInfo) {
        //设置到缓存
        redisUtil.set(RedisKey.USER_INFO.getPrefix(userInfo.getOpenId()), JsonUtil.toJson(userInfo), RedisKey.USER_INFO.getTimeout());
    }
}

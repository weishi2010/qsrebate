package com.rebate.manager.userinfo.impl;

import com.jd.data.redis.RedisUtils;
import com.jd.data.redis.connection.RedisAccessException;
import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.UserInfoDao;
import com.rebate.dao.WhiteUserInfoDao;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EWhiteType;
import com.rebate.domain.query.UserInfoQuery;
import com.rebate.domain.whitelist.WhiteUserInfo;
import com.rebate.manager.userinfo.UserInfoManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userInfoManager")
public class UserInfoManagerImpl implements UserInfoManager {
    private static final Logger LOG = LoggerFactory.getLogger(UserInfoManagerImpl.class);

    @Qualifier("redisUtil")
    @Autowired(required = false)
    private RedisUtils redisUtil;

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Qualifier("whiteUserInfoDao")
    @Autowired
    private WhiteUserInfoDao whiteUserInfoDao;

    @Override
    public void addWhiteAgent(String subUnionId) {
        WhiteUserInfo whiteUserInfo = new WhiteUserInfo();
        whiteUserInfo.setStatus(0);
        whiteUserInfo.setType(EWhiteType.WHITE_AGENT.getCode());
        whiteUserInfo.setSubUnionId(subUnionId);
        WhiteUserInfo existsWhiteUserInfo = whiteUserInfoDao.findBySubUnionId(whiteUserInfo);
        if (null == existsWhiteUserInfo) {
            whiteUserInfoDao.insert(whiteUserInfo);
            //添加到缓存
            addWhiteList(subUnionId);
        }
    }

    @Override
    public void cancelWhiteAgent(String subUnionId) {
        WhiteUserInfo whiteUserInfo = new WhiteUserInfo();
        whiteUserInfo.setSubUnionId(subUnionId);
        whiteUserInfo.setType(EWhiteType.WHITE_AGENT.getCode());
        whiteUserInfoDao.deleteBySubUnionId(whiteUserInfo);
        //从缓存中删除
        deleteWhiteList(subUnionId);
    }


    @Override
    public boolean isWhiteAgent(String subUnionId) {
        boolean ret = false;
        try {
            ret = isInWhiteList(subUnionId);
        } catch (Exception e) {
            LOG.error("isWhiteAgent cache error!subUnionId:" + subUnionId, e);
            //如果缓存异常则从db中查询是否在黑名单
            WhiteUserInfo whiteUserInfo = new WhiteUserInfo();
            whiteUserInfo.setType(EWhiteType.WHITE_AGENT.getCode());
            whiteUserInfo.setSubUnionId(subUnionId);
            WhiteUserInfo existsWhiteUserInfo = whiteUserInfoDao.findBySubUnionId(whiteUserInfo);
            if (null != existsWhiteUserInfo) {
                ret = true;
            }

        }

        return ret;
    }


    @Override
    public long insert(UserInfo userInfo) {
        return userInfoDao.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoDao.update(userInfo);
        //清除缓存
        delUserInfoCache(userInfo.getOpenId());
    }

    @Override
    public UserInfo findLoginUserInfo(UserInfo userInfo) {
        return userInfoDao.findLoginUserInfo(userInfo);
    }

    @Override
    public UserInfo findUserInfoBySubUnionId(UserInfo userInfo) {
        return userInfoDao.findUserInfoBySubUnionId(userInfo);
    }

    @Override
    public int findRecommendUserCount(UserInfoQuery userInfoQuery) {
        return userInfoDao.findRecommendUserCount(userInfoQuery);
    }

    @Override
    public int findUserCount(UserInfoQuery userInfoQuery) {
        return userInfoDao.findUserCount(userInfoQuery);
    }

    @Override
    public List<UserInfo> findAllUsers(UserInfoQuery userInfoQuery) {
        return userInfoDao.findAllUsers(userInfoQuery);
    }
    /**
     * 查询用户缓存
     *
     * @param openId
     */
    @Override
    public UserInfo getUserInfoCache(String openId) {
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
    @Override
    public void setUserInfoCache(UserInfo userInfo) {
        try {
            //设置到缓存
            redisUtil.set(RedisKey.USER_INFO.getPrefix(userInfo.getOpenId()), RedisKey.USER_INFO.getTimeout(), JsonUtil.toJson(userInfo));
        } catch (Exception e) {
            LOG.error("setUserInfoCache error!userInfo:{}", JsonUtil.toJson(userInfo));
        }
    }

    /**
     * 设置缓存
     *
     * @param openId
     */
    @Override
    public void delUserInfoCache(String openId) {
        try {
            //设置到缓存
            redisUtil.del(RedisKey.USER_INFO.getPrefix(openId));
        } catch (Exception e) {
            LOG.error("delUserInfoCache error!openId:{}", openId);
        }
    }

    //----------------------------private methods-------------------------------------------

    private void addWhiteList(String subUnionId) {
        String key = RedisKey.JD_WHITE_LIST.getPrefix("");
        try {
            redisUtil.zadd(key, 0.0, subUnionId);
        } catch (RedisAccessException e) {
            e.printStackTrace();
        }
    }

    private void deleteWhiteList(String subUnionId) {
        String key = RedisKey.JD_WHITE_LIST.getPrefix("");
        try {
            redisUtil.zrem(key, subUnionId);
        } catch (RedisAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否白名单
     * @param subUnionId
     * @return
     */
    private boolean isInWhiteList(String subUnionId) {
        boolean ret = false;
        String key = RedisKey.JD_WHITE_LIST.getPrefix("");
        try {
            Long rank = redisUtil.zrank(key, subUnionId);
            if (null != rank) {
                ret = true;
            }
        } catch (RedisAccessException e) {
           throw new RuntimeException(e);
        }
        return ret;
    }
}

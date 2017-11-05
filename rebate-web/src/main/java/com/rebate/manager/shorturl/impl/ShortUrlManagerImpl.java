package com.rebate.manager.shorturl.impl;

import com.jd.data.redis.RedisUtils;
import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.dao.UserSummaryDao;
import com.rebate.domain.UserSummary;
import com.rebate.domain.property.JDProperty;
import com.rebate.manager.shorturl.ShortUrlManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("shortUrlManager")
public class ShortUrlManagerImpl implements ShortUrlManager {
    private static final Logger LOG = LoggerFactory.getLogger(ShortUrlManagerImpl.class);


    @Qualifier("rebateUrlUtil")
    @Autowired(required = true)
    private RebateUrlUtil rebateUrlUtil;

    @Qualifier("redisUtil")
    @Autowired(required = true)
    private RedisUtils redisUtil;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Autowired
    private UserSummaryDao userSummaryDao;

    @Override
    public String getQsShortPromotinUrl(String jdUnionUrl, String subUnionId) {
        String subUnionIdEncrpyt = DESUtil.encrypt(jDProperty.getEncryptKey(), subUnionId, "UTF-8");
        return rebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(jdUnionUrl, subUnionIdEncrpyt);
    }

    @Override
    public void incrJDUnionUrlClick(String subUnionIdEncrypt) {

        try{

            subUnionIdEncrypt = subUnionIdEncrypt.replace(" ","").replace("","");
            //按子联盟ID进行统计
            String subUnionId = DESUtil.decrypt(jDProperty.getEncryptKey(), subUnionIdEncrypt, "UTF-8");

            //写入缓存
            incrCacheUserClick(subUnionId);

            //写入数据库
            incrDbUserClick(subUnionId);
        }catch (Exception e){
            LOG.error("incrJDUnionUrlClick error!subUnionIdEncrypt:"+subUnionIdEncrypt,e);
        }

    }

    @Override
    public Long getJDUnionUrlClick(String subUnionId, Date date) {

        Long clickCount = getCacheClickCount(subUnionId, date);
        if (null == clickCount) {
            clickCount = (long) getDbClickCount(subUnionId, date);
        }

        return clickCount;
    }

    private int getDbClickCount(String subUnionId, Date date) {
        int clickCount = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            UserSummary userSummaryQuery = new UserSummary();
            userSummaryQuery.setSubUnionId(subUnionId);
            userSummaryQuery.setOpDate(format.parse(format.format(date)));
            UserSummary userSummary = userSummaryDao.findUserSummary(userSummaryQuery);
            if (null != userSummary) {
                clickCount = userSummary.getClickCount();
            }
        } catch (Exception e) {
            LOG.error("getJDUnionUrlClick error!", e);
        }
        return clickCount;
    }


    private Long getCacheClickCount(String subUnionId, Date date) {
        Long clickCount = null;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String day = format.format(date);//获取时间，按天统计
            String key = RedisKey.JD_UNION_URL_CLICK.getPrefix(day + subUnionId);
            String value = redisUtil.get(key);
            if (StringUtils.isNotBlank(value)) {
                clickCount = Long.parseLong(value);
            }
        } catch (Exception e) {
            LOG.error("getJDUnionUrlClick error!", e);
        }
        return clickCount;
    }

    @Override
    public Long getALLJDUnionUrlClick(Date date) {
        Long clickCount = 0l;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String day = format.format(date);//获取时间，按天统计
            //统计全站的点击
            String qsAllClickKey = RedisKey.JD_UNION_URL_CLICK.getPrefix(day + "ALL");
            String value = redisUtil.get(qsAllClickKey);
            if (StringUtils.isNotBlank(value)) {
                clickCount = Long.parseLong(value);
            }
        } catch (Exception e) {
            LOG.error("getALLJDUnionUrlClick error!", e);
        }
        return clickCount;
    }


    public void incrCacheUserClick(String subUnionId) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String day = format.format(new Date());//获取时间，按天统计


            String key = RedisKey.JD_UNION_URL_CLICK.getPrefix(day + subUnionId);
            long clickCount = redisUtil.incr(key);

            //统计全站的点击
            String qsAllClickKey = RedisKey.JD_UNION_URL_CLICK.getPrefix(day + "ALL");
            long allClickCount = redisUtil.incr(qsAllClickKey);
            LOG.error("incrCacheUserClick count:{},subUnionId:" + subUnionId + ",allClickCount:" + allClickCount, clickCount);
        } catch (Exception e) {
            LOG.error("incrJDUnionUrlClick error!:subUnionId:" + subUnionId, e);
        }
    }

    /**
     * 更新数据库点击
     * @param subUnionId
     */
    public void incrDbUserClick(String subUnionId) {
        //更新全站点击
        updateUserSummary("ALL");
        //更新子联盟ID的点击
        updateUserSummary(subUnionId);

    }

    /**
     * 更新用户点击统计
     * @param subUnionId
     */
    private void updateUserSummary(String subUnionId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            UserSummary userSummary = new UserSummary();
            userSummary.setSubUnionId(subUnionId);
            userSummary.setOpDate(sdf.parse(sdf.format(new Date())));

            if (null == userSummaryDao.findUserSummary(userSummary)) {
                userSummary.setClickCount(1);
                userSummaryDao.insert(userSummary);
            } else {
                userSummaryDao.incrUserClick(userSummary);
            }
        } catch (ParseException e) {
            LOG.error("updateUserSummary error!:subUnionId:" + subUnionId, e);
        }
    }

}

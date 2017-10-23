package com.rebate.manager.shorturl.impl;

import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.RedisUtil;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.domain.property.JDProperty;
import com.rebate.manager.shorturl.ShortUrlManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
    private RedisUtil redisUtil;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Override
    public String getQsShortPromotinUrl(String jdUnionUrl, String subUnionId) {
        String subUnionIdEncrpyt = DESUtil.encrypt(jDProperty.getEncryptKey(), subUnionId, "UTF-8");
        return rebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(jdUnionUrl, subUnionIdEncrpyt);
    }

    @Override
    public void incrJDUnionUrlClick(String subUnionIdEncrypt) {
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String day = format.format(new Date());//获取时间，按天统计

            //按子联盟ID进行统计
            String subUnionId = DESUtil.decrypt(jDProperty.getEncryptKey(), subUnionIdEncrypt, "UTF-8");
            String key = RedisKey.JD_UNION_URL_CLICK.getPrefix(day+subUnionId);
            redisUtil.incr(key);

            //统计全站的点击
            String qsAllClickKey = RedisKey.JD_UNION_URL_CLICK.getPrefix(day+"ALL");
            redisUtil.incr(qsAllClickKey);
        } catch (Exception e) {
            LOG.error("incrJDUnionUrlClick error!", e);
        }
    }

    @Override
    public Long getJDUnionUrlClick(String subUnionId,Date date) {
        Long clickCount = 0l;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String day = format.format(date);//获取时间，按天统计
            String key = RedisKey.JD_UNION_URL_CLICK.getPrefix(day+subUnionId);
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
            String qsAllClickKey = RedisKey.JD_UNION_URL_CLICK.getPrefix(day+"ALL");
            String value = redisUtil.get(qsAllClickKey);
            if (StringUtils.isNotBlank(value)) {
                clickCount = Long.parseLong(value);
            }
        } catch (Exception e) {
            LOG.error("getALLJDUnionUrlClick error!", e);
        }
        return clickCount;
    }

}

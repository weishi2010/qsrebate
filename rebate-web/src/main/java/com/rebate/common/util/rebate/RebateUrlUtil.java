package com.rebate.common.util.rebate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL工具处理类
 */
public class RebateUrlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(RebateUrlUtil.class);

    /**
     * JD推广短链接转平台链接
     * @param jdPromotionUrl
     * @return
     */
    public static String jdPromotionUrlToQsrebateShortUrl(String jdPromotionUrl){
        String url = jdPromotionUrl;//默认
        try{
            url =  jdPromotionUrl.replace("https://union-click.jd.com/jdc?","http://m.qingsongfan.com/qsc/index?");
        }catch (Exception e){
            LOG.error("jdPromotionUrlToQsrebateShortUrl error!url:"+jdPromotionUrl,e);
        }
        return url;
    }
}

package com.rebate.common.util.rebate;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.des.DESUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL工具处理类
 */
public class RebateUrlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(RebateUrlUtil.class);

    @Getter
    @Setter
    private String jdUnionUrlPrefix;
    @Getter
    @Setter
    private String rebateUrlPrefix;

    /**
     * JD推广短链接转平台链接
     * @param jdPromotionUrl
     * @return
     */
    public String jdPromotionUrlToQsrebateShortUrl(String jdPromotionUrl,String subUnionIdEncrpyt){
        String url = jdPromotionUrl;//默认
        try{
            url =  jdPromotionUrl.replace(jdUnionUrlPrefix,rebateUrlPrefix);
            url = url+"&sui="+subUnionIdEncrpyt;
        }catch (Exception e){
            LOG.error("jdPromotionUrlToQsrebateShortUrl error!url:"+jdPromotionUrl,e);
        }
        return url;
    }

}

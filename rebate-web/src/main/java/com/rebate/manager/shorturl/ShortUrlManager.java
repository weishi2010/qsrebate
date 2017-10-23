package com.rebate.manager.shorturl;

import java.util.Date;

public interface ShortUrlManager {

    /**
     * 获取轻松返带参跳转链接
     * 说明：推广链接转为本平台跳转链接，带上子联盟账号加密参数，便于用户访问时进行点击统计
     * @param jdUnionUrl
     * @param subUnionId
     * @return
     */
    String getQsShortPromotinUrl(String jdUnionUrl,String subUnionId);

    /**
     * 链接点击统计
     * @param subUnionIdMd5
     */
    void incrJDUnionUrlClick(String subUnionIdMd5);

    /**
     * 按天获取点击
     * @param subUnionId
     * @param day
     * @return
     */
    Long getJDUnionUrlClick(String subUnionId,Date day);

    /**
     * 按天获取全部点击
     * @param date
     * @return
     */
    Long getALLJDUnionUrlClick(Date date);
}

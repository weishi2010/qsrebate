package com.rebate.service.wx;

public interface WxService {
    /**
     * 获取短链接
     *
     * @return
     */
    String getShortUrl(String longUrl);

}

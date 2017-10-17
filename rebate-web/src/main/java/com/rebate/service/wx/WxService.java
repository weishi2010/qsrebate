package com.rebate.service.wx;

import com.rebate.domain.wx.WxUserInfo;

public interface WxService {

    /**
     * 根据accessToken及openid获取用户信息
     * @param accessToken
     * @param openId
     * @return
     */
    WxUserInfo getWxApiUserInfo(String accessToken, String openId);

    /**
     * 获取短链接
     *
     * @return
     */
    String getShortUrl(String longUrl);

}

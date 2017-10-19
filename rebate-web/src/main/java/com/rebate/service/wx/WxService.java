package com.rebate.service.wx;

import com.rebate.domain.wx.WxUserInfo;

import java.util.List;

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

    /**
     * 获取二维码链接
     * @param paramJson
     * @return
     */
    String getQrcodeUrl(String paramJson);

    /**
     * 向订阅用户发送消息
     * @param opendIdList
     * @param content
     * @return
     */
    String sendMessage(List<String> opendIdList, String content);
}

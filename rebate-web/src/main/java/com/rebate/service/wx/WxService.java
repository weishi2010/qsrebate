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
     * 获取微信图片素材mediaId
     * 说明：获取文件流后上传到微信接口获取mediaId
     * @param imgUrl
     * @return
     */
    String getWxImageMediaId(String imgUrl);

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
     * @param openId
     * @param content
     * @return
     */
    String sendMessage(String openId, String content);

    /**
     * 发送图片消息
     * @param openId
     * @param mediaId
     * @return
     */
    String sendImageMessage(String openId,String mediaId);
}

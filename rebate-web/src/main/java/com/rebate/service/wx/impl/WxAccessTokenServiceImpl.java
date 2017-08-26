/*
 * Copyright (c) 2015, www.jd.com. All rights reserved.
 *
 * 警告：本计算机程序受著作权法和国际公约的保护，未经授权擅自复制或散布本程序的部分或全部、以及其他
 * 任何侵害著作权人权益的行为，将承受严厉的民事和刑事处罚，对已知的违反者将给予法律范围内的全面制裁。
 */

package com.rebate.service.wx.impl;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.wx.AuthorizationCodeInfo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.wx.WxAccessTokenService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("wxAccessTokenService")
public class WxAccessTokenServiceImpl implements WxAccessTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(WxAccessTokenServiceImpl.class);


    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxConfig")
    @Autowired(required = true)
    private WxConfig wxConfig;

    @Override
    public AuthorizationCodeInfo getLoginAccessToken(String loginCode) {
        AuthorizationCodeInfo authorizationCodeInfo = null;
        try {
            //获取accessToken
             authorizationCodeInfo  = getWxLoginAccessToken(loginCode);
        } catch (Exception e) {
            LOG.error("获取redis微信token",e);
        }
        return authorizationCodeInfo;
    }

    /**
     * 获取微信accesstoken
     * @return
     */
    private AuthorizationCodeInfo getWxLoginAccessToken(String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", wxConfig.getAppId());
        params.put("secret", wxConfig.getAppSecret());
        params.put("code",code);
        params.put("grant_type", "authorization_code");

        String json = HttpClientUtil.get(wxConfig.getAccessTokenUrl(), params);
        AuthorizationCodeInfo authorizationCodeInfo = null;
        if (json.contains("access_token")) {
             authorizationCodeInfo = JsonUtil.fromJson(json,AuthorizationCodeInfo.class);
        } else {
            LOG.error("get access_token error!json:" +json);
        }
        return authorizationCodeInfo;
    }



    @Override
    public WxUserInfo getWxUserInfo(String code) {
        WxUserInfo wxUserInfo = null;
        AuthorizationCodeInfo authorizationCodeInfo = getLoginAccessToken(code);

        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", authorizationCodeInfo.getAccessToken());
        params.put("openid",authorizationCodeInfo.getOpenId());

        String json = HttpClientUtil.get(wxConfig.getUserInfoUrl(), params);
        if (json.contains("openid")) {
            wxUserInfo = JsonUtil.fromJson(json,WxUserInfo.class);
        }
        return wxUserInfo;
    }

    @Override
    public String getTicket() {
        String jsapiTicket = "";

        //TODO 获取普通API accessToken
        String accessToken = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", accessToken);
        params.put("type", "jsapi");

        JSONObject obj = HttpClientUtil.getWithJsonObj(wxConfig.getJsapiTicketUrl(), params);
        if ("0".equalsIgnoreCase(obj.get("errcode").toString())) {
            jsapiTicket = obj.get("ticket").toString();
        } else {
            LOG.error("get jsapiTicket error!errmsg:" + obj.get("errmsg").toString());
        }
        return jsapiTicket;
    }

}
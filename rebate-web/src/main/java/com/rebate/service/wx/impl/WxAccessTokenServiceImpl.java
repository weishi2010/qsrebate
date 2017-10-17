/*
 * Copyright (c) 2015, www.jd.com. All rights reserved.
 *
 * 警告：本计算机程序受著作权法和国际公约的保护，未经授权擅自复制或散布本程序的部分或全部、以及其他
 * 任何侵害著作权人权益的行为，将承受严厉的民事和刑事处罚，对已知的违反者将给予法律范围内的全面制裁。
 */

package com.rebate.service.wx.impl;

import com.rebate.common.cache.RedisKey;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RedisUtil;
import com.rebate.domain.wx.ApiAccessToken;
import com.rebate.domain.wx.AuthorizationCodeInfo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.wx.WxAccessTokenService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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

    @Qualifier("redisUtil")
    @Autowired(required = false)
    private RedisUtil redisUtil;


    @Override
    public AuthorizationCodeInfo getLoginAccessToken(String loginCode) {
        AuthorizationCodeInfo authorizationCodeInfo = null;
        try {
            //获取accessToken
            authorizationCodeInfo = getWxLoginAccessToken(loginCode);
        } catch (Exception e) {
            LOG.error("[getLoginAccessToken]获取redis微信token异常!loginCode:" + loginCode, e);
        }
        return authorizationCodeInfo;
    }

    /**
     * 获取微信accesstoken
     *
     * @return
     */
    private AuthorizationCodeInfo getWxLoginAccessToken(String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", wxConfig.getAppId());
        params.put("secret", wxConfig.getAppSecret());
        params.put("code", code);
        params.put("grant_type", "authorization_code");

        String json = HttpClientUtil.get(wxConfig.getLoginAccessTokenUrl() + "?appid=" + wxConfig.getAppId() + "&secret=" + wxConfig.getAppSecret() + "&code=" + code + "&grant_type=authorization_code");
        LOG.error("[getWxLoginAccessToken]===================>json:" + json);
        AuthorizationCodeInfo authorizationCodeInfo = null;
        if (json.contains("access_token")) {
            authorizationCodeInfo = JsonUtil.fromJson(json, AuthorizationCodeInfo.class);
        } else {
            LOG.error("get access_token error!json:{},code:{}", json, code);
        }
        return authorizationCodeInfo;
    }


    @Override
    public WxUserInfo getWxUserInfo(String accessToken, String openId) {
        WxUserInfo wxUserInfo = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("access_token", accessToken);
            params.put("openid", openId);

            String json = HttpClientUtil.get(wxConfig.getUserInfoUrl(), params);

            json = new String(json.getBytes("ISO-8859-1"), "UTF-8");
            LOG.error("[getWxUserInfo]json:"+json);

            if (json.contains("openid")) {
                wxUserInfo = JsonUtil.fromJson(json, WxUserInfo.class);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return wxUserInfo;
    }

    @Override
    public ApiAccessToken getApiAccessToken() {
        ApiAccessToken apiAccessToken = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credential");
        params.put("appid", wxConfig.getAppId());
        params.put("secret", wxConfig.getAppSecret());

        String json = HttpClientUtil.get(wxConfig.getApiAccessTokenUrl(), params);

        if (json.contains("access_token")) {
            apiAccessToken = JsonUtil.fromJson(json, ApiAccessToken.class);
        }
        return apiAccessToken;
    }

    @Override
    public String getTicket() {
        String jsapiTicket = "";

        String accessToken = "";
//                //从缓存获取
//        String accessToken = redisUtil.get(RedisKey.WX_API_ACCESSTOKEN.getKey());
//
//        if(StringUtils.isNotBlank(accessToken)){
//            return accessToken;
//        }

        //获取普通API accessToken
        ApiAccessToken apiAccessToken = getApiAccessToken();
        if (null != apiAccessToken) {
            accessToken = apiAccessToken.getAccessToken();
            int timeOut = Integer.parseInt(apiAccessToken.getExpiresIn());
            redisUtil.set(RedisKey.WX_API_ACCESSTOKEN.getKey(), accessToken, timeOut);
        }

        if (StringUtils.isNotBlank(accessToken)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("access_token", accessToken);
            params.put("type", "jsapi");

            JSONObject obj = HttpClientUtil.getWithJsonObj(wxConfig.getJsapiTicketUrl(), params);
            if ("0".equalsIgnoreCase(obj.get("errcode").toString())) {
                jsapiTicket = obj.get("ticket").toString();
            } else {
                LOG.error("get jsapiTicket error!errmsg:" + obj.get("errmsg").toString());
            }
        }
        return jsapiTicket;
    }

}
package com.rebate.service.wx.impl;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service("wxService")
public class WxServiceImpl implements WxService {

    private static Logger LOG = LoggerFactory.getLogger(WxServiceImpl.class);


    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxConfig")
    @Autowired(required = true)
    private WxConfig wxConfig;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = true)
    private WxAccessTokenService wxAccessTokenService;


    @Override
    public WxUserInfo getWxApiUserInfo(String accessToken, String openId) {
        WxUserInfo wxUserInfo = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("access_token", accessToken);
            params.put("openid", openId);
            params.put("lang", "zh_CN");
            String json = HttpClientUtil.get(wxConfig.getUserInfoApiUrl(), params);

            json = new String(json.getBytes("ISO-8859-1"), "UTF-8");
            LOG.error("[getWxApiUserInfo]json:"+json);

            if (json.contains("openid")) {
                wxUserInfo = JsonUtil.fromJson(json, WxUserInfo.class);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return wxUserInfo;
    }

    @Override
    public String getShortUrl(String longUrl) {
        String shortUrl = longUrl;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("access_token", wxAccessTokenService.getApiAccessToken().getAccessToken());
            params.put("long_url", longUrl);
            params.put("action", "long2short");

            String json = HttpClientUtil.post(wxConfig.getShortApiUrl()+"?access_token="+wxAccessTokenService.getApiAccessToken().getAccessToken(), params);
            LOG.error("getShortUrl json:{},params:{}",json,JsonUtil.toJson(params));
            Map map = JsonUtil.fromJson(json, Map.class);
            if (map.containsKey("errcode") && "0".equalsIgnoreCase(map.get("errcode").toString())) {
                shortUrl = map.get("short_url").toString();
            }
        } catch (Exception e) {
            LOG.error("getShortUrl error!longUrl:"+longUrl, e);

        }
        return shortUrl;
    }
}
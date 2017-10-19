package com.rebate.service.wx.impl;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxService;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
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
            LOG.error("[getWxApiUserInfo]json:" + json);

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

            String json = HttpClientUtil.post(wxConfig.getShortApiUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken(), params);
            LOG.error("getShortUrl json:{},params:{}", json, JsonUtil.toJson(params));
            Map map = JsonUtil.fromJson(json, Map.class);
            if (map.containsKey("errcode") && "0".equalsIgnoreCase(map.get("errcode").toString())) {
                shortUrl = map.get("short_url").toString();
            }
        } catch (Exception e) {
            LOG.error("getShortUrl error!longUrl:" + longUrl, e);

        }
        return shortUrl;
    }

    @Override
    public String getQrcodeUrl(String paramJson) {
        String qrcodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
        String ticket = "";
        long expireSeconds = 0l;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("expire_seconds", 2592000);
            params.put("action_name", "QR_STR_SCENE");
            params.put("action_info", " {\"scene\": {\"scene_str\": \"" + paramJson + "\"}}");

            String json = HttpClientUtil.post(wxConfig.getQrcodeUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken(), params);
            LOG.error("getQrcodeUrl json:{},params:{}", json, JsonUtil.toJson(params));
            Map map = JsonUtil.fromJson(json, Map.class);
            if (map.containsKey("url") && map.containsKey("ticket")) {
                String url = map.get("url").toString();
                ticket = map.get("ticket").toString();
                expireSeconds = Long.parseLong(map.get("expire_seconds").toString());
                qrcodeUrl = qrcodeUrl + URLEncoder.encode(ticket, "UTF-8");
            }
        } catch (Exception e) {
            LOG.error("getQrcodeUrl error!paramJson:" + paramJson, e);
        }
        return qrcodeUrl;
    }

    @Override
    public String sendMessage(List<String> opendIdList,String content) {
        String result = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("touser", opendIdList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("media_id",content);
            params.put("mpnews", jsonObject);
            params.put("msgtype", "mpnews");

            String json = HttpClientUtil.post(wxConfig.getSendMessageUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken(), params);
            LOG.error("sendMessage json:{},params:{}", json, JsonUtil.toJson(params));
            Map map = JsonUtil.fromJson(json, Map.class);
            result = json;
        } catch (Exception e) {
            LOG.error("sendMessage error!opendIdList:" + opendIdList, e);
        }
        return result;
    }
}

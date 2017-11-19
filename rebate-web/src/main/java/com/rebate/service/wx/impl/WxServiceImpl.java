package com.rebate.service.wx.impl;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.domain.wx.output.Media;
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

            if (json.contains("openid")) {
                wxUserInfo = JsonUtil.fromJson(json, WxUserInfo.class);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return wxUserInfo;
    }

    @Override
    public String getWxImageMediaId(String imgUrl){
        String mediaId = "";
        try{
            //获取文件流
            byte[] fileBytes = HttpClientUtil.downloadImage(imgUrl.replace("/jfs/","/s800x800_jfs/"));
            //获取文件名
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/"));
            //获取api accessToken
            String accessToken = wxAccessTokenService.getApiAccessToken().getAccessToken();
            //通过微信上传接口上传
            String type = "image";
            String resultJson = HttpClientUtil.uploadImage(wxConfig.getUploadUrl()+"?access_token="+accessToken+"&type="+type,fileBytes,fileName);
            Map map = JsonUtil.fromJson(resultJson,Map.class);
            if(null!=map&& map.containsKey("media_id")){
                mediaId = map.get("media_id").toString();
                LOG.error("getWxImageMediaId imgUrl:{},mediaId:{}",imgUrl,mediaId);
            }else{
                LOG.error("getWxImageMediaId error!resultJson:{}",resultJson);
            }
        }catch (Exception e){
            LOG.error("getWxImageMediaId error!imgUrl:{}",imgUrl,e);
        }
        return mediaId;
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
            params.put("expire_seconds", 60*60*24*30);//30天有效
            params.put("action_name", "QR_STR_SCENE");

            JSONObject sceneStr = new JSONObject();
            sceneStr.put("scene_str",paramJson);
            JSONObject actionInfo = new JSONObject();
            actionInfo.put("scene",sceneStr);

            params.put("action_info", actionInfo);

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
    public String sendMessage(String openId,String content) {
        String result = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("touser", openId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content",content);

            params.put("text", jsonObject);
            params.put("msgtype", "text");
            String paramJson = JsonUtil.toJson(params);

            paramJson = new String(paramJson.getBytes("UTF-8"), "ISO-8859-1");//参数转码
            String json = HttpClientUtil.post(wxConfig.getSendMessageUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken(), paramJson,"UTF-8");
            Map map = JsonUtil.fromJson(json, Map.class);
            result = json;
        } catch (Exception e) {
            LOG.error("sendMessage error!openId:" + openId, e);
        }
        return result;
    }
    @Override
    public String sendImageMessage(String openId,String mediaId) {
        String result = "";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("touser", openId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("media_id",mediaId);

            params.put("image", jsonObject);
            params.put("msgtype", "image");
            String paramJson = JsonUtil.toJson(params);

            paramJson = new String(paramJson.getBytes("UTF-8"), "ISO-8859-1");//参数转码
            String json = HttpClientUtil.post(wxConfig.getSendMessageUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken(), paramJson,"UTF-8");
            LOG.error("sendImageMessage error!json:" + json);
            Map map = JsonUtil.fromJson(json, Map.class);

            result = json;
        } catch (Exception e) {
            LOG.error("sendImageMessage error!openId:" + openId, e);
        }
        return result;
    }
}

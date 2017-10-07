package com.rebate.service.wx.impl;

import com.rebate.common.data.json.JsonDataReader;
import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.domain.wx.WxConfig;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("wxMenuService")
public class WxMenuServiceImpl implements WxMenuService {
    private static Logger LOG = LoggerFactory.getLogger(WxMenuServiceImpl.class);

    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxConfig")
    @Autowired(required = true)
    private WxConfig wxConfig;

    @Qualifier("jsonDataReader")
    @Autowired(required = true)
    private JsonDataReader jsonDataReader;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = true)
    private WxAccessTokenService wxAccessTokenService;

    @Override
    public String createMenu() {
        String result = "";
        try {
            String paramJson = jsonDataReader.get("wxmenu.json");
            paramJson = new String(paramJson.getBytes("UTF-8"), "ISO-8859-1");//参数转码
            String url = wxConfig.getMenuCreateUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken();
            result = HttpClientUtil.post(url, paramJson, "UTF-8");

        } catch (Exception e) {
            LOG.error("createMenu error!", e);
        }
        return result;
    }

    @Override
    public String deleteMenu() {
        String result = "";
        try {
            String url = wxConfig.getMenuDeleteUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken();
            result = HttpClientUtil.get(url);
        } catch (Exception e) {
            LOG.error("deleteMenu error!", e);
        }
        return result;
    }

    @Override
    public String getMenu() {
        String result = "";
        try {
            String url = wxConfig.getMenuGetUrl() + "?access_token=" + wxAccessTokenService.getApiAccessToken().getAccessToken();
            result = HttpClientUtil.get(url);
            result = new String(result.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            LOG.error("getMenu error!", e);
        }
        return result;
    }
}

package com.rebate.service.wx.impl;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.domain.wx.WxConfig;
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

    @Override
    public boolean createMenu() {
        boolean result = false;
        String paramJson = "";
        String json = HttpClientUtil.post(wxConfig.getMenuCreateUrl() + "?access_token=" + wxConfig.getMenuCreateUrl(), paramJson);
        return result;
    }
}

package com.rebate.domain.wx;

/**
 *  微信公众号接口配置
 * @author weishi
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2016-07-01 10:36  weishi 新建代码 
 * =========================== 维护日志 ===========================
 */
public class WxConfig {
    /**
     * 微信公众号的唯一标识
     */
    private String appId = "wxc6691e982b220243";
    /**
     * 应用密钥
     */
    private String appSecret = "c67d84f6fe18ed350649bc9c71cf565d";
    /**
     * 微信公众号认证地址
     */
    private String url = "http://printing.m.jd.com/wxupload/chooseImages";

    /**
     * JS API接口票据服务获取地址
     */
    private String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    /**
     * 公众号接口接入token获取地址
     */
    private String apiAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 公众号接口接入token获取地址
     */
    private String loginAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 用户信息API地址
     */
    private String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 用户授权API地址
     */
    private String authorizeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private String messageCheckToken;

    private String menuCreateUrl;

    private String menuGetUrl;

    private String menuDeleteUrl;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJsapiTicketUrl() {
        return jsapiTicketUrl;
    }

    public void setJsapiTicketUrl(String jsapiTicketUrl) {
        this.jsapiTicketUrl = jsapiTicketUrl;
    }

    public String getApiAccessTokenUrl() {
        return apiAccessTokenUrl;
    }

    public void setApiAccessTokenUrl(String apiAccessTokenUrl) {
        this.apiAccessTokenUrl = apiAccessTokenUrl;
    }

    public String getLoginAccessTokenUrl() {
        return loginAccessTokenUrl;
    }

    public void setLoginAccessTokenUrl(String loginAccessTokenUrl) {
        this.loginAccessTokenUrl = loginAccessTokenUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }

    public String getMessageCheckToken() {
        return messageCheckToken;
    }

    public void setMessageCheckToken(String messageCheckToken) {
        this.messageCheckToken = messageCheckToken;
    }

    public String getMenuCreateUrl() {
        return menuCreateUrl;
    }

    public void setMenuCreateUrl(String menuCreateUrl) {
        this.menuCreateUrl = menuCreateUrl;
    }

    public String getMenuGetUrl() {
        return menuGetUrl;
    }

    public void setMenuGetUrl(String menuGetUrl) {
        this.menuGetUrl = menuGetUrl;
    }

    public String getMenuDeleteUrl() {
        return menuDeleteUrl;
    }

    public void setMenuDeleteUrl(String menuDeleteUrl) {
        this.menuDeleteUrl = menuDeleteUrl;
    }
}

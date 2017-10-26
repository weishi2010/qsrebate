package com.rebate.common.cache;

/**
 * @author xiaodao
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2017/9/13 9:40  xiaodao 新建代码
 * =========================== 维护日志 ===========================
 */
public enum RedisKey {

    /**
     * 用户信息
     */
    USER_INFO ("ns_u_i_",60*60*5),
    /**
     * 微信API访问TOKEN
     */
    WX_API_ACCESSTOKEN("wx_api_at_",7200),
    /**
     * JD联盟推广链接点击
     */
    JD_UNION_URL_CLICK("JD_CK_",60*60*24*1000);

    RedisKey(String key,int timeout){
        this.key = key;
        this.timeout = timeout;
    }

    public String getPrefix(String suff){
        return this.key+suff;
    }

    private String key;
    private int timeout;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

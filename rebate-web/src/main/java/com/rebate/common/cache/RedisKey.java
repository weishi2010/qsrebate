package com.rebate.common.cache;

public enum RedisKey {

    /**
     * 用户信息
     */
    USER_INFO ("nsu_i_",60*60*30),
    /**
     * 微信API访问TOKEN
     */
    WX_API_ACCESSTOKEN("wx_api_nat_",7200),
    /**
     * JD联盟推广链接点击
     */
    JD_UNION_URL_CLICK("JD_CK_",60*60*24*1000),
    /**
     * 内购券商品ID列表
     */
    JD_COUPON_PRODUCT("JD_C_PT",60*60*24*1000),

    /**
     * 9.9元商品ID列表
     */
    JD_SECSKILL_PRODUCTS("JD_S_PT",60*60*24*1000),
    /**
     * 内购券商品单条缓存
     */
    JD_PRODUCT_VO("JD_PT_VO_",60*60*24*1000),
    /**
     * 白名单列表
     */
    JD_WHITE_LIST("JD_WUL",60*60*24*1000),;

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

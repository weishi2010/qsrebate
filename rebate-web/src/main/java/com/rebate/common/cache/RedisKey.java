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
    USER_INFO ("u_i_",60*60*5);

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

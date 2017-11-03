package com.rebate.domain.en;

/**
 * 微信事件类型
 */
public enum EWxEventType {


    /**
     * 关注公众号
     */
    SUBSCRIBE("subscribe"),
    /**
     * 取消关注公众号
     */
    UN_SUBSCRIBE("unsubscribe"),
    /**
     * 点击
     */
    CLICK("CLICK"),

    /**
     * 扫描二维码
     */
    SCAN("SCAN");

    private String value;

    EWxEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

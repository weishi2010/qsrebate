package com.rebate.domain.en;

public enum EWxMsgType {


    /**
     * 文本
     */
    TEXT("text"),
    /**
     * 图片
     */
    IMAGE("image"),

    /**
     * 图文
     */
    NEWS("news"),
    /**
     * 事件
     */
    EVENT("event");

    private String value;

    EWxMsgType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

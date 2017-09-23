package com.rebate.domain.en;

/**
 * 商品来源
 */
public enum EProductSource {
    /**
     * 参数常
     */
    JD(1,"京东"),
    /**
     * 系统异常
     */
    TAOBAO(2,"淘宝");
    private int code;
    private String name;

    EProductSource(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

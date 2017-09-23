package com.rebate.domain.en;

/**
 * 商品返利类型
 */
public enum EProudctRebateType {
    /**
     * 返利
     */
    REBATE(1,"返利"),
    /**
     * 返利
     */
    NOT_REBATE(0,"不返利");

    private int code;
    private String name;

    EProudctRebateType(int code, String name){
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

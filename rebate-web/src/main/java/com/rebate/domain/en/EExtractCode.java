package com.rebate.domain.en;

/**
 * 提现返回码
 */
public enum EExtractCode {
    /**
     * 参数常
     */
    PARAMS_ERROR(-2,"提现失败，接口参数异常！"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(-1,"提现失败，系统异常！"),
    /**
     * 提现成功
     */
    SUCCESS(1,"提现成功"),
    /**
     * 余额不足
     */
    LACK_BALANCE(2,"提现失败，余额不足!");
    private int code;
    private String name;

    EExtractCode(int code, String name){
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

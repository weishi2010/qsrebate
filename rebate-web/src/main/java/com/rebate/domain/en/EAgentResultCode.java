package com.rebate.domain.en;

/**
 * 代理返回码枚举
 */
public enum EAgentResultCode {

    NOT_EXISTS_USER(-2,"System is not exists this subunionId!"),

    ERROR(-1,"System's exception!"),

    SUCCESS(1,"success"),
    /**
     * 代理已存在
     */
    EXISTS(2,"The agent is exists!")    ;
    private int code;
    private String name;

    EAgentResultCode(int code, String name){
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

package com.rebate.domain.en;

/**
 * 代理枚举
 */
public enum EAgent {
    /**
     * 非代理用户
     */
    NOT_AGENT(0,"非代理用户"),
    /**
     * 一级代理
     */
    FIRST_AGENT(1,"一级代理"),

    /**
     * 二级代理
     */
    SECOND_AGENT(2,"二级代理");
    private int code;
    private String name;

    EAgent(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EAgent eExtractStatus:values()){
            if(code==eExtractStatus.getCode()){
                return eExtractStatus.name;
            }
        }
        return "";
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

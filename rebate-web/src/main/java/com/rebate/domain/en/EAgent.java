package com.rebate.domain.en;

/**
 * 代理模式枚举
 */
public enum EAgent {

    /**
     * 管理员
     */
    ADMIN(99,"管理员"),
    /**
     * 非代理用户
     */
    NOT_AGENT(0,"非代理用户"),
    /**
     * 代理模式一代理:一级代理给二级代理设置分佣比例，二级代理根据比例给一级代理分佣金
     */
    FIRST_AGENT(1,"代理模式一"),

    /**
     * 代理模式二代理:代理用户通过分享二维码带来粉丝代理用户
     */
    SECOND_AGENT(2,"代理模式二"),

    /**
     * 普通返利用户
     */
    GENERAL_REBATE_USER(3,"普通返利用户"),
    ;
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

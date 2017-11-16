package com.rebate.domain.en;

/**
 * 白名单类型枚举
 */
public enum EWhiteType {

    /**
     * 白名单代理
     */
    WHITE_AGENT(1,"白名单代理")
    ;
    private int code;
    private String name;

    EWhiteType(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EWhiteType eExtractStatus:values()){
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

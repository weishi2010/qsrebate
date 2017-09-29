package com.rebate.domain.en;

/**
 * 活动状态
 */
public enum EActivityStatus {
    /**
     * 删除
     */
    DELETE(-1,"删除"),
    /**
     * 默认
     */
    DEFAULT(0,"默认"),

    /**
     * 正常
     */
    PASS(1,"正常");
    private int code;
    private String name;

    EActivityStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EActivityStatus eExtractStatus:values()){
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

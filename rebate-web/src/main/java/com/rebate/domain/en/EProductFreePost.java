package com.rebate.domain.en;

/**
 * 包邮状态
 */
public enum EProductFreePost {
    /**
     * 不包邮
     */
    NOT_FREE_POST(0,"不包邮"),

    /**
     * 包邮
     */
    FREE_POST(1,"包邮");
    private int code;
    private String name;

    EProductFreePost(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EProductFreePost eExtractStatus:values()){
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

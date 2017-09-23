package com.rebate.domain.en;

/**
 * 订单状态
 */
public enum EOrderStatus {
    /**
     * 已取消
     */
    CANCEL(0,"已取消"),

    /**
     * 正常
     */
    SETTLEMENT(1,"正常");
    private int code;
    private String name;

    EOrderStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EOrderStatus eExtractStatus:values()){
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

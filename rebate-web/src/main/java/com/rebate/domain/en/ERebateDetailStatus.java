package com.rebate.domain.en;

/**
 * 订单返利明细状态
 */
public enum ERebateDetailStatus {
    /**
     * 未结算
     */
    NEVER_SETTLEMENT(0,"未结算"),

    /**
     * 已结算
     */
    SETTLEMENT(1,"已结算");
    private int code;
    private String name;

    ERebateDetailStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(ERebateDetailStatus eExtractStatus:values()){
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

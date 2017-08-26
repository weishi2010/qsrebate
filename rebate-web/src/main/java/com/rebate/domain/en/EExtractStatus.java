package com.rebate.domain.en;

/**
 * 提现返回码
 */
public enum EExtractStatus {
    /**
     * 系统异常
     */
    UNCHECK(0,"等待审核"),

    /**
     * 提现成功
     */
    PAYMENT(1,"已经付款");
    private int code;
    private String name;

    EExtractStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EExtractStatus eExtractStatus:values()){
            if(code==eExtractStatus.getCode()){
                return eExtractStatus.name;
            }
        }
        return UNCHECK.name;
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

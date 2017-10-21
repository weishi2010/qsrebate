package com.rebate.domain.en;

/**
 * JD联盟转链类型
 */
public enum EJDProCont {
    /**
     * 单品推广短链接
     */
    PRODUCT_URL(1,"单品推广短链接 "),
    /**
     * 活动推广链接
     */
    SALE_ACTIVITY_URL(2,"活动推广链接");
    private int code;
    private String name;

    EJDProCont(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EJDProCont eExtractStatus:values()){
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

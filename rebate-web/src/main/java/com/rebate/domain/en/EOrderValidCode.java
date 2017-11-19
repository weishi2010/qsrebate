package com.rebate.domain.en;

/**
 * 订单验证代码
 * 无效码 ,  12:,13:违规订单,14:订单来源与备案网址不符,-1:无效原因未知
 */
public enum EOrderValidCode {
    INVALID(-1,"无效原因未知"),
    VALID(1,"有效"),
    ORDER_SPLIT(2,"订单拆单"),
    ORDER_CANCEL(3,"订单取消"),
    JD_HELP_MAIN_ORDER(4,"京东帮帮主订单"),
    ACCOUNT_ERROR(5,"账户异常"),
    GITFS_CATEGORY(6,"赠品类目"),
    SCHOOL_ORDER(7,"校园订单"),
    COMPANY_ORDER(8,"企业订单"),
    TUAN_ORDER(9,"团购订单"),
    TAX_ORDER(10,"开增值税专用发票订单"),
    COUNTRY_ORDER(11,"乡村推广员下单"),
    SELF_ORDER(12,"自己推广自己下单"),
    INFRACTION_ORDER(13,"违规订单"),
    ORDER_SOURCE_DIFF(14,"订单来源与备案网址不符");


    private int code;
    private String name;

    EOrderValidCode(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EOrderValidCode eExtractStatus:values()){
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

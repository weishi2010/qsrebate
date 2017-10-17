package com.rebate.domain.en;

/**
 * 收支类型
 */
public enum EIncomeType {
    /**
     * 订单返利
     */
    ORDER_REBATE(1,"订单返利"),
    /**
     * 注册奖励
     */
    REGIST(2,"订单返利"),
    /**
     * 提现支出
     */
    EXTRACT(3,"提现支出");

    private int code;
    private String name;

    EIncomeType(int code, String name){
        this.code = code;
        this.name = name;
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

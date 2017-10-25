package com.rebate.domain.en;

/**
 * 收支类型
 */
public enum EIncomeType {
    /**
     * 注册奖励
     */
    REGIST(1,"注册返利"),
    /**
     * 代理模式一订单返利
     */
    FIRST_ORDER_REBATE(2,"代理模式一订单返利"),
    /**
     * 代理模式一代理奖励分佣
     */
    FIRST_AGENT_REBATE(3,"代理返利"),

    /**
     * 代理模式二订单返利
     */
    SECOND_ORDER_REBATE(4,"代理模式二订单返利"),

    /**
     * 代理模式二代理奖励分佣
     */
    SECOND_AGENT_REBATE(5,"代理返利"),

    /**
     * 普通订单返利
     */
    GENERAL_ORDER_REBATE(6,"普通订单返利"),

    /**
     * 提现支出
     */
    EXTRACT(7,"提现支出");

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

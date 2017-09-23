package com.rebate.domain.en;

/**
 * 商品优惠类型
 */
public enum EProudctCouponType {
    /**
     * 普通商品
     */
    GENERAL(1,"普通商品"),
    /**
     * 优惠券商品
     */
    COUPON(2,"优惠券商品");

    private int code;
    private String name;

    EProudctCouponType(int code, String name){
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

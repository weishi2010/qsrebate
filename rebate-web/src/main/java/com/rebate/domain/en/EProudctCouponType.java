package com.rebate.domain.en;

/**
 * 商品优惠券类型
 */
public enum EProudctCouponType {
    /**
     * 普通商品
     */
    GENERAL(1,"普通商品"),
    /**
     * 优惠券商品
     * 说明：导入时链接是券链接，前台还需要通过商品编号及券链接进行二合一转链
     */
    COUPON(2,"优惠券商品"),

    /**
     * 二合一券商品
     * 说明：导入时链接已是二合一券链接，前台直接跳转即可
     */
    CONVERT_COUPON(3,"二合一券商品");

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

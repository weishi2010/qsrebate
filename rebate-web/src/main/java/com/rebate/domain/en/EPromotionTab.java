package com.rebate.domain.en;

/**
 * 促销活动标签项
 */
public enum EPromotionTab {
    /**
     * 京东首页
     */
    JD(0,"京东首页"),
    /**
     * 每日必买
     */
    COUPON_PROMOTION(1,"独家优惠券"),
    /**
     * 9块9秒杀
     */
    SECKILL(2,"9块9"),
    /**
     * 优惠券活动
     */
    COUPON(3,"优惠券"),
    /**
     * 分享
     */
    SHARE(4,"分享活动");
    private int tab;
    private String name;

    EPromotionTab(int tab,String name){
        this.tab = tab;
        this.name = name;
    }
    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

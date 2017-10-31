package com.rebate.service.job;

public interface RebateJob {
    /**
     * 导入联盟订单
     */
    void importMediaOrder();

    /**
     * 导入所有联盟商品
     */
    void importAllJdMediaProducts();

    /**
     * 导入爆款商品
     */
    void importMediaThemeProducts();

    /**
     * 导入优惠券商品
     */
    void importCouponProducts();

    /**
     * 刷新用户信息
     */
    void refreshUserInfo();

    /**
     * 更新商品信息
     */
    void refreshProducts();

    /**
     * 刷新掌上大学优惠券商品缓存
     */
    void refreshDaxueCouponProductsCache();


}

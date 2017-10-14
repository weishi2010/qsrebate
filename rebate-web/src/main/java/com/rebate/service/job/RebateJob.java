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
     * 清理过期商品及优惠券信息
     */
    void cleanExpireProduct();
}

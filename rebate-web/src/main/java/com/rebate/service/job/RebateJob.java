package com.rebate.service.job;

public interface RebateJob {
    /**
     * 导入联盟订单
     */
    void importMediaOrder();

    /**
     * 导入爆款商品
     */
    void importMediaThemeProducts();
}

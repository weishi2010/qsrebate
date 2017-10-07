package com.rebate.service.wx;

public interface WxMenuService {
    /**
     * 创建菜单
     *
     * @return
     */
    String createMenu();

    /**
     * 查询菜单
     *
     * @return
     */
    String getMenu();

    /**
     * 删除菜单
     *
     * @return
     */
    String deleteMenu();
}

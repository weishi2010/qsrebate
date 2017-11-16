package com.rebate.dao;

import com.rebate.domain.whitelist.WhiteUserInfo;

public interface WhiteUserInfoDao {
    /**
     * 插入
     *
     * @param whiteUserInfo
     * @return
     */
    long insert(WhiteUserInfo whiteUserInfo);

    /**
     * 更新
     *
     * @param whiteUserInfo
     */
    void deleteBySubUnionId(WhiteUserInfo whiteUserInfo);

    /**
     * 根据openId查询
     *
     * @param whiteUserInfo
     * @return
     */
    WhiteUserInfo findBySubUnionId(WhiteUserInfo whiteUserInfo);

}

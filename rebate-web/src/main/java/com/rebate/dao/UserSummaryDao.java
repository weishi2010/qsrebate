package com.rebate.dao;

import com.rebate.domain.UserSummary;

public interface UserSummaryDao {
    /**
     * 插入
     *
     * @param userSummary
     * @return
     */
    long insert(UserSummary userSummary);

    /**
     * 更新
     *
     * @param userSummary
     */
    void update(UserSummary userSummary);

    /**
     * 更新点击
     *
     * @param userSummary
     */
    void incrUserClick(UserSummary userSummary);


    /**
     * 查询
     * @param userSummary
     * @return
     */
    UserSummary findUserSummary(UserSummary userSummary);
}

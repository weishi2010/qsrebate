package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.UserSummaryDao;
import com.rebate.domain.UserSummary;

public class UserSummaryDaoImpl extends BaseDao implements UserSummaryDao {
    @Override
    public long insert(UserSummary userSummary) {
        return (long) insert("UserSummary.insert", userSummary);
    }

    @Override
    public void update(UserSummary userSummary) {
        update("UserSummary.update", userSummary);
    }

    @Override
    public void incrUserClick(UserSummary userSummary) {
        update("UserSummary.incrUserClick", userSummary);
    }

    @Override
    public UserSummary findUserSummary(UserSummary userSummary) {
        return (UserSummary) queryForObject("UserSummary.findUserSummary", userSummary);
    }
}

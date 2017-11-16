package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.WhiteUserInfoDao;
import com.rebate.domain.whitelist.WhiteUserInfo;

public class WhiteUserInfoDaoImpl extends BaseDao implements WhiteUserInfoDao {
    @Override
    public long insert(WhiteUserInfo whiteUserInfo) {
        return (long)insert("WhiteUserInfo.insert",whiteUserInfo);
    }

    @Override
    public void deleteBySubUnionId(WhiteUserInfo whiteUserInfo) {
        delete("WhiteUserInfo.deleteBySubUnionId",whiteUserInfo);
    }

    @Override
    public WhiteUserInfo findBySubUnionId(WhiteUserInfo whiteUserInfo) {
        return (WhiteUserInfo)queryForObject("WhiteUserInfo.findBySubUnionId",whiteUserInfo);
    }
}

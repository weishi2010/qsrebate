package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.RecommendUserInfoDao;
import com.rebate.domain.RecommendUserInfo;
import com.rebate.domain.query.RecommendUserInfoQuery;

import java.util.List;

public class RecommendUserInfoDaoImpl extends BaseDao implements RecommendUserInfoDao {
    @Override
    public long insert(RecommendUserInfo recommendUserInfo) {
        return (long) insert("RecommendUserInfo.insert", recommendUserInfo);
    }

    @Override
    public void update(RecommendUserInfo recommendUserInfo) {
        update("RecommendUserInfo.update", recommendUserInfo);
    }

    @Override
    public RecommendUserInfo findRecommendUserInfo(RecommendUserInfoQuery recommendUserInfoQuery) {
        return (RecommendUserInfo) queryForObject("RecommendUserInfo.findRecommendUserInfo", recommendUserInfoQuery);
    }

    @Override
    public int findRecommendUserCount(RecommendUserInfoQuery recommendUserInfoQuery) {
        return (int) queryForObject("RecommendUserInfo.findRecommendUserCount", recommendUserInfoQuery);
    }

    @Override
    public List<RecommendUserInfo> findRecommendUserInfos(RecommendUserInfoQuery recommendUserInfoQuery) {
        return (List<RecommendUserInfo>)queryForList("RecommendUserInfo.findRecommendUserInfos",recommendUserInfoQuery);
    }
}

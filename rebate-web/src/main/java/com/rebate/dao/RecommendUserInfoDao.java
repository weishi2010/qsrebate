package com.rebate.dao;

import com.rebate.domain.RecommendUserInfo;
import com.rebate.domain.UserInfo;
import com.rebate.domain.query.RecommendUserInfoQuery;
import com.rebate.domain.query.UserInfoQuery;

public interface RecommendUserInfoDao {
    /**
     * 插入
     * @param recommendUserInfo
     * @return
     */
    long insert(RecommendUserInfo recommendUserInfo);

    /**
     * 更新
     * @param recommendUserInfo
     */
    void update(RecommendUserInfo recommendUserInfo);

    /**
     * 根据推荐者及被推荐者查询
     * @param recommendUserInfoQuery
     * @return
     */
    RecommendUserInfo findRecommendUserInfo(RecommendUserInfoQuery recommendUserInfoQuery);

    /**
     * 按时间查询推荐过来的粉丝用户
     * @param recommendUserInfoQuery
     * @return
     */
    int findRecommendUserCount(RecommendUserInfoQuery recommendUserInfoQuery);
}

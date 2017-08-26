package com.rebate.dao;

import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.RebateDetailQuery;

import java.util.List;

/**
 * Created by weishi on 2017/7/15.
 */
public interface RebateDetailDao {
    /**
     * 插入
     *
     * @param rebateDetail
     * @return
     */
    long insert(RebateDetail rebateDetail);

    /**
     * 根据openid查询总数
     *
     * @param rebateDetailQuery
     * @return
     */
    int findCountByOpenId(RebateDetailQuery rebateDetailQuery);

    /**
     * 根据id查询列表
     *
     * @param rebateDetailQuery
     * @return
     */
    List<RebateDetail> findListByOpenId(RebateDetailQuery rebateDetailQuery);
}

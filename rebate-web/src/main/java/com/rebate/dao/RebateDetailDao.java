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
     * 更新
     *
     * @param rebateDetail
     * @return
     */
    void update(RebateDetail rebateDetail);

    /**
     * 根据订单号查询返利明细
     * @param rebateDetailQuery
     * @return
     */
    RebateDetail queryRebateDetailByOrderId(RebateDetailQuery rebateDetailQuery);

    /**
     * 根据openid查询总数
     *
     * @param rebateDetailQuery
     * @return
     */
    int findCountBySubUnionId(RebateDetailQuery rebateDetailQuery);

    /**
     * 查询用户总佣金余额
     * @param rebateDetailQuery
     * @return
     */
    Double findUserTotalCommission(RebateDetailQuery rebateDetailQuery);

    /**
     * 根据id查询列表
     *
     * @param rebateDetailQuery
     * @return
     */
    List<RebateDetail> findListBySubUnionId(RebateDetailQuery rebateDetailQuery);
}

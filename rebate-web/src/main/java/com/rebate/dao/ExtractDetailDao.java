package com.rebate.dao;

import com.rebate.domain.ExtractDetail;
import com.rebate.domain.query.ExtractDetailQuery;

import java.util.List;

/**
 * Created by weishi on 2017/7/15.
 */
public interface ExtractDetailDao {
    /**
     * 插入
     * @param extractDetail
     * @return
     */
    long insert(ExtractDetail extractDetail);

    void updateExtractDetail(ExtractDetail extractDetail);


    /**
     * 根据openId查询总数
     * @param extractDetailQuery
     * @return
     */
    int findExtractDetailCount(ExtractDetailQuery extractDetailQuery);

    /**
     * 根据openId查询列表
     * @param extractDetailQuery
     * @return
     */
    List<ExtractDetail> findExtractDetailList(ExtractDetailQuery extractDetailQuery);

    /**
     *用户总提现金额-
     * @param extractDetailQuery
     * @return
     */
    Double findUserTotalExtract(ExtractDetailQuery extractDetailQuery);
}

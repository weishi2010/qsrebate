package com.rebate.dao;

import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;

/**
 * Created by weishi on 2017/7/15.
 */
public interface CommissionDao {
    /**
     * 插入
     * @param commission
     * @return
     */
    long insert(Commission commission);

    /**
     * 根据openId查询
     * @param commission
     * @return
     */
    Commission findCommissionByOpenId(Commission commission);
}

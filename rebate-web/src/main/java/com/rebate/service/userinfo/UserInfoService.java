package com.rebate.service.userinfo;

import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;

public interface UserInfoService {
    /**
     * 查询用户信息
     * @param openId
     * @return
     */
    UserInfo getUserInfo(String openId);

    /**
     * 查询用户佣金
     * @param openId
     * @return
     */
    Commission getUserCommission(String openId);
}

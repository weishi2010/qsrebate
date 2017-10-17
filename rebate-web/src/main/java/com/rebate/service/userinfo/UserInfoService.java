package com.rebate.service.userinfo;

import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;

public interface UserInfoService {

    UserInfo registUserInfoByLoginAccessToken(String accessToken,String openId,Integer angent);

    /**
     * 注册用户
     * @param openId
     * @param angent
     * @return
     */
    UserInfo registUserInfo(String openId,Integer angent);
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

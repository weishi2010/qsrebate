package com.rebate.service.userinfo.impl;

import com.rebate.dao.CommissionDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Commission;
import com.rebate.domain.UserInfo;
import com.rebate.service.userinfo.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Qualifier("commissionDao")
    @Autowired(required = true)
    private CommissionDao commissionDao;

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Override
    public void registUserInfo(UserInfo userInfo) {
        userInfoDao.insert(userInfo);
    }

    @Override
    public UserInfo getUserInfo(String openId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setOpenId(openId);
        return userInfoDao.findLoginUserInfo(userInfo);
    }

    @Override
    public Commission getUserCommission(String openId) {
        Commission query = new Commission();
        query.setOpenId(openId);
        Commission commission  = commissionDao.findCommissionByOpenId(query);
        if(null==commission){
            commission = new Commission();
            commission.setTotalCommission(0.0);
        }
        return commission;
    }
}

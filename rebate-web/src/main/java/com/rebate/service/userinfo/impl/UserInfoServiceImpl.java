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
        return null;
    }

    @Override
    public Commission getUserCommission(String openId) {
        Commission commission = new Commission();
        commission.setOpenId(openId);
        return commissionDao.findCommissionByOpenId(commission);
    }
}

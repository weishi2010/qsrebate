package com.rebate.domain.vo;

import com.rebate.domain.UserInfo;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息VO
 */
public class UserInfoVo extends UserInfo{

    /**
     * 是否白名单代理
     */
    @Getter
    @Setter
    private boolean isWhiteAgent;

    @Getter
    @Setter
    private String sui;

    public UserInfoVo(UserInfo userInfo){
        BeanUtils.copyProperties(userInfo,this);
    }
}

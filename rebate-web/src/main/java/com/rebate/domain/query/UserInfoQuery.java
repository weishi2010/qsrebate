package com.rebate.domain.query;

import com.rebate.domain.UserInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class UserInfoQuery extends UserInfo {

    @Getter
    @Setter
    private Date startDate;

    @Getter
    @Setter
    private Date endDate;
}

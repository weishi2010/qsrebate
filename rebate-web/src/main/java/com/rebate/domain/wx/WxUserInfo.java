package com.rebate.domain.wx;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信用户信息
 */
public class WxUserInfo {

    /**
     * 开放ID
     */
    @Getter
    @Setter
    private String openid;

    @Getter
    @Setter
    private String nickname;

    @Getter
    @Setter
    private Integer sex;

    @Getter
    @Setter
    private String province;

    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String country;

    @Getter
    @Setter
    private String headimgurl;

    @Getter
    @Setter
    private String[] privilege;

    @Getter
    @Setter
    private String unionid;
}

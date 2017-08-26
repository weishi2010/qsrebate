package com.rebate.domain.wx;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户授权结果信息
 */
public class AuthorizationCodeInfo {
    /**
     * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     */
    @Getter
    @Setter
    private String accessToken;

    /**
     * access_token接口调用凭证超时时间，单位（秒）
     */
    @Getter
    @Setter
    private String openId;

    /**
     * 用户刷新access_token
     */
    @Getter
    @Setter
    private String refreshToken;

    /**
     * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     */
    @Getter
    @Setter
    private String expiresIn;

    /**
     * 用户授权的作用域，使用逗号（,）分隔
     */
    @Getter
    @Setter
    private String scope;
}

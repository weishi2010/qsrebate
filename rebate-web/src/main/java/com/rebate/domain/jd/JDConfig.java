package com.rebate.domain.jd;

import lombok.Getter;
import lombok.Setter;
/**
 * {
 * "access_token": "873049e9-749c-479e-9647-7e91e13eabcf",
 * "code": 0,
 * "expires_in": 31535999,
 * "refresh_token": "c97d6656-9d02-40d5-a59d-2ac26fa093e4",
 * "time": "1504537477085",
 * "token_type": "bearer",
 * "uid": "5730215537",
 * "user_nick": "bestcuitao"
 * }
 */

/**
 * https://oauth.jd.com/oauth/authorize?response_type=code&client_id=BC2C0FCDA61E45DBE36E95D51E29543C&redirect_uri=http://www.jingcuhui.com&state=213
 * https://oauth.jd.com/oauth/token?grant_type=authorization_code&client_id=BC2C0FCDA61E45DBE36E95D51E29543C&redirect_uri=http://www.jingcuhui.com&code=yjEswj&state=213&client_secret=d253bb1c493344c5aa337ff917cfd46b
 */
public class JDConfig {
    //-----------------接口配置--------------
    @Getter
    @Setter
    private String appKey;
    @Getter
    @Setter
    private String appSecret;
    @Getter
    @Setter
    private String accessToken;
    @Getter
    @Setter
    private String apiUrl;


    //-----------------推广配置=--------------
    @Getter
    @Setter
    private Long unionId;//黔ICP备15015084号-2
    @Getter
    @Setter
    private int promotionType;//自定义推广
    @Getter
    @Setter
    private String channel;
    @Getter
    @Setter
    private String webId;
    @Getter
    @Setter
    private String adtType;


}

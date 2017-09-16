package com.rebate.domain.wx;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * api accessToken
 */
public class ApiAccessToken implements Serializable{
    /**
     * 基础支持的access_token
     */
    @JsonProperty("access_token")
    @Getter
    @Setter
    private String accessToken;

    /**
     * 过期时间
     */
    @JsonProperty("expires_in")
    @Getter
    @Setter
    private String expiresIn;
}

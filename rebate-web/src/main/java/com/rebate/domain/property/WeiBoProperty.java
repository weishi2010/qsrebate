package com.rebate.domain.property;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * 微博配置参数
 * @author little four
 * @version 1.0.0
 */
public class WeiBoProperty {
    /**
     * 短链接接口url
     */
    @Getter
    @Setter
    private String shortUrlApiUrl;

    @Getter
    @Setter
    private String accessToken;
}

package com.rebate.domain.property;

import lombok.Getter;
import lombok.Setter;

/**
 * JD配置参数
 * @author little four
 * @version 1.0.0
 */
public class JDProperty {
    /**
     * 京东活动链接域名列表，格式:sale.jd.com,pro.m.jd.com
     */
    @Getter
    @Setter
    private String saleDomains;

    /**
     * 新注册用户奖励
     */
    @Getter
    @Setter
    private Double newRegisterAward;
}

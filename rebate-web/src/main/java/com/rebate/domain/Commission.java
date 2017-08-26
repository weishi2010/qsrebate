package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 佣金
 */
public class Commission {

    private Long id;
    /**
     * 微信openId
     */
    @Getter
    @Setter
    private String openId;
    /**
     * 佣金总额
     */
    @Getter
    @Setter
    private Double totalCommission;
    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;
    /**
     * 创建时间
     */
    @Getter
    @Setter
    private Date created;
    /**
     * 修改时间
     */
    @Getter
    @Setter
    private Date modified;
}

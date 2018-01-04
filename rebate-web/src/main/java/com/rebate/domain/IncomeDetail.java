package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 收入明细
 */
public class IncomeDetail extends BaseQuery {
    private Long id;
    /**
     * 微信openId
     */
    @Getter
    @Setter
    private String openId;

    /**
     * 关注id，如订单号、编号
     */
    @Getter
    @Setter
    private Long referenceId;

    /**
     * 佣金总额
     */
    @Getter
    @Setter
    private Double income;
    /**
     * 收入类型
     */
    @Getter
    @Setter
    private Integer type;

    /**
     * 处理状态
     */
    @Getter
    @Setter
    private Integer dealt;

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

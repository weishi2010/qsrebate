package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class OrderSummary extends BaseQuery {
  /**
   * 微信openId
   */
  @Getter
  @Setter
  private String openId;

  /**
   * 子联盟ID
   */
  @Getter
  @Setter
  private String subUnionId;

  /**
   * 子联盟ID列表
   */
  @Getter
  @Setter
  private String subUnionIds;


  /**
   * 订单时间
   */
  @Getter
  @Setter
  private Date submitDate;
  /**
   * 点击量
   */
  @Getter
  @Setter
  private Long clickCount;
  /**
   * 有效订单量
   */
  @Getter
  @Setter
  private Long orderCount;

  /**
   * 佣金
   */
  @Getter
  @Setter
  private Double commission;

  /**
   * 代理佣金
   */
  @Getter
  @Setter
  private Double agentCommission;

  /**
   * 订单渠道
   */
  @Getter
  @Setter
  private Long orderSource;

}

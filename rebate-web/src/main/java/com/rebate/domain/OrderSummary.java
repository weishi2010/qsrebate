package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class OrderSummary {
  private Long id;
  /**
   * 微信openId
   */
  @Getter
  @Setter
  private Long openId;

  /**
   * 汇总时间
   */
  @Getter
  @Setter
  private Date summaryDate;
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
  private Long activeOrderCount;
  /**
   * 有效订单金额
   */
  @Getter
  @Setter
  private Double activeOrderPrice;
  /**
   * 预计佣金
   */
  @Getter
  @Setter
  private Double estimatedIncome;
  /**
   * 订单渠道
   */
  @Getter
  @Setter
  private Long orderSource;
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

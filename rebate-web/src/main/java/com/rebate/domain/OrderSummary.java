package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class OrderSummary extends BaseQuery {
  /**
   * 子联盟ID
   */
  @Getter
  @Setter
  private String subUnionId;

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
   * 有效订单量
   */
  @Getter
  @Setter
  private Double commission;

  /**
   * 订单渠道
   */
  @Getter
  @Setter
  private Long orderSource;

}

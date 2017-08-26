package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class ExtractDetail {
  /**
   * 主键
   */
  @Getter
  @Setter
  private Long id;
  /**
   * 微信openId
   */
  @Getter
  @Setter
  private String openId;
  /**
   * 提取时间
   */
  @Getter
  @Setter
  private Date extractDate;
  /**
   * 提取金额
   */
  @Getter
  @Setter
  private Double extractPrice;
  /**
   * 状态
   */
  @Getter
  @Setter
  private Long status;
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

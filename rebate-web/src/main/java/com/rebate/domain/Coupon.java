package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class Coupon {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 商品编号
     */
    @Getter
    @Setter
    private Long productId;
    /**
     * 券后价
     */
    @Getter
    @Setter
    private Double couponPrice;
    /**
     * 券链接
     */
    @Getter
    @Setter
    private String couponLink;
    /**
     * 类型
     */
    @Getter
    @Setter
    private Long type;
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

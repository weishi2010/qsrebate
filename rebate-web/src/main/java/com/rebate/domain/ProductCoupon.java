package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class ProductCoupon extends BaseQuery {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;

    /**
     * 商品
     */
    @Getter
    @Setter
    private Long productId;

    /**
     * 排序权重
     */
    @Getter
    @Setter
    private Integer sortWeight;

    /**
     * 原价
     */
    @Getter
    @Setter
    private Double originalPrice;

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
     * 券类型
     */
    @Getter
    @Setter
    private Integer couponTab;
    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;

    /**
     * 券描述
     */
    @Getter
    @Setter
    private String couponNote;
    /**
     * 优惠券面额
     */
    @Getter
    @Setter
    private Double discount;
    /**
     * 优惠券限额
     */
    @Getter
    @Setter
    private Double quota;
    /**
     * 券使用平台
     */
    @Getter
    @Setter
    private Integer platform;
    /**
     * 券数量
     */
    @Getter
    @Setter
    private Integer num;
    /**
     * 券剩余数量
     */
    @Getter
    @Setter
    private Integer remainNum;
    /**
     * 券状态
     */
    @Getter
    @Setter
    private Integer yn;

    /**
     * 商品来源（京东、淘宝、蘑菇街等）
     */
    @Getter
    @Setter
    private Integer sourcePlatform;

    /**
     * 开始推广日期
     */
    @Getter
    @Setter
    private Date startDate;
    /**
     * 结束推广日期
     */
    @Getter
    @Setter
    private Date endDate;

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

package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 商品信息
 */
public class Product extends BaseQuery {
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
     * 名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 图片地址
     */
    @Getter
    @Setter
    private String imgUrl;

    /**
     * 一级分类
     */
    @Getter
    @Setter
    private Integer firstCategory;

    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;

    /**
     * 一级分类名称
     */
    @Getter
    @Setter
    private String firstCategoryName;

    /**
     * 二级分类
     */
    @Getter
    @Setter
    private Integer secondCategory;

    /**
     * 二级分类名称
     */
    @Getter
    @Setter
    private String secondCategoryName;
    /**
     * 三级分类
     */
    @Getter
    @Setter
    private Integer thirdCategory;

    /**
     * 三级分类名称
     */
    @Getter
    @Setter
    private String thirdCategoryName;

    /**
     * 原价
     */
    @Getter
    @Setter
    private Double originalPrice;

    /**
     * 库存
     */
    @Getter
    @Setter
    private Integer stock;

    /**
     * 移动佣金比例
     */
    @Getter
    @Setter
    private Double commissionRatioWl;

    /**
     * PC佣金比例
     */
    @Getter
    @Setter
    private Double commissionRatioPc;

    /**
     * PC佣金
     */
    @Getter
    @Setter
    private Double commissionPc;
    /**
    * 移动佣金
    */
    @Getter
    @Setter
    private Double commissionWl;

    /**
     * 商品类型
     */
    @Getter
    @Setter
    private Integer productType;

    /**
     * 配送方式
     */
    @Getter
    @Setter
    private Integer distribution;

    /**
     * 是否返佣,0返佣 1返佣
     */
    @Getter
    @Setter
    private Integer isRebate;

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

package com.rebate.domain.vo;

import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品信息
 */
public class ProductVo extends BaseQuery {
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


    /**
     * 推广链接
     */
    @Getter
    @Setter
    private String promotionUrl;

    /**
     * 推广短链接
     */
    @Getter
    @Setter
    private String promotionShortUrl;

    /**
     * 是否返佣
     */
    @Getter
    @Setter
    private int isRebate;

    /**
     * 是排序权重
     */
    @Getter
    @Setter
    private Integer sortWeight;

    /**
     * 单价
     */
    @Getter
    @Setter
    private Double unitPrice;
    /**
     * 无线单价
     */
    @Getter
    @Setter
    private Double wlUnitPrice;
    /**
     * 商品来源（京东、淘宝、蘑菇街等）
     */
    @Getter
    @Setter
    private Integer sourcePlatform;

    /**
     * 落地页
     */
    @Getter
    @Setter
    private String materialUrl;

    /**
     * 店铺ID
     */
    @Getter
    @Setter
    private Long shopId;

    /**
     * 优惠类型(1普通商品 2优惠券商品)
     */
    @Getter
    @Setter
    private Integer couponType;
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

    @Getter
    @Setter
    private ProductCoupon productCoupon;

    public ProductVo(Product product){
        BeanUtils.copyProperties(product,this);
    }

}

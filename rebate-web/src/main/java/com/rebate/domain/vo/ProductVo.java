package com.rebate.domain.vo;

import com.rebate.domain.Product;
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
     * 佣金比例
     */
    @Getter
    @Setter
    private Double commissionRatio;


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

    public ProductVo(Product product){
        BeanUtils.copyProperties(product,this);
    }

    /**
     * 获取返利佣金
     */
    public Double getCommission() {
        if (null != commissionRatio && null != originalPrice) {
            Double commission = commissionRatio * originalPrice;
            Double rateForUser = 0.5;//广告佣金计算后再按此比例给用户返利
            return new BigDecimal(commission * rateForUser).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
        }
        return 0.0;
    }


}

package com.rebate.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 商品信息
 */
@JsonIgnoreProperties({"index","pageSize","beginRowNumber","endRowNumber","sort"})
public class ApiProductVo extends BaseQuery {
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
     * 排序权重
     */
    @Getter
    @Setter
    private Integer sortWeight;

    /**
     * 包邮
     */
    @Getter
    @Setter
    private Integer freePost;

    /**
     * 落地页
     */
    @Getter
    @Setter
    private String materialUrl;

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

    public ApiProductVo(){
    }

    public ApiProductVo(ProductVo productVo){
        BeanUtils.copyProperties(productVo,this);
    }

}

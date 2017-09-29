package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 商品分类信息
 */
public class Category extends BaseQuery {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;

    /**
     * 一级分类
     */
    @Getter
    @Setter
    private Integer firstCategory;

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
     * 来源
     */
    @Getter
    @Setter
    private Integer source;

    /**
     * 排序权重
     */
    @Getter
    @Setter
    private Integer sortWeight;

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

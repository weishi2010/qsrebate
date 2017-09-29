package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 商品分类推荐归类
 */
public class RecommendCategory extends BaseQuery {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;

    /**
     * 归类名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 二级分类列表
     */
    @Getter
    @Setter
    private String secondCategoryList;

    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;

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

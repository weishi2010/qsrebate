package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 商品分类信息
 */
public class CategoryInfo extends BaseQuery {

    /**
     * 分类ID
     */
    @Getter
    @Setter
    private Integer id;

    /**
     * 分类名称
     */
    @Getter
    @Setter
    private String name;


}

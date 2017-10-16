package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 活动广告位
 */
public class AdvertismentPosition extends BaseQuery {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 标题
     */
    @Getter
    @Setter
    private String title;
    /**
     * 活动链接
     */
    @Getter
    @Setter
    private String link;
    /**
     * 图片链接
     */
    @Getter
    @Setter
    private String imgUrl;
    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;

    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer position;

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

package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 活动
 */
public class Activity extends BaseQuery {
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
    private String activityLink;
    /**
     * 图片链接
     */
    @Getter
    @Setter
    private String imgUrl;
    /**
     * 描述
     */
    @Getter
    @Setter
    private String remark;
    /**
     * 利益描述
     */
    @Getter
    @Setter
    private String benefit;
    /**
     * 活动开始时间
     */
    @Getter
    @Setter
    private Date beginTime;
    /**
     * 活动结束时间
     */
    @Getter
    @Setter
    private Date endTime;
    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;
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

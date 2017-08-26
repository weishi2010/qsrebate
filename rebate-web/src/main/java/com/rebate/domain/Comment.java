package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by weishi on 2017/7/15.
 */
public class Comment {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;
    /**
     */
    @Getter
    @Setter
    private Long commentId;
    /**
     */
    @Getter
    @Setter
    private String  guid;
    /**
     */
    @Getter
    @Setter
    private String  content;
    /**
     */
    @Getter
    @Setter
    private Long  productId;
    /**
     */
    @Getter
    @Setter
    private Integer status;
    /**
     */
    @Getter
    @Setter
    private String  nickname;
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

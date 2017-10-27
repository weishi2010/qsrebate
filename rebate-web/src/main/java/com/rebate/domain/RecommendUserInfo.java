package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 推荐用户信息
 */
public class RecommendUserInfo implements Serializable{

    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 微信openId
     */
    @Getter
    @Setter
    private String openId;

    /**
     * 推荐账号
     */
    @Getter
    @Setter
    private String recommendAccount;

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

package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 */
public class UserInfo implements Serializable{

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
     * 微信头像
     */
    @Getter
    @Setter
    private String wxImage;
    /**
     * 手机号
     */
    @Getter
    @Setter
    private String phone;
    /**
     * 昵称
     */
    @Getter
    @Setter
    private String nickName;
    /**
     * 邮箱
     */
    @Getter
    @Setter
    private String email;
    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer status;

    /**
     * 子联盟账号
     */
    @Getter
    @Setter
    private String subUnionId;

    /**
     * 代理标识
     */
    @Getter
    @Setter
    private Integer agent;

    /**
     * 推荐账号
     */
    @Getter
    @Setter
    private String recommendAccount;

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

    public UserInfo(){
    }

}

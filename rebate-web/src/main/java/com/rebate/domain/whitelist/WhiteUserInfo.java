package com.rebate.domain.whitelist;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 白名单用户信息
 */
public class WhiteUserInfo extends BaseQuery implements Serializable{

    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 白名单类型
     */
    @Getter
    @Setter
    private Integer type;

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

    public WhiteUserInfo(){
    }

}

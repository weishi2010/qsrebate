package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
public class UserSummary extends BaseQuery implements Serializable{
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 状态
     */
    @Getter
    @Setter
    private Integer clickCount;

    /**
     * 子联盟账号
     */
    @Getter
    @Setter
    private String subUnionId;
    /**
     * 记录时间
     */
    @Getter
    @Setter
    private Date opDate;

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

    public UserSummary(){
    }
}

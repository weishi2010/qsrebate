package com.rebate.domain.agent;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 代理用户关系
 * @author little four
 * @version 1.0.0
 */
public class AgentRelation {
    /**
     * 主键
     */
    @Getter
    @Setter
    private Long id;

    /**
     * 代理子联盟ID
     */
    @Getter
    @Setter
    private String agentSubUnionId;

    /**
     * 父级代理子联盟ID
     */
    @Getter
    @Setter
    private String parentAgentSubUnionId;

    /**
     * 分成比例，平台抽成后佣金按些比例给代理用户返佣金
     */
    @Getter
    @Setter
    private Double commissionRatio;
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

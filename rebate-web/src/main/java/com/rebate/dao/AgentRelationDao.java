package com.rebate.dao;

import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.query.AgentRelationQuery;

import java.util.List;

public interface AgentRelationDao {
    /**
     * 插入
     * @param agentRelation
     * @return
     */
    long insert(AgentRelation agentRelation);

    /**
     * 更新
     * @param agentRelation
     * @return
     */
    long update(AgentRelation agentRelation);

    /**
     * 根据id查询
     * @param agentRelationQuery
     * @return
     */
    AgentRelation findById(AgentRelationQuery agentRelationQuery);

    /**
     * 根据id查询
     * @param agentRelationQuery
     * @return
     */
    AgentRelation findByAgentSubUnionId(AgentRelationQuery agentRelationQuery);
    /**
     * 根据父id查询列表
     * @param agentRelationQuery
     * @return
     */
    List<AgentRelation> findByParentId(AgentRelationQuery agentRelationQuery);
    /**
     * 根据父id代理总数
     * @param agentRelationQuery
     * @return
     */
    int findCountByParentId(AgentRelationQuery agentRelationQuery);
}

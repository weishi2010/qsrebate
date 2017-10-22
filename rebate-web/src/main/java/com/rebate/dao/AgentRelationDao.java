package com.rebate.dao;

import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.query.AgentRelationQuery;

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
}

package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.AgentRelationDao;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.query.AgentRelationQuery;

import java.util.List;

public class AgentRelationDaoImpl extends BaseDao implements AgentRelationDao {
    @Override
    public long insert(AgentRelation agentRelation) {
        return (long)insert("AgentRelation.insert",agentRelation);
    }

    @Override
    public long update(AgentRelation agentRelation) {
        return update("AgentRelation.update",agentRelation);
    }

    @Override
    public AgentRelation findById(AgentRelationQuery agentRelationQuery) {
        return (AgentRelation)queryForObject("AgentRelation.findById",agentRelationQuery);
    }

    @Override
    public AgentRelation findByAgentSubUnionId(AgentRelationQuery agentRelationQuery) {
        return (AgentRelation)queryForObject("AgentRelation.findByAgentSubUnionId",agentRelationQuery);
    }

    @Override
    public List<AgentRelation> findByParentId(AgentRelationQuery agentRelationQuery) {
        return (List<AgentRelation>)queryForList("AgentRelation.findByParentId",agentRelationQuery);
    }

    @Override
    public int findCountByParentId(AgentRelationQuery agentRelationQuery) {
        return (int)queryForObject("AgentRelation.findCountByParentId",agentRelationQuery);
    }
}

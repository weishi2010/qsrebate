package com.rebate.test;

import com.rebate.common.util.JsonUtil;
import com.rebate.dao.ActivityDao;
import com.rebate.dao.AgentRelationDao;
import com.rebate.domain.Activity;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.en.EActivityStatus;
import com.rebate.domain.query.ActivityQuery;
import com.rebate.domain.query.AgentRelationQuery;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class AgentRelationDaoTest extends AbstractJUnit4SpringContextTests {

    private final static TypeReference<List<Map>> mapListTypeReference = new TypeReference<List<Map>>() {
    };

    @Qualifier("agentRelationDao")
    @Autowired(required = true)
    private AgentRelationDao agentRelationDao;

    @Test
    public void testInsert() {
        AgentRelation agentRelation = new AgentRelation();
        agentRelation.setStatus(0);
        agentRelation.setAgentSubUnionId("");
        agentRelation.setParentAgentSubUnionId("111");
        agentRelation.setCommissionRatio(0.3);
        agentRelationDao.insert(agentRelation);
    }

    @Test
    public void testUpdate() {
        AgentRelation agentRelation = new AgentRelation();
        agentRelation.setId(3l);
        agentRelation.setAgentSubUnionId("000");
        agentRelationDao.update(agentRelation);
    }

    @Test
    public void testFind() {
        AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
        agentRelationQuery.setId(3l);
        AgentRelation agentRelation1 = agentRelationDao.findById(agentRelationQuery);
        System.out.println("agentRelation1:"+JsonUtil.toJson(agentRelation1));

        agentRelationQuery.setAgentSubUnionId("111");
        AgentRelation agentRelation2 =   agentRelationDao.findByAgentSubUnionId(agentRelationQuery);
        System.out.println("agentRelation2:"+JsonUtil.toJson(agentRelation2));
    }
}
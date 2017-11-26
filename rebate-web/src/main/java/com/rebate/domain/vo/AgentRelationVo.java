package com.rebate.domain.vo;

import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 代理用户关系VO
 *
 * @author little four
 * @version 1.0.0
 */
public class AgentRelationVo extends AgentRelation {
    @Getter
    @Setter
    private String nickName;
    @Getter
    @Setter
    private String imgUrl;

    public AgentRelationVo(AgentRelation agentRelation) {
        BeanUtils.copyProperties(agentRelation, this);
    }

    public Double getCommissionRatioShow() {
        if (null != this.getCommissionRatio()) {
            return getCommissionRatio() * 100;
        }
        return 0.0;
    }

}

package com.rebate.domain.property;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * JD配置参数
 * @author little four
 * @version 1.0.0
 */
public class JDProperty {
    /**
     * 京东活动链接域名列表，格式:sale.jd.com,pro.m.jd.com
     */
    @Getter
    @Setter
    private String saleDomains;

    /**
     * 新注册用户奖励
     */
    @Getter
    @Setter
    private Double newRegisterAward;

    /**
     * 白名单
     */
    @Getter
    @Setter
    private String whiteList;

    /**
     * 管理员
     */
    @Getter
    @Setter
    private String adminList;

    /**
     * 密钥
     */
    @Getter
    @Setter
    private String encryptKey;


    /**
     * 代理模式一平台抽成
     */
    @Getter
    @Setter
    private Double firstAgentPlatRatio;
    /**
     * 代理模式二平台分成
     */
    @Getter
    @Setter
    private Double sencondAgentPlatRatio;

    /**
     * 代理模式二代理分成
     */
    @Getter
    @Setter
    private Double sencondAgentRatio;

    /**
     * 是否是管理员
     * @param subUnionId
     * @return
     */
    public boolean isAdmin(String subUnionId){
        if(StringUtils.isBlank(adminList)){
            return false;
        }
        String[] array = adminList.split(",");
        for(String agent:array){
            if(agent.equalsIgnoreCase(subUnionId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否白名单代理
     */
    public boolean isWhiteAgent(String subUnionId){
        if(StringUtils.isBlank(whiteList)){
            return false;
        }
        String[] array = whiteList.split(",");
        for(String agent:array){
            if(agent.equalsIgnoreCase(subUnionId)){
                return true;
            }
        }
        return false;
    }
}

package com.rebate.service.order.impl;

import com.google.common.base.Joiner;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.AgentRelationDao;
import com.rebate.dao.ProductDao;
import com.rebate.dao.RebateDetailDao;
import com.rebate.dao.RecommendUserInfoDao;
import com.rebate.domain.*;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.query.AgentRelationQuery;
import com.rebate.domain.query.OrderSummaryQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.query.RecommendUserInfoQuery;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.manager.userinfo.UserInfoManager;
import com.rebate.service.order.RebateDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("rebateDetailService")
public class RebateDetailServiceImpl implements RebateDetailService {
    private static final Logger LOG = LoggerFactory.getLogger(RebateDetailService.class);
    @Qualifier("rebateDetailDao")
    @Autowired
    private RebateDetailDao rebateDetailDao;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("agentRelationDao")
    @Autowired(required = true)
    private AgentRelationDao agentRelationDao;

    @Qualifier("recommendUserInfoDao")
    @Autowired(required = true)
    private RecommendUserInfoDao recommendUserInfoDao;

    @Qualifier("userInfoManager")
    @Autowired(required = true)
    private UserInfoManager userInfoManager;

    @Override
    public PaginatedArrayList<RebateDetailVo> findRebateDetailList(RebateDetailQuery rebateDetailQuery) {
        return getRebateDetails(rebateDetailQuery);
    }

    @Override
    public PaginatedArrayList<RebateDetailVo> findFirstAgentSonRebateDetailList(RebateDetailQuery query) {
        PaginatedArrayList<RebateDetailVo> rebateDetails = new PaginatedArrayList<RebateDetailVo>(query.getIndex(), query.getPageSize());

        //获取子代理列表
        List<String> agentSubUnionIds = getTopFirstAgentSonSubUnionIds(query.getSubUnionId());

        if (agentSubUnionIds.size() > 0) {
            //查询下级代理订单列表
            query.setSubUnionIds(Joiner.on(",").join(agentSubUnionIds));
            query.setSubUnionId(null);
            rebateDetails = getRebateDetails(query);
        }
        return rebateDetails;
    }

    @Override
    public PaginatedArrayList<RebateDetailVo> findSecondAgentRecommendRebateDetailList(RebateDetailQuery query) {
        PaginatedArrayList<RebateDetailVo> rebateDetails = new PaginatedArrayList<RebateDetailVo>(query.getIndex(), query.getPageSize());

        //获取TOP 1W个推广粉丝子联盟id
        List<String> recommendSubUnionIds = getTopSecondAgentRecommendUsers(query.getOpenId());
        if (recommendSubUnionIds.size() > 0) {
            //查询下级代理订单列表
            query.setSubUnionIds(Joiner.on(",").join(recommendSubUnionIds));
            query.setSubUnionId(null);
            rebateDetails = getRebateDetails(query);
        }
        return rebateDetails;
    }

    /**
     * 代理模式一获取子代理列表
     * @param subUnionId
     * @return
     */
    private List<String>  getTopFirstAgentSonSubUnionIds(String subUnionId){
        //获取TOP 1W个下级代理子联盟id
        AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
        agentRelationQuery.setParentAgentSubUnionId(subUnionId);
        agentRelationQuery.setStartRow(0);
        agentRelationQuery.setPageSize(10000);
        List<AgentRelation> agentRelations = agentRelationDao.findByParentId(agentRelationQuery);
        List<String> agentSubUnionIds = new ArrayList<>();
        for (AgentRelation agentRelation : agentRelations) {
            agentSubUnionIds.add("'" + agentRelation.getAgentSubUnionId() + "'");
        }
        return agentSubUnionIds;
    }
    /**
     * 获取代理二推荐粉丝
     * @param openId
     * @return
     */
    private List<String> getTopSecondAgentRecommendUsers(String openId){
        //获取TOP 1W个推广粉丝子联盟id
        RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
        recommendUserInfoQuery.setRecommendAccount(openId);
        recommendUserInfoQuery.setStartRow(0);
        recommendUserInfoQuery.setPageSize(10000);
        List<RecommendUserInfo> recommendUserInfos = recommendUserInfoDao.findRecommendUserInfos(recommendUserInfoQuery);
        List<String> recommendSubUnionIds = new ArrayList<>();
        for (RecommendUserInfo recommendUserInfo : recommendUserInfos) {
            UserInfo userInfoQuery = new UserInfo();
            userInfoQuery.setOpenId(recommendUserInfo.getOpenId());
            UserInfo userInfo = userInfoManager.findLoginUserInfo(userInfoQuery);
            if (null != userInfo) {
                recommendSubUnionIds.add("'" + userInfo.getSubUnionId() + "'");
            }
        }
        return recommendSubUnionIds;
    }

    @Override
    public PaginatedArrayList<OrderSummary> getFirstAgentSonOrderSummary(String subUnionId){
        PaginatedArrayList<OrderSummary> orderSummaryList = new PaginatedArrayList<OrderSummary>(1,10);

        //获取TOP 1W个推广粉丝子联盟id
        List<String> recommendSubUnionIds = getTopFirstAgentSonSubUnionIds(subUnionId);
        if (recommendSubUnionIds.size() > 0) {
            OrderSummaryQuery orderSummaryQuery = new OrderSummaryQuery();
            orderSummaryQuery.setPageSize(30);
            orderSummaryQuery.setSubUnionIds(Joiner.on(",").join(recommendSubUnionIds));
            orderSummaryList = getOrderSummaryBySubUnionIds(orderSummaryQuery);
        }
        return orderSummaryList;
    }

    @Override
    public PaginatedArrayList<OrderSummary> getSecondAgentRecommendUserOrderSummary(String openId){
        PaginatedArrayList<OrderSummary> orderSummaryList = new PaginatedArrayList<OrderSummary>(1, 10);

        //获取TOP 1W个推广粉丝子联盟id
        List<String> recommendSubUnionIds = getTopSecondAgentRecommendUsers(openId);
        if (recommendSubUnionIds.size() > 0) {
            OrderSummaryQuery orderSummaryQuery = new OrderSummaryQuery();
            orderSummaryQuery.setPageSize(30);
            orderSummaryQuery.setSubUnionIds(Joiner.on(",").join(recommendSubUnionIds));
            orderSummaryList = getOrderSummaryBySubUnionIds(orderSummaryQuery);
        }
        return orderSummaryList;
    }

    @Override
    public PaginatedArrayList<OrderSummary> getOrderSummaryBySubUnionIds(OrderSummary orderSummaryQuery) {
        PaginatedArrayList<OrderSummary> orderSummaryList = new PaginatedArrayList<OrderSummary>(orderSummaryQuery.getIndex(), orderSummaryQuery.getPageSize());
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map summaryMap = new HashMap();

            List<OrderSummary> list = rebateDetailDao.getOrderSummaryBySubUnionIds(orderSummaryQuery);
            for (OrderSummary orderSummary : list) {
                String dayStr = format.format(orderSummary.getSubmitDate());
                summaryMap.put(dayStr, orderSummary);

            }


            for (int day = 0; day <= orderSummaryQuery.getPageSize(); day++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -day);
                String dayStr = format.format(calendar.getTime());
                Object obj = summaryMap.get(dayStr);
                OrderSummary orderSummary = null;
                if (null != obj) {
                    orderSummary = (OrderSummary) obj;
                    orderSummary.setCommission(new BigDecimal(orderSummary.getCommission() + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    orderSummary.setAgentCommission(new BigDecimal(orderSummary.getAgentCommission() + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                } else {
                    orderSummary = new OrderSummary();
                    orderSummary.setCommission(0.0);
                    orderSummary.setSubmitDate(calendar.getTime());
                    orderSummary.setOrderCount(0l);
                    orderSummary.setSubUnionId(orderSummaryQuery.getSubUnionId());
                }
                orderSummary.setClickCount(0l);
                orderSummaryList.add(orderSummary);
            }


        } catch (Exception e) {
            LOG.error("getOrderSummaryBySubUnionIds error!orderSummaryQuery:" + JsonUtil.toJson(orderSummaryQuery), e);
        }
        return orderSummaryList;
    }

    @Override
    public PaginatedArrayList<OrderSummary> getOrderSummaryBySubUnionId(OrderSummary orderSummaryQuery) {
        PaginatedArrayList<OrderSummary> orderSummaryList = new PaginatedArrayList<OrderSummary>(orderSummaryQuery.getIndex(), orderSummaryQuery.getPageSize());
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map summaryMap = new HashMap();

            List<OrderSummary> list = rebateDetailDao.getOrderSummaryBySubUnionId(orderSummaryQuery);
            for (OrderSummary orderSummary : list) {
                orderSummary.setTotalMoney(new BigDecimal(orderSummary.getTotalMoney() + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                String dayStr = format.format(orderSummary.getSubmitDate());
                summaryMap.put(dayStr, orderSummary);

            }


            for (int day = 0; day <= orderSummaryQuery.getPageSize(); day++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -day);
                String dayStr = format.format(calendar.getTime());
                Object obj = summaryMap.get(dayStr);
                OrderSummary orderSummary = null;
                if (null != obj) {
                    orderSummary = (OrderSummary) obj;
                    orderSummary.setCommission(new BigDecimal(orderSummary.getCommission() + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    orderSummary.setAgentCommission(new BigDecimal(orderSummary.getAgentCommission() + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                } else {
                    orderSummary = new OrderSummary();
                    orderSummary.setCommission(0.0);
                    orderSummary.setSubmitDate(calendar.getTime());
                    orderSummary.setOrderCount(0L);
                    orderSummary.setSubUnionId(orderSummaryQuery.getSubUnionId());
                    orderSummary.setTotalMoney(0.0);
                }
                orderSummary.setClickCount(shortUrlManager.getJDUnionUrlClick(orderSummaryQuery.getSubUnionId(), orderSummary.getSubmitDate()));
                orderSummaryList.add(orderSummary);
            }


        } catch (Exception e) {
            LOG.error("getOrderSummaryBySubUnionId error!orderSummaryQuery:" + JsonUtil.toJson(orderSummaryQuery), e);
        }
        return orderSummaryList;
    }

    @Override
    public OrderSummary getAllOrderSummaryByDate(OrderSummaryQuery orderSummaryQuery) {
        return rebateDetailDao.getAllOrderSummaryByDate(orderSummaryQuery);
    }

    @Override
    public OrderSummary getRecommendUserOrderSummaryByOpenId(RebateDetailQuery rebateDetailQuery) {
        return rebateDetailDao.getRecommendUserOrderSummaryByOpenId(rebateDetailQuery);
    }

//-------------------------private methods---------------------------

    /**
     * 查询订单明细列表
     *
     * @param rebateDetailQuery
     * @return
     */
    private PaginatedArrayList<RebateDetailVo> getRebateDetails(RebateDetailQuery rebateDetailQuery) {
        PaginatedArrayList<RebateDetailVo> rebateDetails = new PaginatedArrayList<RebateDetailVo>(rebateDetailQuery.getIndex(), rebateDetailQuery.getPageSize());
        try {
            int totalItem = rebateDetailDao.findCount(rebateDetailQuery);
            if (totalItem > 0) {
                rebateDetails.setTotalItem(totalItem);
                rebateDetailQuery.setStartRow(rebateDetails.getStartRow());
                if (rebateDetailQuery.getIndex() <= rebateDetails.getTotalPage()) {

                    List<RebateDetail> list = rebateDetailDao.findList(rebateDetailQuery);
                    for (RebateDetail rebateDetail : list) {
                        RebateDetailVo vo = new RebateDetailVo(rebateDetail);
                        rebateDetails.add(vo);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("getRebateDetails error!rebateDetailQuery:" + JsonUtil.toJson(rebateDetailQuery), e);
        }
        return rebateDetails;
    }
}

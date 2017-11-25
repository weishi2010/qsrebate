package com.rebate.service.order.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.ProductDao;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.OrderSummary;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.OrderSummaryQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.order.RebateDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("rebateDetailService")
public class RebateDetailServiceImpl implements RebateDetailService {
    private static final Logger LOG = LoggerFactory.getLogger(RebateDetailService.class);
    @Qualifier("rebateDetailDao")
    @Autowired
    private RebateDetailDao rebateDetailDao;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Override
    public PaginatedArrayList<RebateDetailVo> findRebateDetailList(RebateDetailQuery rebateDetailQuery) {
        PaginatedArrayList<RebateDetailVo> rebateDetails = new PaginatedArrayList<RebateDetailVo>(rebateDetailQuery.getIndex(), rebateDetailQuery.getPageSize());
        try {
            int totalItem = rebateDetailDao.findCountBySubUnionId(rebateDetailQuery);
            if (totalItem > 0) {
                rebateDetails.setTotalItem(totalItem);
                rebateDetailQuery.setStartRow(rebateDetails.getStartRow());
                if(rebateDetailQuery.getIndex()<=rebateDetails.getTotalPage()) {

                    List<RebateDetail> list = rebateDetailDao.findListBySubUnionId(rebateDetailQuery);
                    for (RebateDetail rebateDetail : list) {
                        RebateDetailVo vo = new RebateDetailVo(rebateDetail);
                        //如果订单中的子联盟ID不是当前查询的用户则说明是粉丝引入的订单，佣金显示按代理分成显示
                        if(!rebateDetail.getSubUnionId().equalsIgnoreCase(rebateDetailQuery.getSubUnionId())){
                            vo.setUserCommission(rebateDetail.getAgentCommission());
                        }
                        rebateDetails.add(vo);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("findRebateDetailList error!rebateDetailQuery:" + JsonUtil.toJson(rebateDetailQuery), e);
        }
        return rebateDetails;
    }

    @Override
    public PaginatedArrayList<OrderSummary> getOrderSummaryBySubUnionId(OrderSummary orderSummaryQuery) {
        PaginatedArrayList<OrderSummary> orderSummaryList = new PaginatedArrayList<OrderSummary>(orderSummaryQuery.getIndex(), orderSummaryQuery.getPageSize());
        try {
            SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
            Map summaryMap =new HashMap();

            List<OrderSummary> list = rebateDetailDao.getOrderSummaryBySubUnionId(orderSummaryQuery);
            for(OrderSummary orderSummary:list){
                String dayStr =format.format(orderSummary.getSubmitDate());
                summaryMap.put(dayStr,orderSummary);

            }


            for(int day=0;day<=orderSummaryQuery.getPageSize();day++){
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE,-day);
                String dayStr =format.format(calendar.getTime());
                Object obj = summaryMap.get(dayStr);
                OrderSummary orderSummary =null;
                if(null!=obj){
                    orderSummary = (OrderSummary)obj;
                    orderSummary.setCommission(new BigDecimal(orderSummary.getCommission()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }else{
                    orderSummary = new OrderSummary();
                    orderSummary.setCommission(0.0);
                    orderSummary.setSubmitDate(calendar.getTime());
                    orderSummary.setOrderCount(0l);
                    orderSummary.setSubUnionId(orderSummaryQuery.getSubUnionId());
                }
                orderSummary.setClickCount(shortUrlManager.getJDUnionUrlClick(orderSummaryQuery.getSubUnionId(),orderSummary.getSubmitDate()));
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


}

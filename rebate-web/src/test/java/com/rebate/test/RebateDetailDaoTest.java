package com.rebate.test;


import com.rebate.common.util.JsonUtil;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.OrderSummary;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EAgent;
import com.rebate.domain.en.EOrderStatus;
import com.rebate.domain.en.ERebateDetailStatus;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.manager.jd.JdSdkManager;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class RebateDetailDaoTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("rebateDetailDao")
    @Autowired
    private RebateDetailDao rebateDetailDao;
    @Autowired
    private JdSdkManager jdSdkManager;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Test
    public void testGetCommissionRebateDetails() {

        String queryTime = "20171017";//yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
        int page = 1;
        int pageSize = 10;
        int count1=0;
        int count2=0;
        List<RebateDetail> list1 = jdSdkManager.getCommissionRebateDetails(queryTime, page, pageSize);
        for(RebateDetail rebateDetail:list1){
            if(rebateDetail.getStatus()== ERebateDetailStatus.SETTLEMENT.getCode()){
                count1++;
            }
        }
        List<RebateDetail> list2 = jdSdkManager.getRebateDetails(queryTime, page, pageSize);
        for(RebateDetail rebateDetail:list2){
            if(rebateDetail.getStatus()== ERebateDetailStatus.SETTLEMENT.getCode()){
                count2++;
            }
        }
        System.out.println(count1+"-------"+count2);

    }

    @Test
    public void testLoadAllRebateDetails(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        int pageSize = 10;
        //获取近30天订单进行更新
        for (int days = 0; days < 50; days++) {
            int count1 = 0;
            int page = 1;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_WEEK, -days);
            String queryTime = format.format(calendar.getTime());

            List<RebateDetail> rebateDetails = jdSdkManager.getCommissionRebateDetails(queryTime, page, pageSize);
            while (rebateDetails.size() > 0) {
                for (RebateDetail rebateDetail : rebateDetails) {
                    if(rebateDetail.getStatus()== ERebateDetailStatus.SETTLEMENT.getCode()){
                        count1++;
                    }
                }

                page++;
                rebateDetails = jdSdkManager.getCommissionRebateDetails(queryTime, page, pageSize);
            }
            System.out.println(queryTime+"===="+count1);
        }
    }



    @Test
    public void batchInsert() {
        String queryTime = "20171007";//yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
        int page = 1;
        int pageSize = 10;
        List<RebateDetail> list = jdSdkManager.getRebateDetails(queryTime, page, pageSize);


        for (RebateDetail rebateDetail : list) {
            RebateDetailQuery rebateDetailQuery = new RebateDetailQuery();
            rebateDetailQuery.setOrderId(rebateDetail.getOrderId());
            if (null == rebateDetailDao.queryRebateDetailByOrderId(rebateDetailQuery)) {
                rebateDetailDao.insert(rebateDetail);
            } else {
                rebateDetailDao.update(rebateDetail);
            }

        }
    }


    @Test
    public void query() {
        RebateDetailQuery query = new RebateDetailQuery();
        query.setSubUnionIds("'JD100001251','JD100001251'");
        int count = rebateDetailDao.findCount(query);

        query.setStartRow(0);
        query.setPageSize(100);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -100);
        query.setStartDate(cal.getTime());

        List list = rebateDetailDao.findList(query);
        System.out.println("count," + count + ",list" + JsonUtil.toJson(list));
    }

    @Test
    public void getRecommendUserOrderSummaryByOpenId() {
        Calendar cal = Calendar.getInstance();


        RebateDetailQuery  rebateDetailQuery = new RebateDetailQuery();
        rebateDetailQuery.setOpenId("oIAUmv0flnzqXPCEF03ZB5lOyVkg");
        rebateDetailQuery.setEndDate(cal.getTime());
        cal.add(Calendar.DATE, -1);
        rebateDetailQuery.setStartDate(cal.getTime());

        OrderSummary orderSummary = rebateDetailDao.getRecommendUserOrderSummaryByOpenId(rebateDetailQuery);
        System.out.println(",orderSummary" + JsonUtil.toJson(orderSummary));
    }

}

package com.rebate.test;


import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.manager.jd.JdSdkManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.text.SimpleDateFormat;
import java.util.*;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class RebateDetailDaoTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("rebateDetailDao")
    @Autowired
    private RebateDetailDao rebateDetailDao;
    @Autowired
    private JdSdkManager jdSdkManager;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void batchInsert() {
        for(int i =1000;i<1100;i++){
            RebateDetail rebateDetail = new RebateDetail();
            rebateDetail.setOpenId("weishi2010");
            rebateDetail.setCommission(100.0);
            rebateDetail.setCommissionRatio(80.0);
            rebateDetail.setFinishDate(new Date());
            rebateDetail.setOrderId(1l+i);
            rebateDetail.setProductId(1l);
            rebateDetail.setPrice(100.0);
            rebateDetail.setOrderStatus(0);
            rebateDetail.setStatus(0);
            rebateDetail.setProductCount(10);
            rebateDetail.setRebateRatio(50.0);
            rebateDetail.setSubmitDate(new Date());
            rebateDetailDao.insert(rebateDetail);

        }
    }


    @Test
    public void query() {
        RebateDetailQuery query = new RebateDetailQuery();
        query.setOpenId("weishi2010");
        int count = rebateDetailDao.findCountByOpenId(query);

        query.setStartRow(0);
        query.setPageSize(100);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-100);
        query.setEndDate(cal.getTime());

        List list = rebateDetailDao.findListByOpenId(query);
        System.out.println("count,"+count+",list"+JsonUtil.toJson(list));
    }


    @Test
    public void testGetMediaProducts(){
        String queryTime = "20170922";//yyyyMMddHHmm,yyyyMMddHHmmss或者yyyyMMddHH格式之一
        int page =1;
        int pageSize = 10;
        List list = jdSdkManager.getRebateDetails(queryTime,page,pageSize);
        System.out.println("list:"+JsonUtil.toJson(list));
    }


}

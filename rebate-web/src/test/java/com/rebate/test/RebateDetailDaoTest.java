package com.rebate.test;


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
import java.util.Calendar;
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
        query.setSubUnionId("JD100001251");
        int count = rebateDetailDao.findCountBySubUnionId(query);

        query.setStartRow(0);
        query.setPageSize(100);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -100);
        query.setStartDate(cal.getTime());

        List list = rebateDetailDao.findListBySubUnionId(query);
        System.out.println("count," + count + ",list" + JsonUtil.toJson(list));
    }


}

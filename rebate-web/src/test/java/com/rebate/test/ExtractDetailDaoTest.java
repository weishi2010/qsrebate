package com.rebate.test;


import com.rebate.common.util.JsonUtil;
import com.rebate.dao.ExtractDetailDao;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.query.ExtractDetailQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class ExtractDetailDaoTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("extractDetailDao")
    @Autowired
    private ExtractDetailDao extractDetailDao;

    @Test
    public void testInsert() {
        for (int i = 0; i < 100; i++) {
            ExtractDetail extractDetail = new ExtractDetail();
            extractDetail.setExtractPrice(100.0);
            extractDetail.setStatus(0);
            extractDetail.setOpenId("weishi2010");
            extractDetailDao.insert(extractDetail);
        }

    }

    @Test
    public void testFindExtractDetailList() throws ParseException {
        SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int year = 2017;
        ExtractDetailQuery extractDetailQuery = new ExtractDetailQuery();
        extractDetailQuery.setOpenId("weishi2010");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        extractDetailQuery.setBeginDate(calendar.getTime());

        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        extractDetailQuery.setEndDate(format.parse(endFormat.format(calendar.getTime())));

        int count = extractDetailDao.findExtractDetailCount(extractDetailQuery);
        if (count > 0) {
            List<ExtractDetail> list = extractDetailDao.findExtractDetailList(extractDetailQuery);
            System.out.println("list:" + JsonUtil.toJson(list));
        }
    }


}

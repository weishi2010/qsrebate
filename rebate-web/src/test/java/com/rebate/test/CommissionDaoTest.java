package com.rebate.test;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.CommissionDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Commission;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class CommissionDaoTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("commissionDao")
    @Autowired(required = true)
    private CommissionDao commissionDao;

    @Test
    public void testInsert() {
        Commission commission = new Commission();
        commission.setOpenId("weishi2010");
        commission.setTotalCommission(5000.0);
        commission.setStatus(0);
        commissionDao.insert(commission);

    }


    @Test
    public void findCommissionByOpenId() {
        Commission commission = new Commission();
        commission.setOpenId("weishi2010");
        Commission existsCommission = commissionDao.findCommissionByOpenId(commission);
        System.out.println("existsCommission:" + JsonUtil.toJson(existsCommission));
    }



}
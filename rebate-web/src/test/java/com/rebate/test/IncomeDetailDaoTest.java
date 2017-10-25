package com.rebate.test;

import com.rebate.dao.IncomeDetailDao;
import com.rebate.domain.IncomeDetail;
import com.rebate.domain.en.EIncomeType;
import com.rebate.domain.query.IncomeDetailQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class IncomeDetailDaoTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("incomeDetailDao")
    @Autowired(required = true)
    private IncomeDetailDao incomeDetailDao;

    @Test
    public void testInsert() {
        IncomeDetail incomeDetail = new IncomeDetail();
        incomeDetail.setOpenId("weishi2010");
        incomeDetail.setIncome(5000.0);
        incomeDetail.setStatus(0);
        incomeDetail.setDealt(0);
        incomeDetail.setReferenceId(123l);
        incomeDetail.setType(EIncomeType.REGIST.getCode());
        incomeDetailDao.insert(incomeDetail);

        incomeDetail.setIncome(1000.0);
        incomeDetail.setType(EIncomeType.EXTRACT.getCode());
        incomeDetailDao.insert(incomeDetail);

    }


    @Test
    public void findCommissionByOpenId() {
        IncomeDetailQuery incomeDetailQuery = new IncomeDetailQuery();
        incomeDetailQuery.setOpenId("weishi2010");
        incomeDetailQuery.setTypeList(EIncomeType.REGIST.getCode()+","+EIncomeType.FIRST_ORDER_REBATE.getCode());
        Double income = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);

        incomeDetailQuery.setTypeList(EIncomeType.EXTRACT.getCode()+"");
        Double payment = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);

        System.out.println("total:" + (income-payment));
    }



}
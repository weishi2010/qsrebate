package com.rebate.test;

import com.google.common.base.Joiner;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.CommissionDao;
import com.rebate.dao.IncomeDetailDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Commission;
import com.rebate.domain.en.EIncomeType;
import com.rebate.domain.query.IncomeDetailQuery;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class CommissionDaoTest extends AbstractJUnit4SpringContextTests {

    @Qualifier("commissionDao")
    @Autowired(required = true)
    private CommissionDao commissionDao;

    @Qualifier("incomeDetailDao")
    @Autowired(required = true)
    private IncomeDetailDao incomeDetailDao;

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

    @Test
    public void updateUserCommission() {

        String openIds = "oIAUmvxE2B8M53Rn5S7_5ctIQqzA,oIAUmv8x60aC5B7FrxVy8Z9-imyY,oIAUmvxREo4dW81EKTNmQr74gctw,oIAUmvzyrx4lw3gtk-dl57uz0vm8,oIAUmv53Q1Uo_V_sk8e5xKBPOteg,oIAUmv-kZuZE6m-DUo4eSiVQ8oVM,oIAUmv-kI-nJDIk_mCxs2Z079wJY,oIAUmv5nmfq2ZfnewTfCMO3rnB-g,oIAUmvwu8emjgrj1a7FU6ZSkabZo,oIAUmv21myUMlOeImfoGcoaKD-H4,oIAUmv9ojvvmwOhLFlY9uYTntJQM,oIAUmv4thty1TbHtF5LbFvmfWuUY,oIAUmv3wAfqHlFMgy6bEGdy0xIcw,oIAUmv8xB0SRbYt4ETBWIpT9gjo4,oIAUmvxnOUWoAwRC-peq0ZYPFP74,oIAUmv2pqc9qaPObIpeE1p2vgX9w,oIAUmv_Al7euAO_cSrOVVXoSYUdo,oIAUmv28cc3xoGfshKucAC9iUriA,oIAUmv5J0dvbV15pX_HQaf2PS8Bs,oIAUmv8E-ya56jCf6HKCP3Y0G220,oIAUmv_rs0DcPXm15kfLZXrXzqjA,oIAUmv6MOf5QDWR0ruROo5HMX1JQ,oIAUmvz_JU7vFTtdRqZGE5d2hPWo,oIAUmvxq5XTeZJ_Yb38e90A24_7g";

        for(String openId:openIds.split(",")){
            updateUserCommissionByOpenId(openId);
        }

    }

    private void updateUserCommissionByOpenId(String openId){
        //计算收入、支出
        IncomeDetailQuery incomeDetailQuery = new IncomeDetailQuery();
        incomeDetailQuery.setOpenId(openId);
        List<Integer> codes = new ArrayList<>();
        codes.add(EIncomeType.REGIST.getCode());
        codes.add(EIncomeType.FIRST_ORDER_REBATE.getCode());
        codes.add(EIncomeType.FIRST_AGENT_REBATE.getCode());
        codes.add(EIncomeType.SECOND_AGENT_REBATE.getCode());
        codes.add(EIncomeType.SECOND_ORDER_REBATE.getCode());
        codes.add(EIncomeType.GENERAL_ORDER_REBATE.getCode());
        incomeDetailQuery.setTypeList(Joiner.on(",").join(codes));
        Double income = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if (null == income) {
            income = 0.0;
        }

        incomeDetailQuery.setTypeList(EIncomeType.EXTRACT.getCode() + "");
        Double payment = incomeDetailDao.findIncomeStatistisByType(incomeDetailQuery);
        if (null == payment) {
            payment = 0.0;
        }

        Double totalCommission = income - payment;//收入减少余额
        //更新用户提现余额
        Commission commission = new Commission();
        commission.setOpenId(openId);
        commission.setTotalCommission(totalCommission);
        Commission userCommission = commissionDao.findCommissionByOpenId(commission);

        if (null == userCommission) {
            commission.setStatus(0);
            commissionDao.insert(commission);
        } else {
            userCommission.setTotalCommission(totalCommission);
            commissionDao.updateTotalCommission(commission);

        }
    }

}
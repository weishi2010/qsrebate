package com.rebate.service.job.impl;

import com.rebate.dao.CommissionDao;
import com.rebate.dao.ProductDao;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.Commission;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.job.RebateJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("rebateJob")
public class RebateJobImpl implements RebateJob {
    private static final Logger LOG = LoggerFactory.getLogger(RebateJobImpl.class);

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("rebateDetailDao")
    @Autowired(required = true)
    private RebateDetailDao rebateDetailDao;

    @Qualifier("commissionDao")
    @Autowired(required = true)
    private CommissionDao commissionDao;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Override
    public void importMediaOrder() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        int page = 1;
        int pageSize = 100;
        //获取前一天的订单
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        String queryTime = format.format(calendar.getTime());
        List<RebateDetail> rebateDetails = jdSdkManager.getRebateDetails(queryTime, page, pageSize);

        for (RebateDetail rebateDetail : rebateDetails) {
            RebateDetailQuery rebateDetailQuery = new RebateDetailQuery();
            rebateDetailQuery.setOrderId(rebateDetail.getOrderId());
            rebateDetailQuery.setOpenId(rebateDetail.getOpenId());
            if (null != rebateDetailDao.queryRebateDetailByOrderId(rebateDetailQuery)) {
                //1.插入明细
                rebateDetailDao.insert(rebateDetail);
                //2.重新获取用户可用余额
                Double totalCommission = rebateDetailDao.findUserTotalCommission(rebateDetailQuery);
                //3.重新计算用户余额
                Commission commission = new Commission();
                commission.setOpenId(rebateDetail.getOpenId());
                commission.setTotalCommission(totalCommission);
                commissionDao.updateTotalCommission(commission);
            }

        }
    }

    @Override
    public void importMediaThemeProducts() {
        int page = 1;
        int pageSize = 1000;
        List<Product> products = jdSdkManager.getMediaThemeProducts(page, pageSize);
        while (products.size() > 0) {
            LOG.error("[importMediaThemeProducts]商品导入任务!size:", products.size());
            for (Product product : products) {
                if (null == productDao.findById(product)) {
                    productDao.insert(product);
                }
            }
            page++;
            products = jdSdkManager.getMediaThemeProducts(page, pageSize);
        }

    }
}

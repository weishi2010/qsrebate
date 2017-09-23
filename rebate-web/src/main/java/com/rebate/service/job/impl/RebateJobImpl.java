package com.rebate.service.job.impl;

import com.rebate.dao.*;
import com.rebate.domain.Commission;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.en.ERebateDetailStatus;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.job.RebateJob;
import org.apache.commons.lang.StringUtils;
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

    @Qualifier("extractDetailDao")
    @Autowired(required = true)
    private ExtractDetailDao extractDetailDao;

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Override
    public void importMediaOrder() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int page = 1;
        int pageSize = 100;
        //获取前一天的订单
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String queryTime = format.format(calendar.getTime());
        List<RebateDetail> rebateDetails = jdSdkManager.getRebateDetails(queryTime, page, pageSize);

        for (RebateDetail rebateDetail : rebateDetails) {
            RebateDetailQuery rebateDetailQuery = new RebateDetailQuery();
            rebateDetailQuery.setOrderId(rebateDetail.getOrderId());
            rebateDetailQuery.setProductId(rebateDetail.getProductId());
            rebateDetailQuery.setSubUnionId(rebateDetail.getSubUnionId());
            if (null == rebateDetailDao.queryRebateDetailByOrderId(rebateDetailQuery)) {
                //查询商品，如果为不可返佣商品则返佣金额设置为0
                Product productQuery = new Product();
                productQuery.setProductId(rebateDetail.getProductId());
                Product product = productDao.findById(productQuery);
                if (null == product || 0 == product.getIsRebate()) {
                    rebateDetail.setCommission(0.0);
                }

                //插入明细
                rebateDetailDao.insert(rebateDetail);

                if(StringUtils.isNotBlank(rebateDetail.getSubUnionId())){

                    //重新获取用户可用余额
                    RebateDetailQuery userTotalCommissionQuery = new RebateDetailQuery();
                    userTotalCommissionQuery.setSubUnionId(rebateDetail.getSubUnionId());
                    userTotalCommissionQuery.setStatus(ERebateDetailStatus.SETTLEMENT.getCode());//查询已经结算总的用户返佣金额
                    Double totalUserCommission = rebateDetailDao.findUserTotalCommission(userTotalCommissionQuery);


                    UserInfo userInfoQuery = new UserInfo();
                    userInfoQuery.setSubUnionId(rebateDetail.getSubUnionId());
                    UserInfo userInfo = userInfoDao.findLoginUserInfo(userInfoQuery);
                    if (null != userInfo) {

                        //查询已提现金额
                        ExtractDetailQuery extractDetailQuery = new ExtractDetailQuery();
                        extractDetailQuery.setOpenId(userInfo.getOpenId());
                        extractDetailQuery.setStatus(EExtractStatus.PAYMENT.getCode());//已经付款状
                        Double extractPrice = extractDetailDao.findUserTotalExtract(extractDetailQuery);

                        //计算用户可提现余额
                        if (null != totalUserCommission) {
                            //用户余额=用户总返佣金额-已提现金额
                            Double totalCommission = totalUserCommission;
                            if (null != extractPrice) {
                                totalCommission = totalUserCommission - extractPrice;
                            }

                            //查询用户可提现余额，已存在则更新，否则进行插入
                            Commission commission = new Commission();
                            commission.setOpenId(rebateDetail.getOpenId());
                            commission.setTotalCommission(totalCommission);
                            Commission  userCommission = commissionDao.findCommissionByOpenId(commission);

                            if (null == userCommission) {
                                commission.setStatus(0);
                                commissionDao.insert(commission);
                            } else {
                                userCommission.setTotalCommission(totalCommission);
                                commissionDao.updateTotalCommission(commission);

                            }

                        }
                    }
                }


            } else {
                rebateDetailDao.update(rebateDetail);
            }

        }
    }

    @Override
    public void importMediaThemeProducts() {
//        int page = 1;
//        int pageSize = 1000;
//        List<Product> products = jdSdkManager.getMediaThemeProducts(page, pageSize);
//        while (products.size() > 0) {
//            LOG.error("[importMediaThemeProducts]商品导入任务!size:" + products.size());
//            for (Product product : products) {
//                if (null == productDao.findById(product)) {
//                    productDao.insert(product);
//                } else {
//
//                    //存在则更新
//                    productDao.update(product);
//                }
//            }
//            page++;
//            products = jdSdkManager.getMediaThemeProducts(page, pageSize);
//        }

    }
}

package com.rebate.service.extract.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.CommissionDao;
import com.rebate.dao.ExtractDetailDao;
import com.rebate.dao.IncomeDetailDao;
import com.rebate.domain.Commission;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.IncomeDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EExtractCode;
import com.rebate.domain.en.EIncomeType;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.IncomeDetailQuery;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.userinfo.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("extractDetailService")
public class ExtractDetailServiceImpl implements ExtractDetailService {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractDetailServiceImpl.class);

    @Qualifier("extractDetailDao")
    @Autowired
    private ExtractDetailDao extractDetailDao;

    @Qualifier("commissionDao")
    @Autowired(required = true)
    private CommissionDao commissionDao;


    @Qualifier("incomeDetailDao")
    @Autowired(required = true)
    private IncomeDetailDao incomeDetailDao;

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @Override
    public EExtractCode extract(ExtractDetail extractDetail) {
        EExtractCode code = EExtractCode.SUCCESS;
        try {
            //查询用户可提现余额
            Commission commissionQuery = new Commission();
            commissionQuery.setOpenId(extractDetail.getOpenId());
            Commission commission = commissionDao.findCommissionByOpenId(commissionQuery);

            //提现金额小于等于账户余额且余额要大于20元
            if (null != commission && commission.getTotalCommission() >= extractDetail.getExtractPrice() && commission.getTotalCommission()>20) {
                //插入提现明细
                long extractId = extractDetailDao.insert(extractDetail);

                //插入支出记录
                IncomeDetail incomeDetail = new IncomeDetail();
                incomeDetail.setOpenId(extractDetail.getOpenId());
                incomeDetail.setReferenceId(extractId);
                incomeDetail.setIncome(extractDetail.getExtractPrice());
                incomeDetail.setStatus(0);
                incomeDetail.setDealt(0);
                incomeDetail.setType(EIncomeType.EXTRACT.getCode());
                incomeDetailDao.insert(incomeDetail);

                //更新用户提现 余额
                userInfoService.updateUserCommission(extractDetail.getOpenId());

            }else{
                code = EExtractCode.LACK_BALANCE;
            }

        } catch (Exception e) {
            code = EExtractCode.SYSTEM_ERROR;
            LOG.error("[extract]提现异常!", e);
        }
        return code;
    }

    @Override
    public void updateExtractDetail(ExtractDetail extractDetail) {
        extractDetailDao.updateExtractDetail(extractDetail);
    }

    @Override
    public PaginatedArrayList<ExtractDetailVo> findExtractDetailList(ExtractDetailQuery extractDetailQuery) {
        PaginatedArrayList<ExtractDetailVo> vos = new PaginatedArrayList<ExtractDetailVo>(extractDetailQuery.getIndex(), extractDetailQuery.getPageSize());
        try {
            int totalItem = extractDetailDao.findExtractDetailCount(extractDetailQuery);
            if (totalItem > 0) {
                vos.setTotalItem(totalItem);
                extractDetailQuery.setStartRow(vos.getStartRow());
               List<ExtractDetail> list =  extractDetailDao.findExtractDetailList(extractDetailQuery);
               for(ExtractDetail detail :list){
                   ExtractDetailVo vo = new ExtractDetailVo(detail);
                   UserInfo userInfo = userInfoService.getUserInfo(detail.getOpenId());
                   if (null != userInfo) {
                       vo.setNickName(userInfo.getNickName());
                   }
                   vos.add(vo);
               }
            }

        } catch (Exception e) {
            LOG.error("findExtractDetailList error!query:" + JsonUtil.toJson(extractDetailQuery), e);
        }
        return vos;
    }
}

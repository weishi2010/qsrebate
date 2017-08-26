package com.rebate.service.extract.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.CommissionDao;
import com.rebate.dao.ExtractDetailDao;
import com.rebate.domain.Commission;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.en.EExtractCode;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.service.extract.ExtractDetailService;
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

    @Override
    public int extract(ExtractDetail extractDetail) {
        int code = EExtractCode.SUCCESS.getCode();
        try {
            //查询用户可提现余额
            Commission commissionQuery = new Commission();
            commissionQuery.setOpenId(extractDetail.getOpenId());
            Commission commission = commissionDao.findCommissionByOpenId(commissionQuery);
            if (null != commission && commission.getTotalCommission() >= extractDetail.getExtractPrice()) {
                //有可提现余额时插入提现明细
                extractDetailDao.insert(extractDetail);
            }else{
                code = EExtractCode.LACK_BALANCE.getCode();
            }

        } catch (Exception e) {
            code = EExtractCode.SYSTEM_ERROR.getCode();
            LOG.error("[extract]提现异常!", e);
        }
        return code;
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
                   vos.add(new ExtractDetailVo(detail));
               }
            }

        } catch (Exception e) {
            LOG.error("findExtractDetailList error!query:" + JsonUtil.toJson(extractDetailQuery), e);
        }
        return vos;
    }
}

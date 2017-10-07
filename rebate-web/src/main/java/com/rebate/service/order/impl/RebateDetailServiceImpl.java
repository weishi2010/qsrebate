package com.rebate.service.order.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.ProductDao;
import com.rebate.dao.RebateDetailDao;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.service.order.RebateDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("rebateDetailService")
public class RebateDetailServiceImpl implements RebateDetailService {
    private static final Logger LOG = LoggerFactory.getLogger(RebateDetailService.class);
    @Qualifier("rebateDetailDao")
    @Autowired
    private RebateDetailDao rebateDetailDao;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Override
    public PaginatedArrayList<RebateDetailVo> findRebateDetailList(RebateDetailQuery rebateDetailQuery) {
        PaginatedArrayList<RebateDetailVo> rebateDetails = new PaginatedArrayList<RebateDetailVo>(rebateDetailQuery.getIndex(), rebateDetailQuery.getPageSize());
        try {
            int totalItem = rebateDetailDao.findCountBySubUnionId(rebateDetailQuery);
            if (totalItem > 0) {
                rebateDetails.setTotalItem(totalItem);
                rebateDetailQuery.setStartRow(rebateDetails.getStartRow());
                if(rebateDetailQuery.getIndex()<=rebateDetails.getTotalPage()) {

                    List<RebateDetail> list = rebateDetailDao.findListBySubUnionId(rebateDetailQuery);
                    for (RebateDetail rebateDetail : list) {
                        RebateDetailVo vo = new RebateDetailVo(rebateDetail);
                        rebateDetails.add(vo);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("findRebateDetailList error!rebateDetailQuery:" + JsonUtil.toJson(rebateDetailQuery), e);
        }
        return rebateDetails;
    }
}

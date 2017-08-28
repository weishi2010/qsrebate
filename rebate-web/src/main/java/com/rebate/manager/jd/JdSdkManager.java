package com.rebate.manager.jd;

import com.rebate.domain.OrderSummary;
import com.rebate.domain.Product;
import com.rebate.domain.RebateDetail;

import java.util.List;

/**
 * JD SDK
 */
public interface JdSdkManager {
    List<Product> getMediaProducts(String skuIds);

    List<RebateDetail> getRebateDetails(String queryTime,int page,int pageSize);
}

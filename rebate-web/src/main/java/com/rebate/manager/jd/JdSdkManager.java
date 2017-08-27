package com.rebate.manager.jd;

import com.rebate.domain.Product;

import java.util.List;

/**
 * JD SDK
 */
public interface JdSdkManager {
    List<Product> getMediaProduct(String skuIds);
}

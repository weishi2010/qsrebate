package com.rebate.domain.query;

import com.rebate.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 商品信息查询
 */
public class ProductQuery extends Product{

    @Getter
    @Setter
    private Double queryPrice;
}

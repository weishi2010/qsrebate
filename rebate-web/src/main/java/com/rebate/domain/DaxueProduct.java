package com.rebate.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 掌上大学商品信息
 */
public class DaxueProduct extends Product {
    /**
     * 链接
     */
    @Getter
    @Setter
    private String promotionUrl;

}

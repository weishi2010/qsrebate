package com.rebate.manager;

import com.rebate.domain.Product;
import com.rebate.domain.vo.ProductVo;

/**
 * 消息模板manager
 * @author little four
 * @version 1.0.0
 */
public interface MessageTempManager {

    /**
     * 获取代理单品推广消息模板
     * @param productVo
     * @param mediaUrl
     * @return
     */
    String getAgentProductMessageTemp(ProductVo productVo, String mediaUrl);

    /**
     * 获取普通用户单品推广消息模板
     * @param product
     * @param mediaUrl
     * @return
     */
    String getRebateUserProductMessageTemp(Product product, String mediaUrl);

    /**
     * 获取代理单品推广消息模板
     * @param product
     * @param mediaUrl
     * @return
     */
    String getAgentProductMessageTemp(Product product, String mediaUrl);
}

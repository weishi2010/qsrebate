package com.rebate.manager.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.domain.Product;
import com.rebate.domain.en.EProductFreePost;
import com.rebate.domain.en.EProudctRebateType;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.MessageTempManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("messageTempManager")
public class MessageTempManagerImpl implements MessageTempManager {
    private static final Logger LOG = LoggerFactory.getLogger(MessageTempManagerImpl.class);

    @Override
    public String getAgentProductMessageTemp(ProductVo productVo, String mediaUrl) {
        LOG.error("getAgentProductMessageTemp,productVo:"+ JsonUtil.toJson(productVo));
        StringBuffer content = new StringBuffer();
        StringBuffer promotionPre = new StringBuffer();

        if (productVo.getFreePost() == EProductFreePost.FREE_POST.getCode() || productVo.getCouponPrice() > 0) {
            promotionPre.append("【");
            if (productVo.getFreePost() == EProductFreePost.FREE_POST.getCode()) {
                promotionPre.append("包邮");
            }
            if (null != productVo.getCouponPrice() && productVo.getCouponPrice() > 0) {
                promotionPre.append(" 优惠券" + productVo.getProductCoupon().getDiscount() + "元");
            }
            promotionPre.append("】");
        }

        content.append("\uD83D\uDD25 " + promotionPre.toString() + productVo.getName() + "\n\n");
        content.append("-----------\n");
        //可返钱
        content.append("◆原价：").append(productVo.getOriginalPrice()).append("元\n");

        if (null != productVo.getProductCoupon() && productVo.getProductCoupon().getCouponPrice() > 0) {
            content.append("◆券后价：").append(productVo.getCouponPrice()).append("元\n\n");
        }

        //推广地址
        content.append("\uE231购买链接：").append(mediaUrl).append("\n");
        content.append("-----------\n");
        content.append("京东商城  正品保证\n\n");

        return content.toString();
    }

    @Override
    public String getRebateUserProductMessageTemp(boolean isRebate,Double commission, String mediaUrl) {
        StringBuffer recommendContent = new StringBuffer();
        //商品名

        if (isRebate) {
            recommendContent.append("已成功转成返钱链接，从返利链接下单，才可以返钱哦！\n\n");
            //可返钱
            recommendContent.append("[Packet]预估返钱：").append(commission).append("元\n\n");
            //推广地址
            recommendContent.append("/:gift返钱链接：").append(mediaUrl).append("");
        }else{
            //推广地址
            recommendContent.append("[Packet]此商品是内购商品，不返钱哈！您可以领取独家内购券：\n").append(mediaUrl).append("\n\n");
        }

        return recommendContent.toString();

    }

    @Override
    public String getAgentProductMessageTemp(Product product, String mediaUrl) {
        StringBuffer recommendContent = new StringBuffer();
        //商品名
        recommendContent.append("已成功转成推广链接！\n\n");
        //可返钱
        recommendContent.append("[Packet]预估佣金：").append(product.getUserCommission()).append("元\n\n");
        //推广地址
        recommendContent.append("/:gift推广链接：").append(mediaUrl).append("");
        return recommendContent.toString();

    }
}

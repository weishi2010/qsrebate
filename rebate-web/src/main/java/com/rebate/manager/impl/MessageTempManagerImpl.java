package com.rebate.manager.impl;

import com.rebate.domain.Product;
import com.rebate.domain.en.EProductFreePost;
import com.rebate.domain.en.EProudctRebateType;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.MessageTempManager;
import org.springframework.stereotype.Component;

@Component("messageTempManager")
public class MessageTempManagerImpl implements MessageTempManager {

    @Override
    public String getAgentProductMessageTemp(ProductVo productVo, String mediaUrl) {
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

        if (null != productVo.getCouponPrice() && productVo.getCouponPrice() > 0) {
            content.append("◆券后价：").append(productVo.getCouponPrice()).append("元\n\n");
        }

        //推广地址
        content.append("\uE231购买链接：").append(mediaUrl).append("\n");
        content.append("-----------\n");
        content.append("京东商城  正品保证\n\n");

        return content.toString();
    }

    @Override
    public String getRebateUserProductMessageTemp(Product product, String mediaUrl) {
        StringBuffer recommendContent = new StringBuffer();
        //商品名
        recommendContent.append("已成功转成返钱链接，从返利链接下单，才可以返钱哦！\n\n");
        if(null!=product && null!=product.getIsRebate() && product.getIsRebate()== EProudctRebateType.NOT_REBATE.getCode()) {

            //可返钱
            recommendContent.append("[Packet]预估返钱：").append(product.getUserCommission()).append("元\n\n");
        }else{
            recommendContent.append("[Packet]内购券商品不返佣!\n\n");
        }
        //推广地址
        recommendContent.append("/:gift返钱链接：").append(mediaUrl).append("");
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

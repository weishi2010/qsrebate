package com.rebate.common.util.rebate;

import com.rebate.domain.en.EProudctRebateType;

import java.math.BigDecimal;

/**
 * 返佣规则工具类
 */
public class RebateRuleUtil {

    /**
     * 返佣限制
     * 说明：联明返还商品佣金达到此限制值再给用户进行返利
     */
    public static final double COMMISSION_LIMIT = 2.0;

    /**
     * 平台抽成比例
     */
    public static final double PLATFORM_USER_COMMISSION_RATIO = 0.5;

    /**
     * 是否给商品进行返佣
     *
     * @param commission
     * @param isCouponProduct
     * @return
     */
    public static boolean isRebate(Double commission, boolean isCouponProduct) {
        if (isCouponProduct) {
            return false;
        }

        if (commission >= COMMISSION_LIMIT) {
            return true;
        }

        return false;
    }

    /**
     * 优惠券商品返利规则
     * @return
     */
    public static int couponProductRebateRule(Double commission){
        //TODO 优惠券规则设置，目前优惠券商品都不返利
        return EProudctRebateType.REBATE.getCode();
    }

    /**
     * 用户返佣金额，平台抽成后
     * @param commission
     * @return
     */
    public static double getJDUserCommission(Double commission){
        double userCommission = PLATFORM_USER_COMMISSION_RATIO*commission;
        return new BigDecimal(userCommission).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}

package com.rebate.common.util.rebate;

/**
 * 返佣规则工具类
 */
public class RebateRuleUtil {

    /**
     * 返佣限制
     * 说明：联明返还商品佣金达到此限制值再给用户进行返利
     */
    private static final double COMMISSION_LIMIT = 2.0;

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
}

package com.rebate.common.util.rebate;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 优惠券工具类
 */
public class CouponUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CouponUtil.class);


    /**
     * 获取优惠券限额
     * @param couponNote
     * @return
     */
    public static Double getCouponQuota(String couponNote){
        Double quota = 0.0;
        if(StringUtils.isBlank(couponNote)){
            return quota;
        }

        String[] array = couponNote.replace("券","").split("减");
        try{

            quota = Double.parseDouble(array[1]);
        }catch (Exception e){
            LOG.error("getCouponQuota error!couponNote:{}",couponNote,e);
        }
        return quota;
    }

    /**
     * 获取优惠券面额
     * @param couponNote
     * @return
     */
    public static Double getCouponDiscount(String couponNote){
        Double discount = 0.0;
        if(StringUtils.isBlank(couponNote)){
            return discount;
        }

        String[] array = couponNote.replace("券","").split("减");
        try{

            discount = Double.parseDouble(array[0]);
        }catch (Exception e){
            LOG.error("getCouponDiscount error!couponNote:{}",couponNote,e);
        }
        return discount;

    }
}

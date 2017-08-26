package com.rebate.common.util.des;


import com.rebate.common.security.DESCoder;
import com.rebate.common.util.EncodeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * @author weishi
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2017/8/2 13:06  weishi 新建代码
 * =========================== 维护日志 ===========================
 */
public class DESUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DESUtil.class);

    /**
     * 加密
     * @param key  必须为32位
     * @param value
     * @param charsetName
     * @return
     */
    public static String encrypt(String key, String value, String charsetName) {
        String encryptValue = "";
        try {
            byte[] data;
            if (!StringUtils.isEmpty(charsetName)) {
                try {
                    data = value.getBytes(charsetName);
                } catch (Exception var5) {
                    LOG.error("charset " + charsetName + " Unsupported!", var5);
                    data = value.getBytes();
                }
            } else {
                data = value.getBytes();
            }

            encryptValue = EncodeUtils.base64Encode(DESCoder.encrypt(data, key));
        } catch (Exception e) {
            LOG.error("encrypt error!", e);
        }
        return encryptValue;
    }

    /**
     * 解密
     * @param key 必须为32位
     * @param value
     * @param charsetName
     * @return
     */
    public static String decrypt(String key, String value, String charsetName) {
        try {
            byte[] data = EncodeUtils.base64Decode(value);
            byte[] bytes = DESCoder.decrypt(data, key);
            if (!StringUtils.isEmpty(charsetName)) {
                try {
                    return new String(bytes, charsetName);
                } catch (UnsupportedEncodingException var6) {
                    LOG.error("charset " + charsetName + " Unsupported!", var6);
                    return new String(bytes);
                }
            } else {
                return new String(bytes);
            }
        } catch (Exception var7) {
            LOG.error("encrypt error", var7);
            return null;
        }
    }

    public static void main(String[] args) {
        String str = "123";
        String key = "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq";//32位的key
        // DES数据加密
        String s1 = encrypt(key, str, "UTF-8");
        System.out.println(s1);
        // DES数据解密
        String s2 = decrypt(key, s1,"UTF-8");
        System.err.println(s2);
    }
}

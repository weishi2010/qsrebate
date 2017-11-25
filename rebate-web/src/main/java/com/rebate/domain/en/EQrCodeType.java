package com.rebate.domain.en;

/**
 * 代理模式枚举
 */
public enum EQrCodeType {

    /**
     * 开通代理模式一二维码
     */
    REGIST_FIRST_AGENT_QR_CODE(1),

    /**
     * 代理模式一推广下级代理二维码
     */
    REGIST_FIRST_AGENT_NEXT_AGENT_QR_CODE(4),

    /**
     * 开通代理二二维码
     */
    REGIST_SECOND_AGENT_QR_CODE(2),

    /**
     * 代理模式二拉粉二维码
     */
    REGIST_SECOND_AGENT_REBATE_USER_QR_CODE(3);
    private int code;

    EQrCodeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}

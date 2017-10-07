package com.rebate.domain.en;

/**
 * 微信点击事件代码
 */
public enum EWxEventCode {


    /**
     * 怎么返？菜单
     */
    QS_WX_CLICK001("QS_WX_CLICK001");

    private String value;

    EWxEventCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

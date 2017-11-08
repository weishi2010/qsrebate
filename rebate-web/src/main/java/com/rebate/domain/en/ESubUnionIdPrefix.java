package com.rebate.domain.en;

/**
 * 子联盟账号前缀
 */
public enum ESubUnionIdPrefix {
    /**
     * 管理员子联盟ID前缀
     */
    ADMIN("QS_ADM_"),
    /**
     * JD联盟
     */
    JD("JD"),

    /**
     * 淘宝
     */
    TAOBAO("TB");
    private String code;

    ESubUnionIdPrefix(String code) {
        this.code = code;
    }

    /**
     * 获取子联盟ID
     * @param code
     * @param id
     * @return
     */
    public static String getSubUnionId(String code,Long id){
        for(ESubUnionIdPrefix eSubUnionIdPrefix:values()){
            if(eSubUnionIdPrefix.getCode().equalsIgnoreCase(code)){
                return eSubUnionIdPrefix.getCode()+id;
            }
        }
        return id.toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}

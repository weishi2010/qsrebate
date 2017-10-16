package com.rebate.domain.en;

/**
 * 广告位
 */
public enum EAdPosition {
    /**
     * 活动主图位置
     */
    MAIN(1,"活动主图位置");
    private int code;
    private String name;

    EAdPosition(int code, String name){
        this.code = code;
        this.name = name;
    }

    public static String getStatusShow(int code){
        for(EAdPosition eExtractStatus:values()){
            if(code==eExtractStatus.getCode()){
                return eExtractStatus.name;
            }
        }
        return "";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

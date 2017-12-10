package com.rebate.domain.en;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品来源
 */
public enum EProductSource {
    /**
     * 参数常
     */
    JD(1,0,"京东"),
    /**
     * 系统异常
     */
    TAOBAO(2,0,"淘宝"),
    /**
     * 亿玛优惠线报京东商品
     */
    YIMA(3,3000000,"亿玛优惠线报商品"),

    /**
     * 京推推京东商品
     */
    JINGTUITUI(4,2000000,"京推推商品"),

    /**
     * 友邻京东商品
     */
    YOULIN(5,1000000,"友邻京东商品");

    /**
     * 枚举代码
     */
    @Getter
    @Setter
    private int code;
    /**
     * 扩展排序默认值
     */
    @Getter
    @Setter
    private long extDefaultSortWeight;

    /**
     * 名称
     */
    @Getter
    @Setter
    private String name;

    EProductSource(int code,int extDefaultSortWeight, String name){
        this.code = code;
        this.extDefaultSortWeight = extDefaultSortWeight;
        this.name = name;
    }

    public static long getExtSortWeight(int code){
        for(EProductSource eProductSource:values()){
            if(code==eProductSource.getCode()){
                return eProductSource.extDefaultSortWeight;
            }
        }
        return 0l;
    }
}

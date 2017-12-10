//package com.rebate.domain.en;
//
//import lombok.Getter;
//import lombok.Setter;
//
///**
// * 商品导入来源
// */
//public enum EProudctImportSource {
//
//    /**
//     * 京推推商品
//     */
//    JINGTUITUI(1,1000000,"京推推商品"),
//    /**
//     * 优惠线报商品
//     */
//    YOUHUIXIANBAO(2,2000000,"优惠线报商品");
//
//    @Getter
//    @Setter
//    private int code;
//    @Getter
//    @Setter
//    private long extDefaultSortWeight;
//    @Getter
//    @Setter
//    private String name;
//
//    EProudctImportSource(int code,int extDefaultSortWeight, String name){
//        this.code = code;
//        this.extDefaultSortWeight = extDefaultSortWeight;
//        this.name = name;
//    }
//
//    public static long getExtSortWeight(int code){
//        for(EProudctImportSource eProudctImportSource:values()){
//            if(code==eProudctImportSource.getCode()){
//                return eProudctImportSource.extDefaultSortWeight;
//            }
//        }
//        return 0l;
//    }
//
//}

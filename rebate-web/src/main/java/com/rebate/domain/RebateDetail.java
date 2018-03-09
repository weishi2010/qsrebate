package com.rebate.domain;

import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 订单返利明细
 */
public class RebateDetail extends BaseQuery {

    /**
     * 编号
     */
    @Getter
    @Setter
    private Long id;
    /**
     * 微信openId
     */
    @Getter
    @Setter
    private String openId;

    /**
     * 订单编号
     */
    @Getter
    @Setter
    private Long orderId;

    /**
     * 商品编号
     */
    @Getter
    @Setter
    private Long productId;

    /**
     * 商品名称
     */
    @Getter
    @Setter
    private String productName;

    /**
     * 商品图片
     */
    @Getter
    @Setter
    private String imgUrl;
    /**
     * 购买金额
     */
    @Getter
    @Setter
    private Double price;

    /**
     * 订单金额
     */
    @Getter
    @Setter
    private Double totalMoney;

    /**
     * 商品数量
     */
    @Getter
    @Setter
    private Integer productCount;
    /**
     * 佣金金额
     */
    @Getter
    @Setter
    private Double commission;
    /**
     * 佣金比例
     */
    @Getter
    @Setter
    private Double commissionRatio;
    /**
     * 分成比例
     */
    @Getter
    @Setter
    private Double rebateRatio;
    /**
     * 创建时间
     */
    @Getter
    @Setter
    private Date submitDate;
    /**
     * 创建时间
     */
    @Getter
    @Setter
    private Date finishDate;
    /**
     * 订单状态
     */
    @Getter
    @Setter
    private Integer orderStatus;
    /**
     * 结算状态
     */
    @Getter
    @Setter
    private Integer status;
    /**
     * 联盟ID
     */
    @Getter
    @Setter
    private String unionId;
    /**
     *  子联盟ID
     */
    @Getter
    @Setter
    private String subUnionId;
    /**
     * 平台返利比例
     */
    @Getter
    @Setter
    private Double platformRatio;
    /**
     * 用户返佣金额
     */
    @Getter
    @Setter
    private Double userCommission;

    /**
     * 代理分佣
     */
    @Getter
    @Setter
    private Double agentCommission;


    /**
     * 推广位
     */
    @Getter
    @Setter
    private String positionId;

    /**
     * 无效码 1:有效,2:订单拆单,3:订单取消,
     * 4:京东帮帮主订单,5:账户异常,6:赠品类目,
     * 7:校园订单,8:企业订单,9:团购订单,
     * 10:开增值税专用发票订单,
     * 11:乡村推广员下单,12:自己推广自己下单,
     * 13:违规订单,14:订单来源与备案网址不符,-1:无效原因未知
     */
    @Getter
    @Setter
    private Integer validCode;


    /**
     * 创建时间
     */
    @Getter
    @Setter
    private Date created;
    /**
     * 修改时间
     */
    @Getter
    @Setter
    private Date modified;
}

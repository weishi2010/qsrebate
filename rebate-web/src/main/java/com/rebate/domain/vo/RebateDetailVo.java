package com.rebate.domain.vo;

import com.rebate.domain.RebateDetail;
import com.rebate.domain.en.ERebateDetailStatus;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 订单返利明细
 */
public class RebateDetailVo extends BaseQuery{

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

    @Getter
    @Setter
    private String productImg;

    /**
     * 购买金额
     */
    @Getter
    @Setter
    private Double price;
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
     * 状态
     */
    @Getter
    @Setter
    private Integer status;
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

    public RebateDetailVo(RebateDetail rebateDetail){
        BeanUtils.copyProperties(rebateDetail,this);
    }

    public String getOrderStatusShow(){
        return ERebateDetailStatus.getStatusShow(orderStatus);
    }
}

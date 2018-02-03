package com.rebate.domain.vo;

import com.rebate.domain.RebateDetail;
import com.rebate.domain.en.EOrderStatus;
import com.rebate.domain.en.EOrderValidCode;
import com.rebate.domain.en.ERebateDetailStatus;
import com.rebate.domain.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
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
     * 有效状态
     */
    @Getter
    @Setter
    private Integer validCode;

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
     * 给上线代理返的佣金
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

    public String getSubmitDateShow(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String show = "";
        if (null != submitDate) {
            show = format.format(submitDate);
        }
        return show;
    }
    public String getOrderStatusShow(){
        return EOrderStatus.getStatusShow(status);
    }

    public String getRebateDetailStatusShow(){
        return ERebateDetailStatus.getStatusShow(orderStatus);
    }

    public String getValidCodeShow(){
        return EOrderValidCode.getStatusShow(validCode);
    }
}

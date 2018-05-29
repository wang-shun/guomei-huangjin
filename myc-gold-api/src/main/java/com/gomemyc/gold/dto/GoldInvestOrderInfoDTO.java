package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @ClassName:GoldInvestOrderDTO
 * @Description:黄金投资订单详情参数实体
 * @author LiuQiangBin
 * @date 2017年3月24日
 */
public class GoldInvestOrderInfoDTO implements Serializable {
	
	private static final long serialVersionUID = 6536097944885658511L;

	//订单Id
	private String id;

	//创建时间
    private Date createTime;

    //剩余支付金额
    private BigDecimal remainAmount;

    //订单号
    private String reqNo;

    //余额支付
    private BigDecimal balancePaidAmount;

    //红包金额
    private BigDecimal couponAmount;

    //奖券Id
    private String couponId;

    //投资金额
    private BigDecimal amount;
    
    public GoldInvestOrderInfoDTO() {
		super();
	}

	public GoldInvestOrderInfoDTO(String id, Date createTime, BigDecimal remainAmount, String reqNo,
			BigDecimal balancePaidAmount, BigDecimal couponAmount, String couponId, BigDecimal amount) {
		super();
		this.id = id;
		this.createTime = createTime;
		this.remainAmount = remainAmount;
		this.reqNo = reqNo;
		this.balancePaidAmount = balancePaidAmount;
		this.couponAmount = couponAmount;
		this.couponId = couponId;
		this.amount = amount;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo == null ? null : reqNo.trim();
    }

    public BigDecimal getBalancePaidAmount() {
        return balancePaidAmount;
    }

    public void setBalancePaidAmount(BigDecimal balancePaidAmount) {
        this.balancePaidAmount = balancePaidAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId == null ? null : couponId.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
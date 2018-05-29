package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @ClassName BuyTimeGoldRDataDto
 * @author zhuyunpeng
 * @description 购买定期金响应参数实体
 * @date 2017年3月16日
 */
public class BuyTimeGoldDTO implements Serializable {
	
	

	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 9153647769489127500L;

	// 成交金价(单位：分/克)
	private BigDecimal realPrice;

	// 成交金重(单位：毫克)
	private BigDecimal realWeight;

	// 成交金额(单位： 分)
	private BigDecimal realAmount;

	// 请求订单号(商户请求订单号)
	private String reqNo;

	// 订单状态
	private String orderStatus;

	// 红包金额
	private BigDecimal couponValue;

	public BigDecimal getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(BigDecimal realPrice) {
		this.realPrice = realPrice;
	}

	public BigDecimal getRealWeight() {
		return realWeight;
	}

	public void setRealWeight(BigDecimal realWeight) {
		this.realWeight = realWeight;
	}

	public BigDecimal getRealAmount() {
		return realAmount;
	}

	public void setRealAmount(BigDecimal realAmount) {
		this.realAmount = realAmount;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public BigDecimal getCouponValue() {
		return couponValue;
	}

	public void setCouponValue(BigDecimal couponValue) {
		this.couponValue = couponValue;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}


package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @ClassName:GoldInvestOrderDTO
 * @Description:黄金投资订单参数实体
 * @author zhuyunpeng
 * @date 2017年3月16日
 */
public class GoldInvestOrderDTO implements Serializable {


	
	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 9030312842843638161L;

	// 订单ID
	private String id;

	// 购买方式(1 按钱买【订单金额必传】，2 按金重买【金重必传】)default 1
	private Integer buyType;

	// 用户ID
	private String userId;

	// 用户手机号
	private String userMobile;

	// 投资金额
	private BigDecimal amount;

	// 成交金价
	private BigDecimal realPrice;

	// 成交金重(单位:毫克)
	private BigDecimal realWeight;

	// 成交金额
	private BigDecimal realAmount;

	// 预下单完成时间
	private Date finishTime;

	// 成功下单时间
	private Date realFinishTime;

	// 订单号
	private String reqNo;

	// 黄金钱包订单号
	private String goldOrderNo;

	// 订单类型(1:预下单 2:成功购买)
	private Integer orderType;

	// 订单状态
	private Integer orderStatus;

	// 投资Id
	private String investId;

	//产品名称
	private String productName;
	
	// 产品id
	private String productId;

	// 错误码
	private String errCode;

	// 错误原因
	private String errMsg;

	//剩余支付金额
	private BigDecimal remainAmount;
	
	//余额支付
	private BigDecimal balancePaidAmount;
	
	//服务器当前时间
	private Long serverTime;
	
	//过期时间(支付的有效时间)
	private Long expireTime;
	
	//红包金额
	private BigDecimal couponValue;
	
	//产品标题
	private String title;
	
	public GoldInvestOrderDTO() {
		super();
	}

	public GoldInvestOrderDTO(String id, Integer buyType, String userId, String userMobile, BigDecimal amount,
			BigDecimal realPrice, BigDecimal realWeight, BigDecimal realAmount, Date finishTime, Date realFinishTime,
			String reqNo, String goldOrderNo, Integer orderType, Integer orderStatus, String investId,
			String productName, String productId, String errCode, String errMsg, BigDecimal remainAmount,
			BigDecimal balancePaidAmount, Long serverTime, Long expireTime, BigDecimal couponValue, String title) {
		super();
		this.id = id;
		this.buyType = buyType;
		this.userId = userId;
		this.userMobile = userMobile;
		this.amount = amount;
		this.realPrice = realPrice;
		this.realWeight = realWeight;
		this.realAmount = realAmount;
		this.finishTime = finishTime;
		this.realFinishTime = realFinishTime;
		this.reqNo = reqNo;
		this.goldOrderNo = goldOrderNo;
		this.orderType = orderType;
		this.orderStatus = orderStatus;
		this.investId = investId;
		this.productName = productName;
		this.productId = productId;
		this.errCode = errCode;
		this.errMsg = errMsg;
		this.remainAmount = remainAmount;
		this.balancePaidAmount = balancePaidAmount;
		this.serverTime = serverTime;
		this.expireTime = expireTime;
		this.couponValue = couponValue;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getCouponValue() {
		return couponValue;
	}

	public void setCouponValue(BigDecimal couponValue) {
		this.couponValue = couponValue;
	}

	public Long getServerTime() {
		return serverTime;
	}

	public void setServerTime(Long serverTime) {
		this.serverTime = serverTime;
	}

	public BigDecimal getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(BigDecimal remainAmount) {
		this.remainAmount = remainAmount;
	}

	public BigDecimal getBalancePaidAmount() {
		return balancePaidAmount;
	}

	public void setBalancePaidAmount(BigDecimal balancePaidAmount) {
		this.balancePaidAmount = balancePaidAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getBuyType() {
		return buyType;
	}

	public void setBuyType(Integer buyType) {
		this.buyType = buyType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

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

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Date getRealFinishTime() {
		return realFinishTime;
	}

	public void setRealFinishTime(Date realFinishTime) {
		this.realFinishTime = realFinishTime;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getGoldOrderNo() {
		return goldOrderNo;
	}

	public void setGoldOrderNo(String goldOrderNo) {
		this.goldOrderNo = goldOrderNo;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	 

}

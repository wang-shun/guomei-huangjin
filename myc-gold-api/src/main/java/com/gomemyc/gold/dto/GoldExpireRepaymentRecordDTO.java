package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 *@ClassName:FinishOrdersDTO
 *@Description:完成订单参数实体
 *@author liujunhan
 *@date 2017年3月16日
 */
public class GoldExpireRepaymentRecordDTO implements Serializable{

   /** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 7817814440305322739L;

	//主键id
    private String id;

    //用户id
    private String userId;

    //订单号（关联黄金投资订单表的订单号）
    private String orderReqNo;

    //黄金钱包订单号
    private String goldWalletOrderNo;

    //用户手机号
    private String mobile;

    //产品名称
    private String productName;

    //卖出时间
    private Date saleTime;

    //到期时间
    private Date dueTime;

    //卖出克重
    private BigDecimal saleWeight;

    //卖出时金价(由黄金钱包返回的产品到期日-1日 下午3点的卖出金价)
    private BigDecimal saleGoldPrice;

    //应还本金(由黄金钱包返回的 该订单的 应还本金)
    private BigDecimal principal;

    //应还利息(由黄金钱包返回的 该订单的 应还利息)
    private BigDecimal interest;

    //接收状态(1:成功;2:失败)
    private Integer receiveStatus;

    public GoldExpireRepaymentRecordDTO() {
    }

    public GoldExpireRepaymentRecordDTO(String id, String userId, String orderReqNo, String goldWalletOrderNo, String mobile, String productName, Date saleTime, Date dueTime, BigDecimal saleWeight, BigDecimal saleGoldPrice, BigDecimal principal, BigDecimal interest, Integer receiveStatus) {
        this.id = id;
        this.userId = userId;
        this.orderReqNo = orderReqNo;
        this.goldWalletOrderNo = goldWalletOrderNo;
        this.mobile = mobile;
        this.productName = productName;
        this.saleTime = saleTime;
        this.dueTime = dueTime;
        this.saleWeight = saleWeight;
        this.saleGoldPrice = saleGoldPrice;
        this.principal = principal;
        this.interest = interest;
        this.receiveStatus = receiveStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderReqNo() {
        return orderReqNo;
    }

    public void setOrderReqNo(String orderReqNo) {
        this.orderReqNo = orderReqNo;
    }

    public String getGoldWalletOrderNo() {
        return goldWalletOrderNo;
    }

    public void setGoldWalletOrderNo(String goldWalletOrderNo) {
        this.goldWalletOrderNo = goldWalletOrderNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public BigDecimal getSaleWeight() {
        return saleWeight;
    }

    public void setSaleWeight(BigDecimal saleWeight) {
        this.saleWeight = saleWeight;
    }

    public BigDecimal getSaleGoldPrice() {
        return saleGoldPrice;
    }

    public void setSaleGoldPrice(BigDecimal saleGoldPrice) {
        this.saleGoldPrice = saleGoldPrice;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public Integer getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(Integer receiveStatus) {
        this.receiveStatus = receiveStatus;
    }
    
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldExpireRepaymentRecord implements Serializable{
	
	

	private static final long serialVersionUID = 7977002512851033461L;

	private String id;

    private String userId;

    private String orderReqNo;

    private String goldWalletOrderNo;

    private String mobile;

    private String productName;

    private Date saleTime;

    private Date dueTime;

    private BigDecimal saleWeight;

    private BigDecimal saleGoldPrice;

    private BigDecimal principal;

    private BigDecimal interest;

    private Integer receiveStatus;



    public GoldExpireRepaymentRecord(String id, String userId, String orderReqNo, String goldWalletOrderNo, String mobile, String productName, Date saleTime, Date dueTime, BigDecimal saleWeight, BigDecimal saleGoldPrice, BigDecimal principal, BigDecimal interest, Integer receiveStatus) {
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
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
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
        this.goldWalletOrderNo = goldWalletOrderNo == null ? null : goldWalletOrderNo.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
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

    public GoldExpireRepaymentRecord() {
    }



	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class GoldInvestAccountCheck implements Serializable{

    private static final long serialVersionUID = -3347747580960564641L;

    private String id;

    private String reqNo;

    private String orderNo;

    private Date goldOrderTime;

    private Date goldFinishTime;

    private BigDecimal goldWeight;

    private BigDecimal realPrice;

    private BigDecimal orderAmount;

    private String productCode;

    private String productName;

    private Date createTime;

    private Integer dataCheckType;


    private Integer comparingStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo == null ? null : reqNo.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Date getGoldOrderTime() {
        return goldOrderTime;
    }

    public void setGoldOrderTime(Date goldOrderTime) {
        this.goldOrderTime = goldOrderTime;
    }

    public Date getGoldFinishTime() {
        return goldFinishTime;
    }

    public void setGoldFinishTime(Date goldFinishTime) {
        this.goldFinishTime = goldFinishTime;
    }

    public BigDecimal getGoldWeight() {
        return goldWeight;
    }

    public void setGoldWeight(BigDecimal goldWeight) {
        this.goldWeight = goldWeight;
    }

    public BigDecimal getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(BigDecimal realPrice) {
        this.realPrice = realPrice;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDataCheckType() {
        return dataCheckType;
    }

    public void setDataCheckType(Integer dataCheckType) {
        this.dataCheckType = dataCheckType;
    }

    public Integer getComparingStatus() {
        return comparingStatus;
    }

    public void setComparingStatus(Integer comparingStatus) {
        this.comparingStatus = comparingStatus;
    }
}
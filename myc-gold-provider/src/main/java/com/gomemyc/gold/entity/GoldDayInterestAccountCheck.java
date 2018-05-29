package com.gomemyc.gold.entity;

import java.math.BigDecimal;
import java.util.Date;

public class GoldDayInterestAccountCheck {

    private String id;

    private String reqNo;

    private String orderNo;

    private Date goldInterestDate;

    private BigDecimal goldWeight;

    private BigDecimal goldPrice;

    private Integer clearRate;

    private BigDecimal dayInterestMoney;

    private String productCode;

    private String productName;

    private Date createTime;

    private Integer dataCheckType;

    private String mobile;

    private Date updateTime;

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

    public Date getGoldInterestDate() {
        return goldInterestDate;
    }

    public void setGoldInterestDate(Date goldInterestDate) {
        this.goldInterestDate = goldInterestDate;
    }

    public BigDecimal getGoldWeight() {
        return goldWeight;
    }

    public void setGoldWeight(BigDecimal goldWeight) {
        this.goldWeight = goldWeight;
    }

    public BigDecimal getGoldPrice() {
        return goldPrice;
    }

    public void setGoldPrice(BigDecimal goldPrice) {
        this.goldPrice = goldPrice;
    }

    public Integer getClearRate() {
        return clearRate;
    }

    public void setClearRate(Integer clearRate) {
        this.clearRate = clearRate;
    }

    public BigDecimal getDayInterestMoney() {
        return dayInterestMoney;
    }

    public void setDayInterestMoney(BigDecimal dayInterestMoney) {
        this.dayInterestMoney = dayInterestMoney;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getComparingStatus() {
        return comparingStatus;
    }

    public void setComparingStatus(Integer comparingStatus) {
        this.comparingStatus = comparingStatus;
    }
}
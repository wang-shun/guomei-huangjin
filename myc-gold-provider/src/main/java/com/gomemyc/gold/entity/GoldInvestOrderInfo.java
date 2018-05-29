package com.gomemyc.gold.entity;

import java.math.BigDecimal;
import java.util.Date;

public class GoldInvestOrderInfo {
    private String id;

    private Date createTime;

    private BigDecimal remainAmount;

    private String reqNo;

    private BigDecimal balancePaidAmount;

    private BigDecimal couponAmount;

    private String couponId;

    private BigDecimal amount;

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
}
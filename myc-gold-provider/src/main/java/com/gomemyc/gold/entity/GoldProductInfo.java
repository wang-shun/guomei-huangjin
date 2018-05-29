package com.gomemyc.gold.entity;

import java.math.BigDecimal;

public class GoldProductInfo {

    private String id;

    private BigDecimal minInvestAmount;

    private BigDecimal maxInvestAmount;

    private Long stepAmount;

    private BigDecimal maxTotalAmount;

    private Integer maxTimes;

    private BigDecimal balance;

    private Integer useCoupon;

    private Integer version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public BigDecimal getMinInvestAmount() {
        return minInvestAmount;
    }

    public void setMinInvestAmount(BigDecimal minInvestAmount) {
        this.minInvestAmount = minInvestAmount;
    }

    public BigDecimal getMaxInvestAmount() {
        return maxInvestAmount;
    }

    public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
        this.maxInvestAmount = maxInvestAmount;
    }

    public Long getStepAmount() {
        return stepAmount;
    }

    public void setStepAmount(Long stepAmount) {
        this.stepAmount = stepAmount;
    }

    public BigDecimal getMaxTotalAmount() {
        return maxTotalAmount;
    }

    public void setMaxTotalAmount(BigDecimal maxTotalAmount) {
        this.maxTotalAmount = maxTotalAmount;
    }

    public Integer getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(Integer maxTimes) {
        this.maxTimes = maxTimes;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(Integer useCoupon) {
        this.useCoupon = useCoupon;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
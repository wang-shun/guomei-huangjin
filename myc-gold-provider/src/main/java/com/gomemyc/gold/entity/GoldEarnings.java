package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldEarnings implements Serializable{
	
	
	private static final long serialVersionUID = 1L;

	private String id;

    private String userId;

    private BigDecimal unitPrice;

    private Integer projectRate;

    private BigDecimal goldWeight;

    private String earningsDate;

    private Date ceateTime;

    private BigDecimal earningsAmount;

    private String investOrderId;

    public GoldEarnings() {
    }

    public GoldEarnings(String id, String userId, BigDecimal unitPrice, Integer projectRate, BigDecimal goldWeight,
                        String earningsDate, Date ceateTime, BigDecimal earningsAmount, String investOrderId) {
		this.id = id;
		this.userId = userId;
		this.unitPrice = unitPrice;
		this.projectRate = projectRate;
		this.goldWeight = goldWeight;
		this.earningsDate = earningsDate;
		this.ceateTime = ceateTime;
		this.earningsAmount = earningsAmount;
		this.investOrderId = investOrderId;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getProjectRate() {
        return projectRate;
    }

    public void setProjectRate(Integer projectRate) {
        this.projectRate = projectRate;
    }

    public BigDecimal getGoldWeight() {
        return goldWeight;
    }

    public void setGoldWeight(BigDecimal goldWeight) {
        this.goldWeight = goldWeight;
    }

    public String getEarningsDate() {
        return earningsDate;
    }

    public void setEarningsDate(String earningsDate) {
        this.earningsDate = earningsDate == null ? null : earningsDate.trim();
    }

    public Date getCeateTime() {
        return ceateTime;
    }

    public void setCeateTime(Date ceateTime) {
        this.ceateTime = ceateTime;
    }

    public BigDecimal getEarningsAmount() {
        return earningsAmount;
    }

    public void setEarningsAmount(BigDecimal earningsAmount) {
        this.earningsAmount = earningsAmount;
    }

    public String getInvestOrderId() {
        return investOrderId;
    }

    public void setInvestOrderId(String investOrderId) {
        this.investOrderId = investOrderId == null ? null : investOrderId.trim();
    }
    

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
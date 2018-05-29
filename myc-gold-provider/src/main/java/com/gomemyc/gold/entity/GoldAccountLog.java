package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldAccountLog implements Serializable {


    private static final long serialVersionUID = -4040917812045819162L;

    private String id;

    private String userId;

    private String productId;

    private String referencedId;

    private Date checkTime;

    private Integer checkStatus;

    private Integer checkOpinion;

    private String checkType;

    private String description;

    private Integer orderSum;

    private BigDecimal moneyDiff;

    private BigDecimal goldWeightDiff;

    public GoldAccountLog() {
        super();
    }

    public GoldAccountLog(String id, String userId, String productId, String referencedId, Date checkTime, Integer checkStatus, Integer checkOpinion, String checkType, Integer orderSum, BigDecimal moneyDiff, BigDecimal goldWeightDiff,String description) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.referencedId = referencedId;
        this.checkTime = checkTime;
        this.checkStatus = checkStatus;
        this.checkOpinion = checkOpinion;
        this.checkType = checkType;
        this.description = description;
        this.orderSum = orderSum;
        this.moneyDiff = moneyDiff;
        this.goldWeightDiff = goldWeightDiff;
    }

    public GoldAccountLog(String id, String userId, String productId, String referencedId, Date checkTime,
                          Integer checkStatus, Integer checkOpinion, String checkType, String description) {
        super();
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.referencedId = referencedId;
        this.checkTime = checkTime;
        this.checkStatus = checkStatus;
        this.checkOpinion = checkOpinion;
        this.checkType = checkType;
        this.description = description;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
    }

    public String getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(String referencedId) {
        this.referencedId = referencedId == null ? null : referencedId.trim();
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Integer getCheckOpinion() {
        return checkOpinion;
    }

    public void setCheckOpinion(Integer checkOpinion) {
        this.checkOpinion = checkOpinion;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType == null ? null : checkType.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(Integer orderSum) {
        this.orderSum = orderSum;
    }

    public BigDecimal getMoneyDiff() {
        return moneyDiff;
    }

    public void setMoneyDiff(BigDecimal moneyDiff) {
        this.moneyDiff = moneyDiff;
    }

    public BigDecimal getGoldWeightDiff() {
        return goldWeightDiff;
    }

    public void setGoldWeightDiff(BigDecimal goldWeightDiff) {
        this.goldWeightDiff = goldWeightDiff;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
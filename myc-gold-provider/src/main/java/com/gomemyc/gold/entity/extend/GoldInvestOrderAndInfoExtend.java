package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2017-04-17.
 */
public class GoldInvestOrderAndInfoExtend implements Serializable {

    private String id;

    private Integer buyType;

    private String userId;

    private String userMobile;

    private BigDecimal amount;

    private BigDecimal realPrice;

    private BigDecimal realWeight;

    private BigDecimal realAmount;

    private Date finishTime;

    private Date realFinishTime;

    private Long expireTime;

    private String reqNo;

    private String goldOrderNo;

    private Integer orderType;

    private Integer orderStatus;

    private String investId;

    private String productName;

    private String productId;

    private String errCode;

    private String errMsg;

    private BigDecimal couponAmount;

    private BigDecimal balancePaidAmount;

    private BigDecimal remainAmount;

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

    public void setExpireTime(Date expireTime) {
        if (expireTime != null)
        this.expireTime = expireTime.getTime();
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

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getBalancePaidAmount() {
        return balancePaidAmount;
    }

    public void setBalancePaidAmount(BigDecimal balancePaidAmount) {
        this.balancePaidAmount = balancePaidAmount;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }
}

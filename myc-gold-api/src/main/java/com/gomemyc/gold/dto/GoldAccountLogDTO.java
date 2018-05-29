package com.gomemyc.gold.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ClassName:
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:   TODO ADD REASON. <br/>
 * Date:     2017/3/22 <br/>
 *
 * @author zhuyunpeng
 * @description
 * @see
 * @since JDK 1.8
 */
public class GoldAccountLogDTO implements Serializable {


	private static final long serialVersionUID = 3603072426268827910L;

	private String id;

    //用户ID
    private String userId;

    //产品ID
    private String productId;

    //被引用ID
    private String referencedId;

    //审核时间(格式yyyy-MM-dd HH:mm:ss】)
    private Date checkTime;

    //审核状态(0代表对账失败 ，1代表对账成功)
    private Integer checkStatus;

    //审核意见(0代表忽略，1代表接受)
    private Integer checkOpinion;

    //对账类型
    private String checkType;

    //对账描述
    private String description;

    //订单总数
    private Integer orderSum;

    //金额差值
    private BigDecimal moneyDiff;

    //克重差值
    private BigDecimal goldWeightDiff;

    public GoldAccountLogDTO() {
		super();
	}

	public GoldAccountLogDTO(String id, String userId, String productId, String referencedId, Date checkTime, Integer checkStatus, Integer checkOpinion, String checkType, Integer orderSum, BigDecimal moneyDiff, BigDecimal goldWeightDiff, String description) {
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

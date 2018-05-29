package com.gomemyc.gold.entity.extend;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;

public class GoldProductsExtend {

    
    private String id;

    private String loginName;

    private String companyName;

    private String loanId;

    private String userId;

    private String typeKey;

    private String typeId;

    private String typeName;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal stepAmount;

    private String goldProductCode;

    private String goldProcductName;

    private Integer goldProductRate;

    private Integer goldProductStatus;

    private String title;

    private BigDecimal amount;

    private BigDecimal investAmount;

    private Integer investNumber;

    private Integer years;

    private Integer months;

    private Integer days;

    private Integer rate;

    private Integer ratePlus;

    private Integer method;

    private Integer status;

    private Integer productSwitch;

    private Date createTime;

    private Date updateTime;

    private Date scheduleTime;

    private Date openTime;

    private Date endTime;

    private Date finishTime;

    private Date settleTime;

    private Date clearTime;

    private Date valueTime;

    private Integer procedureFeeType;

    private Integer preservationType;

    private String dealCostDesc;

    private String earningsDesc;

    private BigDecimal balance;

    private String dueProcessDesc;

    private String dailyLimitDesc;

    private String goldCarietiesDesc;

    private String transferDesc;

    private String tradingDesc;

    private Integer useCoupon;

    private String riskDesc;

    private String projectDetail;

    private String productDetail;

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName == null ? null : loginName.trim();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId == null ? null : loanId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey == null ? null : typeKey.trim();
    }

    public BigDecimal getStepAmount() {
        return stepAmount;
    }

    public void setStepAmount(BigDecimal stepAmount) {
        this.stepAmount = stepAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId == null ? null : typeId.trim();
    }

    public Integer getUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(Integer useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public String getGoldProductCode() {
        return goldProductCode;
    }

    public void setGoldProductCode(String goldProductCode) {
        this.goldProductCode = goldProductCode == null ? null : goldProductCode.trim();
    }

    public String getGoldProcductName() {
        return goldProcductName;
    }

    public void setGoldProcductName(String goldProcductName) {
        this.goldProcductName = goldProcductName == null ? null : goldProcductName.trim();
    }

    public Integer getGoldProductRate() {
        return goldProductRate;
    }

    public void setGoldProductRate(Integer goldProductRate) {
        this.goldProductRate = goldProductRate;
    }

    public Integer getGoldProductStatus() {
        return goldProductStatus;
    }

    public void setGoldProductStatus(Integer goldProductStatus) {
        this.goldProductStatus = goldProductStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public Integer getInvestNumber() {
        return investNumber;
    }

    public void setInvestNumber(Integer investNumber) {
        this.investNumber = investNumber;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRatePlus() {
        return ratePlus;
    }

    public void setRatePlus(Integer ratePlus) {
        this.ratePlus = ratePlus;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProductSwitch() {
        return productSwitch;
    }

    public void setProductSwitch(Integer productSwitch) {
        this.productSwitch = productSwitch;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    public Date getClearTime() {
        return clearTime;
    }

    public void setClearTime(Date clearTime) {
        this.clearTime = clearTime;
    }

    public Date getValueTime() {
        return valueTime;
    }

    public void setValueTime(Date valueTime) {
        this.valueTime = valueTime;
    }

    public Integer getProcedureFeeType() {
        return procedureFeeType;
    }

    public void setProcedureFeeType(Integer procedureFeeType) {
        this.procedureFeeType = procedureFeeType;
    }

    public Integer getPreservationType() {
        return preservationType;
    }

    public void setPreservationType(Integer preservationType) {
        this.preservationType = preservationType;
    }

    public String getDealCostDesc() {
        return dealCostDesc;
    }

    public void setDealCostDesc(String dealCostDesc) {
        this.dealCostDesc = dealCostDesc == null ? null : dealCostDesc.trim();
    }

    public String getEarningsDesc() {
        return earningsDesc;
    }

    public void setEarningsDesc(String earningsDesc) {
        this.earningsDesc = earningsDesc == null ? null : earningsDesc.trim();
    }

    public String getDueProcessDesc() {
        return dueProcessDesc;
    }

    public void setDueProcessDesc(String dueProcessDesc) {
        this.dueProcessDesc = dueProcessDesc == null ? null : dueProcessDesc.trim();
    }

    public String getDailyLimitDesc() {
        return dailyLimitDesc;
    }

    public void setDailyLimitDesc(String dailyLimitDesc) {
        this.dailyLimitDesc = dailyLimitDesc == null ? null : dailyLimitDesc.trim();
    }

    public String getGoldCarietiesDesc() {
        return goldCarietiesDesc;
    }

    public void setGoldCarietiesDesc(String goldCarietiesDesc) {
        this.goldCarietiesDesc = goldCarietiesDesc == null ? null : goldCarietiesDesc.trim();
    }

    public String getTransferDesc() {
        return transferDesc;
    }

    public void setTransferDesc(String transferDesc) {
        this.transferDesc = transferDesc == null ? null : transferDesc.trim();
    }

    public String getTradingDesc() {
        return tradingDesc;
    }

    public void setTradingDesc(String tradingDesc) {
        this.tradingDesc = tradingDesc == null ? null : tradingDesc.trim();
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc == null ? null : riskDesc.trim();
    }

    public String getProjectDetail() {
        return projectDetail;
    }

    public void setProjectDetail(String projectDetail) {
        this.projectDetail = projectDetail;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
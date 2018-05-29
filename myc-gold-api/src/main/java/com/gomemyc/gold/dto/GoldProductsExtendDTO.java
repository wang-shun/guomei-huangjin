package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2017-04-15.
 */
public class GoldProductsExtendDTO implements Serializable {


    private static final long serialVersionUID = 1515273035973025813L;


    //主键
    private String id;

    //企业后台登录名
    private String loginName;

    //企业全称
    private String companyName;

    //标的
    private String loanId;

    //产品拥有人（债转时记录债转人）
    private String userId;

    //标的类型键值
    private String typeKey;

    //标的类型id
    private String typeId;

    //最小投资额
    private BigDecimal minAmount;

    //最大投资额
    private BigDecimal maxAmount;

    //金额增重
    private BigDecimal stepAmount;

    //标的类型名称
    private String typeName;

    //黄金钱包产品编码
    private String goldProductCode;

    //黄金钱包产品名称
    private String goldProcductName;

    //黄金钱包产品利率
    private Integer goldProductRate;

    //黄金钱包产品状态(1-正常,2-已下线)
    private Integer goldProductStatus;

    //标的名称
    private String title;

    //发布金额
    private BigDecimal amount;

    //已投金额
    private BigDecimal investAmount;

    //已投笔数
    private Integer investNumber;

    //期限（年）
    private Integer years;

    //期限（月）
    private Integer months;

    //期限（天
    private Integer days;

    //基础年化率
    private Integer rate;

    //贴息利率
    private Integer ratePlus;

    //还款方式
    private Integer method;

    //产品生命周期
    private Integer status;

    //业务总开关，十进制数
    private Integer productSwitch;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //调度时间
    private Date scheduleTime;

    //开标时间
    private Date openTime;

    //预计募集结束时间
    private Date endTime;

    //实际募集完成时间（满标、到期未满标、流标）
    private Date finishTime;

    //结标时间
    private Date settleTime;

    //还清时间
    private Date clearTime;

    private Date time;

    //起息
    private Date valueTime;

    //手续费类型(1:0元手续费)
    private Integer procedureFeeType;

    //保本类型(1: 非保本型)
    private Integer preservationType;

    //是否可用红包
    private Integer useCoupon;

    //买卖手续费
    private String dealCostDesc;

    //收益计算
    private String earningsDesc;

    //到期处理
    private String dueProcessDesc;

    //每日限制
    private String dailyLimitDesc;

    //黄金品种
    private String goldCarietiesDesc;

    //转让赎回
    private String transferDesc;

    //交易阶段
    private String tradingDesc;

    //风险提示
    private String riskDesc;

    //剩余可投金额
    private BigDecimal balance;

    //总天数
    private int totalDays;

    //投资进度
    private Double investPercent;

    //参考金价
    private BigDecimal goldPrice;

    //到期日期
    private Long dueDate;

    //项目介绍
    private String projectDetail;

    //用户可用余额
    private BigDecimal availableBalance;

    //产品介绍
    private String productDetail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

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

    public BigDecimal getStepAmount() {
        return stepAmount;
    }

    public void setStepAmount(BigDecimal stepAmount) {
        this.stepAmount = stepAmount;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getGoldProductCode() {
        return goldProductCode;
    }

    public void setGoldProductCode(String goldProductCode) {
        this.goldProductCode = goldProductCode;
    }

    public String getGoldProcductName() {
        return goldProcductName;
    }

    public void setGoldProcductName(String goldProcductName) {
        this.goldProcductName = goldProcductName;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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
        this.dealCostDesc = dealCostDesc;
    }

    public String getEarningsDesc() {
        return earningsDesc;
    }

    public void setEarningsDesc(String earningsDesc) {
        this.earningsDesc = earningsDesc;
    }

    public String getDueProcessDesc() {
        return dueProcessDesc;
    }

    public void setDueProcessDesc(String dueProcessDesc) {
        this.dueProcessDesc = dueProcessDesc;
    }

    public String getDailyLimitDesc() {
        return dailyLimitDesc;
    }

    public void setDailyLimitDesc(String dailyLimitDesc) {
        this.dailyLimitDesc = dailyLimitDesc;
    }

    public String getGoldCarietiesDesc() {
        return goldCarietiesDesc;
    }

    public void setGoldCarietiesDesc(String goldCarietiesDesc) {
        this.goldCarietiesDesc = goldCarietiesDesc;
    }

    public String getTransferDesc() {
        return transferDesc;
    }

    public void setTransferDesc(String transferDesc) {
        this.transferDesc = transferDesc;
    }

    public String getTradingDesc() {
        return tradingDesc;
    }

    public void setTradingDesc(String tradingDesc) {
        this.tradingDesc = tradingDesc;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public Double getInvestPercent() {
        return investPercent;
    }

    public void setInvestPercent(Double investPercent) {
        this.investPercent = investPercent;
    }

    public BigDecimal getGoldPrice() {
        return goldPrice;
    }

    public void setGoldPrice(BigDecimal goldPrice) {
        this.goldPrice = goldPrice;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getValueTime() {
        return valueTime;
    }

    public void setValueTime(Date valueTime) {
        this.valueTime = valueTime;
    }

    public Integer getUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(Integer useCoupon) {
        this.useCoupon = useCoupon;
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
}

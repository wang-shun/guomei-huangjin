package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldProductDetailsDTO <br/>
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 上午11:16:43 <br/> 
 * @author   liujunhan 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldProductDetailsDTO  implements Serializable{


	
	
	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 2412002535699096564L;
	
	
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
	private String productKey;

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
	private String loanTitle;

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
	private String method;

	//产品生命周期
	private String status;

	//业务总开关，十进制数
	private Integer productSwitch;

	//创建时间
	private Date createTime;

	//更新时间
	private Date updateTime;

	//调度时间
	private Date scheduleTime;

	//开标时间
	private Date timeOpen;

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
	private Date valueDate;

	//手续费类型(1:0元手续费)
	private Integer procedureFeeType;

	//保本类型(1: 非保本型)
	private Integer preservationType;

	//是否可用红包
	private Boolean useCoupon;

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

	//以下下字段数据库没有提供数据

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
	private String projectDescription;

	//用户可用余额
	private BigDecimal availableBalance;

	//产品介绍
	private String loanDescription;

	public GoldProductDetailsDTO(String id, String goldProductCode) {
		this.id = id;
		this.goldProductCode = goldProductCode;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getStepAmount() {
		return stepAmount;
	}

	public void setStepAmount(BigDecimal stepAmount) {
		this.stepAmount = stepAmount;
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

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}


	public Boolean getUseCoupon() {
		return useCoupon;
	}

	public void setUseCoupon(Boolean useCoupon) {
		this.useCoupon = useCoupon;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
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

	public String getproductKey() {
		return productKey;
	}

	public void setproductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
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

	public String getLoanTitle() {
		return loanTitle;
	}

	public void setLoanTitle(String loanTitle) {
		this.loanTitle = loanTitle;
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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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

	public Date getTimeOpen() {
		return timeOpen;
	}

	public void setTimeOpen(Date timeOpen) {
		this.timeOpen = timeOpen;
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

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getRiskDesc() {
		return riskDesc;
	}

	public void setRiskDesc(String riskDesc) {
		this.riskDesc = riskDesc;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getLoanDescription() {
		return loanDescription;
	}

	public void setLoanDescription(String loanDescription) {
		this.loanDescription = loanDescription;
	}

	public GoldProductDetailsDTO() {
	}

	public GoldProductDetailsDTO(String id, String loginName, String companyName, String loanId, String userId, String productKey, String typeId, String typeName, String goldProductCode, String goldProcductName, Integer goldProductRate, Integer goldProductStatus, String title, BigDecimal amount, BigDecimal investAmount, Integer investNumber, Integer years, Integer months, Integer days, Integer rate, Integer ratePlus, String method, String status, Integer productSwitch, Date createTime, Date updateTime, Date scheduleTime, Date openTime, Date endTime, Date finishTime, Date settleTime, Date clearTime, Date valueTime, Integer procedureFeeType, Integer preservationType, String dealCostDesc, String earningsDesc, String dueProcessDesc, String dailyLimitDesc, String goldCarietiesDesc, String transferDesc, String tradingDesc, String riskDesc) {
		this.id = id;
		this.loginName = loginName;
		this.companyName = companyName;
		this.loanId = loanId;
		this.userId = userId;
		this.productKey = productKey;
		this.typeId = typeId;
		this.typeName = typeName;
		this.goldProductCode = goldProductCode;
		this.goldProcductName = goldProcductName;
		this.goldProductRate = goldProductRate;
		this.goldProductStatus = goldProductStatus;
		this.loanTitle = title;
		this.amount = amount;
		this.investAmount = investAmount;
		this.investNumber = investNumber;
		this.years = years;
		this.months = months;
		this.days = days;
		this.rate = rate;
		this.ratePlus = ratePlus;
		this.method = method;
		this.status = status;
		this.productSwitch = productSwitch;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.scheduleTime = scheduleTime;
		this.timeOpen = openTime;
		this.endTime = endTime;
		this.finishTime = finishTime;
		this.settleTime = settleTime;
		this.clearTime = clearTime;
		this.valueDate = valueTime;
		this.procedureFeeType = procedureFeeType;
		this.preservationType = preservationType;
		this.dealCostDesc = dealCostDesc;
		this.earningsDesc = earningsDesc;
		this.dueProcessDesc = dueProcessDesc;
		this.dailyLimitDesc = dailyLimitDesc;
		this.goldCarietiesDesc = goldCarietiesDesc;
		this.transferDesc = transferDesc;
		this.tradingDesc = tradingDesc;
		this.riskDesc = riskDesc;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

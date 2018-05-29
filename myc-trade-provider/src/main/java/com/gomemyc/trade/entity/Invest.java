package com.gomemyc.trade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.trade.enums.InvestSource;
import com.gomemyc.trade.enums.InvestStatus;

/**
 * 投资表
 * @author lujixiang
 * @creaTime 2017年3月12日
 */
public class Invest extends StringIdEntity{
    
    private static final long serialVersionUID = 201703121127L;
    
    // 投资人手机号
    private String mobile;
    
    // 姓名
    private String name;

    // 标的类型键值
    private String loanTypeKey;
    
    // 标的类型id
    private String loanTypeId;
    
    // 投资人
    private String userId;
    
    // 标的id
    private String loanId;
    
    // 产品id
    private String productId;
    
    // 投资金额
    private BigDecimal amount;
    
    // 基础利率
    private int rate;
    
    // 加息利率
    private int plusRate;
    
    // 期限(年)
    private int years;
    
    // 期限(月)
    private int months;
    
    // 期限(日)
    private int days;
    
    // 还款方式
    private RepaymentMethod repaymentMethod;
    
    // 投资状态
    private InvestStatus status;
    
    // 提交时间
    private Date submitTime;
    
    // 已债转总金额
    private BigDecimal debtAmount;
    
    // 投资来源
    private InvestSource source;
    
    // 设备渠道
    private String equipmentChannel;
    
    // 来源渠道
    private String sourceChannel;
    
    // 奖券分配id
    private String couponPlacememtId;
    
    // 债转时记录原始投资id
    private String rootInvestId;
    
    // 本地资金账号返回流水表
    private String localFreezeNo;
    
    // 账户系统的北京银行解冻成功返回code
    private String bjDfCode;

    // 北京银行投资成功返回code
    private String bjSynCode;

    // 账户系统的本地解冻转账返回code
    private String localDfCode;
    // 是否有奖励
    private boolean reward;
    // 是否债转投资
    private boolean debted;
    
    public boolean isReward() {
		return reward;
	}

	public void setReward(boolean reward) {
		this.reward = reward;
	}

	public String getBjDfCode() {
        return bjDfCode;
    }

    public void setBjDfCode(String bjDfCode) {
        this.bjDfCode = bjDfCode;
    }

    public String getBjSynCode() {
        return bjSynCode;
    }

    public void setBjSynCode(String bjSynCode) {
        this.bjSynCode = bjSynCode;
    }

    public String getLocalDfCode() {
        return localDfCode;
    }

    public void setLocalDfCode(String localDfCode) {
        this.localDfCode = localDfCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoanTypeKey() {
        return loanTypeKey;
    }

    public void setLoanTypeKey(String loanTypeKey) {
        this.loanTypeKey = loanTypeKey;
    }
    
    public String getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getPlusRate() {
        return plusRate;
    }

    public void setPlusRate(int plusRate) {
        this.plusRate = plusRate;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public RepaymentMethod getRepaymentMethod() {
        return repaymentMethod;
    }

    public void setRepaymentMethod(RepaymentMethod repaymentMethod) {
        this.repaymentMethod = repaymentMethod;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public InvestStatus getStatus() {
        return status;
    }

    public void setStatus(InvestStatus status) {
        this.status = status;
    }

    public BigDecimal getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(BigDecimal debtAmount) {
        this.debtAmount = debtAmount;
    }

    public InvestSource getSource() {
        return source;
    }

    public void setSource(InvestSource source) {
        this.source = source;
    }

    public String getEquipmentChannel() {
        return equipmentChannel;
    }

    public void setEquipmentChannel(String equipmentChannel) {
        this.equipmentChannel = equipmentChannel;
    }

    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public String getCouponPlacememtId() {
        return couponPlacememtId;
    }

    public void setCouponPlacememtId(String couponPlacememtId) {
        this.couponPlacememtId = couponPlacememtId;
    }

    public String getRootInvestId() {
        return rootInvestId;
    }

    public void setRootInvestId(String rootInvestId) {
        this.rootInvestId = rootInvestId;
    }

    public String getLocalFreezeNo() {
        return localFreezeNo;
    }

    public void setLocalFreezeNo(String localFreezeNo) {
        this.localFreezeNo = localFreezeNo;
    }

    public boolean isDebted() {
        return debted;
    }

    public void setDebted(boolean debted) {
        this.debted = debted;
    }

}

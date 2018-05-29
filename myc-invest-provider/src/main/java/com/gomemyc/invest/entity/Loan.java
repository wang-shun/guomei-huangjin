package com.gomemyc.invest.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.invest.base.TimeScopeEntity;
import com.gomemyc.invest.enums.LoanPurpose;
import com.gomemyc.invest.enums.LoanSource;
import com.gomemyc.invest.enums.LoanStatus;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.RepaymentMethod;

/**
 * 标的
 * @author lujixiang
 * @creaTime 2017年3月3日
 */
public class Loan extends TimeScopeEntity{
    
    private static final long serialVersionUID = 201703051203L;

    // 资产编号
    private String portfolioNo;
    
    // 企业登录名
    private String loginName;
    
    // 企业全称
    private String companyName;
    
    // 标的产品类型id
    private String loanTypeId;
    
    // 产品类型键值
    private String loanTypeKey;
    
    // 标题
    private String title;
    
    // 融资金额
    private BigDecimal amount;
    
    // 期限 - 年
    private int years;
    
    // 期限 - 月
    private int months;
    
    // 期限 - 日
    private int days;
    
    // 基础利率,单位 = 真实利率 * 10000
    private int rate;
    
    // 贴息利率,单位 = 真实利率 * 10000
    private int plusRate; 
    
    // 还款方式
    private RepaymentMethod method;
    
    // 借款人id
    private String userId;
    
    // 借款目的
    private LoanPurpose purpose;
    
    // 标的状态
    private LoanStatus status;
    
    // 起息日
    private Date valueTime;
    
    // 应还款日
    private Date dueTime;
    
    // 第三方同步订单号
    private String syncOrderNo;
    
    // 第三方同步状态
    private  LoanSyncStatus syncStatus;
    
    // 本地同步状态
    private  LoanSyncStatus localSyncStatus;
    
    // 第三方同步返回时间
    private Date syncReturnTime;
    
    // 标的来源
    private LoanSource source;
    
    
    public Loan() {
    }
    
    public LoanSyncStatus getLocalSyncStatus() {
        return localSyncStatus;
    }

    public void setLocalSyncStatus(LoanSyncStatus localSyncStatus) {
        this.localSyncStatus = localSyncStatus;
    }

    public String getPortfolioNo() {
        return portfolioNo;
    }

    public void setPortfolioNo(String portfolioNo) {
        this.portfolioNo = portfolioNo;
    }

    public String getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public String getLoanTypeKey() {
        return loanTypeKey;
    }

    public void setLoanTypeKey(String loanTypeKey) {
        this.loanTypeKey = loanTypeKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public RepaymentMethod getMethod() {
        return method;
    }

    public void setMethod(RepaymentMethod method) {
        this.method = method;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LoanPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(LoanPurpose purpose) {
        this.purpose = purpose;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Date getValueTime() {
        return valueTime;
    }

    public void setValueTime(Date valueTime) {
        this.valueTime = valueTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getSyncOrderNo() {
        return syncOrderNo;
    }

    public void setSyncOrderNo(String syncOrderNo) {
        this.syncOrderNo = syncOrderNo;
    }

    public LoanSyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(LoanSyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public LoanSource getSource() {
        return source;
    }
    
    public Date getSyncReturnTime() {
        return syncReturnTime;
    }

    public void setSyncReturnTime(Date syncReturnTime) {
        this.syncReturnTime = syncReturnTime;
    }

    public void setSource(LoanSource source) {
        this.source = source;
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

}

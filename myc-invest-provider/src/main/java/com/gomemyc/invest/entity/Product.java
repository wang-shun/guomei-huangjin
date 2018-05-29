package com.gomemyc.invest.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.invest.base.TimeScopeEntity;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.RepaymentMethod;

/**
 * 产品基类
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public abstract class Product extends TimeScopeEntity{

    private static final long serialVersionUID = 201703052011L;
    
    // 标的id
    private String loanId;
    
    // 借款人
    private String userId;
    
    // 类型键值
    private String typeKey;
    
    // 类型id
    private String typeId;
    
    // 标的类型名称
    private String typeName;
    
    // 标题
    private String title;
    
    // 金额
    private BigDecimal amount;
    
    // 已投资金额
    private BigDecimal investAmount;
    
    // 已投资笔数
    private int investNum;
    
    // 期限 - 年
    private int years;
    
    // 期限 - 月
    private int months;
    
    // 期限 - 日
    private int days;
    
    // 基础利率
    private int rate;
    
    // 加息利率
    private int plusRate;
    
    // 还款方式
    private RepaymentMethod method;
    
    // 产品状态
    private ProductStatus status;
    
    // 产品总开关
    private int productSwitch;
    
    // 债转使用,原标的产品Id
    private String rootProductId;
    
    // 调度时间
    private Date scheduleTime;
    
    // 开标时间
    private Date openTime;
    
    // 募集结束时间
    private Date endTime;
    
    // 实际募集完成时间（满标、到期未满标、流标）
    private Date finishTime;
    
    // 结算时间
    private Date settleTime;
    
    // 还款时间
    private Date clearTime;
    
    // 项目详情
    private String projectDetail;
    
    // 产品介绍
    private String productDetail;
    
    // 债转方案
    private String debtPlanId;
    
    // 起息日
    private Date valueTime;
    
    // 是否是债转
    private boolean debted;
    
    // 第三方同步状态
    private LoanSyncStatus syncStatus;
    
    // 本地同步状态
    private LoanSyncStatus localSyncStatus;
    
    // 风险等级
    private int riskType;
    
    // 产品标签key
    private String productTagKey;
    
    // 产品标签value
    private String productTagValue;
    
    // 产品标签颜色
    private String productTagColor;

    //企业名称
    private String companyName;

    //企业后台登录名
    private String loginName;


    public LoanSyncStatus getLocalSyncStatus() {
        return localSyncStatus;
    }

    public void setLocalSyncStatus(LoanSyncStatus localSyncStatus) {
        this.localSyncStatus = localSyncStatus;
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

    public int getInvestNum() {
        return investNum;
    }

    public void setInvestNum(int investNum) {
        this.investNum = investNum;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public int getProductSwitch() {
        return productSwitch;
    }

    public void setProductSwitch(int productSwitch) {
        this.productSwitch = productSwitch;
    }

    public String getRootProductId() {
        return rootProductId;
    }

    public void setRootProductId(String rootProductId) {
        this.rootProductId = rootProductId;
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

    public String getDebtPlanId() {
        return debtPlanId;
    }

    public void setDebtPlanId(String debtPlanId) {
        this.debtPlanId = debtPlanId;
    }

    public Date getValueTime() {
        return valueTime;
    }

    public void setValueTime(Date valueTime) {
        this.valueTime = valueTime;
    }

    public boolean isDebted() {
        return debted;
    }

    public void setDebted(boolean debted) {
        this.debted = debted;
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

    public LoanSyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(LoanSyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getRiskType() {
        return riskType;
    }

    public void setRiskType(int riskType) {
        this.riskType = riskType;
    }

    public String getProductTagKey() {
        return productTagKey;
    }

    public void setProductTagKey(String productTagKey) {
        this.productTagKey = productTagKey;
    }

    public String getProductTagValue() {
        return productTagValue;
    }

    public void setProductTagValue(String productTagValue) {
        this.productTagValue = productTagValue;
    }

    public String getProductTagColor() {
        return productTagColor;
    }

    public void setProductTagColor(String productTagColor) {
        this.productTagColor = productTagColor;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

}

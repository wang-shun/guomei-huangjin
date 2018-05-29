package com.gomemyc.invest.model;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.RepaymentMethod;

/**
 * 标的redis传输对象(数据库和redis对象的中间封装对象)
 * @author lujixiang
 * @creaTime 2017年3月6日
 */
public class RedisProduct extends StringIdEntity{

    private static final long serialVersionUID = 201703061709L;
    
    private String loanId;  //  标的id
    
    private String userId;  // 产品发布者(债转记录转让人)
    
    private String typeKey;  // 标的类型键值
    
    private String typeId; // 标的类型id
    
    private String title;   // 产品标题
    
    private BigDecimal amount;  // 标的金额
    
    private BigDecimal investAmount;    // 已投金额
    
    private int investNum;  // 已投资笔数
    
    private int years;  // 期限(年)
    
    private int months; //期限(月)
    
    private int days;   // 期限(日)
    
    private int rate; // 基础利率
    
    private int plusRate;   // 加息利率
    
    private RepaymentMethod method;  // 还款方式
    
    private ProductStatus status;  // 产品状态
    
    private boolean useCoupon;    // 是否可用奖券
    
    private boolean debtTransfer; // 是否支持债转
    
    private BigDecimal minAmount; // 最小投资金额(单次)
    
    private BigDecimal maxAmount; // 最大投资金额(单次)
    
    private BigDecimal maxTotalAmount;  // 个人最大投资总额(单个产品)
    
    private BigDecimal stepAmount; // 投资金额增量
    
    private int investMaxTimes; // 最大投资次数(单个产品)
    
    private BigDecimal typeMaxTotalAmount;  // 标的类型下的最大金额
    
    private int typeMaxTimes;  // 标的类型下的最大投资次数
    
    private String rootProductId;  // 原产品id
    
    private Date openTime;  // 开标时间
    
    private Date endTime;  // 募集结束时间
    
    private Date finishTime;  // 满标时间
    
    private Date settleTime;   // 结标时间
    
    private Date clearTime;   // 结清时间
    
    private String projectDetail;  // 项目详情
    
    private String productDetail; // 产品详情
    
    private String debtPlanId;  // 债转方案id
    
    private Date valueTime; // 起息日
    
    private boolean debted; // 是否是债转
    
    private Date dueDate; // 应还款日
    
    private int debtAssignDate; // 持有多少天可转让
    
//     private DebtPlanOneOrmany peopleCount;  //转让人数要求
    
    /** 支持债转  --end **/
    
    // 风险类型
    private int riskType;
    
    // 产品标签key
    private String productTagKey;
    
    // 产品标签value
    private String productTagValue;
    
    // 产品标签颜色
    private String productTagColor;
    
    // 承兑银行
    private String acceptanceBank;
    
    
    

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
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

    public BigDecimal getMaxTotalAmount() {
        return maxTotalAmount;
    }

    public void setMaxTotalAmount(BigDecimal maxTotalAmount) {
        this.maxTotalAmount = maxTotalAmount;
    }

    public BigDecimal getStepAmount() {
        return stepAmount;
    }

    public void setStepAmount(BigDecimal stepAmount) {
        this.stepAmount = stepAmount;
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

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public String getProjectDetail() {
        return projectDetail;
    }

    public void setProjectDetail(String projectDetail) {
        this.projectDetail = projectDetail;
    }

    public int getInvestMaxTimes() {
        return investMaxTimes;
    }

    public void setInvestMaxTimes(int investMaxTimes) {
        this.investMaxTimes = investMaxTimes;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDebtTransfer() {
        return debtTransfer;
    }

    public void setDebtTransfer(boolean debtTransfer) {
        this.debtTransfer = debtTransfer;
    }

    public String getDebtPlanId() {
        return debtPlanId;
    }

    public void setDebtPlanId(String debtPlanId) {
        this.debtPlanId = debtPlanId;
    }

    public int getDebtAssignDate() {
        return debtAssignDate;
    }

    public void setDebtAssignDate(int debtAssignDate) {
        this.debtAssignDate = debtAssignDate;
    }

    public String getRootProductId() {
        return rootProductId;
    }

    public void setRootProductId(String rootProductId) {
        this.rootProductId = rootProductId;
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

    public String getAcceptanceBank() {
        return acceptanceBank;
    }

    public void setAcceptanceBank(String acceptanceBank) {
        this.acceptanceBank = acceptanceBank;
    }

    public BigDecimal getTypeMaxTotalAmount() {
        return typeMaxTotalAmount;
    }

    public void setTypeMaxTotalAmount(BigDecimal typeMaxTotalAmount) {
        this.typeMaxTotalAmount = typeMaxTotalAmount;
    }

    public int getTypeMaxTimes() {
        return typeMaxTimes;
    }

    public void setTypeMaxTimes(int typeMaxTimes) {
        this.typeMaxTimes = typeMaxTimes;
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

    public boolean isDebted() {
        return debted;
    }

    public void setDebted(boolean debted) {
        this.debted = debted;
    }
    
}

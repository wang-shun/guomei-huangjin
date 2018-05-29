package com.gomemyc.invest.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.DebtAssignCancelType;
import com.gomemyc.invest.enums.DebtAssignPricingMethod;
import com.gomemyc.invest.enums.DebtAssignStatus;
import com.gomemyc.invest.enums.RepaymentMethod;


/**
 * 债权转让申请 tb_debtassign_request
 * @author zhangWei
 */

public class DebtassignRequest extends StringIdEntity {
	
	/**
	 * 产品ID
	 */
	private String investId;
	/**
	 * 标的id
	 */
	private String loanId;
	/**
	 * 产品ID
	 */
	private String productId;
	/**
	 * 债转方案id
	 */
	private String debtPlanId;
	/**
	 *  审核通过后生成的新产品id
	 */
	private String debtAssignProductId;
	/**
	 *  原标名称
	 */
	private String productTitle;
	/**
	 *  转让人id
	 */
	private String userId;
	/**
	 *  债权转让产品原价（原产品购买金额）
	 */
	private BigDecimal originalAmount;
	/**
	 *  原产品年化收益率
	 */
	private int originalRate;
	/**
	 *  债权转让金额（原始本金+应收利息）
	 */
	private BigDecimal debtAmount;
	/**
	 *  最大可转金额（持有本金+收益日到转让截止日期当天利息）
	 */
	private BigDecimal debtMaxAmount;
	/**
	 *  转让手续费
	 */
	private BigDecimal counterFee;
	/**
	 *  转让价格（转让金额+折价金额）==新产品的募集金额
	 */
	private BigDecimal transferPrice;
	/**
	 *  已回款金额
	 */
	private BigDecimal backAmount;
	/**
	 *  折价金额
	 */
	private BigDecimal discountAmount;
	/**
	 *  转让成功预计回款
	 */
	private BigDecimal expectedReturnAmount;
	/**
	 *  债权转让折价比率
	 */
	private int debtDealRate;
	/**
	 *  募集不足处理（0：全额退回，1：部分退回）
	 */
	private Boolean returnFullAmount;
	/**
	 *  债转申请时间
	 */
	private Date requestStartDate;
	/**
	 *  转让终止时间
	 */
	private Date requestEndDate;
	/**
	 *  转让有效期
	 */
	private int validDate;
	/**
	 *  原产品结标时间
	 */
	private Date productDueDate;
	/**
	 *  最后操作时间
	 */
	private Date lastOperationTime;
	/**
	 *  取消时间
	 */
	private Date cancelDate;
	/**
	 *  取消次数统计
	 */
	private int cancelCount;
	
	/**
	 *  原始投资本金
	 */
	private BigDecimal originalInvestAmount;
	/**
	 *  原始总利息(原始投资本金对应的利息)
	 */
	private BigDecimal originalTotalInterestAmount;
	/**
	 *  应收利息(即起息日到债转募集终止日的利息)
	 */
	private BigDecimal originalDubeInterestAmount;
	/**
	 *  本金比例(即可转让金额中 原始本金占可转让金额的比例,所得小数乘以10000之后存入数据库)
	 */
	private int investInTotalScale;
	/**
	 *  剩余金额(可转让金额-转让金额)
	 */
	private BigDecimal originalRemainTotalAmount;
	/**
	 *  剩余本金(出让债权后的剩余的投资本金)
	 */
	private BigDecimal originalRemainAmount;
	/**
	 *  债权到期剩余未返利息
	 *  债权到期剩余未返利息=原始本金总利息-剩余本金*总利率*天数
	 */
	private BigDecimal originalRemainInterestAmount;
	/**
	 *  债权预期到期收益
	 *  预期到期收益=债权剩余本金+债权到期剩余未返利息-债权转让价格
	 */
	private BigDecimal debtExpectedAmount;
	/** 
	 * 债权预期年化收益率
	 * 债权预期年化收益率=预期到期收益*365/债权转让价格/债权持有期限*100%
	 */
	private int debtExpectedRate;
	/**
	 *  债权预期年化收益率(只用于计算，不做前台展示)
	 *  债权预期年化收益率=预期到期收益*365/债权转让价格/债权持有期限*100%
	 */
	private int debtPreciseExpectedRate;
	/**
	 *  用户相关信息
	 */
	private String userInfo;
	/**
	 *  调价系数
	 */
	private int purchasedRate;
	/**
	 *  债权实际转让金额(转让的本金)
	 */
	private BigDecimal debtRealAmount;
	/**
	 *  浮动系数
	 */
	private int floatingRate;
	/**
	 *  终点调价系数(可直接折算到最原始本金)
	 */
	private int rootPurchasedRate;
	/**
	 *  终点浮动系数(根据折算的最终本加息，计算最原始本金和最原始利息)
	 */
	private int rootFloatingRate;
	/**
	 *  最原始标发布一次债转申请的id
	 */
	private String firstDebtAssignRequestId;
	/**
	 *  日期
	 */
	private int days;

	/**
	 * 月份
	 */
	private int months;

	/**
	 *  年
	 */
	private int years;
	/** 
	 * 定价方式
	 */
	private DebtAssignPricingMethod pricingMethod;
	/**
	 *  还款方式
	 */
	private RepaymentMethod method;
	/**
	 *  债转状态
	 */
	private DebtAssignStatus status;
	/**
	 *  取消类型
	 */
	private DebtAssignCancelType cancelType;
	
	/**
	 * 账户服务的流水ID
	 */
	private String accountApplyId;
	
	/**
	 * 显示在前端
	 * 是否是转让表（true：是，fasle：否，目前统一拿产品类型判断）
	 */
	private Boolean assignLoan;
	
	/**
	 * 显示在前端
	 * 产品类型（根据产品类型判断是不是转让标，债转的类型是ZZ）
	 */
	private String productKey;
	
	 /**
	  * 显示在前端
     * 原产品收益期限
     */
    private int originalDuration;
    
    /**
     * 显示在前端
	 * 最大取消次数
	 */
	private int maxCancelCount;
	
	/**
	 * 拓展字段1
	 * 字符类型
	 */
	private String strfield1;
	
	/**
	 * 拓展字段2
	 * 字符类型
	 */
	private String strfield2;
	
	/**
	 * 拓展字段3
	 * 字符类型
	 */
	private String strfield3;
	
	/**
	 * 拓展字段1
	 * 数值
	 */
	private int intfield1;
	/**
	 * 拓展字段2
	 * 数值
	 */
	private int intfield2;
	/**
	 * 拓展字段3
	 * 数值
	 */
	private int intfield3;
	public String getInvestId() {
		return investId;
	}
	public void setInvestId(String investId) {
		this.investId = investId;
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
	public String getDebtPlanId() {
		return debtPlanId;
	}
	public void setDebtPlanId(String debtPlanId) {
		this.debtPlanId = debtPlanId;
	}
	
	public String getDebtAssignProductId() {
		return debtAssignProductId;
	}
	public void setDebtAssignProductId(String debtAssignProductId) {
		this.debtAssignProductId = debtAssignProductId;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}
	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}
	public int getOriginalRate() {
		return originalRate;
	}
	public void setOriginalRate(int originalRate) {
		this.originalRate = originalRate;
	}
	public BigDecimal getDebtAmount() {
		return debtAmount;
	}
	public void setDebtAmount(BigDecimal debtAmount) {
		this.debtAmount = debtAmount;
	}
	public BigDecimal getDebtMaxAmount() {
		return debtMaxAmount;
	}
	public void setDebtMaxAmount(BigDecimal debtMaxAmount) {
		this.debtMaxAmount = debtMaxAmount;
	}
	public BigDecimal getCounterFee() {
		return counterFee;
	}
	public void setCounterFee(BigDecimal counterFee) {
		this.counterFee = counterFee;
	}
	public BigDecimal getTransferPrice() {
		return transferPrice;
	}
	public void setTransferPrice(BigDecimal transferPrice) {
		this.transferPrice = transferPrice;
	}
	public BigDecimal getBackAmount() {
		return backAmount;
	}
	public void setBackAmount(BigDecimal backAmount) {
		this.backAmount = backAmount;
	}
	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	public BigDecimal getExpectedReturnAmount() {
		return expectedReturnAmount;
	}
	public void setExpectedReturnAmount(BigDecimal expectedReturnAmount) {
		this.expectedReturnAmount = expectedReturnAmount;
	}
	public int getDebtDealRate() {
		return debtDealRate;
	}
	public void setDebtDealRate(int debtDealRate) {
		this.debtDealRate = debtDealRate;
	}
	public Boolean getReturnFullAmount() {
		return returnFullAmount;
	}
	public void setReturnFullAmount(Boolean returnFullAmount) {
		this.returnFullAmount = returnFullAmount;
	}
	public Date getRequestStartDate() {
		return requestStartDate;
	}
	public void setRequestStartDate(Date requestStartDate) {
		this.requestStartDate = requestStartDate;
	}
	public Date getRequestEndDate() {
		return requestEndDate;
	}
	public void setRequestEndDate(Date requestEndDate) {
		this.requestEndDate = requestEndDate;
	}
	public int getValidDate() {
		return validDate;
	}
	public void setValidDate(int validDate) {
		this.validDate = validDate;
	}
	public Date getProductDueDate() {
		return productDueDate;
	}
	public void setProductDueDate(Date productDueDate) {
		this.productDueDate = productDueDate;
	}
	public Date getLastOperationTime() {
		return lastOperationTime;
	}
	public void setLastOperationTime(Date lastOperationTime) {
		this.lastOperationTime = lastOperationTime;
	}
	public Date getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	public int getCancelCount() {
		return cancelCount;
	}
	public void setCancelCount(int cancelCount) {
		this.cancelCount = cancelCount;
	}
	public BigDecimal getOriginalInvestAmount() {
		return originalInvestAmount;
	}
	public void setOriginalInvestAmount(BigDecimal originalInvestAmount) {
		this.originalInvestAmount = originalInvestAmount;
	}
	public BigDecimal getOriginalTotalInterestAmount() {
		return originalTotalInterestAmount;
	}
	public void setOriginalTotalInterestAmount(BigDecimal originalTotalInterestAmount) {
		this.originalTotalInterestAmount = originalTotalInterestAmount;
	}
	public BigDecimal getOriginalDubeInterestAmount() {
		return originalDubeInterestAmount;
	}
	public void setOriginalDubeInterestAmount(BigDecimal originalDubeInterestAmount) {
		this.originalDubeInterestAmount = originalDubeInterestAmount;
	}
	public int getInvestInTotalScale() {
		return investInTotalScale;
	}
	public void setInvestInTotalScale(int investInTotalScale) {
		this.investInTotalScale = investInTotalScale;
	}
	public BigDecimal getOriginalRemainTotalAmount() {
		return originalRemainTotalAmount;
	}
	public void setOriginalRemainTotalAmount(BigDecimal originalRemainTotalAmount) {
		this.originalRemainTotalAmount = originalRemainTotalAmount;
	}
	public BigDecimal getOriginalRemainAmount() {
		return originalRemainAmount;
	}
	public void setOriginalRemainAmount(BigDecimal originalRemainAmount) {
		this.originalRemainAmount = originalRemainAmount;
	}
	public BigDecimal getOriginalRemainInterestAmount() {
		return originalRemainInterestAmount;
	}
	public void setOriginalRemainInterestAmount(BigDecimal originalRemainInterestAmount) {
		this.originalRemainInterestAmount = originalRemainInterestAmount;
	}
	public BigDecimal getDebtExpectedAmount() {
		return debtExpectedAmount;
	}
	public void setDebtExpectedAmount(BigDecimal debtExpectedAmount) {
		this.debtExpectedAmount = debtExpectedAmount;
	}
	public int getDebtExpectedRate() {
		return debtExpectedRate;
	}
	public void setDebtExpectedRate(int debtExpectedRate) {
		this.debtExpectedRate = debtExpectedRate;
	}
	public int getDebtPreciseExpectedRate() {
		return debtPreciseExpectedRate;
	}
	public void setDebtPreciseExpectedRate(int debtPreciseExpectedRate) {
		this.debtPreciseExpectedRate = debtPreciseExpectedRate;
	}
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public int getPurchasedRate() {
		return purchasedRate;
	}
	public void setPurchasedRate(int purchasedRate) {
		this.purchasedRate = purchasedRate;
	}
	public BigDecimal getDebtRealAmount() {
		return debtRealAmount;
	}
	public void setDebtRealAmount(BigDecimal debtRealAmount) {
		this.debtRealAmount = debtRealAmount;
	}
	public int getFloatingRate() {
		return floatingRate;
	}
	public void setFloatingRate(int floatingRate) {
		this.floatingRate = floatingRate;
	}
	public int getRootPurchasedRate() {
		return rootPurchasedRate;
	}
	public void setRootPurchasedRate(int rootPurchasedRate) {
		this.rootPurchasedRate = rootPurchasedRate;
	}
	public int getRootFloatingRate() {
		return rootFloatingRate;
	}
	public void setRootFloatingRate(int rootFloatingRate) {
		this.rootFloatingRate = rootFloatingRate;
	}
	public String getFirstDebtAssignRequestId() {
		return firstDebtAssignRequestId;
	}
	public void setFirstDebtAssignRequestId(String firstDebtAssignRequestId) {
		this.firstDebtAssignRequestId = firstDebtAssignRequestId;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getMonths() {
		return months;
	}
	public void setMonths(int months) {
		this.months = months;
	}
	public int getYears() {
		return years;
	}
	public void setYears(int years) {
		this.years = years;
	}
	public DebtAssignPricingMethod getPricingMethod() {
		return pricingMethod;
	}
	public void setPricingMethod(DebtAssignPricingMethod pricingMethod) {
		this.pricingMethod = pricingMethod;
	}
	public RepaymentMethod getMethod() {
		return method;
	}
	public void setMethod(RepaymentMethod method) {
		this.method = method;
	}
	public DebtAssignStatus getStatus() {
		return status;
	}
	public void setStatus(DebtAssignStatus status) {
		this.status = status;
	}
	public DebtAssignCancelType getCancelType() {
		return cancelType;
	}
	public void setCancelType(DebtAssignCancelType cancelType) {
		this.cancelType = cancelType;
	}
	public String getStrfield1() {
		return strfield1;
	}
	public void setStrfield1(String strfield1) {
		this.strfield1 = strfield1;
	}
	public String getStrfield2() {
		return strfield2;
	}
	public void setStrfield2(String strfield2) {
		this.strfield2 = strfield2;
	}
	public String getStrfield3() {
		return strfield3;
	}
	public void setStrfield3(String strfield3) {
		this.strfield3 = strfield3;
	}
	public int getIntfield1() {
		return intfield1;
	}
	public void setIntfield1(int intfield1) {
		this.intfield1 = intfield1;
	}
	public int getIntfield2() {
		return intfield2;
	}
	public void setIntfield2(int intfield2) {
		this.intfield2 = intfield2;
	}
	public int getIntfield3() {
		return intfield3;
	}
	public void setIntfield3(int intfield3) {
		this.intfield3 = intfield3;
	}
	public String getAccountApplyId() {
		return accountApplyId;
	}
	public void setAccountApplyId(String accountApplyId) {
		this.accountApplyId = accountApplyId;
	}
	
	public Boolean getAssignLoan() {
		return assignLoan;
	}
	public void setAssignLoan(Boolean assignLoan) {
		this.assignLoan = assignLoan;
	}
	public String getProductKey() {
		return productKey;
	}
	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}
	public int getOriginalDuration() {
		return originalDuration;
	}
	public void setOriginalDuration(int originalDuration) {
		this.originalDuration = originalDuration;
	}
	public int getMaxCancelCount() {
		return maxCancelCount;
	}
	public void setMaxCancelCount(int maxCancelCount) {
		this.maxCancelCount = maxCancelCount;
	}
	
}
package com.gomemyc.invest.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.common.StringIdEntity;

/**
 * 投资队列
 * 
 * @author 何健
 *
 */
public class ReserveQueue extends StringIdEntity{
	
	
	/**
	 * 投资记录单号
	 */
	private String recordId;
	
	/**
	 * 	标的id
	 */
	private String  loanId;
	
	/**
	 * 产品id
	 */
	private String productId;
	
	/**
	 * 队列生成时间
	 */
	private Date createTime;
	
	/**
	 * 预约状态
	 */
	private Integer status;
	
	/**
	 * 总撮合金额
	 */
	private BigDecimal matchedAmount;
	
	/**
	 *可预约产品ID 
	 */
	private String loanTypeId;
	
	/**
	 *可预约产品中文名称
	 */
	private String loanTypeName;
	
	/**
	 * 可预约利率区间ID
	 */
	private String rateRangeId;
	
	/**
	 *可预约利率区间描述
	 */
	private String ratePeriodDesc;
	
	/**
	 *可预约投资期限ID
	 */
	private String  investPeriodId;
	
	/**
	 *可预约投资期限描述
	 */
	private String investPeriodDesc;
	
	/**
	 *理财产品名称
	 */
	private String loanName;
	
	/**
	 *资产编号
	 */
	private String assetId;
	
	/**
	 * 标的编号
	 */
	private String loanRequestSerial;

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getMatchedAmount() {
		return matchedAmount;
	}

	public void setMatchedAmount(BigDecimal matchedAmount) {
		this.matchedAmount = matchedAmount;
	}

	public String getLoanTypeId() {
		return loanTypeId;
	}

	public void setLoanTypeId(String loanTypeId) {
		this.loanTypeId = loanTypeId;
	}

	public String getLoanTypeName() {
		return loanTypeName;
	}

	public void setLoanTypeName(String loanTypeName) {
		this.loanTypeName = loanTypeName;
	}

	public String getRateRangeId() {
		return rateRangeId;
	}

	public void setRateRangeId(String rateRangeId) {
		this.rateRangeId = rateRangeId;
	}

	public String getRatePeriodDesc() {
		return ratePeriodDesc;
	}

	public void setRatePeriodDesc(String ratePeriodDesc) {
		this.ratePeriodDesc = ratePeriodDesc;
	}

	public String getInvestPeriodId() {
		return investPeriodId;
	}

	public void setInvestPeriodId(String investPeriodId) {
		this.investPeriodId = investPeriodId;
	}

	public String getInvestPeriodDesc() {
		return investPeriodDesc;
	}

	public void setInvestPeriodDesc(String investPeriodDesc) {
		this.investPeriodDesc = investPeriodDesc;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getLoanRequestSerial() {
		return loanRequestSerial;
	}

	public void setLoanRequestSerial(String loanRequestSerial) {
		this.loanRequestSerial = loanRequestSerial;
	}
}


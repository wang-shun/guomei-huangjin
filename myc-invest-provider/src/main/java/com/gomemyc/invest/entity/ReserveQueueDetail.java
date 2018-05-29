package com.gomemyc.invest.entity;

import java.math.BigDecimal;

import com.gomemyc.common.StringIdEntity;

/**
 * 预约队列明细
 * 
 * @author 何健
 *
 */
public class ReserveQueueDetail extends StringIdEntity{
	/**
	 * 主键id
	 */
	private String  id;
	/**
	 * 投资队列id
	 */
	private String InvestQueueId;
	/**
	 * 预约申请id
	 */
	private String ReserveApplyId;
	/**
	 * 撮合资金
	 */
	private BigDecimal matchingFunds;
	/**
	 * 预约产品类型名称
	 */
	private String LoanTypeName;
	/**
	 * 	利率区间
	 */
	private String RateDate;
	/**
	 * 	投资期限区间
	 */
	private String InvestmentInterval;
	/**
	 * 投资id
	 */
	private String InvestId;
	
	public String getInvestQueueId() {
		return InvestQueueId;
	}
	public void setInvestQueueId(String investQueueId) {
		InvestQueueId = investQueueId;
	}
	public String getReserveApplyId() {
		return ReserveApplyId;
	}
	public void setReserveApplyId(String reserveApplyId) {
		ReserveApplyId = reserveApplyId;
	}
	public BigDecimal getMatchingFunds() {
		return matchingFunds;
	}
	public void setMatchingFunds(BigDecimal matchingFunds) {
		this.matchingFunds = matchingFunds;
	}
	public String getLoanTypeName() {
		return LoanTypeName;
	}
	public void setLoanTypeName(String loanTypeName) {
		LoanTypeName = loanTypeName;
	}
	public String getRateDate() {
		return RateDate;
	}
	public void setRateDate(String rateDate) {
		RateDate = rateDate;
	}
	public String getInvestmentInterval() {
		return InvestmentInterval;
	}
	public void setInvestmentInterval(String investmentInterval) {
		InvestmentInterval = investmentInterval;
	}
	public String getInvestId() {
		return InvestId;
	}
	public void setInvestId(String investId) {
		InvestId = investId;
	}
}

package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;

import java.math.BigDecimal;

/**
 * 预约队列明细
 * 
 * @author 何健
 *
 */
public class ReserveQueueDetail extends StringIdEntity{

	/**
	 * 投资队列id
	 */
	private String investQueueId;
	/**
	 * 预约申请id
	 */
	private String reserveApplyId;
	/**
	 * 撮合资金
	 */
	private BigDecimal matchingFunds;
	/**
	 * 预约产品类型名称
	 */
	private String loanTypeName;
	/**
	 * 	利率区间
	 */
	private String rateDate;
	/**
	 * 	投资期限区间
	 */
	private String investmentInterval;
	/**
	 * 投资id
	 */
	private String investId;

	public String getInvestQueueId() {
		return investQueueId;
	}

	public void setInvestQueueId(String investQueueId) {
		this.investQueueId = investQueueId;
	}

	public String getReserveApplyId() {
		return reserveApplyId;
	}

	public void setReserveApplyId(String reserveApplyId) {
		this.reserveApplyId = reserveApplyId;
	}

	public BigDecimal getMatchingFunds() {
		return matchingFunds;
	}

	public void setMatchingFunds(BigDecimal matchingFunds) {
		this.matchingFunds = matchingFunds;
	}

	public String getLoanTypeName() {
		return loanTypeName;
	}

	public void setLoanTypeName(String loanTypeName) {
		this.loanTypeName = loanTypeName;
	}

	public String getRateDate() {
		return rateDate;
	}

	public void setRateDate(String rateDate) {
		this.rateDate = rateDate;
	}

	public String getInvestmentInterval() {
		return investmentInterval;
	}

	public void setInvestmentInterval(String investmentInterval) {
		this.investmentInterval = investmentInterval;
	}

	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}
}

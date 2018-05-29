package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 可预约投资期限
 * 
 * @author 何健
 *
 */
public class ReserveInvestPeriod extends StringIdEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6902909735887452940L;
	/**
	 * 是否删除该投资期限
	 */
	private boolean deleted;
	/**
	 * 是否启用该利率区间
	 */
	private boolean  state;

	/**
	 * 最大投资期限
	 */
	private int maxInvestPeriod;
	/**
	 * 最小投资期限
	 */
	private int minInvestPeriod;
	
	/**
	 * 投资期限区间描述
	 */
	private String investPeriodDesc;
	public String getInvestPeriodDesc() {
		return investPeriodDesc;
	}
	public void setInvestPeriodDesc(String investPeriodDesc) {
		this.investPeriodDesc = investPeriodDesc;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public int getMaxInvestPeriod() {
		return maxInvestPeriod;
	}
	public void setMaxInvestPeriod(int maxInvestPeriod) {
		this.maxInvestPeriod = maxInvestPeriod;
	}
	public int getMinInvestPeriod() {
		return minInvestPeriod;
	}
	public void setMinInvestPeriod(int minInvestPeriod) {
		this.minInvestPeriod = minInvestPeriod;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	
}

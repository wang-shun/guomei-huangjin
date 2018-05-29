package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 已预约产品（表）tbl_reserve_already
 * @author CAOXIAOYANG
 *
 */
public class ReserveAlready extends StringIdEntity{
	private String reserveId;
	private String loanTypeId;
	private String loanTypeName;
	private String  loanTypeKey;
	public String getReserveId() {
		return reserveId;
	}
	public void setReserveId(String reserveId) {
		this.reserveId = reserveId;
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
	public String getLoanTypeKey() {
		return loanTypeKey;
	}
	public void setLoanTypeKey(String loanTypeKey) {
		this.loanTypeKey = loanTypeKey;
	}
	
}

package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 保存已预约投资期限
 * @author CAOXIAOYANG
 *
 */
public class ReserveAlreadyDate extends StringIdEntity{
	private String reserveId;	
	private String reserveDateId;	
	private String minDate	;
	private String maxDate;	
	private String termDesc;
	public String getReserveId() {
		return reserveId;
	}
	public void setReserveId(String reserveId) {
		this.reserveId = reserveId;
	}
	public String getReserveDateId() {
		return reserveDateId;
	}
	public void setReserveDateId(String reserveDateId) {
		this.reserveDateId = reserveDateId;
	}
	public String getMinDate() {
		return minDate;
	}
	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}
	public String getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}
	public String getTermDesc() {
		return termDesc;
	}
	public void setTermDesc(String termDesc) {
		this.termDesc = termDesc;
	}	
	
}

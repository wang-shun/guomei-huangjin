package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 已预约利率（表）tbl_reserve_rate
 * @author CAOXIAOYANG
 *
 */
public class ReserveAlreadyRate extends StringIdEntity{
	private String id;
	private String  reserveId;
	private String rateId;
	private String rateDesc;
	private Integer minRate;
	private Integer maxRate;
	public String getReserveId() {
		return reserveId;
	}
	public void setReserveId(String reserveId) {
		this.reserveId = reserveId;
	}
	public String getRateId() {
		return rateId;
	}
	public void setRateId(String rateId) {
		this.rateId = rateId;
	}
	public String getRateDesc() {
		return rateDesc;
	}
	public void setRateDesc(String rateDesc) {
		this.rateDesc = rateDesc;
	}
	public Integer getMinRate() {
		return minRate;
	}
	public void setMinRate(Integer minRate) {
		this.minRate = minRate;
	}
	public Integer getMaxRate() {
		return maxRate;
	}
	public void setMaxRate(Integer maxRate) {
		this.maxRate = maxRate;
	}
}

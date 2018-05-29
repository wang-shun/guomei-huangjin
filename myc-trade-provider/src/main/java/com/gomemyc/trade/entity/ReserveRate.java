package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 可预约利率区间表
 * 
 * @author 何健
 *
 */
public class ReserveRate extends StringIdEntity{
	/**
	 * 投资年化收益最小利率
	 */
	private Integer minRate;

	/**
	 * 投资年化收益最大利率
	 */
	private Integer maxRate;
	
	/** 是否启用该利率期间 */
	private Boolean enable;

	/** 是否使用红包， 0:不使用 1:使用 */
	private Boolean coupon;

	/** 利率区间描述 */
	private String rateDesc;
	
	/** 是否删除该利率区间 */
	private Boolean deleted;
	
	/** 是否显示推荐 */
	private String recommend;

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getRateDesc() {
		return rateDesc;
	}

	public void setRateDesc(String rateDesc) {
		this.rateDesc = rateDesc;
	}

	public Integer getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(Integer maxRate) {
		this.maxRate = maxRate;
	}

	public Integer getMinRate() {
		return minRate;
	}

	public void setMinRate(Integer minRate) {
		this.minRate = minRate;
	}

	public Boolean getCoupon() {
		return coupon;
	}

	public void setCoupon(Boolean coupon) {
		this.coupon = coupon;
	}
}

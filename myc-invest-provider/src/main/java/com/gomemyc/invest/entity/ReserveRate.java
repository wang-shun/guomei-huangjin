package com.gomemyc.invest.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 可预约利率区间表
 * 
 * @author 何健
 *
 */
public class ReserveRate extends StringIdEntity{
	/**
	 * 	主键id
	 */
	private String id;
	/**
	 * 投资年化收益最小利率
	 */
	private Integer minDate;
	/**
	 * 投资年化收益最大利率
	 */
	private Integer maxDate;
	
	/** 是否启用该利率期间 */
	private Boolean enable;
	
	/** 利率区间描述 */
	private String rateDesc;
	
	/** 是否删除该利率区间 */
	private Boolean deleted;
	
	/** 是否显示推荐 */
	private String recommend;

	public Integer getMinDate() {
		return minDate;
	}

	public void setMinDate(Integer minDate) {
		this.minDate = minDate;
	}

	public Integer getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Integer maxDate) {
		this.maxDate = maxDate;
	}

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
	
}

package com.gomemyc.gold.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @ClassName:GoldProductStatusEnum.java
 * @Description:黄金产品状态类型
 * @author zhuyunpeng
 * @date 2017年3月29日
 */
public enum GoldProductStatusEnum {

	/**
	 * 黄金产品状态（基础）
	 */
	PRODUCTSTATUS_BASIC(-1, "基础"),

	/**
	 * 黄金产品状态（初始）
	 */
	PRODUCTSTATUS_INITIAL(0, "初始"),

	/**
	 * 黄金产品状态（已安排）
	 */
	PRODUCTSTATUS_ARRANGED(1, "调度中"),
	
	/**
	 * 黄金产品状态（已安排）
	 */
	PRODUCTSTATUS_ARRANGED_READY(2, "准备调度中"),

	/**
	 * 黄金产品状态（产品投放）
	 */
	PRODUCTSTATUS_LAUNCH(3, "产品投放"),

	/**
	 * 黄金产品状态（已满标）
	 */
	PRODUCTSTATUS_REACHED_THE_STANDARD(4, "已满标"),

	/**
	 * 黄金产品状态（到期未满标）
	 */
	PRODUCTSTATUS_DUE_UNDER_THE_STANDARD(5, "到期未满标"),

	/**
	 * 黄金产品状态（已结算）
	 */
	PRODUCTSTATUS_SETTLED(6, "已结算"),

	/**
	 * 黄金产品状态（已还清）
	 */
	PRODUCTSTATUS_CLEARED(7, "已还清"),

	/**
	 * 黄金产品状态（已存档）
	 */
	PRODUCTSTATUS_SAVED(8, "已存档"),

	/**
	 * 黄金产品状态（已取消）
	 */
	PRODUCTSTATUS_CANCELED(-2, "已取消"),

	/**
	 * 黄金产品状态（已流标）
	 */
	PRODUCTSTATUS_FLOWED_THE_STANDARD(-3, "已流标"),;

	private int status;

	private String key;

	private GoldProductStatusEnum(int status, String key) {
		this.status = status;
		this.key = key;
	}

	public int getStatus() {
		return status;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}

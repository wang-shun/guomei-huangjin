package com.gomemyc.gold.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @ClassName:GoldProductStatusEnum.java
 * @Description:付款方式状态类型
 * @author zhuyunpeng
 * @date 2017年3月29日
 */
public enum GoldMethodStatusEnum {


	/**
	 * 付款方式
	 */
	METHOD_STATUS_ONCE(1, "一次性还本付息");


	private int status;

	private String key;

	private GoldMethodStatusEnum(int status, String key) {
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

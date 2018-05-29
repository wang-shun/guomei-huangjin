package com.gomemyc.invest.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 配置信息
 * @author CAOXIAOYANG
 *
 */
public class ReserveConfig extends StringIdEntity{
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 参数名
	 */
	private String parameterName;
	/**
	 * 参数值
	 */
	private String parameterValue;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
}

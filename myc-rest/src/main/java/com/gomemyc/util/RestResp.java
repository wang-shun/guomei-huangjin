package com.gomemyc.util;

import java.io.Serializable;

import com.sun.research.ws.wadl.Include;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 响应返回值格式
 * @author wuhui
 *
 */
@JSONType(orders = { "status", "msg", "data" })
public class RestResp implements Serializable {

	private static final long serialVersionUID = -3197616652643404121L;

	private int status;

	private String msg;

	private Object data;

	public RestResp() {
	}

	public RestResp(InfoCode infoCode, Object data) {
		if (infoCode != null) {
			setStatus(infoCode.getStatus());
			setMsg(infoCode.getMsg());
		}
		this.data = data;
	}

	public static RestResp build(InfoCode infoCode, Object data) {
		return new RestResp(infoCode, data);
	}

	public static RestResp build(InfoCode infoCode) {
		return new RestResp(infoCode, null);
	}

	public static RestResp build(int status, String message, Object data) {
		RestResp resp = new RestResp();
		resp.setData(data);
		resp.setMsg(message);
		resp.setStatus(status);
		return resp;
	}
	
	public static RestResp build(int status, String message ) {
		RestResp resp = new RestResp();
		resp.setMsg(message);
		resp.setStatus(status);
		return resp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	

}

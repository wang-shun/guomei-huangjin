package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryPriceResultModel
 * @author zhuyunpeng
 * @description 实时金价查询响应参数实体
 * @date 2017年3月8日
 */
public class QueryPriceResultDto implements Serializable{

	
	private static final long serialVersionUID = 2097521284719769058L;

	//响应码(成功：000000)
	private String retCode;

	//响应信息(错误消息)
	private String retMsg;

	//返回业务数据
	private QueryPriceRDataDto data;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryPriceResultDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QueryPriceRDataDto getData() {
		return data;
	}

	public void setData(QueryPriceRDataDto data) {
		this.data = data;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

}

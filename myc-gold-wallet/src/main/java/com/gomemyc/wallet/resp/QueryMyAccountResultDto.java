package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryMyAccountResultModel
 * @author zhuyunpeng
 * @description 账户信息查询响应参数实体
 * @date 2017年3月8日
 */
public class QueryMyAccountResultDto implements Serializable {

	
	private static final long serialVersionUID = 4141453345144316973L;

	//响应码(成功：000000)
	private String retCode;

	//响应信息(错误消息)
	private String retMsg;

	//签名结果
	private String sign;

	//返回业务数据
	private QueryMyAccountRDataDto data;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryMyAccountResultDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public QueryMyAccountRDataDto getData() {
		return data;
	}

	public void setData(QueryMyAccountRDataDto data) {
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

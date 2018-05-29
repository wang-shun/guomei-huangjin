package com.gomemyc.wallet.resp;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName BuyTimeGoldResultDto
 * @author zhuyunpeng
 * @description 购买定期金响应参数实体
 * @date 2017年3月8日
 */
public class BuyTimeGoldResultDto implements Serializable {

	
	private static final long serialVersionUID = -5899036328588549544L;

	//响应码(成功：000000)
	private String retCode; 
	
	//响应信息(错误消息)
	private String retMsg; 
	
	//签名结果
	private String sign; 
	
	//返回业务数据
	private BuyTimeGoldRDataDto data; 
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public BuyTimeGoldResultDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BuyTimeGoldRDataDto getData() {
		return data;
	}

	public void setData(BuyTimeGoldRDataDto data) {
		this.data = data;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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

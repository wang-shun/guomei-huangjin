package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName ConfirmBuyTimeGoldResultDto
 * @author liuqiangbin
 * @description 2.7.4 定期到期利息汇总对账响应参数实体
 * @date 2017年3月9日
 */
public class CheckSumInterestResultDto implements Serializable {
	
	
	private static final long serialVersionUID = -6848987555563415426L;

	//响应码(成功：000000)
	private String retCode;
	
	//响应信息(错误消息)
	private String retMsg; 
	
	//签名结果
	private String sign; 
	
	//返回业务数据
	private CheckSumInterestRDataDto[] data; 
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public CheckSumInterestResultDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CheckSumInterestRDataDto[] getData() {
		return data;
	}

	public void setData(CheckSumInterestRDataDto[] data) {
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

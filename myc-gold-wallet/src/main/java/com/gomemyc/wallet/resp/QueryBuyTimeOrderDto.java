package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryBuyTimeOrderDto
 * @author zhuyunpeng
 * @description 购买定期金交易结果查询参数实体
 * @date 2017年3月8日
 */
public class QueryBuyTimeOrderDto implements Serializable {

	
	private static final long serialVersionUID = 7843659230485152007L;

	//版本号
	private String version;

	//商户编码
	private String merchantCode;

	//请求订单号(全局唯一)
	private String reqNo;

	//签名结果
	private String sign;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryBuyTimeOrderDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QueryBuyTimeOrderDto(String version, String merchantCode, String reqNo, String sign) {
		super();
		this.version = version;
		this.merchantCode = merchantCode;
		this.reqNo = reqNo;
		this.sign = sign;
	}
	
	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getMerchantCode() {
		return merchantCode;
	}
	
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

}

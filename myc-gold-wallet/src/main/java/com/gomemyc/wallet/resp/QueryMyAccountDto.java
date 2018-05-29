package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryMyAccountModel
 * @author zhuyunpeng
 * @description 账户信息查询参数实体
 * @date 2017年3月8日
 */
public class QueryMyAccountDto implements Serializable {

	
	private static final long serialVersionUID = -1614013063559847838L;

	//版本号
	private String version;

	//商户编码
	private String merchantCode;

	//签名结果
	private String sign;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryMyAccountDto() {
		super();
		// TODO Auto-generated constructor stub
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

	public QueryMyAccountDto(String version, String merchantCode, String sign) {
		super();
		this.version = version;
		this.merchantCode = merchantCode;
		this.sign = sign;
	}

}

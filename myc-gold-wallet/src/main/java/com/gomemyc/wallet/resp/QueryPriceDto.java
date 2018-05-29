package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryPriceModel
 * @author zhuyunpeng
 * @description 实时金价查询参数实体
 * @date 2017年3月8日
 */
public class QueryPriceDto implements Serializable {


	private static final long serialVersionUID = -2127238548168431944L;

	//版本号
	private String version;

	//商户编码
	private String merchantCode;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryPriceDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QueryPriceDto(String version, String merchantCode) {
        super();
        this.version = version;
        this.merchantCode = merchantCode;
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

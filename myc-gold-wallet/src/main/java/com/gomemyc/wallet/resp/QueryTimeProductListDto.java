package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryTimeProductListModel
 * @author zhuyunpeng
 * @description 定期金产品查询参数实体
 * @date 2017年3月8日
 */
public class QueryTimeProductListDto implements Serializable {

	
	private static final long serialVersionUID = -7244882292390956396L;

	//版本号
	private String version;

	//商户编码
	private String merchantCode;

	//起始记录行数(从0开始，默认为0)
	private Integer start;

	//获取记录数大小(默认为0)
	private Integer size;

	//签名结果
	private String sign;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryTimeProductListDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public QueryTimeProductListDto(String version, String merchantCode, Integer start, Integer size, String sign) {
        super();
        this.version = version;
        this.merchantCode = merchantCode;
        this.start = start;
        this.size = size;
        this.sign = sign;
    }

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
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

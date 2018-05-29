package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName CheckSumInterestModel
 * @author zhuyunpeng
 * @description: 2.7.4定期到期利息汇总对账请求参数实体
 * @date 2017年3月9日
 */
public class CheckSumInterestDto implements Serializable {
	
	
	private static final long serialVersionUID = 6977876996515573771L;

	//版本号
    private String version; 
    
    //商户编码
    private String merchantCode; 
    
    //产品编码
    private String productCode; 
    
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

    public CheckSumInterestDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CheckSumInterestDto(String version, String merchantCode, String productCode, Integer start, Integer size,
            String sign) {
        super();
        this.version = version;
        this.merchantCode = merchantCode;
        this.productCode = productCode;
        this.start = start;
        this.size = size;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
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
    
}

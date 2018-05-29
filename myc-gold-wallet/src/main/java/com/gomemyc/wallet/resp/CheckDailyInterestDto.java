package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName CheckDailyInterestModel
 * @author zhuyunpeng
 * @description: 2.7.3每天利息对账请求参数实体
 * @date 2017年3月9日
 */
public class CheckDailyInterestDto implements Serializable{
	
	
	private static final long serialVersionUID = -1932983838607228339L;

	//版本号
    private String version; 
    
    //商户编码
    private String merchantCode; 
    
    //产品编码
    private String productCode; 
    
    //收益日期[格式yyyy-MM-dd]
    private String orderDate;
    
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
    
    public CheckDailyInterestDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CheckDailyInterestDto(String version, String merchantCode, String productCode, String orderDate,
            Integer start, Integer size, String sign) {
        super();
        this.version = version;
        this.merchantCode = merchantCode;
        this.productCode = productCode;
        this.orderDate = orderDate;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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

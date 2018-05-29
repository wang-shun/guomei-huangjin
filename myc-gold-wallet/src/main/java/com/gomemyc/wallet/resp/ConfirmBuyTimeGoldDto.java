package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName ConfirmBuyTimeGoldDto
 * @author zhuyunpeng
 * @description 购买定期金确认参数实体
 * @date 2017年3月8日
 */
public class ConfirmBuyTimeGoldDto implements Serializable {

	
	private static final long serialVersionUID = 421929125280014939L;

	//版本号
	private String version;
	
	//商户编码
	private String merchantCode;
	
	//订单时间 (yyyy-MM-dd HH:mm:ss)
	private String orderTime;
    
	//确认金重(单位：毫克)
	private BigDecimal confirmWeight; 
	
	//确认金额(单位： 分)
	private BigDecimal confirmAmount; 
	
	//确认金价(单位： 分)
	private BigDecimal confirmPrice; 
	
	//确认产品代码
	private String productCode; 
	
	//确认订单号(买金商户请求订单号)
	private String reqNo; 
	
	//签名结果
	private String sign; 
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
    public ConfirmBuyTimeGoldDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ConfirmBuyTimeGoldDto(String version, String merchantCode, String orderTime, BigDecimal confirmWeight,
            BigDecimal confirmAmount, BigDecimal confirmPrice, String productCode, String reqNo, String sign) {
        super();
        this.version = version;
        this.merchantCode = merchantCode;
        this.orderTime = orderTime;
        this.confirmWeight = confirmWeight;
        this.confirmAmount = confirmAmount;
        this.confirmPrice = confirmPrice;
        this.productCode = productCode;
        this.reqNo = reqNo;
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

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public BigDecimal getConfirmWeight() {
		return confirmWeight;
	}

	public void setConfirmWeight(BigDecimal confirmWeight) {
		this.confirmWeight = confirmWeight;
	}

	public BigDecimal getConfirmAmount() {
		return confirmAmount;
	}

	public void setConfirmAmount(BigDecimal confirmAmount) {
		this.confirmAmount = confirmAmount;
	}

	public BigDecimal getConfirmPrice() {
		return confirmPrice;
	}

	public void setConfirmPrice(BigDecimal confirmPrice) {
		this.confirmPrice = confirmPrice;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
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

}

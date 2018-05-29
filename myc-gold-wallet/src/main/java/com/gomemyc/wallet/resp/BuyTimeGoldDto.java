package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName BuyTimeGoldDto
 * @author zhuyunpeng
 * @description 购买定期金参数实体
 * @date 2017年3月8日
 */
public class BuyTimeGoldDto implements Serializable {
	
	
	private static final long serialVersionUID = 3419057962924417862L;

	//版本号
	private String version; 
	
	//商户编码
	private String merchantCode; 
	
	//订单时间 (yyyy-MM-dd HH:mm:ss)
	private String orderTime;
	
	//订单金重(单位：毫克)
	private BigDecimal goldWeight;
    
	//订单金额(单位： 分)
	private BigDecimal orderAmount;
	
	//请求订单号(全局唯一)
	private String reqNo; 
	
	//购买类型 (1按钱买【订单金额必传】，2按金重买【金重必传】)
	private String buyType; 
	
	//产品编码
	private String productCode; 
	
	//国美金融的标的代码
	private String depositCode; 
	
	//签名结果
	private String sign; 
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public BuyTimeGoldDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public BuyTimeGoldDto(String version, String merchantCode, String orderTime, BigDecimal goldWeight,
			BigDecimal orderAmount, String reqNo, String buyType, String productCode, String depositCode,
			String sign) {
		super();
		this.version = version;
		this.merchantCode = merchantCode;
		this.orderTime = orderTime;
		this.goldWeight = goldWeight;
		this.orderAmount = orderAmount;
		this.reqNo = reqNo;
		this.buyType = buyType;
		this.productCode = productCode;
		this.depositCode = depositCode;
		this.sign = sign;
	}
	
	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public BigDecimal getGoldWeight() {
		return goldWeight;
	}

	public void setGoldWeight(BigDecimal goldWeight) {
		this.goldWeight = goldWeight;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getBuyType() {
		return buyType;
	}

	public void setBuyType(String buyType) {
		this.buyType = buyType;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getDepositCode() {
		return depositCode;
	}

	public void setDepositCode(String depositCode) {
		this.depositCode = depositCode;
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

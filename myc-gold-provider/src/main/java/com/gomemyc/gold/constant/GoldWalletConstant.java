package com.gomemyc.gold.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("goldWalletConstant") 
public class GoldWalletConstant {

	@Value("${gold.ip}")  
	private String ip; // 黄金钱包接口ip
	
	@Value("${gold.version}") 
	private String version; // 版本号
	
	@Value("${gold.merchantCode}") 
	private String merchantCode; // 商户编码
	
	@Value("${gold.sign}") 
	private String sign; // 签名结果
	
	@Value("${gold.buyType}") 
	private String buyType; // 购买类型 (1按钱买【订单金额必传】，2按金重买【金重必传】)
	
	@Value("${gold.expireTime}") 
	private String expireTime; // 到期时间

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBuyType() {
		return buyType;
	}

	public void setBuyType(String buyType) {
		this.buyType = buyType;
	}

	public String getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}


}

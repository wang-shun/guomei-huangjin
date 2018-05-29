package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;

/** 
 * Class: GoldDayInterestAccountCheckExtend
 * Date:     2017年3月22日 
 * @author   LiuQiangBin
 * @since    JDK 1.8 
 */
public class GoldDayInterestAccountCheckExtend implements Serializable{

	private static final long serialVersionUID = 7148903364623431049L;

	private BigDecimal goldPrice;

    private Integer clearRate;

    private String productCode;
    
    private String productName;

	public BigDecimal getGoldPrice() {
		return goldPrice;
	}

	public void setGoldPrice(BigDecimal goldPrice) {
		this.goldPrice = goldPrice;
	}

	public Integer getClearRate() {
		return clearRate;
	}

	public void setClearRate(Integer clearRate) {
		this.clearRate = clearRate;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

}
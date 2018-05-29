package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;

/** 
 * Class: GoldInvestOrderExtend
 * Date:     2017年3月22日 
 * @author   LiuQiangBin
 * @since    JDK 1.8 
 */
public class GoldInvestOrderExtend implements Serializable{
	
	private static final long serialVersionUID = 1524477479082525333L;
	
	private BigDecimal realWeight;

    private String reqNo;

    private String goldOrderNo;

    private String productId;

    private String productCode;

	public BigDecimal getRealWeight() {
		return realWeight;
	}

	public void setRealWeight(BigDecimal realWeight) {
		this.realWeight = realWeight;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getGoldOrderNo() {
		return goldOrderNo;
	}

	public void setGoldOrderNo(String goldOrderNo) {
		this.goldOrderNo = goldOrderNo;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
    
}

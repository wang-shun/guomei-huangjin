package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 *@ClassName:ContractInvest
 *@Description:订单中与合同相关字段实体
 *@author zhuyunpeng
 *@date 2017年4月11日
 */
public class ContractInvest implements Serializable{


	private static final long serialVersionUID = 780775731021967183L;
	
	//用户Id
	private String userId;
	
	//购买克重
	private BigDecimal goldWeight;
	
	//产品Id
	private String productId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId == null ? null : productId.trim();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId == null ? null : userId.trim();
	}

	public BigDecimal getGoldWeight() {
		return goldWeight;
	}

	public void setGoldWeight(BigDecimal goldWeight) {
		this.goldWeight = goldWeight;
	}
	

}

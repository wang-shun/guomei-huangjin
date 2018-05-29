package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryMyAccountRDataModel
 * @author zhyunpeng
 * @description 账户信息查询响应data参数实体
 * @date 2017年3月8日
 */
public class QueryMyAccountRDataDto implements Serializable {

	
	private static final long serialVersionUID = -7064453230278448278L;

	//账户金重(单位：毫克)
	private BigDecimal accountGoldWeight;

	//账户余额(单位：分)
	private BigDecimal accountBalance;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public QueryMyAccountRDataDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getAccountGoldWeight() {
		return accountGoldWeight;
	}

	public void setAccountGoldWeight(BigDecimal accountGoldWeight) {
		this.accountGoldWeight = accountGoldWeight;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

}

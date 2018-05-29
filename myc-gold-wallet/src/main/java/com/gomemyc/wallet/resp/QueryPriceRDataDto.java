package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryPriceRDataModel
 * @author zhuyunpeng
 * @description 实时金价查询响应data参数实体
 * @date 2017年3月8日
 */
public class QueryPriceRDataDto implements Serializable {

	
	private static final long serialVersionUID = -6517493076578786868L;
	//实时金价(单位：分/克)
	private BigDecimal realPrice;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
    public QueryPriceRDataDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(BigDecimal realPrice) {
		this.realPrice = realPrice;
	}

}

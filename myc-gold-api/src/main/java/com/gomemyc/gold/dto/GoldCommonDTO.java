/** 
 * Project Name:myc-gold-api 
 * File Name:GoldCommonDTO.java 
 * Package Name:com.gomemyc.gold.dto 
 * Date:2017年3月12日上午11:29:54 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  

package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldCommonDTO <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 上午11:29:54 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldCommonDTO  implements Serializable{


	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 5429365440740014461L;
	
	
	//成交金价
	private  BigDecimal realPrice;

	public GoldCommonDTO() {
	}

	public BigDecimal getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(BigDecimal realPrice) {
		this.realPrice = realPrice;
	} 
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
  
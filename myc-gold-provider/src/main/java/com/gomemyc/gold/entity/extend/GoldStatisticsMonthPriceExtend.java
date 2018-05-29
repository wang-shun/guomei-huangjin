/** 
 * Project Name:myc-gold-provider 
 * File Name:GoldStatisticsMonthPriceExtend.java 
 * Package Name:com.gomemyc.gold.entity.extend 
 * Date:2017年3月19日下午5:32:23 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/** 
 * ClassName:GoldStatisticsMonthPriceExtend <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月19日 下午5:32:23 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldStatisticsMonthPriceExtend implements Serializable {

	
    /** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 1L;

	private Long id;

    private Integer priceYears;

    private Integer priceMonth;

    private BigDecimal price;

    private Date createTime;
    
    private BigDecimal totalPrice;
    
    private Integer totalDate;
    
    
    
    
    
    
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPriceYears() {
		return priceYears;
	}

	public void setPriceYears(Integer priceYears) {
		this.priceYears = priceYears;
	}

	public Integer getPriceMonth() {
		return priceMonth;
	}

	public void setPriceMonth(Integer priceMonth) {
		this.priceMonth = priceMonth;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getTotalDate() {
		return totalDate;
	}

	public void setTotalDate(Integer totalDate) {
		this.totalDate = totalDate;
	}
    
     
    
    
}
  
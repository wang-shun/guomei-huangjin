/** 
 * Project Name:myc-gold-api 
 * File Name:GoldStatisticsDayPriceDTO.java 
 * Package Name:com.gomemyc.gold.dto 
 * Date:2017年3月13日下午3:50:14 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldStatisticsDayPriceDTO <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月13日 下午3:50:14 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldStatisticsDayPriceDTO  implements Serializable{



	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 5609930692783103456L;

	private Long id;

    private Date priceDate;

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

    public Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(Date priceDate) {
        this.priceDate = priceDate;
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
	 
	public int getTotalDate() {
		return totalDate;
	}

	public void setTotalDate(int totalDate) {
		this.totalDate = totalDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
  
/** 
 * Project Name:myc-gold-api 
 * File Name:GoldStatisticsHourPriceDTO.java 
 * Package Name:com.gomemyc.gold.dto 
 * Date:2017年3月13日下午3:53:02 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldStatisticsHourPriceDTO <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月13日 下午3:53:02 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldStatisticsHourPriceDTO implements Serializable {


	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 8478015137724673389L;

	private Long id;

    private Date priceDate;

    private Integer priceHour;

    private BigDecimal price;

    private Date createTime;
    
    private BigDecimal totalPrice;
    
    private Integer totalDate;


    public GoldStatisticsHourPriceDTO() {
    }

    public GoldStatisticsHourPriceDTO(Long id, Date priceDate, Integer priceHour, BigDecimal price, Date createTime) {
        this.id = id;
        this.priceDate = priceDate;
        this.priceHour = priceHour;
        this.price = price;
        this.createTime = createTime;
    }

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

    public Integer getPriceHour() {
        return priceHour;
    }

    public void setPriceHour(Integer priceHour) {
        this.priceHour = priceHour;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
  
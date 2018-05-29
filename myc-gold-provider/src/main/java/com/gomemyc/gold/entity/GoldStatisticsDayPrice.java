package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldStatisticsDayPrice implements Serializable{
	
	
	private static final long serialVersionUID = 8169692624787704144L;

	private Long id;

    private Date priceDate;

    private BigDecimal price;

    private Date createTime;
 
    
    
    

	public GoldStatisticsDayPrice() {
		super();
	}
	
	

	public GoldStatisticsDayPrice(Date priceDate, BigDecimal price, Date createTime) {
		this.priceDate = priceDate;
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
    
    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
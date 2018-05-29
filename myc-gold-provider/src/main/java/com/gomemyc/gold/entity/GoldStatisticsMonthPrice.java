package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldStatisticsMonthPrice implements Serializable {
	
	
	private static final long serialVersionUID = 4614269904763876305L;

	private Long id;

    private Integer priceYears;

    private Integer priceMonth;

    private BigDecimal price;

    private Date createTime;

    
     
     
    
    public GoldStatisticsMonthPrice() {
		super();
	}
    
    

	public GoldStatisticsMonthPrice(Integer priceYears, Integer priceMonth, BigDecimal price, Date createTime) {
		this.priceYears = priceYears;
		this.priceMonth = priceMonth;
		this.price = price;
		this.createTime = createTime;
	}

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
    
    
    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
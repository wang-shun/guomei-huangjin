package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldStatisticsHourPrice implements Serializable{
	
	

	private static final long serialVersionUID = -1155249393754064681L;

	private Long id;

    private Date priceDate;

    private Integer priceHour;

    private BigDecimal price;

    private Date createTime;


    public GoldStatisticsHourPrice() {
    }

    public GoldStatisticsHourPrice(Long id, Date priceDate, Integer priceHour, BigDecimal price, Date createTime) {
		this.id = id;
		this.priceDate = priceDate;
		this.priceHour = priceHour;
		this.price = price;
		this.createTime = createTime;
	}

	public GoldStatisticsHourPrice(Date priceDate, Integer priceHour, BigDecimal price, Date createTime) {
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
    
    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
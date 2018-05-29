package com.gomemyc.gold.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GoldStatisticsYearPrice  implements Serializable {



	private static final long serialVersionUID = -5305568021249145285L;

	private Long id;

    private Integer priceYears;

    private BigDecimal price;

    private Date createTime;
    
    
    
    
    
    

    public GoldStatisticsYearPrice() {
		super();
	}
    
    

	public GoldStatisticsYearPrice(Integer priceYears, BigDecimal price, Date createTime) {
		this.priceYears = priceYears;
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
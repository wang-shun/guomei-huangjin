package com.gomemyc.gold.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by TianBin on 2017/3/16.
 */
public class GoldStatisticsYearPriceDTO implements Serializable {




    /** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = -8305440101498314345L;

	private Integer priceYear;

    private BigDecimal price;


    public GoldStatisticsYearPriceDTO() {
    }

    public GoldStatisticsYearPriceDTO(Integer priceYear, BigDecimal price) {
        this.priceYear = priceYear;
        this.price = price;
    }

    public Integer getPriceYear() {
        return priceYear;
    }

    public void setPriceYear(Integer priceYear) {
        this.priceYear = priceYear;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

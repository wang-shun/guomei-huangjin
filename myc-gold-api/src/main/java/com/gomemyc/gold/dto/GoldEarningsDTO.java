/** 
 * Project Name:myc-gold-api 
 * File Name:GoldEarningsDTO.java 
 * Package Name:com.gomemyc.gold.dto 
 * Date:2017年3月6日下午4:39:59 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldEarningsDTO <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月6日 下午4:39:59 <br/> 
 * @author  zhuyunpeng
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description 
 */
public class GoldEarningsDTO  implements Serializable{
	

	
	
    /** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = -5622261421471813985L;
	
	

	//主键
	private String id; 
	
	//用户ID
	private String userId;
	
	//单价(金钱包在（00：20：00）)
	private BigDecimal unitPrice;
	
	//项目利率
	private Integer projectRate;
	
	//黄金克重
	private BigDecimal goldWeight;
	
	//收益日期
	private String earningsDate;
	
	//创建时间
	private Date ceateTime;
	
	//收益金额
	private BigDecimal earningsAmount;

    //订单id
	private String investOrderId;

    public GoldEarningsDTO() {
 		super();
 	}
    
    
    public GoldEarningsDTO(String id, String userId, BigDecimal unitPrice, Integer projectRate, BigDecimal goldWeight, String earningsDate, Date ceateTime, BigDecimal earningsAmount, String investOrderId) {
        this.id = id;
        this.userId = userId;
        this.unitPrice = unitPrice;
        this.projectRate = projectRate;
        this.goldWeight = goldWeight;
        this.earningsDate = earningsDate;
        this.ceateTime = ceateTime;
        this.earningsAmount = earningsAmount;
        this.investOrderId = investOrderId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getProjectRate() {
        return projectRate;
    }

    public void setProjectRate(Integer projectRate) {
        this.projectRate = projectRate;
    }

    public BigDecimal getGoldWeight() {
        return goldWeight;
    }

    public void setGoldWeight(BigDecimal goldWeight) {
        this.goldWeight = goldWeight;
    }

    public String getEarningsDate() {
        return earningsDate;
    }

    public void setEarningsDate(String earningsDate) {
        this.earningsDate = earningsDate == null ? null : earningsDate.trim();
    }

    public Date getCeateTime() {
        return ceateTime;
    }

    public void setCeateTime(Date ceateTime) {
        this.ceateTime = ceateTime;
    }

    public BigDecimal getEarningsAmount() {
        return earningsAmount;
    }

    public void setEarningsAmount(BigDecimal earningsAmount) {
        this.earningsAmount = earningsAmount;
    }
    
	public String getInvestOrderId() {
		return investOrderId;
	}

	public void setInvestOrderId(String investOrderId) {
		this.investOrderId = investOrderId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
  
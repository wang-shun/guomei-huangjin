package com.gomemyc.gold.dto;
/** 
 * ClassName:GoldProductDetailsDTO <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 上午11:16:43 <br/>
 * @author   liujunhan 
=======
/** 
 * Project Name:myc-gold-api 
 * File Name:GoldProductDTO.java 
 * Package Name:com.gomemyc.gold.dto 
 * Date:2017年3月6日下午4:13:50  GoldProductDTO
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldProductDTO <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月6日 下午4:13:50 <br/>
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
 
public class GoldProductDTO  implements Serializable{
	

	/** 
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 * @since JDK 1.8 
	 */  
	private static final long serialVersionUID = 4168081122257893081L;

	private String id;
	
	//产品状态
	private String status;
	
	//投资比例
	private Integer rate;
	
	//加息利率
	private Integer raiseRate;
	
	//投资总天数
	private Integer totalDays;
	
	//还款方式(1;到期一次性支付)
	private String method;
	
	//还款方式(1;到期一次性支付)
	private String methodName;
	
	//募集金额
	private BigDecimal amount;
	
	//最小投资金额
	private BigDecimal minAmount;
	
	//产品名称   有歧义
	private String loanTitle;
	
	//产品开始时间
	private Date timeOpen;
	
	//服务器时间
	private Date time;
	
	//该产品是否能用红包（0 不可用 1 可用）
	private Boolean useCoupon;
	
	//产品类型键值
	private String productKey;
	
	//投资进度
	private Double investPercent;
	 
	public GoldProductDTO(String id, String status, Integer rate, Integer ratePlus, Integer totolDays, String method,
		 BigDecimal amount, BigDecimal minAmount, String title, Date openTime,
			Date serverTime, Boolean useCoupon, String productKey, Double investPercent) {
		super();
		this.id = id;
		this.status = status;
		this.rate = rate;
		this.raiseRate = ratePlus;
		this.totalDays = totolDays;
		this.method = method;

		this.amount = amount;
		this.minAmount = minAmount;
		this.loanTitle = title;
		this.timeOpen = openTime;
		this.time = serverTime;
		this.useCoupon = useCoupon;
		this.productKey = productKey;
		this.investPercent = investPercent;
	}

	public GoldProductDTO() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getminAmount() {
		return minAmount;
	}

	public void setminAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public Integer getRaiseRate() {
		return raiseRate;
	}

	public void setRaiseRate(Integer raiseRate) {
		this.raiseRate = raiseRate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public String getLoanTitle() {
		return loanTitle;
	}

	public void setLoanTitle(String loanTitle) {
		this.loanTitle = loanTitle;
	}

	public Date getTimeOpen() {
		return timeOpen;
	}

	public void setTimeOpen(Date timeOpen) {
		this.timeOpen = timeOpen;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Boolean getUseCoupon() {
		return useCoupon;
	}

	public void setUseCoupon(Boolean useCoupon) {
		this.useCoupon = useCoupon;
	}

	public String getproductKey() {
		return productKey;
	}

	public void setproductKey(String productKey) {
		this.productKey = productKey;
	}

	public Double getInvestPercent() {
		return investPercent;
	}

	public void setInvestPercent(Double investPercent) {
		this.investPercent = investPercent;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
  

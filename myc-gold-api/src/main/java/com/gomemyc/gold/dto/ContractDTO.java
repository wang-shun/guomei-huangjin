package com.gomemyc.gold.dto;

import java.io.Serializable;
/**
 * 
 *@ClassName:ContractDTO.java 
 *@Description:国美黄金消费综合服务协议实体
 *@author zhuyunpeng
 *@date 2017年4月10日
 */
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
public class ContractDTO implements Serializable{

	
	private static final long serialVersionUID = 8084522887691161193L;

	//甲方
	private String partiesA;
	
	//乙方
	private String partiesB;
	
	//甲方身份证号
	private String idCardNo;
	
	//乙方住所
	private String currentAdress;
	
	//黄金品种
	private String goldName;
	
	//购买重量（单位：克）
	private BigDecimal goldWeight;
	
	//年化存管收益率
	private BigDecimal rate;
	
	//存管期间起始年
	private String startYear;
	
	//存管期间起始月
	private String startMonth;
	
	//存管期间起始日
	private String startDay;
	
	//存管期间终止年
	private String endYear;
	
	//存管期间终止月
	private String endMonth;
	
	//存管期间终止日
	private String endDay;
	
	//标的Id
	private String loanId;
	
	//产品Id
	private String productId;
	
	public ContractDTO() {
		super();
	}

	public ContractDTO(String partiesA, String partiesB, String idCardNo, String currentAdress, String goldName,
			BigDecimal goldWeight, BigDecimal rate, String startYear, String startMonth, String startDay,
			String endYear, String endMonth, String endDay, String loanId,String productId) {
		super();
		this.partiesA = partiesA;
		this.partiesB = partiesB;
		this.idCardNo = idCardNo;
		this.currentAdress = currentAdress;
		this.goldName = goldName;
		this.goldWeight = goldWeight;
		this.rate = rate;
		this.startYear = startYear;
		this.startMonth = startMonth;
		this.startDay = startDay;
		this.endYear = endYear;
		this.endMonth = endMonth;
		this.endDay = endDay;
		this.loanId = loanId;
		this.productId = productId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPartiesA() {
		return partiesA;
	}

	public void setPartiesA(String partiesA) {
		this.partiesA = partiesA;
	}

	public String getPartiesB() {
		return partiesB;
	}

	public void setPartiesB(String partiesB) {
		this.partiesB = partiesB;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getCurrentAdress() {
		return currentAdress;
	}

	public void setCurrentAdress(String currentAdress) {
		this.currentAdress = currentAdress;
	}

	public String getGoldName() {
		return goldName;
	}

	public void setGoldName(String goldName) {
		this.goldName = goldName;
	}

	public BigDecimal getGoldWeight() {
		return goldWeight;
	}

	public void setGoldWeight(BigDecimal goldWeight) {
		this.goldWeight = goldWeight;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartDay() {
		return startDay;
	}

	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndDay() {
		return endDay;
	}

	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}
	
	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}

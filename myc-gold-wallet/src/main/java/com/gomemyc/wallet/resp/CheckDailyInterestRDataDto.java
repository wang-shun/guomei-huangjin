package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName CheckDailyInterestResultDto
 * @author zhuyunpeng
 * @description: 2.7.3每天利息对账响应参数Data实体
 * @date 2017年3月9日
 */
public class CheckDailyInterestRDataDto implements Serializable {
	
	
	private static final long serialVersionUID = -2075995960786192935L;

	//购买定期请求号
    private String requestNo; 
    
    //黄金钱包订单号
    private String orderNo; 
    
    //收益日期[格式yyyy-MM-dd]
    private String interestDate;
    
    //清算克重
    private BigDecimal goldWeight;
    
    //清算金价
    private BigDecimal goldPrice;
    
    //清算利率
    private BigDecimal clearRate;
    
    //清算利息
    private BigDecimal dayInterestMoney;
    
    //购买的产品代码
    private String productCode;
    
    //购买的产品名称
    private String productName;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getGoldWeight() {
		return goldWeight;
	}

	public void setGoldWeight(BigDecimal goldWeight) {
		this.goldWeight = goldWeight;
	}

	public BigDecimal getGoldPrice() {
		return goldPrice;
	}

	public void setGoldPrice(BigDecimal goldPrice) {
		this.goldPrice = goldPrice;
	}

	public BigDecimal getClearRate() {
		return clearRate;
	}

	public void setClearRate(BigDecimal clearRate) {
		this.clearRate = clearRate;
	}

	public String getInterestDate() {
        return interestDate;
    }

    public void setInterestDate(String interestDate) {
        this.interestDate = interestDate;
    }


    public BigDecimal getDayInterestMoney() {
		return dayInterestMoney;
	}

	public void setDayInterestMoney(BigDecimal dayInterestMoney) {
		this.dayInterestMoney = dayInterestMoney;
	}

	public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
}

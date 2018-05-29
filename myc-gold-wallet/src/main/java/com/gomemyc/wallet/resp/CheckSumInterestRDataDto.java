package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName CheckSumInterestRDataDto
 * @author zhuyunpeng
 * @description: 2.7.4 定期到期利息汇总对账响应参数Data实体
 * @date 2017年3月9日
 */
public class CheckSumInterestRDataDto implements Serializable{
	
	
	private static final long serialVersionUID = -3077001112676876584L;

	//购买定期请求号
    private String requestNo; 
    
    //黄金钱包订单号
    private String orderNo;
    
    //清算克重
    private BigDecimal goldWeight;
    
    //清算利率
    private BigDecimal clearRate;
    
    //清算利息
    private BigDecimal interestAmount; 
    
    //购买的产品代码
    private String productCode; 
    
    //购买的产品名称
    private String productName;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public CheckSumInterestRDataDto() {
		super();
		// TODO Auto-generated constructor stub
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

	public BigDecimal getClearRate() {
		return clearRate;
	}

	public void setClearRate(BigDecimal clearRate) {
		this.clearRate = clearRate;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
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

package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName CheckTimeOrderResultDto
 * @author zhuyunpeng
 * @description: 2.7.1买定期金对账响应参数实体
 * @date 2017年3月9日
 */
public class CheckTimeOrderRDataDto implements Serializable {
	
	
	private static final long serialVersionUID = 2021258722142741031L;

	//订单请求号
    private String reqNo;
    
    //黄金钱包订单号
    private String orderNo; 
    
    //下单时间[格式yyyy-MM-dd HH:mm:ss ]
    private String orderTime;
    
    //完成时间[格式yyyy-MM-dd HH:mm:ss ]
    private String finishTime;
    
    //订单克重
    private BigDecimal goldWeight;
    
    //订单金价
    private BigDecimal realPrice; 
    
    //订单金额
    private BigDecimal orderAmount; 
    
    //购买的产品代码
    private String productCode; 
    
    //购买的产品名称
    private String productName;
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    public CheckTimeOrderRDataDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public BigDecimal getGoldWeight() {
		return goldWeight;
	}

	public void setGoldWeight(BigDecimal goldWeight) {
		this.goldWeight = goldWeight;
	}

	public BigDecimal getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(BigDecimal realPrice) {
		this.realPrice = realPrice;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
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

package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldDueSumAccountCheckDTO
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 
 * @author  LiuQiangBin
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description 
 */
public class GoldDueSumAccountCheckDTO implements Serializable{
	
	private static final long serialVersionUID = 1556308556880782409L;
	
	//定期到期利息汇总对账文件id
    private String id;

    //原购买定期请求号
    private String reqNo;

    //黄金钱包订单号
    private String orderNo;

    //清算克重
    private BigDecimal goldWeight;

    //清算利率
    private Integer clearRate;

    //清算利息
    private BigDecimal dayInterestMoney;

    //购买的产品代码
    private String productCode;

    //购买的产品名称
    private String productName;

    //对账数据类型
    private Integer dataCheckType;

    //对账结果
    private Integer comparingStatus;

    public GoldDueSumAccountCheckDTO() {
    }

    public GoldDueSumAccountCheckDTO(String id, String reqNo, String orderNo, BigDecimal goldWeight,
                                     Integer clearRate, BigDecimal dayInterestMoney, String productCode, String productName) {
		super();
		this.id = id;
		this.reqNo = reqNo;
		this.orderNo = orderNo;
		this.goldWeight = goldWeight;
		this.clearRate = clearRate;
		this.dayInterestMoney = dayInterestMoney;
		this.productCode = productCode;
		this.productName = productName;
	}
	
	
	
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo == null ? null : reqNo.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public BigDecimal getGoldWeight() {
        return goldWeight;
    }

    public void setGoldWeight(BigDecimal goldWeight) {
        this.goldWeight = goldWeight;
    }

    public Integer getClearRate() {
        return clearRate;
    }

    public void setClearRate(Integer clearRate) {
        this.clearRate = clearRate;
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
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Integer getDataCheckType() {
        return dataCheckType;
    }

    public void setDataCheckType(Integer dataCheckType) {
        this.dataCheckType = dataCheckType;
    }

    public Integer getComparingStatus() {
        return comparingStatus;
    }

    public void setComparingStatus(Integer comparingStatus) {
        this.comparingStatus = comparingStatus;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


    

}
package com.gomemyc.gold.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * ClassName:GoldInvestAccountCheckDTO 
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 
 * @author  LiuQiangBin
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description 
 */
public class GoldInvestAccountCheckDTO implements Serializable{
	
	private static final long serialVersionUID = 2559465466171810372L;

	//买定期金对账文件id
    private String id;

    //订单请求号
    private String reqNo;

    //黄金钱包订单号
    private String orderNo;

    //下单时间
    private Date goldOrderTime;

    //完成时间
    private Date goldFinishTime;

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

    //对账数据类型
    private Integer dataCheckType;

    //对账结果
    private Integer comparingStatus;


    public GoldInvestAccountCheckDTO() {
    }
    
    
	public GoldInvestAccountCheckDTO(String id, String reqNo, String orderNo, Date goldOrderTime, Date goldFinishTime,
			BigDecimal goldWeight, BigDecimal realPrice, BigDecimal orderAmount, String productCode,
			String productName) {
		super();
		this.id = id;
		this.reqNo = reqNo;
		this.orderNo = orderNo;
		this.goldOrderTime = goldOrderTime;
		this.goldFinishTime = goldFinishTime;
		this.goldWeight = goldWeight;
		this.realPrice = realPrice;
		this.orderAmount = orderAmount;
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

    public Date getGoldOrderTime() {
        return goldOrderTime;
    }

    public void setGoldOrderTime(Date goldOrderTime) {
        this.goldOrderTime = goldOrderTime;
    }

    public Date getGoldFinishTime() {
        return goldFinishTime;
    }

    public void setGoldFinishTime(Date goldFinishTime) {
        this.goldFinishTime = goldFinishTime;
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
  
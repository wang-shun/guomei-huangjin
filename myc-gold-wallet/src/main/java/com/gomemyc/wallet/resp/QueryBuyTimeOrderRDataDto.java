package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName BuyTimeGoldRDataDto
 * @author zhuyunpeng
 * @description 购买定期金响应data参数实体
 * @date 2017年3月8日
 */
public class QueryBuyTimeOrderRDataDto implements Serializable{

	
	private static final long serialVersionUID = -4233943869236091528L;

	//成交金价(单位：分/克)
	private BigDecimal realPrice;

	//成交金重(单位：毫克)
	private BigDecimal realWeight;

	//成交金额(单位： 分)
	private BigDecimal realAmount;

	//产品编码
	private String productCode;

	//产品名称
	private String productName;

	//成交完成时间(yyyy-MM-dd HH:mm:ss)
	private String finishTime;

	//订单号(商户请求订单号)
	private String reqNo;

	//黄金钱包订单号
	private String orderNo;

	//订单状态
	private String status;

	//错误码
	private String errCode;

	//错误原因
	private String errMsg;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryBuyTimeOrderRDataDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(BigDecimal realPrice) {
		this.realPrice = realPrice;
	}

	public BigDecimal getRealWeight() {
		return realWeight;
	}

	public void setRealWeight(BigDecimal realWeight) {
		this.realWeight = realWeight;
	}

	public BigDecimal getRealAmount() {
		return realAmount;
	}

	public void setRealAmount(BigDecimal realAmount) {
		this.realAmount = realAmount;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
}

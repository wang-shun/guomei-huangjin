package com.gomemyc.trade.entity;

import com.gomemyc.common.StringIdEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约申请单
 * 
 * @author 何健
 *
 */
public class ReserveRequest extends StringIdEntity{

	/**
	 * 投资人ID
	 */
	private String userId;
	/**
	 * 预约投资额
	 */
	private BigDecimal reserveAmount;
	/**
	 * 累计撮合成交金额
	 */
	private BigDecimal investedAmount;
	/**
	 * 剩余预约投资额
	 */
	private BigDecimal balanceAmount;
	/**
	 * 预约状态
	 */
	private Integer status;
	/**
	 * 是否可以取消预约
	 */
	private Integer  enableCancelled;
	/**
	 * 预约申请提交时间
	 */
	private Date submitTime;
	/**
	 * 预约等待期限
	 */
	private Integer waitPeriod;
	/**
	 * 预约到期时间
	 */
	private Date expireTime;
	/**
	 * 预约产品类型名称
	 */
	private String productNames;
	/**
	 * 利率区间
	 */
	private String ratePeriods;
	/**
	 * 投资期限区间
	 */
	private String investPeriods;
	
	/**
	 * 取消类型
	 */
	private Integer reserveCancelType;
	/**
	 * 取消时间
	 */
	private Date cancelTime;
	/**
	 * 预约红包(使用的是预约红包的id)
	 */
	private String  reserveCoupon;
	/**
	 * 本地资金账号流水号
	 */
	private String orderId;

	/** 账户系统冻结返回code */
	private String frozenCode;

	/** 只用于取消时账户系统解冻返回code */
	private String unFrozenCode;

	public String getFrozenCode() {
		return frozenCode;
	}

	public void setFrozenCode(String frozenCode) {
		this.frozenCode = frozenCode;
	}

	public String getUnFrozenCode() {
		return unFrozenCode;
	}

	public void setUnFrozenCode(String unFrozenCode) {
		this.unFrozenCode = unFrozenCode;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public BigDecimal getReserveAmount() {
		return reserveAmount;
	}
	public void setReserveAmount(BigDecimal reserveAmount) {
		this.reserveAmount = reserveAmount;
	}
	public BigDecimal getInvestedAmount() {
		return investedAmount;
	}
	public void setInvestedAmount(BigDecimal investedAmount) {
		this.investedAmount = investedAmount;
	}
	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public Integer getWaitPeriod() {
		return waitPeriod;
	}
	public void setWaitPeriod(Integer waitPeriod) {
		this.waitPeriod = waitPeriod;
	}
	public Date getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
	public String getRatePeriods() {
		return ratePeriods;
	}
	public void setRatePeriods(String ratePeriods) {
		this.ratePeriods = ratePeriods;
	}
	public String getInvestPeriods() {
		return investPeriods;
	}
	public void setInvestPeriods(String investPeriods) {
		this.investPeriods = investPeriods;
	}
	public Date getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}
	public String getReserveCoupon() {
		return reserveCoupon;
	}
	public void setReserveCoupon(String reserveCoupon) {
		this.reserveCoupon = reserveCoupon;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getEnableCancelled() {
		return enableCancelled;
	}
	public void setEnableCancelled(Integer enableCancelled) {
		this.enableCancelled = enableCancelled;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public String getProductNames() {
		return productNames;
	}

	public void setProductNames(String productNames) {
		this.productNames = productNames;
	}

	public Integer getReserveCancelType() {
		return reserveCancelType;
	}

	public void setReserveCancelType(Integer reserveCancelType) {
		this.reserveCancelType = reserveCancelType;
	}
}

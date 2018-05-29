package com.gomemyc.invest.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.DebtPlanOneOrmany;
/**
 * 债转方案
 */
public class DebtPlan extends StringIdEntity{
	/**
	 * 方案名称
	 */
	private String planName;

	/**
	 * 使用红包标识 0：不使用 1：使用
	 */
	private Boolean applyRedPacket;

	/**
	 * 人工审核标识 0：自动审核 1：人工审核
	 */
	private Boolean manualAuditFlag;

	/**
	 * 转让优惠债权标识
	 */
	private Boolean preferentialDebt;

	/**
	 * 转让募集期限 上限
	 */
	private int maxCollectDeadline;

	/**
	 * 转让募集期 下限
	 */
	private int minCollectTimeLimit;

	/**
	 *  取消转让次数上限
	 */
	private int maxCancelCount;

	/**
	 *  一手债权持有最低期限
	 */
	private int firstDebtDate;

	/**
	 *  二手债权持有最低期限
	 */
	private int secondDebtDate;

	/**
	 *  最少剩余期限
	 */
	private int overplusDeadline;

	/**
	 *  转让手续费率
	 */
	private int transferRate;

	/**
	 *  调价系数上限
	 */
	private int maxPriceRate;

	/**
	 *  调价系数下限
	 */
	private int minPriceRate;

	/**
	 *  受让方人数要求
	 */
	private DebtPlanOneOrmany peopleCount;

	/**
	 * 新增时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 *  操作人员
	 */
	private String oprId;

	/**
	 * 转让次数  0:无限制
	 */
	private int debtCount;

	
	/**
	 * 预期年化下限
	 */
	private int minExpectedRate;
	/**
	 * 预期年化上限
	 */
	private int maxExpectedRate;
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public Boolean getApplyRedPacket() {
		return applyRedPacket;
	}
	public void setApplyRedPacket(Boolean applyRedPacket) {
		this.applyRedPacket = applyRedPacket;
	}
	public Boolean getManualAuditFlag() {
		return manualAuditFlag;
	}
	public void setManualAuditFlag(Boolean manualAuditFlag) {
		this.manualAuditFlag = manualAuditFlag;
	}
	public Boolean getPreferentialDebt() {
		return preferentialDebt;
	}
	public void setPreferentialDebt(Boolean preferentialDebt) {
		this.preferentialDebt = preferentialDebt;
	}
	public int getMaxCollectDeadline() {
		return maxCollectDeadline;
	}
	public void setMaxCollectDeadline(int maxCollectDeadline) {
		this.maxCollectDeadline = maxCollectDeadline;
	}
	public int getMinCollectTimeLimit() {
		return minCollectTimeLimit;
	}
	public void setMinCollectTimeLimit(int minCollectTimeLimit) {
		this.minCollectTimeLimit = minCollectTimeLimit;
	}
	public int getMaxCancelCount() {
		return maxCancelCount;
	}
	public void setMaxCancelCount(int maxCancelCount) {
		this.maxCancelCount = maxCancelCount;
	}
	public int getFirstDebtDate() {
		return firstDebtDate;
	}
	public void setFirstDebtDate(int firstDebtDate) {
		this.firstDebtDate = firstDebtDate;
	}
	public int getSecondDebtDate() {
		return secondDebtDate;
	}
	public void setSecondDebtDate(int secondDebtDate) {
		this.secondDebtDate = secondDebtDate;
	}
	public int getOverplusDeadline() {
		return overplusDeadline;
	}
	public void setOverplusDeadline(int overplusDeadline) {
		this.overplusDeadline = overplusDeadline;
	}
	public int getTransferRate() {
		return transferRate;
	}
	public void setTransferRate(int transferRate) {
		this.transferRate = transferRate;
	}
	public int getMaxPriceRate() {
		return maxPriceRate;
	}
	public void setMaxPriceRate(int maxPriceRate) {
		this.maxPriceRate = maxPriceRate;
	}
	public int getMinPriceRate() {
		return minPriceRate;
	}
	public void setMinPriceRate(int minPriceRate) {
		this.minPriceRate = minPriceRate;
	}
	public DebtPlanOneOrmany getPeopleCount() {
		return peopleCount;
	}
	public void setPeopleCount(DebtPlanOneOrmany peopleCount) {
		this.peopleCount = peopleCount;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getOprId() {
		return oprId;
	}
	public void setOprId(String oprId) {
		this.oprId = oprId;
	}
	public int getDebtCount() {
		return debtCount;
	}
	public void setDebtCount(int debtCount) {
		this.debtCount = debtCount;
	}
	public int getMinExpectedRate() {
		return minExpectedRate;
	}
	public void setMinExpectedRate(int minExpectedRate) {
		this.minExpectedRate = minExpectedRate;
	}
	public int getMaxExpectedRate() {
		return maxExpectedRate;
	}
	public void setMaxExpectedRate(int maxExpectedRate) {
		this.maxExpectedRate = maxExpectedRate;
	}

}

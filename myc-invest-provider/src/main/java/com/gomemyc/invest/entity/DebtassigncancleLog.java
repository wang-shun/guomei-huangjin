package com.gomemyc.invest.entity;
import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.AssignLoanCancelStatus;
import com.gomemyc.invest.enums.DebtAssignCancelType;

/**
 * 债权撤销操作记录
 * 
 */
public class DebtassigncancleLog  extends StringIdEntity{
	/**
	 * 产品id
	 */
	private String productId;

	/**
	 * 标的id
	 */
	private String loanId;
	/**
	 * 债转ID
	 */
	private String debtassignId;
	/**
	 * 债转产品ID	
	 */
	private String debtassignProductId;

	/**
	 * 投资记录ID（债转原标的投资记录ID）
	 */
	private String investId;

	/**
	 * 债转申请人ID
	 */
	private String assignApplyUserId;

	/**
	 * 债转申请人手机号码
	 */
	private String assignApplyUserMobile;

	/**
	 * 预期退回金额
	 */
	private BigDecimal expactRollBackMoney;

	/**
	 * 操作类型
	 */
	private DebtAssignCancelType assignLoanCancelType;

	/**
	 * 撤销时间
	 */
	private Date operateTime;

	/**
	 * 操作人ID
	 */
	private String operatorId;

	/**
	 * 操作人名称
	 */
	private String operatorName;

	/**
	 * 撤销操作结果
	 */
	private AssignLoanCancelStatus result;

	/**
	 * 撤销原因
	 */
	private String cancleReason;

	/**
	 * 描述
	 */
	private String description;


	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDebtassignId() {
		return debtassignId;
	}

	public void setDebtassignId(String debtassignId) {
		this.debtassignId = debtassignId;
	}

	public String getDebtassignProductId() {
		return debtassignProductId;
	}

	public void setDebtassignProductId(String debtassignProductId) {
		this.debtassignProductId = debtassignProductId;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getInvestId() {
		return investId;
	}

	public void setInvestId(String investId) {
		this.investId = investId;
	}

	public String getAssignApplyUserId() {
		return assignApplyUserId;
	}

	public void setAssignApplyUserId(String assignApplyUserId) {
		this.assignApplyUserId = assignApplyUserId;
	}

	public String getAssignApplyUserMobile() {
		return assignApplyUserMobile;
	}

	public void setAssignApplyUserMobile(String assignApplyUserMobile) {
		this.assignApplyUserMobile = assignApplyUserMobile;
	}

	public BigDecimal getExpactRollBackMoney() {
		return expactRollBackMoney;
	}

	public void setExpactRollBackMoney(BigDecimal expactRollBackMoney) {
		this.expactRollBackMoney = expactRollBackMoney;
	}

	public DebtAssignCancelType getAssignLoanCancelType() {
		return assignLoanCancelType;
	}

	public void setAssignLoanCancelType(DebtAssignCancelType assignLoanCancelType) {
		this.assignLoanCancelType = assignLoanCancelType;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public AssignLoanCancelStatus getResult() {
		return result;
	}

	public void setResult(AssignLoanCancelStatus result) {
		this.result = result;
	}

	public String getCancleReason() {
		return cancleReason;
	}

	public void setCancleReason(String cancleReason) {
		this.cancleReason = cancleReason;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
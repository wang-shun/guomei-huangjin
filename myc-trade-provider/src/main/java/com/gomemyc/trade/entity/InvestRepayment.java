package com.gomemyc.trade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.trade.enums.RepaymentStatus;


public class InvestRepayment {
    private String id;

    private String loanId;

    private String investId;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    private BigDecimal interestPlusAmount;

    private BigDecimal interestCouponAmount;

    private BigDecimal outstandingAmount;

    private Date dueDate;

    private RepaymentStatus status;

    private BigDecimal repayAmount;

    private Date repayDate;
    private BigDecimal surplusAmount; 
    
    private String userId;
    
    private Integer years;
    
    private Integer months;
    
    private Integer days;
    
    private Date valueTime;

    public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

	public Integer getMonths() {
		return months;
	}

	public void setMonths(Integer months) {
		this.months = months;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Date getValueTime() {
		return valueTime;
	}

	public void setValueTime(Date valueTime) {
		this.valueTime = valueTime;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId == null ? null : loanId.trim();
    }

    public String getInvestId() {
        return investId;
    }

    public void setInvestId(String investId) {
        this.investId = investId == null ? null : investId.trim();
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getInterestPlusAmount() {
        return interestPlusAmount;
    }

    public void setInterestPlusAmount(BigDecimal interestPlusAmount) {
        this.interestPlusAmount = interestPlusAmount;
    }

    public BigDecimal getInterestCouponAmount() {
        return interestCouponAmount;
    }

    public void setInterestCouponAmount(BigDecimal interestCouponAmount) {
        this.interestCouponAmount = interestCouponAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

  

    public RepaymentStatus getStatus() {
		return status;
	}

	public void setStatus(RepaymentStatus status) {
		this.status = status;
	}

	public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Date getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(Date repayDate) {
        this.repayDate = repayDate;
    }

	public BigDecimal getSurplusAmount() {
		return surplusAmount;
	}

	public void setSurplusAmount(BigDecimal surplusAmount) {
		this.surplusAmount = surplusAmount;
	}

	
	    /**
	    * @return userId
	    */
	    
	public String getUserId() {
		return userId;
	}

	
	    /**
	     * @param userId the userId to set
	     */
	    
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
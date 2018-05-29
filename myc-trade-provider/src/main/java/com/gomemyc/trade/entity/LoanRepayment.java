package com.gomemyc.trade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.gomemyc.trade.enums.RepaymentStatus;

public class LoanRepayment {
    private String id;

    private String loanId;

    private Integer currentPeriod;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;
    
    private BigDecimal interestPlusAmount;

    private BigDecimal outstandingAmount;
    
    private BigDecimal interestCouponAmount;

    private Date dueDate;

    private RepaymentStatus status;

    private BigDecimal repayAmount;

    private Date repayDate;

    private Date timeCreatime;
    
    private String accountSrl;
    
    private String depositSrl;
    
    private String userId;
    /**
     * 北京还款流水号
     */
    private String bjOrderNo;
    /**
     * 本地还款流水号
     */
    private String localOrderNo;
    
    public String getAccountSrl() {
		return accountSrl;
	}

	public void setAccountSrl(String accountSrl) {
		this.accountSrl = accountSrl;
	}

	public String getDepositSrl() {
		return depositSrl;
	}

	public void setDepositSrl(String depositSrl) {
		this.depositSrl = depositSrl;
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

    public Integer getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(Integer currentPeriod) {
        this.currentPeriod = currentPeriod;
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

    public Date getTimeCreatime() {
        return timeCreatime;
    }

    public void setTimeCreatime(Date timeCreatime) {
        this.timeCreatime = timeCreatime;
    }

	public BigDecimal getInterestPlusAmount() {
		return interestPlusAmount;
	}

	public void setInterestPlusAmount(BigDecimal interestPlusAmount) {
		this.interestPlusAmount = interestPlusAmount;
	}

	
	    /**
	    * @return interestCouponAmount
	    */
	    
	public BigDecimal getInterestCouponAmount() {
		return interestCouponAmount;
	}

	
	    /**
	     * @param interestCouponAmount the interestCouponAmount to set
	     */
	    
	public void setInterestCouponAmount(BigDecimal interestCouponAmount) {
		this.interestCouponAmount = interestCouponAmount;
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

			public String getBjOrderNo() {
				return bjOrderNo;
			}

			public void setBjOrderNo(String bjOrderNo) {
				this.bjOrderNo = bjOrderNo;
			}

			public String getLocalOrderNo() {
				return localOrderNo;
			}

			public void setLocalOrderNo(String localOrderNo) {
				this.localOrderNo = localOrderNo;
			}
}
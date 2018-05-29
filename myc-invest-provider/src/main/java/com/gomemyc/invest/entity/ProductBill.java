package com.gomemyc.invest.entity;

import java.util.Date;

/**
 * 票据产品
 * @author lujixiang
 * @creaTime 2017年3月7日
 */
public class ProductBill extends Product{

    
    private static final long serialVersionUID = 1L;
    
    // 票据产品类型
    private String billType;
    
    // 承兑银行
    private String acceptanceBank;
    
    // 宽限期到期日
    private Date extendExpireDate;
    
    // 宽限期(单位：天数)
    private int extendDeadline;
    
    
    
    public ProductBill() {
    }

    public String getBillType() {
        return billType;
    }

    public int getExtendDeadline() {
		return extendDeadline;
	}

	public void setExtendDeadline(int extendDeadline) {
		this.extendDeadline = extendDeadline;
	}

	public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getAcceptanceBank() {
        return acceptanceBank;
    }

    public void setAcceptanceBank(String acceptanceBank) {
        this.acceptanceBank = acceptanceBank;
    }

    public Date getExtendExpireDate() {
        return extendExpireDate;
    }

    public void setExtendExpireDate(Date extendExpireDate) {
        this.extendExpireDate = extendExpireDate;
    }
    
}

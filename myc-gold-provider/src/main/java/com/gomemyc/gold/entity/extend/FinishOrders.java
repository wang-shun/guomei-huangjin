package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 *@ClassName:FinishOrdersDTO
 *@Description:完成订单参数实体
 *@author liujunhan
 *@date 2017年3月27日
 */
public class FinishOrders implements Serializable{

    private static final long serialVersionUID = 1169790506091561186L;

    private String id;

    //标的
    private String loanId;

    //标的标题
    private String loanTitle;

    //还款方式(1:一次性还本付息)
    private Integer method;

    //还款名称
    private String methodName;

    //基础年化率
    private Integer rate;

    //贴息利率
    private Integer raisePlus;

    //当前收益额
    private BigDecimal accruedIncomeAmount;

    //成交金重(单位:毫克)
    private BigDecimal realWeight;

    //成交金价
    private BigDecimal realPrice;

    //成交金额
    private BigDecimal realAmount;

    //起息日期
    private Date valueDate;

    //到期日期
    private Date dueDate;

    //是否可用红包(0:不可用 1:可用)
    private Integer useCoupon;

    //合同链接
    private String contractUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getLoanTitle() {
        return loanTitle;
    }

    public void setLoanTitle(String loanTitle) {
        this.loanTitle = loanTitle;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRaisePlus() {
        return raisePlus;
    }

    public void setRaisePlus(Integer raisePlus) {
        this.raisePlus = raisePlus;
    }

    public BigDecimal getAccruedIncomeAmount() {
        return accruedIncomeAmount;
    }

    public void setAccruedIncomeAmount(BigDecimal accruedIncomeAmount) {
        this.accruedIncomeAmount = accruedIncomeAmount;
    }

    public BigDecimal getRealWeight() {
        return realWeight;
    }

    public void setRealWeight(BigDecimal realWeight) {
        this.realWeight = realWeight;
    }

    public BigDecimal getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(BigDecimal realPrice) {
        this.realPrice = realPrice;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(Integer useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }
}

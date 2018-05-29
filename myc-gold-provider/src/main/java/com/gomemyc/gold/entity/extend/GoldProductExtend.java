package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class GoldProductExtend implements Serializable{

    private static final long serialVersionUID = -6291832912568516337L;

    private String id;

    //产品状态
    private Integer status;

    //投资比例
    private Integer rate;

    //加息利率
    private Integer ratePlus;

    //投资期限年
    private Integer years;

    //投资期限月
    private Integer months;

    //投资期限日
    private Integer days;

    //还款方式(1;到期一次性支付)
    private Integer method;

    //还款方式(1;到期一次性支付)
    private String methodName;

    //募集金额
    private BigDecimal amount;

    //最小投资金额
    private BigDecimal minInvestAmount;

    private BigDecimal balance;

    //产品名称
    private String title;

    //产品开始时间
    private Date openTime;

    //服务器时间
    private Date serverTime= new Date();

    //该产品是否能用红包（0 不可用 1 可用）
    private Integer useCoupon;

    //产品类型键值
    private String typeKey;

    //已投金额
    private BigDecimal investAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRatePlus() {
        return ratePlus;
    }

    public void setRatePlus(Integer ratePlus) {
        this.ratePlus = ratePlus;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMinInvestAmount() {
        return minInvestAmount;
    }

    public void setMinInvestAmount(BigDecimal minInvestAmount) {
        this.minInvestAmount = minInvestAmount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Integer getUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(Integer useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }
}

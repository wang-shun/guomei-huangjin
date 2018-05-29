package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2017-03-24.
 */
public class GoldProductDetailsExtend implements Serializable{

    private static final long serialVersionUID = -6929119034943689546L;
        //剩余可投金额
        private BigDecimal balance;

        //总天数
        private Integer totalDays;

        //投资进度
        private Double investPercent;

        //参考金价
        private BigDecimal goldPrice;

        //到期日期
        private Long dueDate;

        public GoldProductDetailsExtend() {
        }

        public GoldProductDetailsExtend(BigDecimal balance, Integer totalDays, Double investPercent, BigDecimal goldPrice, Long dueDate) {
            this.balance = balance;
            this.totalDays = totalDays;
            this.investPercent = investPercent;
            this.goldPrice = goldPrice;
            this.dueDate = dueDate;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public Integer getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(Integer totalDays) {
            this.totalDays = totalDays;
        }

        public Double getInvestPercent() {
            return investPercent;
        }

        public void setInvestPercent(Double investPercent) {
            this.investPercent = investPercent;
        }

        public BigDecimal getGoldPrice() {
            return goldPrice;
        }

        public void setGoldPrice(BigDecimal goldPrice) {
            this.goldPrice = goldPrice;
        }

        public Long getDueDate() {
            return dueDate;
        }

        public void setDueDate(Long dueDate) {
            this.dueDate = dueDate;
        }
    }



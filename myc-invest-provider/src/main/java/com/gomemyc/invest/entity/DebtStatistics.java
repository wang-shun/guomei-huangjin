package com.gomemyc.invest.entity;

import java.math.BigDecimal;
import java.util.Date;

public class DebtStatistics {
    private String id;

    private Date time;

    private BigDecimal requestSum;

    private Integer requestCount;

    private Integer requestUserCount;

    private BigDecimal successSum;

    private Integer successCount;

    private Integer successUserCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public BigDecimal getRequestSum() {
        return requestSum;
    }

    public void setRequestSum(BigDecimal requestSum) {
        this.requestSum = requestSum;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getRequestUserCount() {
        return requestUserCount;
    }

    public void setRequestUserCount(Integer requestUserCount) {
        this.requestUserCount = requestUserCount;
    }

    public BigDecimal getSuccessSum() {
        return successSum;
    }

    public void setSuccessSum(BigDecimal successSum) {
        this.successSum = successSum;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getSuccessUserCount() {
        return successUserCount;
    }

    public void setSuccessUserCount(Integer successUserCount) {
        this.successUserCount = successUserCount;
    }
}
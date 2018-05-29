package com.gomemyc.trade.entity;

import java.math.BigDecimal;
import java.util.Date;

public class OriginProduct {
    private String id;

    private String queueId;

    private String productName;

    private Boolean isNew;

    private String supplierId;

    private String assetsId;

    private String subid;

    private Integer assetrate;

    private BigDecimal invoiced;

    private Integer productTerm;

    private Date releTerm;

    private Date fullTerm;

    private Date valueDate;

    private Date assetsDate;

    private Date foundDate;

    private Boolean assetsState;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId == null ? null : queueId.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId == null ? null : supplierId.trim();
    }

    public String getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(String assetsId) {
        this.assetsId = assetsId == null ? null : assetsId.trim();
    }

    public String getSubid() {
        return subid;
    }

    public void setSubid(String subid) {
        this.subid = subid == null ? null : subid.trim();
    }

    public Integer getAssetrate() {
        return assetrate;
    }

    public void setAssetrate(Integer assetrate) {
        this.assetrate = assetrate;
    }

    public BigDecimal getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(BigDecimal invoiced) {
        this.invoiced = invoiced;
    }

    public Integer getProductTerm() {
        return productTerm;
    }

    public void setProductTerm(Integer productTerm) {
        this.productTerm = productTerm;
    }

    public Date getReleTerm() {
        return releTerm;
    }

    public void setReleTerm(Date releTerm) {
        this.releTerm = releTerm;
    }

    public Date getFullTerm() {
        return fullTerm;
    }

    public void setFullTerm(Date fullTerm) {
        this.fullTerm = fullTerm;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getAssetsDate() {
        return assetsDate;
    }

    public void setAssetsDate(Date assetsDate) {
        this.assetsDate = assetsDate;
    }

    public Date getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }

    public Boolean getAssetsState() {
        return assetsState;
    }

    public void setAssetsState(Boolean assetsState) {
        this.assetsState = assetsState;
    }
}
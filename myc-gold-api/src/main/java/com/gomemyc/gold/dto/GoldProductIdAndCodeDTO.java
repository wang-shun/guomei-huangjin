package com.gomemyc.gold.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-03-22.
 */
public class GoldProductIdAndCodeDTO implements Serializable{
    private static final long serialVersionUID = 5728005248966330650L;
   //主键
    String id;

    //黄金钱包产品编码
    private String goldProductCode;

    public GoldProductIdAndCodeDTO(String id, String goldProductCode) {
        this.id = id;
        this.goldProductCode = goldProductCode;
    }

    public GoldProductIdAndCodeDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoldProductCode() {
        return goldProductCode;
    }

    public void setGoldProductCode(String goldProductCode) {
        this.goldProductCode = goldProductCode;
    }
}

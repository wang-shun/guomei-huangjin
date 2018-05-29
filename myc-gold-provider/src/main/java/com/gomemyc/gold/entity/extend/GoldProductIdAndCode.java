package com.gomemyc.gold.entity.extend;

import java.io.Serializable;

/**
 * ClassName:GoldProductIdAndCode <br/>
 * Date:     2017年3月22日  <br/>
 * @author   liujunhan
 * @version
 * @since    JDK 1.8
 * @see
 * @description
 */
public class GoldProductIdAndCode implements Serializable{

    private static final long serialVersionUID = 601734522916923777L;
    //主键
    String id;

    //黄金钱包产品编码
    private String goldProductCode;

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

package com.gomemyc.invest.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 标的类型
 * @author lujixiang
 * @creaTime 2017年3月3日
 */
public class LoanType extends StringIdEntity{
    
    private static final long serialVersionUID = 201703051117L;

    // 类型键值
    private String key;
    
    // 类型名称
    private String name;
    
    // 业务总开关
    private int typeSwitch;
    
    // 类型描述
    private String description;
    
    public LoanType() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTypeSwitch() {
        return typeSwitch;
    }

    public void setTypeSwitch(int typeSwitch) {
        this.typeSwitch = typeSwitch;
    }
    

}

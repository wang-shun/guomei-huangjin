package com.gomemyc.invest.entity;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.RulesClazz;
import com.gomemyc.invest.enums.RulesType;

/**
 * 规则表(标的和标的类型的规则表)
 * @author lujixiang
 * @creaTime 2017年3月6日
 */
public class Rules extends StringIdEntity{
    
    private static final long serialVersionUID = 201703081L;

    // 规则名称
    private String name;
    
    // 规则键值
    private RulesType type;
    
    // 所属分类
    private RulesClazz clazz;
    
    // 过滤类
    private String filterClazz;
    
    public Rules() {
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RulesClazz getClazz() {
        return clazz;
    }

    public void setClazz(RulesClazz clazz) {
        this.clazz = clazz;
    }
    
    public String getFilterClazz() {
        return filterClazz;
    }

    public void setFilterClazz(String filterClazz) {
        this.filterClazz = filterClazz;
    }

    public RulesType getType() {
        return type;
    }

    public void setType(RulesType type) {
        this.type = type;
    }
    
}

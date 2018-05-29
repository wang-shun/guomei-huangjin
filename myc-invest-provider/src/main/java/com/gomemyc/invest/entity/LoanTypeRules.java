package com.gomemyc.invest.entity;

import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.RulesType;

/**
 * 标的类型规则
 * @author lujixiang
 * @creaTime 2017年3月10日
 */
public class LoanTypeRules extends StringIdEntity{
    
    
    private static final long serialVersionUID = 1L;

    // 标的类型id
    private String loanTypeId;
    
    // 规则id
    private String rulesId;
    
    // 规则类型
    private RulesType type;
    
    // 规则值
    private String value;
    

    public String getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(String loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public String getRulesId() {
        return rulesId;
    }

    public void setRulesId(String rulesId) {
        this.rulesId = rulesId;
    }

    public RulesType getType() {
        return type;
    }

    public void setType(RulesType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

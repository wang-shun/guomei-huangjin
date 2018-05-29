package com.gomemyc.invest.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.invest.entity.LoanTypeRules;
import com.gomemyc.invest.enums.RulesType;

/**
 * 规则dao
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public interface LoanTypeRulesDao {
    
    int save(LoanTypeRules rules);
    
    List<LoanTypeRules> listByLoanTypeId(@Param("loanTypeId") String loanTypeId);
    
    LoanTypeRules getByLoanTypeIdAndRulesType(@Param("loanTypeId") String loanTypeId, @Param("type") RulesType rulesType);

}

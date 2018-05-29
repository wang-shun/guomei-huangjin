package com.gomemyc.invest.dao;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.invest.entity.Rules;
import com.gomemyc.invest.enums.RulesClazz;
import com.gomemyc.invest.enums.RulesType;

/**
 * 规则dao
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public interface RulesDao {
    
    int save(Rules rules);
    
    Rules findByTypeAndClazz(@Param("type") RulesType type, @Param("clazz") RulesClazz clazz);

}

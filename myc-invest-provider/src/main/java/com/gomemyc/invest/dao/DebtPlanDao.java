package com.gomemyc.invest.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.invest.entity.DebtPlan;

/**
 * 债转方案
 * @author zhangWei
 *
 */
public interface DebtPlanDao {

	/**
	 * 根据ID查询
	 * @param id
	 * @return
	 */
	DebtPlan findById(@Param("id") String id);
	
	/**
	 * 根据id集合查询
	 * @param ids
	 * @return
	 */
	List<DebtPlan> findByIds(@Param("ids") List<String> ids);
}

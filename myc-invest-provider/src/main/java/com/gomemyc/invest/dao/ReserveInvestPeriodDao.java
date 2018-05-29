package com.gomemyc.invest.dao;

import java.util.List;
import com.gomemyc.invest.entity.ReserveInvestPeriod;

/**
 * 可预约投资期限表
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveInvestPeriodDao {
	
	/**
	 * 查询已启用、未删除的期限列表。
	 * 
	 * @return 期限集合
	 */
    List<ReserveInvestPeriod> findByEnableList();

}

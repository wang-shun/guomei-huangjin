package com.gomemyc.invest.dao;

import java.util.List;
import com.gomemyc.invest.entity.ReserveRate;

/**
 * 可预约利率区间表
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveRateDao {
    
	/**
	 * 查询已启用、未删除的利率区间列表。
	 * 
	 * @return 利率期间集合
	 */
    List<ReserveRate> findByEnableList();
    
}

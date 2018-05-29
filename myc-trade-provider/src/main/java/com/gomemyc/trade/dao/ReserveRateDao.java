package com.gomemyc.trade.dao;


import com.gomemyc.trade.entity.ReserveRate;

import java.util.List;

/**
 * 可预约利率区间表
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveRateDao {

    /**
     * 根据利率区间id查询数据。
     *
     * @param id 配置id
     * @return
     */
    ReserveRate findById(String id);
    
	/**
	 * 查询已启用、未删除的利率区间列表。
	 * 
	 * @return 利率期间集合
	 */
    List<ReserveRate> findByEnableList();
    
}

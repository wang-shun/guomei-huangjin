package com.gomemyc.trade.dao;

import com.gomemyc.trade.entity.ReserveInvestPeriod;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 可预约投资期限表
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveInvestPeriodDao {

    /**
     * 根据id，获得期限。
     *
     * @param id
     * @return
     */
    ReserveInvestPeriod findById(@Param("id") String id);
	
	/**
	 * 查询已启用、未删除的期限列表。
	 * 
	 * @return 期限集合
	 */
    List<ReserveInvestPeriod> findByEnableList();

}

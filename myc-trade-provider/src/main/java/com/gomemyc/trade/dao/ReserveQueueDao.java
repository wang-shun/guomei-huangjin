package com.gomemyc.trade.dao;


import com.gomemyc.trade.entity.ReserveQueue;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 投资队列DAO 
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveQueueDao {
    
	/**
	 * 根据队列ID查找队列。
	 * 
	 * @param queueId
	 * @return
	 */
	ReserveQueue findById(String queueId);
	
	/**
	 * 持久化队列记录。
	 * 
	 * @param reserveQueue
	 * @return
	 */
	Integer insert(ReserveQueue reserveQueue);

    /**
     * 更新记录
     *
     * @param reserveQueue
     * @return
     */
    int updateBySelective(ReserveQueue reserveQueue);

	/**
	 * 根据id,更新status
	 *
	 * @param map 只包含id,status
	 */
	Integer updateStatusById(Map<String, Object> map);


	List<ReserveQueue> listByRequestIdAndStatus(@Param("requestId")String requestId, @Param("status")Integer status);



}

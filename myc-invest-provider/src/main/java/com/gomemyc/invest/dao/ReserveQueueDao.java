package com.gomemyc.invest.dao;

import com.gomemyc.invest.entity.ReserveQueue;

/**
 * 投资队列DAO 
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveQueueDao {
    
	/**
	 * 根据队里ID查找队列
	 * 
	 * @param queueId
	 * @return
	 */
	ReserveQueue findByQueueId(String queueId);
	
	/**
	 * 
	 * 
	 * @param reserveQueue
	 * @return
	 */
	Integer insert(ReserveQueue reserveQueue);
	
    int deleteByPrimaryKey(String id);

    int insertSelective(ReserveQueue record);

    ReserveQueue selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReserveQueue record);

    int updateByPrimaryKey(ReserveQueue record);

}

package com.gomemyc.trade.dao;

import com.gomemyc.trade.entity.OriginProduct;
import org.apache.ibatis.annotations.Param;

/**
 * 原始产品DAO
 * 
 * @author 何健
 * @creaTime 2017年3月30日
 */
public interface OriginProductDao {

	/**
	 * 根据id查找记录
	 *
	 * @param id
	 * @return
	 */
	OriginProduct findById(String id);

	/**
	 * 更新原始产品状态
	 *
	 * @return
	 */
	Integer updateStatusByQueueId(@Param("assetsState") Integer assetsState, @Param("queueId") String queueId);


}


package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;

/** 
 * ClassName:GoldInvestOrderAndProductService
 * Date:     2017年3月28日
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
public interface GoldInvestOrderAndProductService {

	/**
	 * 
	 * 查询订单表和产品表，更新订单状态和剩余可投资金额
	 * 
	 * @param currentTime (String) 当前时间(必填) 格式yyyy-MM-dd HH:mm:ss
	 * @ServiceException
	 *                30001   预下单信息不存在
	 *                50000   操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月28日
	 */
	void updatePrepayStatusAndBalance(String currentTime) throws ServiceException;
	
}
  
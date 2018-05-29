
package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;

/** 
 * GoldInvestOrderAndAccountService
 * Date:     2017年3月30日
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
public interface GoldInvestOrderAndAccountService {

	/**
	 * 
	 * 更新所有处理中订单状态，调用北京银行投资接口，将用户投资资金放入标的账户
	 * 
	 * @ServiceException
	 *                30004   处理中订单信息不存在
	 *                30005   确认下单信息不存在
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月28日
	 */
	void updateOrderStatusAndAmount() throws ServiceException;
	
}
  
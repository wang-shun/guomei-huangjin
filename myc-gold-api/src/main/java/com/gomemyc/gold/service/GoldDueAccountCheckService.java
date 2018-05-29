/** 
 * Project Name:myc-gold-api 
 * File Name:GoldDueAccountCheckService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月20日下午4:26:36 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.gold.dto.GoldDueAccountCheckDTO;

import java.util.List;

/**
 * ClassName:GoldDueAccountCheckServic 
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:26:36
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public interface GoldDueAccountCheckService {

	/**
	 * 
	 * 保存定期金到期对账文件数据
	 * 
	 * @param productCode (String) 产品编码(必填)
	 * @param orderDate   (String) 订单日期 (必填)  格式:yyyy-MM-dd 
	 * @param start       (Integer) 起始记录行数(选填)
	 * @param size        (Integer) 获取记录数大小(选填)
	 * @return            (Integer) 影响的记录数
	 * @ServiceException
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                30005:确认下单信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月20日
	 */
	Integer saveGoldDueAccountCheck() throws ServiceException;
	 /**
	 *
	 * 根据创建时间查询今天所有的需要对比的数据
	 *
	 * @param createTime 产品编码
	 * @return List<GoldDueAccountCheckDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月20日
	 */
	List<GoldDueAccountCheckDTO> getByCreateTime(String createTime);
	/**
	 *
	 * 根据创建时间查询今天所有的需要对比的数据
	 *
	 * @param orderNo 产品编码
	 * @param  comparingStatus 对账结果
	 * @return List<GoldDueAccountCheckDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月05日
	 */
	int updateComparingStatusBuOrderNo(String orderNo,Integer comparingStatus);

	/**
	 *
	 *本地对账的数据保存
	 *
	 * @return List<GoldDueAccountCheckDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月05日
	 */

	Integer saveDueAccountCheck() throws ServiceException;

}
  
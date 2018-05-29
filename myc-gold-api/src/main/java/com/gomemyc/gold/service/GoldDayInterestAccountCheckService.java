/** 
 * Project Name:myc-gold-api 
 * File Name:GoldDayInterestAccountCheckService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月20日下午4:26:20 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.gold.dto.GoldDayInterestAccountCheckDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ClassName:GoldDayInterestAccountCheckService 
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:26:20
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public interface GoldDayInterestAccountCheckService {
	
	/**
	 * 
	 * 保存黄金钱包每天利息对账文件数据
	 * 
	 * @return            (Integer) 影响的记录数
	 * @ServiceException 
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月20日
	 */
	Integer saveGoldDayInterestAccountCheck() throws ServiceException;
	
	/**
	 * 
	 * 保存平台每天利息对账文件数据
	 * 
	 * @return (Integer) 影响的记录数
	 * @exception
	 *         50000   操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月22日
	 */
	Integer savePlatformDayInterestAccountCheck() throws ServiceException;

	/**
	 *根据创建时间查询当天创建的所有订单
	 *
	 *
	 * @param createTime 更新时间
	 * @return  List<GoldDayInterestAccountCheckExtend>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月30日
	 */
	List<GoldDayInterestAccountCheckDTO> getBycreateTime(String createTime );
	/**
	 *
	 * 更新对比结果
	 *
	 * @param orderNo 订单号
	 * @param comparingStatus 比对结果状态吗
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月30日
	 */
	int updateComparingStatusByOrderNo(String orderNo,Integer comparingStatus);
	/**
	 *
	 * 根据订单号和创建时间查询当前时间创建的相应的订单
	 *
	 * @param reqNo 订单号
	 * @param createTime 创建时间
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月30日
	 */

	GoldDayInterestAccountCheckDTO getBycreateTimeAndReqNo( String createTime, String reqNo );


	/**
	 *
	 * 保存平台每天利息对账文件数据
	 *
	 * @return (Integer) 影响的记录数
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月22日
	 */
	Integer saveEarnings();
	
}
  
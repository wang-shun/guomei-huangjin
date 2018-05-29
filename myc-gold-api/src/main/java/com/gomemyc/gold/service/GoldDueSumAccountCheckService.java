/** 
 * Project Name:myc-gold-api 
 * File Name:GoldDueSumAccountCheckService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月20日下午4:26:46 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.gold.dto.GoldDueSumAccountCheckDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName:GoldDueSumAccountCheckService
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON 
 * Date:     2017年3月20日 下午4:26:46
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public interface GoldDueSumAccountCheckService {
	
	/**
	 * 
	 * 保存定期到期利息汇总对账文件数据
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
	Integer saveGoldDueSumAccountCheck() throws ServiceException;

	/**
	 * 查询今日产生的数据
	 * @param createTime
	 * @return  List<GoldDueSumAccountCheckDTO>
	 * @author liujunhan
	 */
	List<GoldDueSumAccountCheckDTO> getByCreateTime(String createTime);
	/**
	 * 更新对比结果
	 * @param orderNo
	 * @param comparingStatus
	 * @return int
	 * @author liujunhan
	 */
	int updateComparingStatusByOrderNo(String orderNo,Integer comparingStatus );


	/**
	 *
	 * 保存定期到期利息汇总对账文件数据
	 *
	 * @return (Integer) 影响的记录数
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月20日
	 */
	Integer saveDueSumAccountCheck()throws ServiceException;
}
  
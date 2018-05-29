/** 
 * Project Name:myc-gold-api 
 * File Name:GoldInvestAccountCheckService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月20日下午4:27:02 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.gold.dto.GoldInvestAccountCheckDTO;

import java.util.List;

/**
 * ClassName:GoldInvestAccountCheckService 
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:27:02
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description  买定期金对账文件
 */
public interface GoldInvestAccountCheckService {
	
	/**
	 * 
	 * 保存买定期金对账文件数据
	 * 
	 * @param productCode (String) 产品编码(必填)
	 * @param orderDate   (String) 订单日期(必填) 格式:yyyy-MM-dd
	 * @param start       (Integer) 起始记录行数(选填)
	 * @param size        (Integer) 获取记录数大小(选填)
	 * @return            (Integer) 影响的记录数
	 * @ServiceException
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月20日
	 */
	Integer saveGoldInvestAccountCheck() throws ServiceException;

	/**
	 *
	 * 保存买定期金对账文件数据
	 * @return            (Integer) 影响的记录数
	 * @ServiceException
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月20日
	 */
	Integer saveInvestAccountCheck() throws ServiceException;
	/**
	 *
	 * 根据创建时间，查询前一天下的单
	 *
	 * @param createTime  当前时间
	 * @return   List<GoldInvestAccountCheckDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月28日
	 */
	List<GoldInvestAccountCheckDTO> getByCreateTime(String createTime);


	/**
	 *
	 * 根据订单号，更新订单比较结果
	 *@param orderNo
	 * @param comparingStatus
	 * @return  Integer
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月28日
	 */
	int updateComparingStatusByOrderNo(String orderNo,Integer comparingStatus);

}
  
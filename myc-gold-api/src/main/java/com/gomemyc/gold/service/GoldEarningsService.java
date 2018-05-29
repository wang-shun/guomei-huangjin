/** 
 * Project Name:myc-gold-api 
 * File Name:GoldEarningsService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月6日下午2:55:36 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.gold.dto.GoldEarningsDTO;

/** 
 * ClassName:GoldEarningsService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月6日 下午2:55:36 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description 黄金收益
 */
public interface GoldEarningsService {
	
	/**
	 * 
	 * 查询用户昨日收益 
	 * 
	 * @author TianBin 
	 * @date 2017年3月06日
	 * @param userId
	 * @return 
	 * @since JDK 1.8
	 */
	
	GoldEarningsDTO listPageYesterDay(String userId );
	
	/**
	 * 
	 * 查询用户历史收益
	 * 
	 * @author TianBin 
	 * @date 2017年3月06日
	 * @param userId  用户id
	 * @param pageSize 每页条数
	 * @parm  page  第几页
	 * @return 
	 * @since JDK 1.8
	 */
	
	GoldEarningsDTO listPageEarnings(int page,int pageSize,String userId);
	
}
  
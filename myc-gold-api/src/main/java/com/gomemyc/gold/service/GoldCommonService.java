/** 
 * Project Name:myc-gold-api 
 * File Name:GoldOtherService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月12日上午11:26:09 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.gold.dto.GoldCommonDTO;

/** 
 * ClassName:GoldOtherService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 上午11:26:09 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */

public interface GoldCommonService {

	/**
	 * 
	 * 轮询查询黄金金价放入redis
	 * 
	 * @author TianBin 
	 * @date 2017年3月12日
	 * @return 
	 * @since JDK 1.8
	 */
	GoldCommonDTO selectGoldRealPrice();
	
}
  
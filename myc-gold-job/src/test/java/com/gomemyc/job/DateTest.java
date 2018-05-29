/** 
 * Project Name:myc-gold-job 
 * File Name:DateTest.java 
 * Package Name:com.gomemyc.job 
 * Date:2017年3月12日下午6:22:09 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job;

import com.gomemyc.constant.GoldJobRedisConstant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** 
 * ClassName:DateTest <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 下午6:22:09 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class DateTest {

	
	
	
	public static void main(String args[]){
		LocalDateTime localDateTime=LocalDateTime.now();

		localDateTime.format(DateTimeFormatter.ofPattern("YYYY-mm-dd"));

		System.out.println(LocalDateTime.now().getDayOfMonth());
		System.out.println(LocalDateTime.now().getDayOfWeek());
		System.out.println(LocalDateTime.now().getDayOfYear());

		System.out.println(String.format(GoldJobRedisConstant.GOLD_MINUTE_LIST_PREFIX_KEY,localDateTime.format(DateTimeFormatter.ofPattern("YYYY-mm-dd")),localDateTime.getHour()));

	}
}

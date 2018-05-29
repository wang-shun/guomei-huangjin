/** 
 * Project Name:myc-gold-job 
 * File Name:GoldRealPriceMonthSimpleJob.java 
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月20日下午4:44:24 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.gold.dto.GoldStatisticsDayPriceDTO;
import com.gomemyc.gold.service.GoldStatisticsPriceService;

/** 
 * ClassName:GoldRealPriceMonthSimpleJob <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月20日 下午4:44:24 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldRealPriceMonthSimpleJob implements SimpleJob{

   
	
	
	private static final Logger logger = LoggerFactory.getLogger(GoldRealPriceMonthSimpleJob.class);
	
	@Reference
	private GoldStatisticsPriceService goldStatisticsPriceService;
	

	
	@Override
	public void execute(ShardingContext shardingContext) {
		LocalDate currentDate=LocalDate.now().minusMonths(1);
		GoldStatisticsDayPriceDTO goldStatisticsDayPriceDTO=goldStatisticsPriceService.selectDayObjByMonth(currentDate.getMonthValue());
		if(goldStatisticsDayPriceDTO!=null && goldStatisticsDayPriceDTO.getTotalPrice()!=null  &&  goldStatisticsDayPriceDTO.getTotalPrice().compareTo(BigDecimal.ZERO)==1){
			BigDecimal avg_privc=goldStatisticsDayPriceDTO.getTotalPrice().divide(BigDecimal.valueOf(goldStatisticsDayPriceDTO.getTotalDate())).setScale(2, RoundingMode.DOWN);
			goldStatisticsPriceService.saveGoldStatisticsMonthPrice(currentDate.getYear(), currentDate.getMonthValue(),avg_privc);
		} 
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	
	}

	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}



}
  
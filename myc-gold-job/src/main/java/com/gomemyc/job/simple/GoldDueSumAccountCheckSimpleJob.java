/** 
 * Project Name:myc-gold-job 
 * File Name:GoldDueSumAccountCheckSimpleJob.java 
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月22日下午6:02:38 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.gomemyc.util.GoldDueSumAccountCheckUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.service.GoldDueSumAccountCheckService;
import com.gomemyc.gold.service.GoldProductService;
import org.springframework.beans.factory.annotation.Autowired;

/** 
 * ClassName:GoldDueSumAccountCheckSimpleJob
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON. 
 * Date:     2017年3月22日 下午6:02:38 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldDueSumAccountCheckSimpleJob implements SimpleJob{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldDueSumAccountCheckSimpleJob.class);
     
	@Reference
	private GoldDueSumAccountCheckService goldDueSumAccountCheckService;
	
	@Reference
	private GoldProductService goldProductService;

	@Autowired
	 private GoldDueSumAccountCheckUtil goldDueSumAccountCheckUtil;
	
	@Override
	public void execute(ShardingContext shardingcontext) {
		logger.info("GoldDueSumAccountCheckSimpleJob begin,the time is {}",LocalDateTime.now());
		
		//开标时间为当前时间的前一天
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDueSumAccountCheckSimpleJob,the openTime is {}",openTime);
		
	    //根据开标时间查询产品id和产品编码
	    List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByOpenTime(openTime);
	    
	    //遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
	    for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
	    {
			logger.info("execute in GoldDueSumAccountCheckSimpleJob,the productCode is {}",goldProductIdAndCodeDTO.getGoldProductCode());
			int insertRows=goldDueSumAccountCheckService.saveDueSumAccountCheck(goldProductIdAndCodeDTO.getGoldProductCode(), 0, 0);
			logger.info("execute in GoldDueSumAccountCheckSimpleJob,the insertRows is {}",insertRows);
	    }
		
		logger.info("GoldDueSumAccountCheckSimpleJob end,the time is {}",LocalDateTime.now());

	    //调用数据比对，并更新比对结果
		goldDueSumAccountCheckUtil.goldDueSumAccountCheck();
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	
	}
	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}

}
  
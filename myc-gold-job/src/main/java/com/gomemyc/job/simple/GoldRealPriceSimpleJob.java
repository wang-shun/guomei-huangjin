/** 
 * Project Name:myc-gold-job 
 * File Name:GoldRealPriceSimpleJob.java 
 * Package Name:com.gomemyc.job 
 * Date:2017年3月12日下午3:02:05 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.constant.GoldJobRedisConstant;
import com.gomemyc.gold.dto.GoldCommonDTO;
import com.gomemyc.gold.service.GoldCommonService;

/** 
 * ClassName:GoldRealPriceSimpleJob <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 下午3:02:05 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description  金价放入redis
 */
public class GoldRealPriceSimpleJob implements SimpleJob {



	private static final Logger logger = LoggerFactory.getLogger(GoldRealPriceSimpleJob.class);

	@Reference
	private  GoldCommonService goldCommonService;

	@Autowired
	@Qualifier("redisStringStringTemplate")
	private  RedisTemplate<String, String> redisTemplate;
	
	
	
	/**
	 * 
	 * TODO 简单描述该方法的实现功能（可选）. 
	 * @see com.dangdang.ddframe.job.api.simple.SimpleJob#execute(com.dangdang.ddframe.job.api.ShardingContext)
	 */

	@Override
	public void execute(ShardingContext shardingContext) {
		  GoldCommonDTO goldCommonDTO=goldCommonService.selectGoldRealPrice();
		  logger.info("GoldRealPriceSimpleJob is execute,the goldCommonDTO is [getRealPrice={}] ",goldCommonDTO.getRealPrice());
		  if(goldCommonDTO!=null && goldCommonDTO.getRealPrice().compareTo(BigDecimal.ZERO)>0){
			  String listKey=String.format(GoldJobRedisConstant.GOLD_MINUTE_LIST_PREFIX_KEY,LocalDate.now(),LocalDateTime.now().getHour());
		      redisTemplate.opsForValue().set(GoldJobRedisConstant.GOLD_SECONDS_REAL_PRICE_KEY, goldCommonDTO.getRealPrice().toString());
		      redisTemplate.expire(listKey,2, TimeUnit.HOURS);
		      redisTemplate.opsForList().rightPush(listKey, goldCommonDTO.getRealPrice().toString());
		      logger.info("GoldRealPriceSimpleJob is execute,the result listKey is [listKey={}] ",listKey);
		  }
	}
	
	
	
	
	/**
     * 处理作业执行时异常.
     * 
     * @param jobExecutionException 作业执行时异常
     * @throws JobExecutionException 作业执行时异常
     */
	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}
	
	 /**
     * 处理作业执行时异常.
     * 
     * @param jobExecutionException 作业执行时异常
     * @throws JobExecutionException 作业执行时异常
     */

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		
	}

	
	
	
	
	

}
  
/** 
 * Project Name:myc-gold-job 
 * File Name:GoldDueAccountCheckSimpleJob.java 
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月22日下午6:06:36 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.time.LocalDateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.gold.service.GoldInvestOrderAndProductService;
import com.gomemyc.util.DateUtil;

/** 
 * ClassName:GoldInvestOrderAndProductSimpleJob
 * Date:     2017年3月28日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
public class GoldInvestOrderAndProductSimpleJob implements SimpleJob{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldInvestOrderAndProductSimpleJob.class);
	
	@Reference
	private GoldInvestOrderAndProductService goldInvestOrderAndProductService;
	
	@Override
	public void execute(ShardingContext shardingcontext) {
		logger.info("GoldInvestOrderAndProductSimpleJob begin,the time is {}",LocalDateTime.now());
		//查询订单表和产品表，更新订单状态和剩余可投资金额
		goldInvestOrderAndProductService.updatePrepayStatusAndBalance(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		logger.info("GoldInvestOrderAndProductSimpleJob end,the time is {}",LocalDateTime.now());
	}
	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}

}
  
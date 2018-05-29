/** 
 * Project Name:myc-gold-job 
 * File Name:GoldDueAccountCheckSimpleJob.java 
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月22日下午6:06:36 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.gomemyc.util.GoldDueAccountCheckUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.service.GoldDueAccountCheckService;
import com.gomemyc.gold.service.GoldProductService;
import org.springframework.beans.factory.annotation.Autowired;

/** 
 * ClassName:GoldDueAccountCheckSimpleJob 
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月22日 下午6:06:36 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldDueAccountCheckSimpleJob implements SimpleJob{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldDueAccountCheckSimpleJob.class);
	
	@Reference
	private GoldDueAccountCheckService goldDueAccountCheckService;
	
	@Reference
	private GoldProductService goldProductService;

	@Autowired
    private GoldDueAccountCheckUtil goldDueAccountCheckUtil;
	
	@Override
	public void execute(ShardingContext shardingcontext) {
		logger.info("GoldDueAccountCheckSimpleJob begin,the time is {}",LocalDateTime.now());
		
		//开标时间为当前时间的前一天
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDueAccountCheckSimpleJob,the openTime is {}",openTime);
		
	    //根据开标时间查询产品id和产品编码
	    List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByOpenTime(openTime);
	    
	    //遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
	    for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
	    {
			logger.info("execute in GoldDueAccountCheckSimpleJob,the productCode is {}",goldProductIdAndCodeDTO.getGoldProductCode());
			int insertRows=goldDueAccountCheckService.saveDueAccountCheck(goldProductIdAndCodeDTO.getGoldProductCode(), openTime, 0, 0);
			logger.info("execute in GoldDueAccountCheckSimpleJob,the insertRows is {}",insertRows);
	    }
		logger.info("GoldDueAccountCheckSimpleJob end,the time is {}",LocalDateTime.now());

	    //调用比较方法，更新比对结果
	    goldDueAccountCheckUtil.goldInvestAccountCheck();

	}
	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}

}
  
/** 
 * Project Name:myc-gold-job 
 * File Name:GoldDayInterestAccountCheckSimpleJob.java 
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月22日下午5:48:15 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.gomemyc.util.GoldDayInterestAccountCheckUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.service.GoldDayInterestAccountCheckService;
import com.gomemyc.gold.service.GoldProductService;
import org.springframework.beans.factory.annotation.Autowired;

/** 
 * ClassName:GoldDayInterestAccountCheckSimpleJob 
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON 
 * Date:     2017年3月22日 下午5:48:15 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description  每天利息对账
 */
public class GoldDayInterestAccountCheckSimpleJob  implements SimpleJob{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldDayInterestAccountCheckSimpleJob.class);
	
	@Reference
	private GoldDayInterestAccountCheckService goldDayInterestAccountCheckService;
	
	@Reference
	private GoldProductService goldProductService;

	@Autowired
	private GoldDayInterestAccountCheckUtil goldDayInterestAccountCheckUtil;
	
	@Override
	public void execute(ShardingContext shardingcontext) {
		logger.info("GoldDayInterestAccountCheckSimpleJob begin,the time is {}",LocalDateTime.now());
		
		//开标时间为当前时间的前一天
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the openTime is {}",openTime);
		
	    //根据开标时间查询产品id和产品编码
	    List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByOpenTime(openTime);
	    
	    //遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
	    for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
	    {
			logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the productCode is {}",goldProductIdAndCodeDTO.getGoldProductCode());
			int insertRows=goldDayInterestAccountCheckService.saveDayInterestAccountCheck(goldProductIdAndCodeDTO.getGoldProductCode(), openTime, 0, 0);
			logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the insertRows is {}",insertRows);
	    }
		logger.info("GoldDayInterestAccountCheckSimpleJob end,the time is {}",LocalDateTime.now());

		//调用比较方法，对边数据并更细比对结果
		goldDayInterestAccountCheckUtil.goldDayInterestAccount();
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	
	}
	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}

}
  
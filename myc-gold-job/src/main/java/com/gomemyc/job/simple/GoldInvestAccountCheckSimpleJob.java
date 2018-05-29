/** 
 * Project Name:myc-gold-job 
 * File Name:GoldInvestAccountCheckSimpleJob.java 
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月22日下午5:09:18 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.gomemyc.util.GoldInvestAccountCheckUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.service.GoldInvestAccountCheckService;
import com.gomemyc.gold.service.GoldProductService;
import org.springframework.beans.factory.annotation.Autowired;

/** 
 * ClassName:GoldInvestAccountCheckSimpleJob
 * Function: TODO ADD FUNCTION.
 * Reason:   TODO ADD REASON.
 * Date:     2017年3月22日 下午5:09:18 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description  买定期金对账
 */
public class GoldInvestAccountCheckSimpleJob  implements SimpleJob{
	
    private static final Logger logger = LoggerFactory.getLogger(GoldInvestAccountCheckSimpleJob.class);
	
	@Reference
	private GoldInvestAccountCheckService goldInvestAccountCheckService;
	
	@Reference
	private GoldProductService goldProductService;

	@Autowired
	private GoldInvestAccountCheckUtil goldInvestAccountCheckUtil;
	
	/**
	 * 
	 * 黄金钱包的订单对账
	 * 理论上T+1 日24 可对账.平台 00:10 进行对账
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(ShardingContext arg0) {
	    logger.info("GoldInvestAccountCheckSimpleJob begin,the time is {}",LocalDateTime.now());
	    
		//开标时间为当前时间的前一天
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDueSumAccountCheckSimpleJob,the openTime is {}",openTime);
	    
	    //根据开标时间查询产品id和产品编码
	    List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByOpenTime(openTime);
	    
	    //遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
	    for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
	    {
	    	logger.info("execute in GoldInvestAccountCheckSimpleJob,the productCode is {}",goldProductIdAndCodeDTO.getGoldProductCode());
			int insertRows=goldInvestAccountCheckService.saveInvestAccountCheck(goldProductIdAndCodeDTO.getGoldProductCode(), openTime, 0,0);
			logger.info("execute in GoldInvestAccountCheckSimpleJob,the insertRows is {}",insertRows);
	    }
	    
		logger.info("GoldInvestAccountCheckSimpleJob end,the time is {}",LocalDateTime.now());

	    //调用数据比对，并更新比对结果
		goldInvestAccountCheckUtil.goldInvestAccountCheck();
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
	}
	
	@Override
	public void handleJobExecutionException(JobExecutionException jobExecutionException) throws JobExecutionException {
		
	}

}
  
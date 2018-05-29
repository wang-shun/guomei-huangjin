/** 
 * Project Name:myc-gold-job 
 * File Name:GoldRealPriceHourSimpleJob.java
 * Package Name:com.gomemyc.job.simple 
 * Date:2017年3月12日下午5:38:34 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.job.simple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.gomemyc.constant.GoldJobRedisConstant;
import com.gomemyc.gold.service.GoldStatisticsPriceService;
import com.gomemyc.util.DateUtil;

/** 
 * ClassName:GoldRealPriceHourSimpleJob <br/>
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 下午5:38:34 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description   定点请求之前的数据
 */
public class GoldRealPriceHourSimpleJob implements SimpleJob {



    private static final Logger logger = LoggerFactory.getLogger(GoldRealPriceHourSimpleJob.class);


	@Reference
	private GoldStatisticsPriceService goldStatisticsPriceService;
	
	@Autowired
	@Qualifier("redisStringStringTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	
	
	
	
	/**
	 * 
	 * 1小时之后执行计算前一个小时的数据
	 *
	 */
	
	@Override
	public void execute(ShardingContext shardingContext) {

        LocalDateTime localDateTime=LocalDateTime.now();
        logger.info("GoldRealPriceHourSimpleJob begin,the time is {}",localDateTime);
		
        int hour=localDateTime.getHour()==0?GoldJobRedisConstant.GOLD_HOUR:localDateTime.getHour()-1;
        String prevDateStr=hour==0?localDateTime.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")):localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String redisListKey=String.format(GoldJobRedisConstant.GOLD_MINUTE_LIST_PREFIX_KEY,prevDateStr,hour);
        logger.info("GoldRealPriceHourSimpleJob is running,the param [prevDateStr={}] [hour={}] [redisListKey={}]",prevDateStr,hour,redisListKey);
        logger.info("GoldRealPriceHourSimpleJob is key {}",redisTemplate.hasKey(redisListKey));
        //判断redis的key是否存在
		if(redisTemplate.hasKey(redisListKey)){
            //获取list大小
            Long goldListSize=redisTemplate.opsForList().size(redisListKey);
            logger.info("GoldRealPriceHourSimpleJob is running,the redis goldListSize is {} ",goldListSize);
            BigDecimal totalPrice=BigDecimal.ZERO;
            List<String> goldList = new ArrayList<String>();
            ListOperations<String, String> listOperation = redisTemplate.opsForList();
            for(int i = 0 ; i < goldListSize ; i ++){
            	goldList.add(listOperation.leftPop(redisListKey));
            }
            
            logger.info("GoldRealPriceHourSimpleJob is running,the redis result  goldList size  is {}",goldList.size());
            long count=0L;
            for (Object object : goldList) {
                if(object!=null){
                	 count++;
                	 totalPrice=totalPrice.add(new BigDecimal(object.toString()));
                }  
            }
            logger.info("GoldRealPriceHourSimpleJob is running,the count is [{}],totalPrice is [{}]",count,totalPrice);
            if(count>0 && totalPrice.compareTo(BigDecimal.ZERO)>0){
			    Date prevDate=DateUtil.convert2Date(prevDateStr, "yyyy-MM-dd");
			    logger.info("GoldRealPriceHourSimpleJob is running,the table param [prevDate={}] [hour={}] [totalPrice={}] [count={}]  goldList size  is ",prevDate,hour,totalPrice,count);
			    goldStatisticsPriceService.saveGoldStatisticsHourPrice(prevDate,hour,totalPrice.divide(BigDecimal.valueOf(count)).setScale(2,RoundingMode.DOWN));
			}
		}

        logger.info("GoldRealPriceHourSimpleJob end,the time is {}",LocalDateTime.now());
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
		// TODO Auto-generated method stub
		
	}

	
}
  
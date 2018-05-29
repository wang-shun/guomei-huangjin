/** 
 * Project Name:myc-rest 
 * File Name:GoldStatisticsController.java 
 * Package Name:com.gomemyc.controller 
 * Date:2017年3月5日下午4:50:15 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.gold.dto.GoldEarningsDTO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.service.GoldInvestService;
import com.gomemyc.http.MediaTypes;
import com.gomemyc.util.InfoCode;
import com.gomemyc.util.RestResp;
import com.google.common.collect.Maps;

/** 
 * ClassName:GoldStatisticsController
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月5日 下午4:50:15
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Component
@Path("/api/v1/gold/statistics")
@Produces(MediaTypes.JSON_UTF_8)
public class GoldStatisticsController {

	@Reference
	private GoldInvestService goldInvestService;

	private static final Logger logger = LoggerFactory.getLogger(GoldStatisticsController.class);

	/**
	 *
	 * 统计黄金克数
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @return
	 * @since JDK 1.8
	 */
	@GET
	@Path("/totalweight")
	public Object totalweight(@QueryParam("uid")String  uid){
		logger.info("totalweight in GoldStatisticsController, the params uid is [{}]",uid);
		if (StringUtils.isBlank(uid))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			Map<String,BigDecimal> map = Maps.newHashMap();

			 map.put("totalWeight",goldInvestService.statisticsGoldWeight(uid)) ;
			return map;
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("totalweight in GoldStatisticsController, the params error is [{}]","totalweightError");
			logger.error("totalweight in GoldStatisticsController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}

	/**
	 *
	 * 统计昨日收益
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @return
	 * @since JDK 1.8
	 */
	@GET
	@Path("/yesterday")
	public Object yesterday(@QueryParam("uid")String uid){
		logger.info("yesterday in GoldStatisticsController, the params uid is [{}]",uid);
		if (StringUtils.isBlank(uid))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.getYesterdayEarnings(uid);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("yesterday in GoldStatisticsController, the params error is [{}]","yesterdayError");
			logger.error("yesterday in GoldStatisticsController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}

	/**
	 *
	 * 统计历史收益
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @param page 页数
	 * @param pageSize 每页显示数量
	 * @return
	 * @since JDK 1.8
	 */
	@GET
	@Path("/history")
	public Object history(@QueryParam("uid")String  uid,
						  @DefaultValue("1") @QueryParam("page") int page,
						  @DefaultValue("10") @QueryParam("pageSize") int pageSize){
		logger.info("history in GoldStatisticsController, the params uid is [{}],page is [{}],pageSize is [{}]",uid,page,pageSize);
		if (StringUtils.isBlank(uid))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			Map<String, Object> mapData = Maps.newHashMap();
			int ecord = goldInvestService.getHistoryEarningrEcord(uid);
			List<GoldEarningsDTO> goldEarningsDTOlist = goldInvestService.getHistoryEarnings(uid,page,pageSize);
			mapData.put("rows", goldEarningsDTOlist);
			mapData.put("totalEarnings", goldInvestService.getHistoryTotalEarningrs(uid));
			mapData.put("totalSize",ecord);
			mapData.put("currentPage", page);
			return mapData;
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("history in GoldStatisticsController, the params error is [{}]","historyError");
			logger.error("history in GoldStatisticsController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new HashMap<String,Object>();
	}
	
}
  
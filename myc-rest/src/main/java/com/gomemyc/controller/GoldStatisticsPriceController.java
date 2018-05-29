/** 
 * Project Name:myc-rest 
 * File Name:GoldStatisticsPriceController.java 
 * Package Name:com.gomemyc.controller 
 * Date:2017年3月17日下午6:42:35 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.controller;

import java.util.Date;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.gold.service.GoldCommonService;
import com.gomemyc.gold.service.GoldStatisticsPriceService;
import com.gomemyc.http.MediaTypes;
import com.gomemyc.util.InfoCode;

/** 
 * ClassName:GoldStatisticsPriceController
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月17日 下午6:42:35
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Component
@Path("/api/v1/gold/statisticsprice")
@Produces(MediaTypes.JSON_UTF_8)
public class GoldStatisticsPriceController {
	private static final Logger logger = LoggerFactory.getLogger(GoldStatisticsPriceController.class);

	@Reference
	private GoldStatisticsPriceService goldStatisticsPriceService;
	
	@Reference
	private GoldCommonService goldCommonService;
	
	/**
	 * 
	 * 统计小时数据 
	 * 
	 * @author TianBin 
	 * @param page
	 * @param pageSize
	 * @return 
	 * @since JDK 1.8
	 */
	@GET
	@Path("/hour")
	public Object hourList(@DefaultValue("1") @QueryParam("page")int page,@DefaultValue("24") @QueryParam("pageSize")int pageSize){
		try{
			return goldStatisticsPriceService.selectHourPriceList(new Date(), page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("hourList in GoldStatisticsPriceController, the params error is [{}]","hourListError");
			logger.error("hourList in GoldStatisticsPriceController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}
	
	/**
	 * 
	 * 统计天数据 
	 * 
	 * @author TianBin 
	 * @param page
	 * @param pageSize
	 * @return 
	 * @since JDK 1.8
	 */
	@GET
	@Path("/day")
	public Object dayList(@DefaultValue("1") @QueryParam("page")int page,@DefaultValue("7") @QueryParam("pageSize")int pageSize){
		try{
			return goldStatisticsPriceService.selectDayPriceList(new Date(), page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("dayList in GoldStatisticsPriceController, the params error is [{}]","dayListError");
			logger.error("dayList in GoldStatisticsPriceController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}
	
	/**
	 * 
	 * 统计月数据 
	 * 
	 * @author TianBin 
	 * @param page
	 * @param pageSize
	 * @return 
	 * @since JDK 1.8
	 */
	@GET
	@Path("/month")
	public Object monthList(@DefaultValue("1") @QueryParam("page")int page,@DefaultValue("12") @QueryParam("pageSize")int pageSize){
		try{
			return goldStatisticsPriceService.selectMonthPriceList(new Date(), page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("monthList in GoldStatisticsPriceController, the params error is [{}]","monthListError");
			logger.error("monthList in GoldStatisticsPriceController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}
	
	/**
	 * 
	 * 统计年数据 
	 * 
	 * @author TianBin 
	 * @param page
	 * @param pageSize
	 * @return 
	 * @since JDK 1.8
	 */
	@GET
	@Path("/year")
	public Object yearList(@DefaultValue("1") @QueryParam("page")int page,@DefaultValue("10") @QueryParam("pageSize")int pageSize){
		try{
			return goldStatisticsPriceService.selectYearPriceList(new Date(), page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("yearList in GoldStatisticsPriceController, the params error is [{}]","yearListError");
			logger.error("yearList in GoldStatisticsPriceController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}
	
	/**
	 * 
	 * 查询实时金价
	 * 
	 * @author LiuQiangBin 
	 * @return 
	 * @since JDK 1.8
	 */
	@GET
	@Path("/goldprice")
	public Object queryGoldPrice(){
		try{
		     return goldCommonService.selectGoldRealPrice();
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("queryGoldPrice in GoldStatisticsPriceController, the params error is [{}]","queryGoldPriceError");
			logger.error("queryGoldPrice in GoldStatisticsPriceController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return null;
	}
}
  
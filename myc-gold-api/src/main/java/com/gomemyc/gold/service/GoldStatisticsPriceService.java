/** 
 * Project Name:myc-gold-api 
 * File Name:GoldStatisticsPriceService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月13日下午3:40:16 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.gold.dto.GoldStatisticsDayPriceDTO;
import com.gomemyc.gold.dto.GoldStatisticsHourPriceDTO;
import com.gomemyc.gold.dto.GoldStatisticsMonthPriceDTO;
import java.math.BigDecimal;
import java.util.Date;

/** 
 * ClassName:GoldStatisticsPriceService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月13日 下午3:40:16 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public interface GoldStatisticsPriceService {
	
	
	

	
	/**
	 * 
	 * 新增每个小时的金价
	 * 
	 * @author TianBin 
	 * @param priceDate  金价日期
	 * @param price      金价
	 * @param priceHour  金价小时
	 *
	 * @return 
	 * @since JDK 1.8
	 */
	int saveGoldStatisticsHourPrice(Date priceDate,Integer priceHour,BigDecimal price) throws ServiceException;

	/**
	 *
	 * 统计小时的金价
	 *
	 * @author TianBin
	 * @param currentDate  日期
	 * @param page  第几页
	 * @param pageSize 每页多少条
	 * @exception
	 *         10000  参数不完整
	 *
	 * @return
	 * @since JDK 1.8
	 */
	Page selectHourPriceList(Date currentDate, int page, int pageSize)  throws ServiceException;

	/**
	 * 
	 *  新增每天的金价
	 * 
	 * @author TianBin 
	 * @param priceDate  金价日期
	 * @param price      金价
	 * @return 
	 * @since JDK 1.8
	 */
	int saveGoldStatisticsDayPrice(Date priceDate,BigDecimal price)  throws ServiceException;

	/**
	 *
	 * 统计天的金价
	 *
	 * @author TianBin
	 * @param currentDate  日期
	 * @param page  第几页
	 * @param pageSize 每页多少条
	 * @return
	 * @since JDK 1.8
	 */
	Page  selectDayPriceList(Date currentDate,int page,int pageSize)  throws ServiceException;
	
	/**
	 * 
	 *  新增月的金价
	 * 
	 * @author TianBin 
	 * @param priceYears  年
	 * @param priceMonth  月
	 * @param price      金价
	 * @return 
	 * @since JDK 1.8
	 */
	int saveGoldStatisticsMonthPrice(Integer priceYears,Integer priceMonth,BigDecimal price)  throws ServiceException;

	/**
	 *
	 * 统计月的金价
	 *
	 * @author TianBin
	 * @param currentDate  日期
	 * @param page  第几页
	 * @param pageSize  每页多少条
	 * @return
	 * @since JDK 1.8
	 */
	Page  selectMonthPriceList(Date currentDate,int page,int pageSize)  throws ServiceException;

	/**
	 *
	 * 统计年的金价
	 *
	 * @author TianBin
	 * @param currentDate  日期
	 * @param page  第几页
	 * @param pageSize  每页多少条
	 * @return
	 * @since JDK 1.8
	 */
	Page  selectYearPriceList(Date currentDate,int page,int pageSize)  throws ServiceException;

	/**
	 *
	 * 查询小时的金价,进行日的金价统计
	 *
	 * @author TianBin
	 * @param priceDate  日期
	 * @return
	 * @since JDK 1.8
	 */
	GoldStatisticsHourPriceDTO  selectHourObjByPriceDate(Date priceDate);

	/**
	 *
	 * 查询日的金价列表,进行月的金价统计
	 *
	 * @author TianBin
	 * @param priceDate  日期
	 * @return
	 * @since JDK 1.8
	 */
	GoldStatisticsDayPriceDTO selectDayObjByMonth(Integer month);

	/**
	 *
	 * 查询月的金价列表,进行年的金价统计
	 *
	 * @author TianBin
	 * @param priceYear  年
	 * @return
	 * @since JDK 1.8
	 */
	GoldStatisticsMonthPriceDTO selectMonthObjByPriceDate(Integer priceYear);

	/**
	 *
	 *  新增年的金价
	 *
	 * @author TianBin
	 * @param priceYear  年
	 * @param price   价格
	 * @return
	 * @since JDK 1.8
	 */
	int saveGoldStatisticsYearPrice(Integer priceYear,BigDecimal price)  throws ServiceException;

}
  
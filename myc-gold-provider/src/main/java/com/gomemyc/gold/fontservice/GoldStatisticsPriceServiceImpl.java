/** 
 * Project Name:myc-gold-provider 
 * File Name:GoldStatisticsPriceServiceImpl.java 
 * Package Name:com.gomemyc.gold.fontservice 
 * Date:2017年3月13日下午4:00:25 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.fontservice;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.gold.dao.GoldStatisticsDayPriceDao;
import com.gomemyc.gold.dao.GoldStatisticsHourPriceDao;
import com.gomemyc.gold.dao.GoldStatisticsMonthPriceDao;
import com.gomemyc.gold.dao.GoldStatisticsYearPriceDao;
import com.gomemyc.gold.dto.GoldStatisticsDayPriceDTO;
import com.gomemyc.gold.dto.GoldStatisticsHourPriceDTO;
import com.gomemyc.gold.dto.GoldStatisticsMonthPriceDTO;
import com.gomemyc.gold.entity.GoldStatisticsDayPrice;
import com.gomemyc.gold.entity.GoldStatisticsHourPrice;
import com.gomemyc.gold.entity.GoldStatisticsMonthPrice;
import com.gomemyc.gold.entity.GoldStatisticsYearPrice;
import com.gomemyc.gold.service.GoldStatisticsPriceService;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;

/**
 * ClassName:GoldStatisticsPriceServiceImpl <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月13日 下午4:00:25 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service(timeout=6000)
public class GoldStatisticsPriceServiceImpl implements GoldStatisticsPriceService {


	private static final Logger logger = LoggerFactory.getLogger(GoldStatisticsPriceServiceImpl.class);
	
	

    @Autowired
    private  GoldStatisticsHourPriceDao goldStatisticsHourPriceDao;

    @Autowired
    private  GoldStatisticsDayPriceDao goldStatisticsDayPriceDao;

    @Autowired
    private  GoldStatisticsMonthPriceDao goldStatisticsMonthPriceDao;
    
    @Autowired
    private  GoldStatisticsYearPriceDao goldStatisticsYearPriceDao;



    /**
     *
     * 新增每个小时的金价
     *
     * @author TianBin
     * @param priceDate  金价日期
     * @param price      金价
     * @param priceHour  金价小时
     * @return
     * @since JDK 1.8
     */
    public int saveGoldStatisticsHourPrice(Date priceDate, Integer priceHour, BigDecimal price) throws ServiceException {
        return goldStatisticsHourPriceDao.insert(new GoldStatisticsHourPrice(priceDate, priceHour, price, new Date()));
    }

    /**
     *
     * 统计小时的金价
     *
     * @author TianBin
     * @param currentDate  日期
     * @param page  第几页
     * @param pageSize 每页多少条
     * @return
     * @since JDK 1.8
     */
	public Page selectHourPriceList(Date currentDate, int page, int pageSize) throws ServiceException {

        if(page < 1 ||  pageSize < 1)
			logger.info("selectHourPriceList  in  GoldStatisticsPriceServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
        Date paramDate=currentDate==null?new Date():currentDate;
        int totalCount=goldStatisticsHourPriceDao.selectCountBeforePriceDate(paramDate);
        List<GoldStatisticsHourPrice> list=goldStatisticsHourPriceDao.selectListPageBeforePriceDate(paramDate,(page-1) * pageSize,pageSize);
        logger.info("selectHourPriceList in GoldStatisticsPriceServiceImpl ,the result [paramDate={}] [totalCount={}],[list={}]",paramDate,totalCount,list);
        return new Page(BeanMapper.mapList(list, GoldStatisticsHourPrice.class),page,pageSize,(long) totalCount);
    }


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
    public Page selectDayPriceList(Date currentDate,int page,int pageSize){

        if(page < 1 ||  pageSize < 1)
			logger.info("selectDayPriceList  in  GoldStatisticsPriceServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
        Date paramDate=currentDate==null?new Date():currentDate;
        int totalCount=goldStatisticsDayPriceDao.selectCountBeforePriceDate(paramDate);
        List<GoldStatisticsDayPrice> list=goldStatisticsDayPriceDao.selectListPageBeforePriceDate(paramDate,(page-1) * pageSize,pageSize);
        return  new Page(BeanMapper.mapList(list, GoldStatisticsDayPrice.class),page,pageSize,(long) totalCount);
    }
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
    public int saveGoldStatisticsDayPrice(Date priceDate,BigDecimal price){
        return goldStatisticsDayPriceDao.insert(new GoldStatisticsDayPrice(priceDate,price,new Date()));
    }


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
    public int saveGoldStatisticsMonthPrice(Integer priceYears,Integer priceMonth,BigDecimal price){
        return goldStatisticsMonthPriceDao.insert(new GoldStatisticsMonthPrice(priceYears,priceMonth,price,new Date()));
    }


    /**
     *
     * 统计月的金价
     *
     * @author TianBin
     * @param currentDate  日期
     * @param page  第几页
     * @param pageSize 每页多少条
     * @return
     * @since JDK 1.8
     */
    @Override
    public Page selectMonthPriceList(Date currentDate, int page, int pageSize) {
        if(page < 1 ||  pageSize < 1)
			logger.info("selectMonthPriceList  in  GoldStatisticsPriceServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
        Date paramDate=currentDate==null?new Date():currentDate;
        int totalCount=goldStatisticsMonthPriceDao.selectCountBeforePriceDate(paramDate);
        List<GoldStatisticsMonthPrice> list=goldStatisticsMonthPriceDao.selectListPageBeforePriceDate(paramDate,(page-1) * pageSize,pageSize);
        return  new Page(BeanMapper.mapList(list, GoldStatisticsMonthPrice.class),page,pageSize,(long) totalCount);
    }

    /**
     *
     * 统计年的金价
     *
     * @author TianBin
     * @param currentDate  日期
     * @param page  第几页
     * @param pageSize 每页多少条
     * @return
     * @since JDK 1.8
     */
    @Override
    public Page selectYearPriceList(Date currentDate, int page, int pageSize) {
    	 if(page < 1 ||  pageSize < 1)
 			logger.info("selectMonthPriceList  in  GoldStatisticsPriceServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
    	List<GoldStatisticsYearPrice> list=goldStatisticsYearPriceDao.selectListPage((page-1) * pageSize,pageSize);
    	int totalCount=goldStatisticsYearPriceDao.selectCount();
        return new Page(BeanMapper.mapList(list, GoldStatisticsYearPrice.class),page,pageSize,(long)totalCount);
        
        
    } 
    

    /**
	 *
	 * 查询小时的金价,进行日的金价统计
	 *
	 * @author TianBin
	 * @param priceDate  日期
	 * @return
	 * @since JDK 1.8
	 */
	@Override
	public GoldStatisticsHourPriceDTO selectHourObjByPriceDate(Date priceDate) {
		GoldStatisticsHourPriceDTO goldStatisticsHourPriceDTO=new GoldStatisticsHourPriceDTO();
    	BeanMapper.copy(goldStatisticsHourPriceDao.selectHourObjByPriceDate(priceDate), goldStatisticsHourPriceDTO);
		return  goldStatisticsHourPriceDTO;
	}
	/**
	 *
	 * 查询日的金价列表,进行月的金价统计
	 *
	 * @author TianBin
	 * @param month  月份
	 * @return
	 * @since JDK 1.8
	 */
    @Override
    public GoldStatisticsDayPriceDTO selectDayObjByMonth(Integer month){
    	GoldStatisticsDayPriceDTO goldStatisticsDayPriceDTO=new GoldStatisticsDayPriceDTO();
    	BeanMapper.copy(goldStatisticsDayPriceDao.selectDayObjByMonth(month), goldStatisticsDayPriceDTO);
		return  goldStatisticsDayPriceDTO;
	} 

	/**
	 *
	 * 查询月的金价列表,进行年的金价统计
	 *
	 * @author TianBin
	 * @param priceYear  年
	 * @return
	 * @since JDK 1.8
	 */
    @Override
    public GoldStatisticsMonthPriceDTO selectMonthObjByPriceDate(Integer priceYear){
    	GoldStatisticsMonthPriceDTO goldStatisticsMonthPriceDTO=new GoldStatisticsMonthPriceDTO();
    	BeanMapper.copy(goldStatisticsMonthPriceDao.selectMonthObjByPriceYear(priceYear), goldStatisticsMonthPriceDTO);
		return  goldStatisticsMonthPriceDTO;
    }

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
    @Override
    public int saveGoldStatisticsYearPrice(Integer priceYear,BigDecimal price)  throws ServiceException{
    	  return  goldStatisticsYearPriceDao.insert(new GoldStatisticsYearPrice(priceYear,price,new Date()));
	}

}
  
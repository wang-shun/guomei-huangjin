/** 
 * Project Name:myc-gold-provider 
 * File Name:GoldStatisticsPriceServiceTest.java 
 * Package Name:com.gomemyc.provider 
 * Date:2017年3月17日下午3:51:08 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.provider;

import java.util.Date;
import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.service.GoldStatisticsPriceService;
import com.gomemyc.gold.util.DateUtil;

/** 
 * ClassName:GoldStatisticsPriceServiceTest <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月17日 下午3:51:08 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public class GoldStatisticsPriceServiceTest extends BaseFunctionalTestCase {

	
	

	     @Reference
	     GoldStatisticsPriceService gldStatisticsPriceService;




	    //小时统计数据查询
	    @Test
	    public  void testSelectHourPriceList(){
	        System.out.println(gldStatisticsPriceService.selectHourPriceList(new Date(),1,2));
	     }


		//天统计数据查询
		@Test
		public  void testSelectDayPriceList(){
			System.out.println(gldStatisticsPriceService.selectDayPriceList(new Date(),1,2));
		}


		//月统计数据查询
		@Test
		public  void testSelectMonthPriceList(){
			System.out.println(gldStatisticsPriceService.selectMonthPriceList(new Date(),1,2));
		}

		
		//根据小时数据统计天数据
		@Test
		public void testSelectHourObjByPriceDate(){
			System.out.println(gldStatisticsPriceService.selectHourObjByPriceDate(DateUtil.strToDateLong(new Date().toString())));
		}
		//根据天数据统计月数据
		@Test
		public void testSelectDayObjByPriceDate(){
		}
		
		
		//根据月数据统计月数据
		@Test
		public void testSelectMonthObjByPriceDate(){
		}
		
		
		
		
		


}
  
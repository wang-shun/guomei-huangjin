package com.gomemyc.provider;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.dto.GoldProductDetailsDTO;
import com.gomemyc.gold.service.GoldProductService;

/**
 *@ClassName:GoldProductsByCurrentTimeService.java 
 *@Description:
 *@author zhuyunpeng
 *@date 2017年4月17日
 */
public class GoldProductsByCurrentTimeService extends BaseFunctionalTestCase{
	
	
	@Reference
	GoldProductService goldProductService;
	
	@Test
	public void testGoldProductByCurrentTimeService(){
//		List<GoldProductDetailsDTO> list= goldProductService.findProductsByCurrentTime("2017-04-12 15:35:00");
//		System.out.println("testGoldProductByCurrentTimeService in GoldProductsByCurrentTimeService the list ="+list.toString());
	}
}

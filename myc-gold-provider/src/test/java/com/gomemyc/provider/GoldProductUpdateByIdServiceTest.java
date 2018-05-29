package com.gomemyc.provider;

import org.junit.Test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.service.GoldProductService;

/**
 *@ClassName:GoldProductUpdateByIdServiceTest.java 
 *@Description:
 *@author zhuyunpeng
 *@date 2017年4月17日
 */
public class GoldProductUpdateByIdServiceTest extends BaseFunctionalTestCase {

	@Reference
	GoldProductService goldProductService;
	
	@Test
	public void testGoldProductUpdateByIdService(){
		String productId = "GOLD-f6ab6da8-b2cc-41c5-8b02-f64dd87d71f7";
		int result = goldProductService.updateStatusById(productId, 3);
		System.out.println("testGoldProductUpdateByIdService in GoldProductUpdateByIdServiceTest the result ="+result);
	}
}

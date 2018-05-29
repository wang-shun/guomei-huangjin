package com.gomemyc.provider;


import java.util.List;

import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.common.page.Page;
import com.gomemyc.gold.dto.GoldProductDTO;
import com.gomemyc.gold.dto.GoldProductDetailsDTO;
import com.gomemyc.gold.dto.GoldQueryTimeProductDTO;
import com.gomemyc.gold.service.GoldProductService;


/**
 * 
 * @ClassName GoldProductServiceImpTest
 * @author liuqiangbin
 * @description: 
 * @date 2017年3月15日
 */
public class  GoldProductServiceTest extends BaseFunctionalTestCase{
	
	@Reference
	GoldProductService goldProductService;
	
	//3.1  首页产品列表 
	@Test
	public void testListPageIndexList(){
		Page<GoldProductDTO> page = goldProductService.listPageIndexList(1, 5);
		System.out.println(page.getContent().get(0).getAmount());
	}
	
	//3.2  产品详情 
	@Test
	public void testFindByProductId(){
		GoldProductDetailsDTO goldProductDetailsDTO = goldProductService.findInfoByProductId("003");
		System.out.println(goldProductDetailsDTO.toString());
	}
	
	//3.13 投资预下单产品详细 
	@Test
	public void testFindInfoByProductId(){
		GoldProductDetailsDTO goldProductDetailsDTO = goldProductService.findByProductId("003","test");
		System.out.println(goldProductDetailsDTO);
	}
	
	//查询所有产品信息
	@Test
	public void testQueryTimeProduct(){
		List<GoldQueryTimeProductDTO> queryTimeProduct = goldProductService.queryTimeProduct(0, 0);
		System.out.println(queryTimeProduct.toString());
	}

	@Test
	public void testFindIdAndCodeByClearTime(){
		List<GoldProductIdAndCodeDTO> goldProductIdAndCodeDTO = goldProductService.findIdAndCodeByClearTime("2017-05-01");
		System.out.println(goldProductIdAndCodeDTO.size());
	}
	
	@Test
	public void testSelectProductCountByCurrentTimeAndStatus(){
		Integer rows = goldProductService.selectProductCount();
		System.out.println(rows);
	}
	
}
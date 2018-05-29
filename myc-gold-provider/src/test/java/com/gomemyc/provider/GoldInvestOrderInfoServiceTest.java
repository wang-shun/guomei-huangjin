package com.gomemyc.provider;

import java.math.BigDecimal;

import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.dto.GoldInvestOrderInfoDTO;
import com.gomemyc.gold.service.GoldInvestOrderInfoService;

/**
 * GoldInvestOrderInfoServiceTest
 * Date:     2017年3月24日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
public class GoldInvestOrderInfoServiceTest extends BaseFunctionalTestCase{
	
	@Reference
	GoldInvestOrderInfoService goldInvestOrderInfoService;

	
	/**
	 * 
	 * 根据订单号,查询黄金投资订单详情表(测试用例)
	 * 
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月24日
	 */
	@Test
	public void testGoldInvestOrderInfoService(){
		GoldInvestOrderInfoDTO goldInvestOrderInfoDTO = goldInvestOrderInfoService.selectByReqNo("1234");
		System.out.println(goldInvestOrderInfoDTO.toString());
	}
	
	/**
	 * 
	 * 保存黄金投资订单详情 (测试用例)
	 * 
	 * @param reqNo  订单号
	 * @param amount  投资金额
	 * @param remainAmount  剩余支付金额
	 * @param balancePaidAmount  余额支付
	 * @param couponAmount  红包金额
	 * @param couponId  奖券Id
	 * @return  int  影响的行数
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月24日
	 */
	@Test
	public void testGoldInvestOrderInfoService1(){
		int saveGoldInvestOrderInfo = goldInvestOrderInfoService.saveGoldInvestOrderInfo("1234", new BigDecimal("8000.00"), new BigDecimal("8000.00"), new BigDecimal("8000.00"), new BigDecimal("8000.00"), "2345");
		System.out.println(saveGoldInvestOrderInfo);
	}
}
  
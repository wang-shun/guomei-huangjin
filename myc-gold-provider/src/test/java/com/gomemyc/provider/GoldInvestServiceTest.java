package com.gomemyc.provider;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.gomemyc.common.page.Page;
import com.gomemyc.gold.dto.FinishOrdersDTO;
import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.dto.GoldEarningsDTO;
import com.gomemyc.gold.dto.GoldInvestOrderDTO;
import com.gomemyc.gold.service.GoldInvestService;


/**
 * 
 * @ClassName GoldInvestServiceImpTest
 * @author liuqiangbin
 * @description: 
 * @date 2017年3月15日
 */
public class GoldInvestServiceTest extends BaseFunctionalTestCase{
	
	@Reference
	GoldInvestService goldInvestService;
	
	/**
	 * 
	 * 3.3用户预下单测试用例testPrePay
	 * 
	 * @param amount  投资金额
	 * @param userId  用户id
	 * @param productId  产品id
	 * @param couponId  红包id (选填)
	 * @return  goldInvestOrderDTO
	 * @since JDK 1.8
	 * @author liuqiangbin 
	 * @date 2017年3月15日
	 */
	@Test
	public void testPrePay(){
		GoldInvestOrderDTO goldInvestOrderDTO = goldInvestService.prePay( new BigDecimal(2400.00), "06D48A1B-59EC-4DC0-B6D0-7C1BCC4D551A", "GOLD-4ad3ceec-2426-4863-b7cd-89f0e8e963db", "");
		System.out.println(goldInvestOrderDTO.toString());
	}
	
	/**
	 * 
	 * 3.4用户确认下单测试用例testConfirm
	 * 
	 * @param userId  用户id
	 * @param reqNo  订单号
	 * @return  GoldInvestOrderDTO
	 * @since JDK 1.8
	 * @author liuqiangbin 
	 * @date 2017年3月15日
	 */
//	@Test
//	public void testConfirm(){
//		GoldInvestOrderDTO goldInvestOrderDTO = goldInvestService.confirm("test99", "8A015FF15B2862480015B015B286248BC00000");
//		System.out.println(goldInvestOrderDTO.toString());
//	}
//	
	/**
	 * 
	 * 3.4用户确认下单测试用例testConfirm
	 * 
	 * @param userId  用户id
	 * @param reqNo  订单号
	 * @return  GoldInvestOrderDTO
	 * @since JDK 1.8
	 * @author liuqiangbin 
	 * @date 2017年3月15日
	 */
	@Test
	public void testConfirm1(){
		
		String userId = "06D48A1B-59EC-4DC0-B6D0-7C1BCC4D551A";
		String reqNo = "8A01589D5B6F932D0015B015B6F932D2D00000";
		//用户确认下单(查询用户预下单信息，产品信息，产品详情信息)
		HashMap<String, Object> confirmBySelect = goldInvestService.confirmBySelect(userId, reqNo);
		//生成投资id
		String investId = (String)confirmBySelect.get("investId");
		
		//用户确认下单(调用黄金钱包接口，确认下单)
		boolean confirmByGoldResult = goldInvestService.confirmByGold(userId, reqNo, investId);
		if(!confirmByGoldResult)
		{
			goldInvestService.confirmUpdateStatus(userId, reqNo);
		}
		GoldInvestOrderDTO confirmSaveInvestOrder = goldInvestService.confirmSaveInvestOrder(userId, reqNo);
		System.out.println(confirmSaveInvestOrder.toString());
	}
	
	//3.5  查询产品购买记录 
	@Test
	public void testListPageByUserIdLoanId(){
//		List<GoldInvestOrderDTO> list = goldInvestService.listPageByUserIdLoanId(null, null, 1, 1);
//		System.out.println(list.toString());
	}
	
	//3.6  查询交易结果 
	@Test
	public void testFindResultByUserIdReqNo(){
		GoldInvestOrderDTO goldInvestOrderDTO = goldInvestService.findResultByUserIdReqNo(null, null);
		System.out.println(goldInvestOrderDTO.toString());
	}
	
	//3.7 统计黄金总克数 
	@Test
	public void testStatisticsGoldWeight(){
		BigDecimal statisticsGoldWeight = goldInvestService.statisticsGoldWeight("G101");
		System.out.println(statisticsGoldWeight);
	}
	
	//3.8 昨日收益 
	@Test
	public void testGetYesterdayEarnings(){
		GoldEarningsDTO goldEarningsDTO = goldInvestService.getYesterdayEarnings("no13");
		System.out.println(goldEarningsDTO.toString());
	}
	
	//3.9 历史收益 
	@Test
	public void testGetHistoryEarnings(){
		List<GoldEarningsDTO> list = goldInvestService.getHistoryEarnings("G101", 1, 1);
		System.out.println(list.toString());
	}
	
	//3.10 募集中 
	@Test
	public void testListPageCollectByUserId(){
		Page<FinishOrdersDTO> page = goldInvestService.listPageCollectByUserId("06D48A1B-59EC-4DC0-B6D0-7C1BCC4D551A", 0, 10);
		System.out.println(page.getContent().get(0).getId());
	}

	//ch
	@Test
	public void testGetResultByUserIdReqNo(){
		GoldInvestOrderDTO goldInvestOrderDTO = goldInvestService.getResultByUserIdReqNo("06D48A1B-59EC-4DC0-B6D0-7C1BCC4D551A","8A015C865B708BE20015B015B708BE29C00000");
		System.out.println(goldInvestOrderDTO.getBalancePaidAmount());
	}
	
	//3.11 收益中  
	@Test
	public void testListPageEarningsByUserId(){
//		Page<FinishOrdersDTO> page = goldInvestService.listPageEarningsByUserId(null);
//		System.out.println(page.toString());
	}
	
	//3.12 已完结 
	@Test
	public void testListPageClearedByUserId(){
//		Page<FinishOrdersDTO> page = goldInvestService.listPageClearedByUserId(null, 1, 1);
//		System.out.println(page.toString());
	}
	
	
}
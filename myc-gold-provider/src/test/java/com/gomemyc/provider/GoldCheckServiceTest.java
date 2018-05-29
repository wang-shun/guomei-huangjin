package com.gomemyc.provider;

import com.gomemyc.gold.service.*;
import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;

/**
 * 
 * @ClassName GoldCheckServiceTest
 * @author LiuQiangBin
 * @description:  对账文件测试用例
 * @date 2017年3月20日
 */
public class GoldCheckServiceTest extends BaseFunctionalTestCase{
	
	@Reference
	GoldDayInterestAccountCheckService goldDayInterestAccountCheckService;

	@Reference
	GoldDueSumAccountCheckUtilService goldDueSumAccountCheckUtilService;

	@Reference
	GoldDueAccountCheckUtilService goldDueAccountCheckUtilService;
	
	@Reference
	GoldDueAccountCheckService goldDueAccountCheckService;
	
	@Reference
	GoldDueSumAccountCheckService goldDueSumAccountCheckService;
	
	@Reference
	GoldInvestAccountCheckService goldInvestAccountCheckService;

	@Reference
	private GoldDayInterestAccountCheckUtilService goldDayInterestAccountCheckUtilService;
	//2.7.1  买定期金对账文件
	@Test
	public void checkAccountTimeGold(){
//		int saveInvestAccountCheck = goldInvestAccountCheckService.saveInvestAccountCheck("300702", "2017-03-26", 0, 0);
//		System.out.println(saveInvestAccountCheck);
	}
	
	//2.7.2定期金到期对账文件
	@Test
	public void checkExpireOrderGold(){
//		Integer saveDueAccountCheck = goldDueAccountCheckService.saveDueAccountCheck("300705", "2017-04-15", 0, 10);
//		System.out.println(saveDueAccountCheck);
	}
	
	//2.7.3每天利息对账文件(调用黄金钱包直接存入平台数据库)
	@Test
	public void checkDailyInterest(){
//		int saveDayInterestAccountCheck = goldDayInterestAccountCheckService.saveDayInterestAccountCheck("300702", "2017-03-26", 0, 0);
//		System.out.println(saveDayInterestAccountCheck);
	}
	
	//2.7.3每天利息对账文件(核对后存入平台数据库)
	@Test
	public void checkDailyInterestSelf(){
		int savePlatformDayInterestAccountCheck = goldDayInterestAccountCheckService.savePlatformDayInterestAccountCheck();
		System.out.println(savePlatformDayInterestAccountCheck);
	}
	
	//2.7.4定期到期利息汇总对账文件
	@Test
	public void checkSumInterest(){
//		Integer saveDueSumAccountCheck = goldDueSumAccountCheckService.saveDueSumAccountCheck("300702", 0, 0);
//		System.out.println(saveDueSumAccountCheck);
	}
	@Test
	public void savePlatformDayInterestAccountCheck(){
		Integer saveDueSumAccountCheck = goldDayInterestAccountCheckService.savePlatformDayInterestAccountCheck();
		System.out.println(saveDueSumAccountCheck);
	}
	@Test
	public void testGoldDayInterestAccountCheckUtil(){
		goldDayInterestAccountCheckUtilService.goldDayInterestAccount();
		System.out.println("===============================================================");
	}
	@Test
	public void testGoldDueAccountCheckUtilService(){
		goldDueAccountCheckUtilService.goldInvestAccountCheck();
		System.out.println("===============================================================");
	}
	@Test
	public void testGoldDueSumAccountCheckUtilService(){
		goldDueSumAccountCheckUtilService.goldDueSumAccountCheck();
		System.out.println("===============================================================");
	}

}
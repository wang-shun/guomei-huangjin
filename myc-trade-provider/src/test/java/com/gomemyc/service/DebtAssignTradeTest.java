package com.gomemyc.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.trade.service.DebtAssignTradeService;

public class DebtAssignTradeTest  extends BaseFunctionalTestCase {

	 @Reference
	 DebtAssignTradeService debtAssignTradeService;
	 @org.junit.Test
	 public void settlementDebtAssign(){
		 System.out.println("11");
		 debtAssignTradeService.settlementDebtAssign("dq-79850086-0af6-11e7-a905-0050569426e2");
	 }
}

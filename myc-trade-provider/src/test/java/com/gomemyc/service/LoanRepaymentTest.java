package com.gomemyc.service;

import org.junit.Test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.trade.dto.LoanRepaymentDTO;
import com.gomemyc.trade.service.LoanRepaymentService;

public class LoanRepaymentTest extends BaseFunctionalTestCase{

	@Reference
	private LoanRepaymentService loanRepaymentService;
	
	@Test
	public void findByLoanId(){
		LoanRepaymentDTO dto=loanRepaymentService.findByLoanId("8160e1bc-0859-11e7-a905-0050569426e2");
		System.out.println(dto.getId());
	}
}

package com.gomemyc.service;

import org.junit.Test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.service.InvestRepaymentService;

public class InvestRepaymentTest extends BaseFunctionalTestCase{

	@Reference
	InvestRepaymentService investRepaymentService;
	@Test
	public void findById(){
		InvestRepaymentDTO dto=investRepaymentService.findById("8A015D0A5ACAB4D00015A015ACAB4D03B00000");
		System.out.println(dto.getId());
	}
	@Test
	public void findByInvestId(){
		InvestRepaymentDTO dto=investRepaymentService.findByInvestId("8A015D0A5ACAA22A0015A015ACAA22A3400000");
		System.out.println(dto.getId());
	}
}

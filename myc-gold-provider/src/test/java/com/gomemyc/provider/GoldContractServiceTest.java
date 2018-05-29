package com.gomemyc.provider;

import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.dto.ContractDTO;
import com.gomemyc.gold.service.GoldContractService;

/**
 *@ClassName:GoldContractServiceTest.java 
 *@Description:
 *@author zhuyunpeng
 *@date 2017年4月10日
 */
public class GoldContractServiceTest extends BaseFunctionalTestCase {

	@Reference
	private GoldContractService goldContractService;
	
	
	@Test
	public void goldContractServiceTest(){
		String investId = "111";
		ContractDTO contractDTO = goldContractService.getContractDTOByInvestId(investId);
		System.out.println("ContractDTO===================="+contractDTO.toString());
	}
}

package com.gomemyc.gold.service;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.gold.dto.ContractDTO;

/**
 *@ClassName:GoldContractService.java 
 *@Description:
 *@author zhuyunpeng
 *@date 2017年4月10日
 */
public interface GoldContractService {

	
	/**
	 * 
	 * 国美黄金消费综合服务协议实体的构建及返回
	 * 
	 * @author zhuyunpeng
	 * @date 2017年4月10日
	 * @return Map<String, ContractDTO>
	 *@ServiceException
	 *                95000:黄金产品表中查询不到该对象
	 *                95001:用户不存在
	 * @since JDK 1.8
	 */
	 ContractDTO getContractDTOByInvestId(String investId) throws ServiceException;
}

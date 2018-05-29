package com.gomemyc.trade.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.service.RepaymentService;

@Service(timeout = 10000)
public class RepaymentServiceImpl implements RepaymentService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RepaymentService RepaymentBridge;
	
	@Override
	public Boolean saveReceivedPayments(String productId) {
		if(StringUtils.isBlank(productId))
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(), ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
		
		try {
			return RepaymentBridge.saveReceivedPayments(productId);
		} catch (Exception e2) {
			logger.error("saveReceivedPayments Exception error, exception:{}", e2);
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e2.getMessage());
		}
	}

	@Override
	public Boolean saveRepayments(String productId) {
		if(StringUtils.isBlank(productId))
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(), ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
		
		try {
			return RepaymentBridge.saveRepayments(productId);
		} catch (ServiceException e) {
			logger.error("saveRepayments ServiceException error, exception:{}", e);
			throw e;
		} catch (Exception e2) {
			logger.error("saveRepayments Exception error, exception:{}", e2);
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e2.getMessage());
		}
	}

	@Override
	public Boolean settleLoan(String productId) throws ServiceException{
		if(StringUtils.isBlank(productId))
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(), ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
		
		try {
			return RepaymentBridge.settleLoan(productId);
		} catch (ServiceException e) {
			logger.error("settleLoan ServiceException error, exception:{}", e);
			throw e;
		} catch (Exception e2) {
			logger.error("settleLoan Exception error, exception:{}", e2);
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e2.getMessage());
		}
		
	}
	/**
	 * @Title 黄金回款计划保存
	 * @param productId 产品编号
	 * @param investRepaymentList 回款计划
	 * @author tianbin
	 * @date 2017年4月03日
	 */
	public Boolean saveGoldInvestPayments(String productId,List<InvestRepaymentDTO> investRepaymentList){
		return RepaymentBridge.saveGoldInvestPayments(productId, investRepaymentList);
	}
	

	@Override
	public Boolean goldSettleLoan(String productId) throws ServiceException {
		if(StringUtils.isBlank(productId))
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(), ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
		
		try {
			return RepaymentBridge.goldSettleLoan(productId);
		} catch (Exception e2) {
			logger.error("settleLoan Exception error, exception:{}", e2);
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e2.getMessage());
		}
	}

	@Override
	public Boolean updateGoldInvestPayments(String productId, List<InvestRepaymentDTO> investRepaymentList) {
		return RepaymentBridge.updateGoldInvestPayments(productId, investRepaymentList);
	}
}

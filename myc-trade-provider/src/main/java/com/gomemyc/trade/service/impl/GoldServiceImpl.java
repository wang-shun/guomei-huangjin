/** 
 * Project Name:myc-trade-provider 
 * File Name:GoldServiceImpl.java 
 * Package Name:com.gomemyc.trade.service.impl 
 * Date:2017年4月3日下午5:51:47 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.trade.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.InvestService;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.PayAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.BatchAccountErrorDataDto;
import com.gomemyc.agent.resp.BatchProdTenderDataDto;
import com.gomemyc.agent.resp.BatchProdTenderRSDto;
import com.gomemyc.agent.resp.BatchProdTenderResultDto;
import com.gomemyc.agent.resp.OrderQueryResultDto;
import com.gomemyc.agent.util.DateUtil;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.gold.service.GoldInvestService;
import com.gomemyc.trade.constant.OrderResultConstant;
import com.gomemyc.trade.constant.SyncResultConstant;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dto.GoldInvestOrderDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.GoldService;
import com.gomemyc.trade.util.GoldInfoCode;
import com.gomemyc.user.api.UserService;
import com.gomemyc.util.BeanMapper;

/** 
 * ClassName:GoldServiceImpl <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年4月3日 下午5:51:47 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service(timeout=3000)
public class GoldServiceImpl implements GoldService{

	private static final Logger logger = LoggerFactory.getLogger(GoldServiceImpl.class);

	@Reference
	private GoldInvestService goldInvestService;
	
	@Reference
	private UserService userService;
	
	@Reference
	private AccountService accountService;
	
	@Reference
	private InvestService investService;
	
	@Reference
	private CouponService couponService;
	
    @Autowired
    private InvestDao investDao;
    
    @Autowired
    private AgentConfig agentConfig;
	
	/**
	 * 
	 * 用户确认下单
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @return GoldInvestOrderDTO
	 * @ServiceException
	 *                30000:参数错误
	 *                30001:预下单信息不存在
	 *                30005:确认下单信息不存在
	 *                50000:操作失败，请重试 
	 *                50002:确认下单失败
	 *                50003:确认下单处理中
	 *                60000:产品不存在
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	@Override
	public GoldInvestOrderDTO confirm(String uid, String reqNo) throws ServiceException {
		logger.info("confirm  in  GoldServiceImpl ,the param [uid={}],[reqNo={}]",uid,reqNo);

		
		//用户确认下单(查询用户预下单信息，产品信息，产品详情信息)
		//-------------------------------start-------------------------------
		HashMap<String, Object> confirmBySelectMap = null;
		try {
			confirmBySelectMap = goldInvestService.confirmBySelect(uid, reqNo);
			logger.info("confirm  in  GoldServiceImpl ,the param [confirmBySelectMap={}]",confirmBySelectMap);
		} catch (ServiceException e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError is 用户确认下单(查询用户预下单信息，产品信息，产品详情信息)错误"); 
			throw e;
		} catch (Exception e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError is [用户确认下单(查询用户预下单信息，产品信息，产品详情信息)]");
    		throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		}
		//用户确认下单(查询用户预下单信息，产品信息，产品详情信息)
		//--------------------------------end-------------------------------
		
		//调用账户接口，查询用户账户余额
		//-------------------------------start-------------------------------
		BigDecimal amount = (BigDecimal)confirmBySelectMap.get("amount");
		AccountDTO accountDTO = null;
		try {
			accountDTO = accountService.getByUserId(uid);
			logger.info("confirm  in  GoldServiceImpl ,the param [accountDTO={}]",accountDTO);
			if(accountDTO == null)
				throw new ServiceException(GoldInfoCode.ACCOUNT_NOT_OPEN.getStatus(), GoldInfoCode.ACCOUNT_NOT_OPEN.getMsg());
		    logger.info("confirm  in  GoldServiceImpl ,the param [userAccountAmount={}],[amount={}]",accountDTO.getTotalAmount(),amount);
			if(accountDTO.getTotalAmount().compareTo(amount) < 0)
				//确认下单失败(用户余额不足)
				throw new ServiceException(GoldInfoCode.ACCOUNT_BALANCE_NOT_ENOUGH.getStatus(), GoldInfoCode.ACCOUNT_BALANCE_NOT_ENOUGH.getMsg());
		} catch (ServiceException e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError is 账户异常"); 
			throw e;
		}  catch (Exception e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError is [accountError]");
			throw new ServiceException(GoldInfoCode.ACCOUNT_ERROR.getStatus(), GoldInfoCode.ACCOUNT_ERROR.getMsg());
		}
		//调用账户接口，查询用户账户余额
		//--------------------------------end-------------------------------
		
		
		//调用投资服务，将用户账户金额冻结
		//-------------------------------start-------------------------------
		//投资id
		String investId = (String)confirmBySelectMap.get("investId");
		String loanId = (String)confirmBySelectMap.get("loanId");
		BigDecimal couponAmount = (BigDecimal)confirmBySelectMap.get("couponAmount");
		String localFreezeId = null;
		try {
			localFreezeId = investService.investFreeze(investId, uid, loanId, amount.subtract(couponAmount), couponAmount);
		    logger.info("confirm  in  GoldServiceImpl ,the param [localFreezeId={}]",localFreezeId);
		} catch (ServiceException e) {
		    logger.info("confirm  in  GoldServiceImpl ,the catchError is [本地冻结用户金额错误]");
			throw new ServiceException(GoldInfoCode.LOCAL_FREEZE_AMOUNT_FAIL.getStatus(), GoldInfoCode.LOCAL_FREEZE_AMOUNT_FAIL.getMsg());
		}
        if (StringUtils.isBlank(localFreezeId)) 
			throw new ServiceException(GoldInfoCode.LOCAL_FREEZE_AMOUNT_FAIL.getStatus(), GoldInfoCode.LOCAL_FREEZE_AMOUNT_FAIL.getMsg());
        //调用投资服务，将用户账户金额冻结
		//--------------------------------end-------------------------------
        
        
        // 记录本地冻结流水号，更新投资状态
		//-------------------------------start-------------------------------
        int updateLocalFrozenNo = investDao.updateLocalFrozenNo(investId, localFreezeId);
		logger.info("confirm  in  GoldServiceImpl ,the param [updateLocalFrozenNo={}]",updateLocalFrozenNo);
        if (1 != updateLocalFrozenNo) 
    		throw new ServiceException(GoldInfoCode.INVEST_UPDATE_LOCAL_FROZEN_NO_FAIL.getStatus(), GoldInfoCode.INVEST_UPDATE_LOCAL_FROZEN_NO_FAIL.getMsg());
    	int updateInvestStatus = investDao.updateInvestStatus(investId, InvestStatus.LOCAL_FROZEN_SUCCESS);
		logger.info("confirm  in  GoldServiceImpl ,the param [updateInvestStatus={}]",updateInvestStatus);
    	if (1 != updateInvestStatus)
    		throw new ServiceException(GoldInfoCode.INVEST_UPDATE_FROZEN_FAILED.getStatus(), GoldInfoCode.INVEST_UPDATE_FROZEN_FAILED.getMsg());
    	// 记录本地冻结流水号，更新投资状态
		//--------------------------------end-------------------------------

		//用户确认下单(调用黄金钱包接口，确认下单)
		//-------------------------------start-------------------------------
		boolean confirmByGoldResult = false;
		try {
			confirmByGoldResult = goldInvestService.confirmByGold(uid, reqNo, investId);
			logger.info("confirm  in  GoldServiceImpl ,the param [confirmByGoldResult={}]",confirmByGoldResult);
		} catch (ServiceException e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError 用户确认下单(调用黄金钱包接口，确认下单)错误"); 
			throw e;
		} catch (Exception e) {
		    logger.info("confirm  in  GoldServiceImpl ,the catchError is [用户确认下单(调用黄金钱包接口，确认下单)异常]");
    		throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		}
		if(!confirmByGoldResult)
	        //确认下单失败，交由定时器跑批处理(解冻红包金额，解冻用户投资金额，更新预下单下单状态)
    		throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		//用户确认下单(调用黄金钱包接口，确认下单)
		//--------------------------------end-------------------------------
		 
		// 确认下单成功，调用北京银行投资接口，将用户的真实账户中的钱放入标的账户 
		//-------------------------------start-------------------------------
        Invest invest = investDao.findById(investId);
		logger.info("confirm  in  GoldServiceImpl ,the param [invest={}]",invest);
        // 没有生成投资记录
        if (null == invest)
    		throw new ServiceException(GoldInfoCode.INVEST_NOT_EXIST.getStatus(), GoldInfoCode.INVEST_NOT_EXIST.getMsg());
		logger.info("confirm  in  GoldServiceImpl ,the param [investStatus={}]",invest.getStatus());
		try {
			// 只有本地冻结和北京银行冻结成功的投资记录才能同步北京银行
			if (!InvestStatus.LOCAL_FROZEN_SUCCESS.equals(invest.getStatus()) &&
			    !InvestStatus.BJ_SYN_SUCCESS.equals(invest.getStatus()))
				throw new ServiceException(GoldInfoCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getStatus(), GoldInfoCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getMsg());
			// 判断是否已和北京银行同步
			//-------------------------------start-------------------------------
			if (InvestStatus.LOCAL_FROZEN_SUCCESS.equals(invest.getStatus())) {
				// 调用北京银行投资接口
				//-------------------------------start-------------------------------
			    Date transDate = null;
				BatchProdTenderResultDto result = null;
				try {
					LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);   // 标的发标接口
					transDate = new Date();
					result = loanAgent.prodBatchBidd(agentConfig.getPlatNo(), // 平台编号
					                                                          UUIDGenerator.generate(), // 批量订单号
					                                                          DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
					                                                          DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间, 
					                                                          1, 
					                                                          invest.getLoanId(), 
					                                                          Arrays.asList(
					                                                                  new BatchProdTenderDataDto(invest.getId(), // 明细单号
					                                                                                             accountDTO.getPlatcust(), 
					                                                                                             invest.getAmount(), // 交易金额
					                                                                                             BigDecimal.ZERO, // 体验金金额
					                                                                                             couponAmount, // 抵现券金额
					                                                                                             invest.getAmount().subtract(couponAmount), // 自费金额
					                                                                                             null,// 加息券加息 
					                                                                                             DictionaryEnum.SUBJECTPRIORITY0, // 可提优先
					                                                                                             null, // 手续费入账平台
					                                                                                             null, // 手续费金额
					                                                                                             null)  // 备注信息
					                                                                  ).toArray(new BatchProdTenderDataDto[1]));
				} catch (Exception e) {
					logger.info("confirm  in  GoldServiceImpl ,the catchError [调用北京银行批量投标接口失败]");
			   		throw new ServiceException(GoldInfoCode.INVEST_LOAN_INVOKE_BJ_FAIL.getStatus(), GoldInfoCode.INVEST_LOAN_INVOKE_BJ_FAIL.getMsg());
				}
				logger.info("confirm  in  GoldServiceImpl ,the param [result={}]",result);
			    if (null == result) {
			   		throw new ServiceException(GoldInfoCode.INVEST_INVOKE_BJ_INVEST_RESULT_NULL.getStatus(), GoldInfoCode.INVEST_INVOKE_BJ_INVEST_RESULT_NULL.getMsg());
			   	}
				//-------------------------------end-------------------------------
			    
			    
			    // 订单受理成功
				//-------------------------------start-------------------------------
			    if (SyncResultConstant.SUCCESS_CODE.equals(result.getRecode())) {
			    	
			        // 获取成功订单
					//-------------------------------start-------------------------------
			        List<BatchProdTenderRSDto> batchProdTenderRSDtos = result.getSuccessData();
					logger.info("confirm  in  GoldServiceImpl ,the param [ ListBatchProdTenderRSDto={}]",batchProdTenderRSDtos);
			        if (null != batchProdTenderRSDtos && !batchProdTenderRSDtos.isEmpty()) {
			            for(BatchProdTenderRSDto batchProdTenderRSDto : batchProdTenderRSDtos){
			                if (invest.getId().equals(batchProdTenderRSDto.getDetailNo())) {
			            		logger.info("confirm  in  GoldServiceImpl ,the param [ batchProdTenderRSDto={}]",batchProdTenderRSDto);
			                    // 更新订单状态
			                    if (1 != investDao.updateInvestStatus(invest.getId(), InvestStatus.BJ_SYN_SUCCESS))
			                   		throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getStatus(), GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getMsg());
			                    // 北京银行投资成功
			                    logger.info("confirm  in  GoldServiceImpl ,success to update bj sync status, the investId = {}", invest.getId());
			                }
			            }
			        }
			        // 获取成功订单
					//-------------------------------end-------------------------------
			        
			        
			        // 获取失败订单
					//-------------------------------start-------------------------------
			        List<BatchAccountErrorDataDto> batchAccountErrorDataDtos = result.getErrorData();
					logger.info("confirm  in  GoldServiceImpl ,the param [ batchAccountErrorDataDtos={}]",batchAccountErrorDataDtos);
			        if (null != batchAccountErrorDataDtos && !batchAccountErrorDataDtos.isEmpty()) {
			            for (BatchAccountErrorDataDto batchAccountErrorDataDto : batchAccountErrorDataDtos) {
			                if (invest.getId().equals(batchAccountErrorDataDto.getDetailNo())) {
			            		logger.info("confirm  in  GoldServiceImpl ,the param [ batchAccountErrorDataDto={}]",batchAccountErrorDataDto);
			                    
			            		
			            		// 订单重复
			            		//-------------------------------start-------------------------------
			                    if (SyncResultConstant.ORDER_REPEAT.equals(batchAccountErrorDataDto.getErrorNo())) {
			                        // 查询订单真实状态
			                        PayAgent payAgent = PayAgent.getInstance(agentConfig);  
			                        OrderQueryResultDto orderQueryResultDto = payAgent.queryOrder(agentConfig.getPlatNo(), 
			                                                                                      UUIDGenerator.generate(),
			                                                                                      DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
			                                                                                      DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间, 
			                                                                                      invest.getId());
			                		logger.info("confirm  in  GoldServiceImpl ,the param [ orderQueryResultDto={}]",orderQueryResultDto);
			                        if (null == orderQueryResultDto)
			                       		throw new ServiceException(GoldInfoCode.ORDER_QUERY_FAIL.getStatus(), GoldInfoCode.ORDER_QUERY_FAIL.getMsg());
			                        // 返回结果成功
			                        if (SyncResultConstant.SUCCESS_CODE.equals(orderQueryResultDto.getRecode())) {
			                            // 已同步成功
			                            if (null != orderQueryResultDto.getData() && 
			                            	OrderResultConstant.SUCCESS.equals(orderQueryResultDto.getData().getStatus())) {
			                                if (1 != investDao.updateInvestStatus(invest.getId(), InvestStatus.BJ_SYN_SUCCESS))
			                               		throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getStatus(), GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getMsg());
			                                // 北京银行投资成功
			                                logger.info("confirm  in  GoldServiceImpl ,success to update bj sync status, the investId = {}", invest.getId());
			                            }
			                            // 已同步失败
			                            if (null != orderQueryResultDto.getData() &&
			                                OrderResultConstant.FAIL.equals(orderQueryResultDto.getData().getStatus())) {
			                                logger.info("confirm  in  GoldServiceImpl ,fali to sync invest, the investId = {}, the result = {}", invest.getId(), orderQueryResultDto.getRemsg());
			                                // 更新订单状态
			                                if(1 != investDao.updateInvestStatus(invest.getId(), InvestStatus.SYNC_FAILED))
                                                throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getStatus(), GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getMsg());
			                                // 北京银行投资失败
			                            	throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_INVOKE_FAIL.getStatus(), GoldInfoCode.INVEST_SUCCESS_INVOKE_FAIL.getMsg());
			                            }
			                        }
			                    // 其余情况默认投资失败    
			                    }else{
			                        logger.info("fali to sync invest, the investId = {}, the result = {}", invest.getId(), batchAccountErrorDataDto.getErrorInfo());
			                        // 更新订单状态
			                        if(1 != investDao.updateInvestStatus(invest.getId(), InvestStatus.SYNC_FAILED))
                                        throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getStatus(), GoldInfoCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getMsg());
			                        // 北京银行投资失败
			                   		throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_INVOKE_FAIL.getStatus(), GoldInfoCode.INVEST_SUCCESS_INVOKE_FAIL.getMsg());
			                   		}
			                    // 订单重复
				                //-------------------------------end-------------------------------
			                    
			                    
			                    }
			                }
			            }
			        // 获取失败订单
			        //-------------------------------end-------------------------------
			        
			        
			        }
			    // 订单受理成功
			    //-------------------------------end-------------------------------
			    
			    
			    }
			// 调用北京银行投资接口
			//-------------------------------end-------------------------------
			
			
			// 北京银行同步成功
			//-------------------------------start-------------------------------
			invest = investDao.findById(invest.getId());
			if (InvestStatus.BJ_SYN_SUCCESS.equals(invest.getStatus())) {
			    // 同步本地投资接口
			    String localInvestNo = null;
				try {
					localInvestNo = investService.investSuccess(invest.getLocalFreezeNo(), invest.getId());
				    logger.info("confirm  in  GoldServiceImpl ,the param [ localInvestNo={}]",localInvestNo);
				} catch (Exception e) {
			   		throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL.getStatus(), GoldInfoCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL.getMsg());
				}
			    if (StringUtils.isBlank(localInvestNo))
			   		throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL.getStatus(), GoldInfoCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL.getMsg());
			    // 本地投资成功即投资成功
			    if (1 != investDao.updateLocalDfCodeAndStatus(invest.getId(), 
			                                                  localInvestNo, 
			                                                  InvestStatus.SUCCESS)) {
			        logger.info("confirm  in  GoldServiceImpl ,fail to invest success sync, fail to update invest status, the investId = {}", invest.getId());
			  		throw new ServiceException(GoldInfoCode.INVEST_SUCCESS_UPDATE_INVEST_STATUS_FAIL.getStatus(), GoldInfoCode.INVEST_SUCCESS_UPDATE_INVEST_STATUS_FAIL.getMsg());
			    }
			}
			//北京银行同步成功
			//-------------------------------end-------------------------------
			
		} catch (ServiceException e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError 确认下单成功，调用北京银行投资接口，将用户的真实账户中的钱放入标的账户 ---错误"); 
			throw e;
		} catch (Exception e) {
	        logger.info("confirm  in  GoldServiceImpl , the catchError is [确认下单成功，调用北京银行投资接口，将用户的真实账户中的钱放入标的账户错误 ]");
	  		throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		}
		// 确认下单成功，调用北京银行投资接口，将用户的真实账户中的钱放入标的账户 
		//-------------------------------end-------------------------------
		
		
		//调用用户确认下单(更新订单状态，保存订单信息)
		//-------------------------------start-------------------------------
		com.gomemyc.gold.dto.GoldInvestOrderDTO confirmSaveInvestOrder = null;
		try {
			confirmSaveInvestOrder = goldInvestService.confirmSaveInvestOrder(uid, reqNo);
            logger.info("confirm  in  GoldServiceImpl ,the param [ confirmSaveInvestOrder={}]",confirmSaveInvestOrder);
		} catch (ServiceException e) {
			logger.info("confirm  in  GoldServiceImpl ,the catchError is [调用用户确认下单(更新订单状态，保存订单信息)]");
	  		throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		}
		//调用用户确认下单(更新订单状态，保存订单信息)
		//-------------------------------end-------------------------------
		
		
		GoldInvestOrderDTO goldInvestOrderDTO = new GoldInvestOrderDTO();
		BeanMapper.copy(confirmSaveInvestOrder, goldInvestOrderDTO);
		return goldInvestOrderDTO;
	}
}
  
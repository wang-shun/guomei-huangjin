package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
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
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.gold.constant.GoldOrderResultConstant;
import com.gomemyc.gold.constant.GoldSyncResultConstant;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dao.GoldInvestOrderInfoDao;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.entity.GoldInvestOrderInfo;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.service.GoldInvestOrderAndAccountService;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.wallet.resp.QueryBuyTimeOrderResultDto;
import com.gomemyc.wallet.walletinter.GoldQueryWallet;

/** 
 * ClassName:GoldInvestOrderAndAccountServiceImpl
 * Date:     2017年3月28日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
@Service(timeout=6000)
public class GoldInvestOrderAndAccountServiceImpl implements GoldInvestOrderAndAccountService{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldInvestOrderAndAccountServiceImpl.class);

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;
	
	@Autowired
	private GoldWalletConstant goldWalletConstant;
	
	@Autowired
	private AgentConfig agentConfig;
	
	@Autowired
	private GoldInvestOrderInfoDao goldInvestOrderInfoDao;
	
	@Reference
	private InvestService investService;
	
	@Reference
	private CouponService couponService;
	
	@Reference
	private AccountService accountService;
	
	@Reference
	private com.gomemyc.trade.service.InvestService tradeInvestService;
	
	
	/**
	 * 
	 * 更新所有处理中订单状态，调用北京银行投资接口，将用户投资资金放入标的账户
	 * 
	 * @ServiceException
	 *                30004   处理中订单信息不存在
	 *                30005   确认下单信息不存在
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月28日
	 */
	@Override
	public void updateOrderStatusAndAmount() throws ServiceException {
		//查询所有处理中的确认下单订单
		List<GoldInvestOrder> listGoldInvestOrder = goldInvestOrderDao.listByOrderStatus(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus());
		logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [listGoldInvestOrder={}]",listGoldInvestOrder);
		//判断是否有处理中的订单信息
		if(listGoldInvestOrder == null || listGoldInvestOrder.size() == 0)
			return ;
		//遍历订单
		for(GoldInvestOrder goldInvestOrder : listGoldInvestOrder)
		{
			//根据黄金钱包订单号，查询订单信息
			QueryBuyTimeOrderResultDto queryBuyTimeOrder = GoldQueryWallet.queryBuyTimeOrder(goldWalletConstant.getVersion(), 
																							 goldWalletConstant.getMerchantCode(), 
																							 goldInvestOrder.getReqNo(),
																							 goldWalletConstant.getSign(), 
																							 goldWalletConstant.getIp());
			logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [queryBuyTimeOrder={}]",queryBuyTimeOrder);
			if(queryBuyTimeOrder == null || queryBuyTimeOrder.getData() == null)
				continue ;
			logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [orderStatus={}]",queryBuyTimeOrder.getData().getStatus());
			//如果订单状态是成功
			if(queryBuyTimeOrder.getData().getStatus().equals(String.valueOf(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus())))
			{
				
				//调用账户接口
				//-------------------------------start-------------------------------
				AccountDTO accountDTO = null;
				try {
					accountDTO = accountService.getByUserId(goldInvestOrder.getUserId());
					logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [accountDTO={}]",accountDTO);
					if(accountDTO == null)
						continue ;
				} catch (Exception e1) {
					logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the catchError is [accountError]");
					continue ;
				}
				//调用账户接口
				//--------------------------------end-------------------------------
				
				
				//查询投资订单详情表
				GoldInvestOrderInfo goldInvestOrderInfo = goldInvestOrderInfoDao.selectByPrimaryKey(goldInvestOrder.getProductId());
			    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [goldInvestOrderInfo={}]",goldInvestOrderInfo);
				if(goldInvestOrderInfo == null)
					continue ;

				
				// 确认下单成功，调用北京银行投资接口，将用户的真实账户中的钱放入标的账户 
				//-------------------------------start-------------------------------
		        InvestDTO investDTO = tradeInvestService.findById(goldInvestOrder.getInvestId());
				logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [investDTO={}]",investDTO);
		        // 没有生成投资记录
		        if (null == investDTO)
		        	continue ;
				try {
					logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [investDTO={}]",investDTO.getStatus());
					// 只有本地冻结和北京银行冻结成功的投资记录才能同步北京银行
					if (!InvestStatus.LOCAL_FROZEN_SUCCESS.equals(investDTO.getStatus()) &&
					    !InvestStatus.BJ_SYN_SUCCESS.equals(investDTO.getStatus()))
						continue ;
					// 判断是否已和北京银行同步
					//-------------------------------start-------------------------------
					if (InvestStatus.LOCAL_FROZEN_SUCCESS.equals(investDTO.getStatus())) {
						// 调用北京银行投资接口
						//-------------------------------start-------------------------------
					    LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);   // 标的发标接口
					    Date transDate = new Date();
					    BatchProdTenderResultDto result = loanAgent.prodBatchBidd(agentConfig.getPlatNo(), // 平台编号
					                                                              UUIDGenerator.generate(), // 批量订单号
					                                                              DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
					                                                              DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间, 
					                                                              1, 
					                                                              investDTO.getLoanId(), 
					                                                              Arrays.asList(
					                                                                      new BatchProdTenderDataDto(investDTO.getId(), // 明细单号
					                                                                                                 accountDTO.getPlatcust(), 
					                                                                                                 investDTO.getAmount(), // 交易金额
					                                                                                                 BigDecimal.ZERO, // 体验金金额
					                                                                                                 goldInvestOrderInfo.getCouponAmount(), // 抵现券金额
					                                                                                                 investDTO.getAmount().subtract(goldInvestOrderInfo.getCouponAmount()), // 自费金额
					                                                                                                 null,// 加息券加息 
					                                                                                                 DictionaryEnum.SUBJECTPRIORITY0, // 可提优先
					                                                                                                 null, // 手续费入账平台
					                                                                                                 null, // 手续费金额
					                                                                                                 null)  // 备注信息
					                                                                      ).toArray(new BatchProdTenderDataDto[1]));
						logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [result={}]",result);
					    if (null == result) 
					    	continue ;
						//-------------------------------end-------------------------------
					    
					    
					    // 订单受理成功
						//-------------------------------start-------------------------------
					    if (GoldSyncResultConstant.SUCCESS_CODE.equals(result.getRecode())) {
					    	
					        // 获取成功订单
							//-------------------------------start-------------------------------
					        List<BatchProdTenderRSDto> batchProdTenderRSDtos = result.getSuccessData();
							logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ ListBatchProdTenderRSDto={}]",batchProdTenderRSDtos);
					        if (null != batchProdTenderRSDtos && !batchProdTenderRSDtos.isEmpty()) {
					            for(BatchProdTenderRSDto batchProdTenderRSDto : batchProdTenderRSDtos){
					                if (investDTO.getId().equals(batchProdTenderRSDto.getDetailNo())) {
					            		logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ batchProdTenderRSDto={}]",batchProdTenderRSDto);
					                    // 更新订单状态
					                    if (1 != tradeInvestService.updateStatusById(investDTO.getId(), InvestStatus.BJ_SYN_SUCCESS.getIndex()))
					                    	continue ;
					                    // 北京银行投资成功
					                    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,success to update bj sync status, the investId = {}", investDTO.getId());
					                }
					            }
					        }
					        // 获取成功订单
							//-------------------------------end-------------------------------
					        
					        
					        // 获取失败订单
							//-------------------------------start-------------------------------
					        List<BatchAccountErrorDataDto> batchAccountErrorDataDtos = result.getErrorData();
							logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ batchAccountErrorDataDtos={}]",batchAccountErrorDataDtos);
					        if (null != batchAccountErrorDataDtos && !batchAccountErrorDataDtos.isEmpty()) {
					            for (BatchAccountErrorDataDto batchAccountErrorDataDto : batchAccountErrorDataDtos) {
					                if (investDTO.getId().equals(batchAccountErrorDataDto.getDetailNo())) {
					            		logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ batchAccountErrorDataDto={}]",batchAccountErrorDataDto);
					                    
					            		
					            		// 订单重复
					            		//-------------------------------start-------------------------------
					                    if (GoldSyncResultConstant.ORDER_REPEAT.equals(batchAccountErrorDataDto.getErrorNo())) {
					                        // 查询订单真实状态
					                        PayAgent payAgent = PayAgent.getInstance(agentConfig);  
					                        OrderQueryResultDto orderQueryResultDto = payAgent.queryOrder(agentConfig.getPlatNo(), 
					                                                                                      UUIDGenerator.generate(),
					                                                                                      DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
					                                                                                      DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间, 
					                                                                                      investDTO.getId());
					                		logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ orderQueryResultDto={}]",orderQueryResultDto);
					                        if (null == orderQueryResultDto)
					                        	continue ;
					                        // 返回结果成功
					                        if (GoldSyncResultConstant.SUCCESS_CODE.equals(orderQueryResultDto.getRecode())) {
					                            // 已同步成功
					                            if (null != orderQueryResultDto.getData() && 
					                            	GoldOrderResultConstant.SUCCESS.equals(orderQueryResultDto.getData().getStatus())) {
					                                if (1 != tradeInvestService.updateStatusById(investDTO.getId(), InvestStatus.BJ_SYN_SUCCESS.getIndex()))
					                                	continue ;
					                                // 北京银行投资成功
					                                logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,success to update bj sync status, the investId = {}", investDTO.getId());
					                            }
					                            // 已同步失败
					                            if (null != orderQueryResultDto.getData() &&
					                                GoldOrderResultConstant.FAIL.equals(orderQueryResultDto.getData().getStatus())) {
					                                logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,fali to sync invest, the investId = {}, the result = {}", investDTO.getId(), orderQueryResultDto.getRemsg());
					                                // 更新订单状态
					                                int updateInvestStatusSyncFailedRows = tradeInvestService.updateStatusById(investDTO.getId(), InvestStatus.SYNC_FAILED.getIndex());
					                                logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ updateInvestStatusSyncFailedRows={}]",updateInvestStatusSyncFailedRows);
					                                // 北京银行投资失败
					                                continue ;
					                            }
					                        }
					                    // 其余情况默认投资失败    
					                    }else{
					                        logger.info("updateOrderStatusAndAmount in GoldInvestOrderAndAccountServiceImpl,fali to sync invest, the investId = {}, the result = {}", investDTO.getId(), batchAccountErrorDataDto.getErrorInfo());
					                        // 更新订单状态
					                        int updateInvestStatusSyncFailedRows = tradeInvestService.updateStatusById(investDTO.getId(), InvestStatus.SYNC_FAILED.getIndex());
					                        logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ updateInvestStatusSyncFailedRows={}]",updateInvestStatusSyncFailedRows);
					                        // 北京银行投资失败
					                        continue ;
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
					investDTO = tradeInvestService.findById(goldInvestOrder.getUserId());
					if (InvestStatus.BJ_SYN_SUCCESS.equals(investDTO.getStatus())) {
					    // 同步本地投资接口
					    String localInvestNo = investService.investSuccess(investDTO.getLocalFreezeNo(), investDTO.getId());
					    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [ localInvestNo={}]",localInvestNo);
					    if (StringUtils.isBlank(localInvestNo))
					    	continue ;
					    // 本地投资成功即投资成功
					    if (1 != tradeInvestService.updateLocalDfCodeById(investDTO.getId(), localInvestNo)) {
					        logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,fail to invest success sync, fail to update invest status, the investId = {}", investDTO.getId());
					        continue ;
					    }
					}
					//北京银行同步成功
					//-------------------------------end-------------------------------
					
					
				} catch (ServiceException e) {
			        logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the catchError is UserbjBankInvestInterfaceError");
			        continue ;
				}
				// 确认下单成功，调用北京银行投资接口，将用户的真实账户中的钱放入标的账户 
				//-------------------------------end-------------------------------
				
				//更新确认下单订单状态为下单成功
				int updateRowsConfirmInvestOrder = goldInvestOrderDao.updateByUseridAndReqNo(goldInvestOrder.getUserId(), goldInvestOrder.getReqNo(), goldInvestOrder.getOrderType(), goldInvestOrder.getOrderStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
				logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [updateRowsConfirmInvestOrder={}]",updateRowsConfirmInvestOrder);
				//更新预下单的状态更改为预下单成功，下单成功
				Integer updateByUseridAndReqNo = goldInvestOrderDao.updateByUseridAndReqNo(goldInvestOrder.getUserId(), goldInvestOrder.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_SUCCESS.getStatus());
				logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [updateByUseridAndReqNo={}]",updateByUseridAndReqNo);

			}
			//如果订单状态是失败
			if(queryBuyTimeOrder.getData().getStatus().equals(String.valueOf(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_FAIL.getStatus())))
			{
				//取出红包Id
				InvestDTO investDTO = tradeInvestService.getInvestment(goldInvestOrder.getInvestId());
				String couponId = investDTO.getCouponPlacememtId();
				//如果有红包id，那么解冻红包
		        if(StringUtils.isNotBlank(couponId)){
		        	try {
		        		//调用红包服务，解冻红包
						CouponPlacement couponPlacement = couponService.findCouponPlacementbyId(null, couponId);
						logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [couponPlacement={}]",couponPlacement);
						//将红包状态改为可用
						couponPlacement.setStatus(CouponStatus.PLACED);
				        if (!couponService.updateCouponPlacement(null, couponPlacement))
						    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is unFreezeCouponError");
					} catch (Exception e) {
					    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is unFreezeCouponError");
					    continue ;
					}
		        }
		        String localFreezeNo = investDTO.getLocalFreezeNo();
		        if(StringUtils.isNotBlank(localFreezeNo)) {
					//调用投资服务，将用户账户金额解冻
					String unFreezeNo = null;
					try {
						unFreezeNo = investService.investUnfreezeAfterFreeze(goldInvestOrder.getInvestId(), localFreezeNo);
				        // 记录本地解冻流水号
				        Integer updateLocalFrozenNo = tradeInvestService.updateLocalDfCodeById(investDTO.getId(), unFreezeNo);
						logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [updateLocalFrozenNo={}]",updateLocalFrozenNo);
				        if (1 != updateLocalFrozenNo) {
						    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is updateUnFreezeNoError");
						    continue ;
				        }
				    	// 更新投资状态
				    	Integer updateInvestStatus = tradeInvestService.updateStatusById(investDTO.getId(), InvestStatus.INITIAL.getIndex());
						logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [updateInvestStatus={}]",updateInvestStatus);
				    	if (1 != updateInvestStatus){
				    		logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is updateInvestStatusError");
				    		continue ;
				    	}
					} catch (ServiceException e) {
					    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is unFreezeNoError");
					    continue ;
					}
				    if (StringUtils.isBlank(unFreezeNo)) {
					    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is unFreezeNoError");
					    continue ;
				    }
		        }
				//更新确认下单订单状态为下单失败
				goldInvestOrderDao.updateByUseridAndReqNo(goldInvestOrder.getUserId(), goldInvestOrder.getReqNo(), goldInvestOrder.getOrderType(), goldInvestOrder.getOrderStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_FAIL.getStatus());
				//更新预下单状态为预下单成功，确认下单失败
				goldInvestOrderDao.updateByUseridAndReqNo(goldInvestOrder.getUserId(), goldInvestOrder.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_ERROR.getStatus());
			}
		}
	}
}
  
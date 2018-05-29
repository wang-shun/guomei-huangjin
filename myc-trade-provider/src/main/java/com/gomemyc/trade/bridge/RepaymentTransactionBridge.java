package com.gomemyc.trade.bridge;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import com.dangdang.ddframe.rdb.sharding.hint.HintManagerHolder;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.dto.LoanAccountDTO;
import com.gomemyc.account.enums.RechargeApply;
import com.gomemyc.account.service.LoanAccountService;
import com.gomemyc.account.service.RechargeService;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.ProdFoundAbandonRPLDto;
import com.gomemyc.agent.resp.ProdFoundAbandonResultDto;
import com.gomemyc.agent.util.DateUtil;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.gold.dto.GoldProductsExtendDTO;
import com.gomemyc.invest.constant.TimeConstant;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dao.InvestRepaymentDao;
import com.gomemyc.trade.dao.LoanRepaymentDao;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.entity.LoanRepayment;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.enums.RepaymentStatus;

@Component("RepaymentTransactionBridge")
public class RepaymentTransactionBridge {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private InvestDao investDao;
	
	@Autowired
	private LoanRepaymentDao loanRepaymentDao;
	
	@Autowired
	private InvestRepaymentDao investRepaymentDao;
	
	@Autowired
	private AgentConfig agentConfig;
	
	@Reference
	private ProductRegularService productRegularService;
	
	@Reference
	private LoanAccountService loanAccountService;
	
	@Reference
	private LoanService loanService;
	
	@Reference
	private RechargeService rechargeService;
	
	@Reference
	private CouponService couponService;
	
	/**
	 * @Title 生成回款和还款计划状态(事务隔离)
	 * @param productId 产品编号
	 * @param productDTO 产品信息
	 * @param loanAccountDTO 标的账户信息
	 * @param userId 融资方id
	 * @author lifeifei
	 * @return 
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public LoanRepayment saveRepaymentRecode(String productId, ProductDTO productDTO, LoanAccountDTO loanAccountDTO,String userId){
		
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		logger.error("productid:{} settleLoan saveRepaymentRecode begining...", productId);
		/*************************************判断产品是否已满标或到期未满标**************************************************/
		
		if(productDTO.getStatus() != ProductStatus.FINISHED && 
				productDTO.getStatus() != ProductStatus.FAILED)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_NOT_FULL.getIndex(), ExceptionCode.LOAN_SETTLE_NOT_FULL.getErrMsg());
		
		/*************************************检验所有投资是否已成功**************************************************/
		
		Integer resultNum = investDao.checkInvestByProduct(productId, InvestStatus.LOCAL_FROZEN_SUCCESS, InvestStatus.BJ_SYN_SUCCESS);
		if(resultNum > 0)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getErrMsg());
		
		/*************************************校验标的账户的募集金额和投资总金额是否一致**************************************************/
		
		BigDecimal totalInvestAmount = investDao.findAmountListByProduct(productId,InvestStatus.SUCCESS);
		if(loanAccountDTO.getRaiseAmount().compareTo(totalInvestAmount) != 0)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getErrMsg());
		
		BigDecimal rate = new BigDecimal(productDTO.getRate()).add(new BigDecimal(productDTO.getPlusRate()))
	    		.divide(new BigDecimal(10000));//产品基础利率+加息利息
		Integer days = productDTO.getTotalDays();//产品期限（天）
		Date settleDate = new Date();
		
		/*************************************生成还款计划**************************************************/
		List<Invest> invests = investDao.findListByProduct(productId, InvestStatus.SUCCESS);
		if(invests == null || invests.isEmpty())
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(), ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
		
		//还款计划列表
		List<InvestRepayment> investRepayments = new ArrayList<>();
		InvestRepayment investRepayment;
		CouponPackage couponPackage;
		CouponPlacement couponPlacement  = null;
		String couponPlacememtId;
		BigDecimal totalCouponAmount = BigDecimal.ZERO;
		BigDecimal interestAmount = BigDecimal.ZERO;
		Invest invest;
		for(int i = 0 ; i < invests.size() ; i++){
			invest = invests.get(i);
			investRepayment = new InvestRepayment();
			investRepayment.setId(UUID.randomUUID().toString());
			investRepayment.setLoanId(productDTO.getLoanId());
			investRepayment.setInvestId(invest.getId());
			investRepayment.setPrincipalAmount(invest.getAmount());
			
			//基础利息 = (募集金额*基础利息/365)*标的期限
			BigDecimal interestRateAmount = invest.getAmount().multiply(
					rate).divide(
							new BigDecimal(TimeConstant.DAYS_PER_YEAR),20,BigDecimal.ROUND_HALF_UP).multiply(
									new BigDecimal(days));
			investRepayment.setInterestAmount(interestRateAmount.setScale(2, BigDecimal.ROUND_DOWN));
			investRepayment.setInterestPlusAmount(BigDecimal.ZERO);
			investRepayment.setUserId(invest.getUserId());
			//加息券待确定
			BigDecimal investCouponAmount = BigDecimal.ZERO;
			couponPlacememtId =  invest.getCouponPlacememtId();
			if(StringUtils.isNotBlank(couponPlacememtId)){
				try {
					couponPlacement = couponService.findCouponPlacementbyId(null, couponPlacememtId);
					if(couponPlacement != null){
						couponPackage = couponPlacement.getCouponPackage();
						if(CouponType.INTEREST == couponPackage.getType()){
							BigDecimal couponRate = new BigDecimal(couponPackage.getParValue()).divide(new BigDecimal(10000));
							//基础利息 = (募集金额*基础利息/365)*标的期限
							investCouponAmount = invest.getAmount().multiply(
								couponRate).divide(
										new BigDecimal(TimeConstant.DAYS_PER_YEAR),20,BigDecimal.ROUND_HALF_UP).multiply(
												new BigDecimal(couponPackage.getRateDays() == 0 ? days : couponPackage.getRateDays())); 
						}
					}
					
				} catch (Exception e) {
					logger.error("settleLoan saveRepaymentRecode findCouponPackagebyId error, exception:{}", e);
					throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
				}
			}
			investRepayment.setInterestCouponAmount(investCouponAmount.setScale(2, BigDecimal.ROUND_DOWN));
			investRepayment.setYears(invest.getYears());
			investRepayment.setMonths(invest.getMonths());
			investRepayment.setDays(invest.getDays());
			if(productDTO.getValueTime() != null){
				investRepayment.setDueDate(DateUtil.addDate(productDTO.getValueTime(), productDTO.getTotalDays()));
				investRepayment.setValueTime(productDTO.getValueTime());
			}else{
				investRepayment.setDueDate(DateUtil.addDate(settleDate, (productDTO.getTotalDays()+1)));
				investRepayment.setValueTime(DateUtil.addDate(settleDate, 1));
			}
			investRepayment.setStatus(RepaymentStatus.WAITACCOUNT);
			
			investRepayments.add(investRepayment);
			
			totalCouponAmount = totalCouponAmount.add(investCouponAmount.setScale(2, BigDecimal.ROUND_DOWN));
			interestAmount = interestAmount.add(interestRateAmount.setScale(2, BigDecimal.ROUND_DOWN));
			
		}
		if(investRepaymentDao.insertBatch(investRepayments) != invests.size())//插入还款计划
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
	
		/*************************************生成回款计划**************************************************/
		Date currentTime = new Date();
		LoanRepayment loanRepayment = new LoanRepayment();
		loanRepayment.setId(UUID.randomUUID().toString());
		loanRepayment.setLoanId(productDTO.getLoanId());
		loanRepayment.setCurrentPeriod(1);
		loanRepayment.setPrincipalAmount(loanAccountDTO.getRaiseAmount());
		//todo 确定计息是否保留两位小数
		loanRepayment.setInterestAmount(interestAmount.setScale(2, BigDecimal.ROUND_DOWN));
		loanRepayment.setInterestPlusAmount(BigDecimal.ZERO);
		// 应还加息券利息 (总的投资加息券利息之和)
		loanRepayment.setInterestCouponAmount(totalCouponAmount.setScale(2, BigDecimal.ROUND_DOWN));
		//应还款日
		if(productDTO.getValueTime() != null){
			loanRepayment.setDueDate(DateUtil.addDate(productDTO.getValueTime(), productDTO.getTotalDays()));
		}else{
			loanRepayment.setDueDate(DateUtil.addDate(settleDate, (productDTO.getTotalDays()+1)));
		}
		//回款状态：待北京银行还款
		loanRepayment.setStatus(RepaymentStatus.WAITBANK);
		loanRepayment.setUserId(userId);
		loanRepayment.setTimeCreatime(currentTime);
		
		if(loanRepaymentDao.insert(loanRepayment) != 1)//插入回款计划
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
		
		return loanRepaymentDao.findByLoanId(productDTO.getLoanId());
	}
	
	/**
	 * @Title 生成回款和还款计划状态-黄金产品(事务隔离)
	 * @param productId 产品编号
	 * @param productDTO 产品信息
	 * @param loanAccountDTO 标的账户信息
	 * @author lifeifei
	 * @return 
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public LoanRepayment saveRepaymentRecode(String productId, GoldProductsExtendDTO productDTO, LoanAccountDTO loanAccountDTO){
		
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		/*************************************判断产品是否已满标或到期未满标**************************************************/
		
		if(productDTO.getStatus() != ProductStatus.OPENED.getIndex() && 
				productDTO.getStatus() != ProductStatus.FINISHED.getIndex() && 
				productDTO.getStatus() != ProductStatus.FAILED.getIndex())
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_NOT_FULL.getIndex(), ExceptionCode.LOAN_SETTLE_NOT_FULL.getErrMsg());
		 
		/*************************************检验所有投资是否已成功**************************************************/
		
		Integer resultNum = investDao.checkInvestByProduct(productId, InvestStatus.INITIAL, InvestStatus.BJ_SYN_SUCCESS);
		if(resultNum > 0)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getErrMsg());
		
		/*************************************校验标的账户的募集金额和投资总金额是否一致**************************************************/
		
		BigDecimal totalInvestAmount = investDao.findAmountListByProduct(productId,InvestStatus.SUCCESS);
		if(loanAccountDTO.getRaiseAmount().compareTo(totalInvestAmount) != 0)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getErrMsg());
		
		Integer rate = (productDTO.getRate() + productDTO.getRatePlus())/10000;//产品基础利率+加息利息
		Integer days = productDTO.getTotalDays();//产品期限（天）
		Date settleDate = new Date();
		
		/*************************************生成还款计划**************************************************/
		List<Invest> invests = investDao.findListByProduct(productId, InvestStatus.SUCCESS);
		if(invests == null || invests.isEmpty())
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(), ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
		
		//还款计划列表
		List<InvestRepayment> investRepayments = new ArrayList<>();
		InvestRepayment investRepayment;
		CouponPackage couponPackage;
		CouponPlacement couponPlacement;
		String couponPlacememtId;
		BigDecimal totalCouponAmount = BigDecimal.ZERO;
		BigDecimal interestAmount = BigDecimal.ZERO;
		Invest invest;
		for(int i = 0 ; i < invests.size() ; i++){
			invest = invests.get(i);
			investRepayment = new InvestRepayment();
			investRepayment.setId(UUID.randomUUID().toString());
			investRepayment.setLoanId(productDTO.getLoanId());
			investRepayment.setInvestId(invest.getId());
			investRepayment.setPrincipalAmount(invest.getAmount());
			
			//基础利息 = (募集金额*基础利息/365)*标的期限
			BigDecimal interestRateAmount = invest.getAmount().multiply(
					new BigDecimal(rate)).divide(
							new BigDecimal(TimeConstant.DAYS_PER_YEAR),20,BigDecimal.ROUND_HALF_UP).multiply(
									new BigDecimal(days));
			investRepayment.setInterestAmount(interestRateAmount.setScale(2, BigDecimal.ROUND_DOWN));
			investRepayment.setInterestPlusAmount(BigDecimal.ZERO);
			//加息券待确定 
			BigDecimal investCouponAmount = BigDecimal.ZERO;
			couponPlacememtId =  invest.getCouponPlacememtId();
			if(StringUtils.isNotBlank(couponPlacememtId)){
				try {
					couponPlacement = couponService.findCouponPlacementbyId(null, couponPlacememtId);
					if(couponPlacement != null){
						couponPackage = couponPlacement.getCouponPackage();
						if(CouponType.INTEREST == couponPackage.getType()){
							BigDecimal couponRate = new BigDecimal(couponPackage.getParValue()).divide(new BigDecimal(10000));
							//基础利息 = (募集金额*基础利息/365)*标的期限
							investCouponAmount = invest.getAmount().multiply(
								couponRate).divide(
										new BigDecimal(TimeConstant.DAYS_PER_YEAR) ,20,BigDecimal.ROUND_HALF_UP).multiply(
												new BigDecimal(couponPackage.getRateDays() == 0 ? days : couponPackage.getRateDays()));
						}
					}
					
				} catch (Exception e) {
					logger.error("settleLoan saveRepaymentRecode findCouponPackagebyId error, exception:{}", e);
					throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e.getMessage());
				}
			}
			investRepayment.setInterestCouponAmount(investCouponAmount.setScale(2, BigDecimal.ROUND_DOWN));
			investRepayment.setYears(invest.getYears());
			investRepayment.setMonths(invest.getMonths());
			investRepayment.setDays(invest.getDays());
			if(productDTO.getValueTime() != null){
				investRepayment.setDueDate(DateUtil.addDate(productDTO.getValueTime(), productDTO.getTotalDays()));
				investRepayment.setValueTime(productDTO.getValueTime());
			}else{
				investRepayment.setDueDate(DateUtil.addDate(settleDate, (productDTO.getTotalDays()+1)));
				investRepayment.setValueTime(DateUtil.addDate(settleDate, 1));
			}
			investRepayment.setStatus(RepaymentStatus.WAITACCOUNT);
			
			investRepayments.add(investRepayment);
			
			totalCouponAmount = totalCouponAmount.add(investCouponAmount.setScale(2, BigDecimal.ROUND_DOWN));
			interestAmount = interestAmount.add(interestRateAmount.setScale(2, BigDecimal.ROUND_DOWN));
			
		}
		if(investRepaymentDao.insertBatch(investRepayments) != invests.size())//插入还款计划
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
	
		/*************************************生成回款计划**************************************************/
		Date currentTime = new Date();
		LoanRepayment loanRepayment = new LoanRepayment();
		loanRepayment.setId(UUID.randomUUID().toString());
		loanRepayment.setLoanId(productDTO.getLoanId());
		loanRepayment.setCurrentPeriod(1);
		loanRepayment.setPrincipalAmount(loanAccountDTO.getRaiseAmount());
		//todo 确定计息是否保留两位小数
		loanRepayment.setInterestAmount(interestAmount.setScale(2, BigDecimal.ROUND_DOWN));
		loanRepayment.setInterestPlusAmount(BigDecimal.ZERO);
		// 应还加息券利息 (总的投资加息券利息之和)
		loanRepayment.setInterestCouponAmount(totalCouponAmount.setScale(2, BigDecimal.ROUND_DOWN));
		//应还款日
		if(productDTO.getValueTime() != null){
			loanRepayment.setDueDate(DateUtil.addDate(productDTO.getValueTime(), productDTO.getTotalDays()));
		}else{
			loanRepayment.setDueDate(DateUtil.addDate(settleDate, (productDTO.getTotalDays()+1)));
		}
		//回款状态：待北京银行还款
		loanRepayment.setStatus(RepaymentStatus.WAITBANK);
		loanRepayment.setTimeCreatime(currentTime);
		
		if(loanRepaymentDao.insert(loanRepayment) != 1)//插入回款计划
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
		
		return loanRepaymentDao.findByLoanId(productDTO.getLoanId());
	}
	
	/**
	 * @Title 银行存管协议成标 （事务隔离）
	 * @param productId
	 * @param productRegularDTO
	 * @param loanRepayment
	 * @author lifeifei
	 * @return 
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public LoanRepayment settleWaitBank(String productId, ProductDTO productDTO, LoanRepayment loanRepayment){
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		logger.error("productid:{} settleLoan settleWaitBank begining...", productId);
		
		List<InvestRepayment> investRepayments = investRepaymentDao.findByLoanId(productDTO.getLoanId());
		if(investRepayments == null || investRepayments.isEmpty()){
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
		}
		
		//封装北京银行存管协议还款计划
		ProdFoundAbandonRPLDto[] prodFoundAbandonRPLDtos = new ProdFoundAbandonRPLDto[investRepayments.size()];
		InvestRepayment investRepayment;
		for(int i = 0 ; i < investRepayments.size() ; i++){
			investRepayment = investRepayments.get(i);
			//北京银行成标的还款计划
			//todo 确认还款金额
			prodFoundAbandonRPLDtos[i] = 
					new ProdFoundAbandonRPLDto(investRepayment.getPrincipalAmount().add(
							investRepayment.getInterestAmount()).add(
									investRepayment.getInterestPlusAmount()), BigDecimal.ZERO, 
							1, investRepayment.getDueDate());
		}
		Date currentTime = new Date();
			
		/***********************************调用存管协议成标接口****************************************************/
		LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		ProdFoundAbandonResultDto prodFoundAbandonResultDto;
		try {//4.3.2.标的成废
			prodFoundAbandonResultDto = 
					loanAgent.publishFoundAbandon(agentConfig.getPlatNo(), UUID.randomUUID().toString(), DateUtil.dateToStr(currentTime, DateUtil.DFS_yyyyMMdd), 
							sdf.format(currentTime), productDTO.getLoanId(), DictionaryEnum.WASTELABEL2, 
							null, null, prodFoundAbandonRPLDtos, null);
		} catch (ServiceException e) {
			logger.error("settleLoan loanAgent publishFoundAbandon error, exception:{}", e);
			throw e;
		}
		//返回码，10000为成功
		//订单状态，0:已接受, 1:处理中,2:处理成功,3:处理失败
		if(!"10000".equals(prodFoundAbandonResultDto.getRecode())){
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getErrMsg());
		}
//		if(!"2".equals(prodFoundAbandonResultDto.getData().getOrderStatus())){//放回码：10000,但是订单状态不是：2
//			throw new ServiceException(ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getErrMsg());
//		}
		
		//更新回款状态为：WAITACCOUNT(-1,"待本地账户成标") ------事务隔离
		loanRepaymentDao.updateStatusById(RepaymentStatus.WAITACCOUNT, 
				loanRepayment.getId(), null, null);
		
		return loanRepaymentDao.findByLoanId(productDTO.getLoanId());
	}
	
	/**
	 * @Title 银行存管协议成标-黄金产品 （事务隔离）
	 * @param productId
	 * @param productRegularDTO
	 * @param loanRepayment
	 * @author lifeifei
	 * @return 
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public LoanRepayment settleWaitBank(String productId, GoldProductsExtendDTO productDTO, LoanRepayment loanRepayment){
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		List<InvestRepayment> investRepayments = investRepaymentDao.findByLoanId(productDTO.getLoanId());
		if(investRepayments == null || investRepayments.isEmpty()){
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
		}
		
		//封装北京银行存管协议还款计划
		ProdFoundAbandonRPLDto[] prodFoundAbandonRPLDtos = new ProdFoundAbandonRPLDto[investRepayments.size()];
		InvestRepayment investRepayment;
		for(int i = 0 ; i < investRepayments.size() ; i++){
			investRepayment = investRepayments.get(i);
			//北京银行成标的还款计划
			//todo 确认还款金额
			prodFoundAbandonRPLDtos[i] = 
					new ProdFoundAbandonRPLDto(investRepayment.getPrincipalAmount().add(
							investRepayment.getInterestAmount()).add(
									investRepayment.getInterestPlusAmount()).add(
											investRepayment.getInterestCouponAmount()), BigDecimal.ZERO, 
							1, investRepayment.getDueDate());
		}
		Date currentTime = new Date();
			
		/***********************************调用存管协议成标接口****************************************************/
		LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		ProdFoundAbandonResultDto prodFoundAbandonResultDto;
		try {//4.3.2.标的成废
			prodFoundAbandonResultDto = 
					loanAgent.publishFoundAbandon(agentConfig.getPlatNo(), UUID.randomUUID().toString(), DateUtil.dateToStr(currentTime, DateUtil.DFS_yyyyMMdd), 
							sdf.format(currentTime), productDTO.getLoanId(), DictionaryEnum.WASTELABEL2, 
							null, null, prodFoundAbandonRPLDtos, null);
		} catch (ServiceException e) {
			logger.error("settleLoan loanAgent publishFoundAbandon error, exception:{}", e);
			throw e;
		}
		//返回码，10000为成功
		//订单状态，0:已接受, 1:处理中,2:处理成功,3:处理失败
		if(!"10000".equals(prodFoundAbandonResultDto.getRecode())){
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getErrMsg());
		}
//		if(!"2".equals(prodFoundAbandonResultDto.getData().getOrderStatus())){//放回码：10000,但是订单状态不是：2
//			throw new ServiceException(ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_HTTP_ERROR.getErrMsg());
//		}
		
		//更新回款状态为：WAITACCOUNT(-1,"待本地账户成标") ------事务隔离
		loanRepaymentDao.updateStatusById(RepaymentStatus.WAITACCOUNT, 
				loanRepayment.getId(), null, null);
		
		return loanRepaymentDao.findByLoanId(productDTO.getLoanId());
	}
	
	/**
	 * @Title 本地账户成标出账（事务隔离）
	 * @param productRegularDTO
	 * @param loanRepayment
	 * @author lifeifei
	 * @return 
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public LoanRepayment settleWaitAccount(ProductDTO productDTO, LoanRepayment loanRepayment){
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		logger.error("productid:{} settleLoan settleWaitAccount begining...", productDTO.getId());
		
		//北京银行流水号
		String queryId = loanRepayment.getDepositSrl();
		/************************************调用账户服务的成标出账************************************************/
		//todo 融资方确认还款金额
		FreezeResultDto freezeResultDto = null;
		// 暂时注释,待开发
		try {
			freezeResultDto = loanAccountService.loanSettle(productDTO.getLoanId(), 
				queryId, loanRepayment.getPrincipalAmount()
				.add(loanRepayment.getInterestAmount()).setScale(2, BigDecimal.ROUND_DOWN),
				RechargeApply.Y);
		} catch (ServiceException e) {
			logger.error("settleLoan account loanSettle error, exception:{}", e);
			throw e;
		}
		
		//更新回款状态为：UNDUE(0,"未到期")
		loanRepaymentDao.updateStatusById(RepaymentStatus.UNDUE, loanRepayment.getId(), freezeResultDto.getFundOperateId(), null);
		
		return loanRepaymentDao.findByLoanId(productDTO.getLoanId());
	}
	
	/**
	 * @Title 本地账户成标出账（事务隔离）
	 * @param productRegularDTO
	 * @param loanRepayment
	 * @author lifeifei
	 * @return 
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public LoanRepayment settleWaitAccount(GoldProductsExtendDTO productDTO, LoanRepayment loanRepayment){
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		//北京银行流水号
		String queryId = loanRepayment.getDepositSrl();
		/************************************调用账户服务的成标出账************************************************/
		//todo 融资方确认还款金额
		FreezeResultDto freezeResultDto = null;
		// 暂时注释,待开发
		try {
			freezeResultDto = loanAccountService.loanSettle(productDTO.getLoanId(), 
				queryId, loanRepayment.getPrincipalAmount()
				.add(loanRepayment.getInterestAmount()).setScale(2, BigDecimal.ROUND_HALF_UP),
				RechargeApply.Y);
		} catch (ServiceException e) {
			logger.error("settleLoan account loanSettle error, exception:{}", e);
			throw e;
		}
		
		//更新回款状态为：UNDUE(0,"未到期")
		loanRepaymentDao.updateStatusById(RepaymentStatus.UNDUE, loanRepayment.getId(), freezeResultDto.getFundOperateId(), null);
		
		return loanRepaymentDao.findByLoanId(productDTO.getLoanId());
	}
	
	/**
	 * @Title 生成融资方充值订单
	 * @param loanRepayment
	 * @param loanDTO
	 * @author lifeifei
	 * @date 2017年3月30日
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void settleWaitRecharge(LoanRepayment loanRepayment,LoanDTO loanDTO){
		
//		//更新回款状态为：WAITRECHARGE(0,"未到期")
//		loanRepaymentDao.updateStatusById(RepaymentStatus.UNDUE.getIndex(), loanRepayment.getId(),null,null);
//		
//		/************************************调用账户服务的充值订单************************************************/
//		//todo 是否保存账户充值业务流水号
//		try {
//			String rechargeSrl = rechargeService.companyRechargeApply(loanDTO.getUserId(), loanRepayment.getPrincipalAmount()
//					.add(loanRepayment.getInterestAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
//		} catch (ServiceException e) {
//			logger.error("settleLoan account companyRechargeApply error, exception:{}", e);
//			throw e;
//		}
		
	}

}

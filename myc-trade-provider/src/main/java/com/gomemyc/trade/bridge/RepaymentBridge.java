package com.gomemyc.trade.bridge;

import java.math.BigDecimal;
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
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import com.dangdang.ddframe.rdb.sharding.hint.HintManagerHolder;
import com.gomemyc.account.dto.LoanAccountDTO;
import com.gomemyc.account.service.LoanAccountService;
import com.gomemyc.account.service.RechargeService;
import com.gomemyc.agent.util.DateUtil;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.gold.dto.GoldProductsExtendDTO;
import com.gomemyc.gold.service.GoldProductService;
import com.gomemyc.invest.constant.TimeConstant;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.trade.constant.MQConstant;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dao.InvestRepaymentDao;
import com.gomemyc.trade.dao.LoanRepaymentDao;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.entity.LoanRepayment;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.enums.RepaymentStatus;
import com.gomemyc.trade.service.RepaymentService;
import com.gomemyc.util.BeanMapper;

import net.sf.json.JSONObject;

@Component("RepaymentBridge")
public class RepaymentBridge implements RepaymentService{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private InvestDao investDao;
	
	@Autowired
	private LoanRepaymentDao loanRepaymentDao;
	
	@Autowired
	private InvestRepaymentDao investRepaymentDao;
	
	@Autowired
	private RepaymentTransactionBridge repaymentTransactionBridge;
	
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
	
	@Reference
	private GoldProductService goldProductService;
	
	@Autowired
	DefaultMQProducer defaultMQProducer;
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Boolean settleLoan(String productId) throws ServiceException {
		
		//为避免统一事物中先读后写未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		logger.error("productid:{} settleLoan begining...", productId);
		
		ProductDTO productDTO = null;
		try {//调用标的服务获取产品信息
			productDTO = loanService.getProduct(productId);
		} catch (ServiceException e) {
			logger.error("settleLoan findByPsroductId error,expetion:{}", e);
			throw e;
		}
		if(productDTO == null)
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(), ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
	
		LoanDTO loanDTO = null;
		try {//调用标的服务获取标的信息
			loanDTO = loanService.findById(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("settleLoan find loan error,expetion:{}", e);
			throw e;
		}
		
		if(loanDTO == null)
			throw new ServiceException(ExceptionCode.INVEST_NOT_DATE_OPEN.getIndex(), ExceptionCode.INVEST_NOT_DATE_OPEN.getErrMsg());
		
		LoanAccountDTO loanAccountDTO = null;
		try {//调用账户信息查询标的账户信息
			loanAccountDTO = loanAccountService.selectLoanAccountByLoanId(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("settleLoan selectLoanAccountByLoanId error,expetion:{}", e);
			throw e;
		}
		if(loanAccountDTO == null){
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getErrMsg());
		}
		
		//todo 如何操作标的状态给标的结算上锁
		
		//判断是否存在回款计划
		LoanRepayment loanRepayment = loanRepaymentDao.findByLoanId(productDTO.getLoanId());
		if(loanRepayment == null){
			//生成回款和还款计划    ----事务隔离
			try {
				loanRepayment = repaymentTransactionBridge.saveRepaymentRecode(productId, productDTO, loanAccountDTO,loanDTO.getUserId());
			} catch (Exception e) {
				logger.error("settleLoan saveRepaymentRecode error,expetion:{}", e);
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e.getMessage());
			}
			
		}
		
		/******************************************待北京银行成标*******************************************/
		if(loanRepayment.getStatus() == RepaymentStatus.WAITBANK){
			
			//调用北京银行存管成标 ----事务隔离
			try {
				loanRepayment = repaymentTransactionBridge.settleWaitBank(productId, productDTO, loanRepayment);
			} catch (Exception e) {
				logger.error("settleLoan saveRepaymentRecode error,expetion:{}", e);
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e.getMessage());
			}
		}
		/******************************************待本地账户成标出账*******************************************/
		if(loanRepayment.getStatus() == RepaymentStatus.WAITACCOUNT){
			
			//本地成标出账 ----事务隔离
			try {
				loanRepayment = repaymentTransactionBridge.settleWaitAccount(productDTO, loanRepayment);
			} catch (Exception e) {
				logger.error("settleLoan setWaitAccount error, exception:{}", e);
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e.getMessage());
			}
		}
		
		/******************************************待生成融资方充值订单*******************************************/
//		if(loanRepayment.getStatus() == RepaymentStatus.WAITRECHARGE.getIndex()){
//			
//			//生成融资方充值订单 ----事务隔离
//			try {
//				this.settleWaitRecharge(loanRepayment, loanDTO);
//			} catch (Exception e) {
//				logger.error("settleLoan settleWaitRecharge error, exception:{}", e);
//				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
//			}
//			
//			loanRepayment = loanRepaymentDao.findByLoanId(productDTO.getLoanId());
//			
//		}
		/*******************************更新标的、产品、回款和还款计划状态为：已结算下状态*************************/
		if(loanRepayment.getStatus() == RepaymentStatus.UNDUE){
			
			List<Invest> invests = investDao.findListByProduct(productId, InvestStatus.SUCCESS);
			if(invests == null || invests.isEmpty()){
				throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(), ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
			}
			
			//还款计划状态更新：UNDUE(0,"未到期"),
			if(investRepaymentDao.updateStatus(productDTO.getLoanId(), RepaymentStatus.UNDUE.getIndex()) != invests.size()){
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
			
			//投资状态更新：SETTLED(6,"结标"),
			if(investDao.updateByProductId(productId, InvestStatus.SETTLED, InvestStatus.SUCCESS) != invests.size()){
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
			Date settleTime = null; 
			Date valueTime = null;
			Date dueTime = loanRepayment.getDueDate();
			if(productDTO.getValueTime() != null){
				valueTime = productDTO.getValueTime();
				settleTime = new Date();
			}else{
				settleTime = DateUtil.addDate(dueTime, -(productDTO.getTotalDays()+1));
				valueTime = DateUtil.addDate(dueTime, -(productDTO.getTotalDays()));
			}
			
			//更新产品和标的的状态为：已结算，结算时间，起息时间，还款日
			try {
				loanService.modifyProductStatusAndSettleTime(productId,productDTO.getLoanId(), 
						ProductStatus.SETTLED, settleTime,valueTime,dueTime);
			} catch (ServiceException e) {
				logger.error("settleLoan modifyProductStatus error, exception:{}", e);
				throw e;
			}
			
			//票据产品推送已结算消息到资管系统
			if(productDTO.getId().startsWith(("pj-"))){
				try {
					JSONObject object = new JSONObject();
					object.put(MQConstant.TYPE, MQConstant.PRODUCTSTATUS);
					object.put(MQConstant.ID, loanDTO.getPortfolioNo());
					object.put(MQConstant.STATUSTYPE, MQConstant.STATUS6);
					object.put(MQConstant.AMOUNT, productDTO.getInvestAmount());
					object.put(MQConstant.PAYAMOUNT, loanRepayment.getPrincipalAmount().add(loanRepayment.getInterestAmount()));
					object.put(MQConstant.FACTKNOTTIME, settleTime.getTime());
					logger.error("settleLoan send MQ to zgSystem begining...");
					logger.error("settleLoan send MQ to zgSystem params:{}",object.toString());
					//消息
					Message msg = new Message(MQConstant.ZGTOPIC,MQConstant.ZGBILLTAG,UUID.randomUUID().toString(),
							object.toString().getBytes("utf-8"));
					SendResult result = defaultMQProducer.send(msg);
					
					if(result.getSendStatus() != SendStatus.SEND_OK){
						throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), "发送MQ消息失败");
					}
					
				} catch (Exception e1) {
					logger.error("settleLoan send MQ to zgSystem error,exception:", e1);
					throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e1.getMessage());
				}
			}
		}
		
		return true;
	}
	
	
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Boolean saveReceivedPayments(String productId) {
		
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		ProductDTO productDTO = null;
		try {//调用标的服务获取产品信息
			productDTO = loanService.getProduct(productId);
		} catch (ServiceException e) {
			logger.error("settleLoan findByPsroductId error,expetion:{}", e);
			throw e;
		}
		if(productDTO == null)
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(), ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
		
		LoanAccountDTO loanAccountDTO = null;
		try {//调用账户信息查询标的账户信息
			loanAccountDTO = loanAccountService.selectLoanAccountByLoanId(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("settleLoan selectLoanAccountByLoanId error,expetion:{}", e);
			throw e;
		}
		if(loanAccountDTO == null){
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getErrMsg());
		}
		
		/*************************************判断产品是否已满标或到期未满标**************************************************/
		
		if(productDTO.getStatus() != ProductStatus.FINISHED && 
				productDTO.getStatus() != ProductStatus.FAILED)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_NOT_FULL.getIndex(), ExceptionCode.LOAN_SETTLE_NOT_FULL.getErrMsg());
		
		/*************************************检验所有投资是否已成功**************************************************/
		
		Integer resultNum = investDao.checkInvestByProduct(productId, InvestStatus.INITIAL, InvestStatus.BJ_SYN_SUCCESS);
		if(resultNum > 0)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getErrMsg());
		
		/*************************************校验标的账户的募集金额和投资总金额是否一致**************************************************/
		
		BigDecimal totalInvestAmount = investDao.findAmountListByProduct(productId,InvestStatus.SUCCESS);
		if(loanAccountDTO.getRaiseAmount().compareTo(totalInvestAmount) != 1)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getErrMsg());
		
		BigDecimal rate = new BigDecimal(productDTO.getRate()).add(new BigDecimal(productDTO.getPlusRate()))
	    		.divide(new BigDecimal(10000));//产品基础利率+加息利息
//		Integer plusRate = productDTO.getPlusRate()/10000;//加息利率
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
			//加息利息 = 加息后的总利息 - 基础利息
//			BigDecimal totalInvestRateAmount = invest.getAmount().multiply(
//					new BigDecimal((rate + plusRate))).divide(
//							new BigDecimal(TimeConstant.DAYS_PER_YEAR)).multiply(
//									new BigDecimal(days));
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
							
							BigDecimal couponRate = new BigDecimal(couponPackage.getParValue()).divide(new BigDecimal(10000));//加息券利率
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
			if(productDTO.getValueTime() != null){
				investRepayment.setDueDate(DateUtil.addDate(productDTO.getValueTime(), productDTO.getTotalDays()));
				investRepayment.setValueTime(productDTO.getValueTime());
			}else{
				investRepayment.setDueDate(DateUtil.addDate(settleDate, (productDTO.getTotalDays()+1)));
				investRepayment.setValueTime(DateUtil.addDate(settleDate, 1));
			}
			investRepayment.setStatus(RepaymentStatus.WAITACCOUNT);
			
			investRepayments.add(investRepayment);
			
		}
		if(investRepaymentDao.insertBatch(investRepayments) != invests.size())//插入还款计划
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
	
		return true;
	}

	@Override
	public Boolean saveRepayments(String productId) {
		
		//为避免统一事物中先写后读未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		ProductDTO productDTO = null;
		try {//调用标的服务获取产品信息
			productDTO = loanService.getProduct(productId);
		} catch (ServiceException e) {
			logger.error("saveRepayments findByPsroductId error,expetion:{}", e);
			throw e;
		}
		if(productDTO == null)
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(), ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
		
		LoanDTO loanDTO = null;
		try {//调用标的服务获取标的信息
			loanDTO = loanService.findById(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("saveRepayments find loan error,expetion:{}", e);
			throw e;
		}
		
		LoanAccountDTO loanAccountDTO = null;
		try {//调用账户信息查询标的账户信息
			loanAccountDTO = loanAccountService.selectLoanAccountByLoanId(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("saveRepayments selectLoanAccountByLoanId error,expetion:{}", e);
			throw e;
		}
		if(loanAccountDTO == null){
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getErrMsg());
		}
		
		/*************************************判断产品是否已满标或到期未满标**************************************************/
		
		if(productDTO.getStatus() != ProductStatus.FINISHED && 
				productDTO.getStatus() != ProductStatus.FAILED)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_NOT_FULL.getIndex(), ExceptionCode.LOAN_SETTLE_NOT_FULL.getErrMsg());
		
		/*************************************检验所有投资是否已成功**************************************************/
		
		Integer resultNum = investDao.checkInvestByProduct(productId, InvestStatus.INITIAL, InvestStatus.BJ_SYN_SUCCESS);
		if(resultNum > 0)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ERROR.getErrMsg());
		
		/*************************************校验标的账户的募集金额和投资总金额是否一致**************************************************/
		
		BigDecimal totalInvestAmount = investDao.findAmountListByProduct(productId,InvestStatus.SUCCESS);
		if(loanAccountDTO.getRaiseAmount().compareTo(totalInvestAmount) != 1)
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getIndex(), ExceptionCode.LOAN_SETTLE_AMOUNT_ERROR.getErrMsg());
		
		List<InvestRepayment> investRepayments = investRepaymentDao.findByLoanId(productDTO.getLoanId());
		if(investRepayments == null || investRepayments.isEmpty()){
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
		}
		
		BigDecimal totalCouponAmount = BigDecimal.ZERO;
		BigDecimal interestAmount = BigDecimal.ZERO;
		for(InvestRepayment investRepayment: investRepayments){
			totalCouponAmount = totalCouponAmount.add(investRepayment.getInterestCouponAmount());
			interestAmount = interestAmount.add(investRepayment.getInterestAmount());
		}
		
		/*************************************生成回款计划**************************************************/
		Date currentTime = new Date();
		LoanRepayment loanRepayment = new LoanRepayment();
		loanRepayment.setId(UUID.randomUUID().toString());
		loanRepayment.setLoanId(productDTO.getLoanId());
		loanRepayment.setCurrentPeriod(1);
		loanRepayment.setUserId(loanDTO.getUserId());
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
			loanRepayment.setDueDate(DateUtil.addDate(new Date(), (productDTO.getTotalDays()+1)));
		}
		//回款状态：待北京银行还款
		loanRepayment.setStatus(RepaymentStatus.WAITBANK);
		loanRepayment.setTimeCreatime(currentTime);
		
		if(loanRepaymentDao.insert(loanRepayment) != 1)//插入回款计划
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
		
		return true;
		
	}
	
	/**
	 * @Title 黄金回款计划保存
	 * @param productId 产品编号
	 * @param investRepaymentList 回款计划
	 * @author tianbin
	 * @date 2017年4月03日
	 */
	public Boolean saveGoldInvestPayments(String productId,List<InvestRepaymentDTO> investRepaymentList){
		boolean flag=true;
		for (InvestRepaymentDTO investRepaymentDTO : investRepaymentList) {
			InvestRepayment investRepayment = BeanMapper.map(investRepaymentDTO, InvestRepayment.class);
			int insert = investRepaymentDao.insertSelective(investRepayment);
			if (insert!=1) {
				flag=false;
				return flag;
			}
		}
		return flag;
	}



	@Override
	public Boolean goldSettleLoan(String productId) throws ServiceException {
		//为避免统一事物中先读后写未切换master主库，统一强制采用主库读写
		HintManagerHolder.clear();
    	HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		
		logger.error("productid:{} goldSettleLoan begining...", productId);
		
		GoldProductsExtendDTO productDTO = null;
		try {//调用标的服务获取产品信息
			productDTO = goldProductService.serviceFindInfoByProductId(productId);
		} catch (ServiceException e) {
			logger.error("settleLoan findByPsroductId error,expetion:{}", e);
			throw e;
		}
		if(productDTO == null)
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(), ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
	
		LoanDTO loanDTO = null;
		try {//调用标的服务获取标的信息
			loanDTO = loanService.findById(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("settleLoan find loan error,expetion:{}", e);
			throw e;
		}
		
		if(loanDTO == null)
			throw new ServiceException(ExceptionCode.INVEST_NOT_DATE_OPEN.getIndex(), ExceptionCode.INVEST_NOT_DATE_OPEN.getErrMsg());
		
		LoanAccountDTO loanAccountDTO = null;
		try {//调用账户信息查询标的账户信息
			loanAccountDTO = loanAccountService.selectLoanAccountByLoanId(productDTO.getLoanId());
		} catch (ServiceException e) {
			logger.error("settleLoan selectLoanAccountByLoanId error,expetion:{}", e);
			throw e;
		}
		if(loanAccountDTO == null){
			throw new ServiceException(ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getIndex(), ExceptionCode.LOAN_SETTLE_INVEST_ACCOUNT.getErrMsg());
		}
		
		//todo 如何操作标的状态给标的结算上锁
		
		//判断是否存在回款计划
		LoanRepayment loanRepayment = loanRepaymentDao.findByLoanId(productDTO.getLoanId());
		if(loanRepayment == null){
			//生成回款和还款计划    ----事务隔离
			try {
				loanRepayment = repaymentTransactionBridge.saveRepaymentRecode(productId, productDTO, loanAccountDTO);
			} catch (Exception e) {
				logger.error("settleLoan saveRepaymentRecode error,expetion:{}", e);
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), e.getMessage());
			}
			
		}
		
		/******************************************待北京银行成标*******************************************/
		if(loanRepayment.getStatus() == RepaymentStatus.WAITBANK){
			
			//调用北京银行存管成标 ----事务隔离
			try {
				loanRepayment = repaymentTransactionBridge.settleWaitBank(productId, productDTO, loanRepayment);
			} catch (Exception e) {
				logger.error("settleLoan saveRepaymentRecode error,expetion:{}", e);
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
		}
		/******************************************待本地账户成标出账*******************************************/
		if(loanRepayment.getStatus() == RepaymentStatus.WAITACCOUNT){
			
			//本地成标出账 ----事务隔离
			try {
				loanRepayment = repaymentTransactionBridge.settleWaitAccount(productDTO, loanRepayment);
			} catch (Exception e) {
				logger.error("settleLoan setWaitAccount error, exception:{}", e);
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
		}
		
		/******************************************待生成融资方充值订单*******************************************/
//				if(loanRepayment.getStatus() == RepaymentStatus.WAITRECHARGE.getIndex()){
//					
//					//生成融资方充值订单 ----事务隔离
//					try {
//						this.settleWaitRecharge(loanRepayment, loanDTO);
//					} catch (Exception e) {
//						logger.error("settleLoan settleWaitRecharge error, exception:{}", e);
//						throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
//					}
//					
//					loanRepayment = loanRepaymentDao.findByLoanId(productDTO.getLoanId());
//					
//				}
		/*******************************更新标的、产品、回款和还款计划状态为：已结算下状态*************************/
		if(loanRepayment.getStatus() == RepaymentStatus.UNDUE){
			
			List<Invest> invests = investDao.findListByProduct(productId, InvestStatus.SUCCESS);
			if(invests == null || invests.isEmpty()){
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
			
			//还款计划状态更新：UNDUE(0,"未到期"),
			if(investRepaymentDao.updateStatus(productDTO.getLoanId(), RepaymentStatus.UNDUE.getIndex()) != invests.size()){
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
			
			//投资状态更新：SETTLED(6,"结标"),
			if(investDao.updateByProductId(productId, InvestStatus.SETTLED, InvestStatus.SUCCESS) != invests.size()){
				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
			}
			Date valueTime = new Date();
			if(productDTO.getValueTime() != null){
				valueTime = productDTO.getValueTime();
			}else{
				valueTime = DateUtil.addDate(valueTime, 1);
			}
			//更新产品的状态为：已结算
			try {
				goldProductService.updateStstusByProductId(productId, ProductStatus.SETTLED.getIndex(), new Date(), valueTime);
			} catch (ServiceException e) {
				logger.error("settleLoan modifyProductStatus error, exception:{}", e);
				throw e;
			}
			//todo 标的状态啥时候更新
			
		}
		
		return true;
	}



	@Override
	public Boolean updateGoldInvestPayments(String productId, List<InvestRepaymentDTO> investRepaymentList) {
		boolean flag=true;
		for (InvestRepaymentDTO investRepaymentDTO : investRepaymentList) {
			InvestRepayment investRepayment = BeanMapper.map(investRepaymentDTO, InvestRepayment.class);
			int insert = investRepaymentDao.update(investRepayment);
			if (insert!=1) {
				flag=false;
				return flag;
			}
		}
		return flag;
	}


}

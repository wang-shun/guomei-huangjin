/** 
 * Project Name:myc-gold-provider 
 * File Name:GoldInvestServiceImpl.java 
 * Package Name:com.gomemyc.gold.fontservice 
 * Date:2017年3月7日上午11:31:37 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.gomemyc.gold.entity.GoldProduct;
import com.gomemyc.gold.entity.GoldProductInfo;
import com.gomemyc.gold.entity.extend.FinishOrders;
import com.gomemyc.gold.enums.GoldMethodStatusEnum;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.enums.InvestSource;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dao.GoldEarningsDao;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dao.GoldInvestOrderInfoDao;
import com.gomemyc.gold.dao.GoldProductDao;
import com.gomemyc.gold.dao.GoldProductInfoDao;
import com.gomemyc.gold.dto.FinishOrdersDTO;
import com.gomemyc.gold.dto.GoldEarningsDTO;
import com.gomemyc.gold.dto.GoldInvestOrderDTO;
import com.gomemyc.gold.entity.GoldEarnings;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.entity.GoldInvestOrderInfo;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.enums.GoldProductStatusEnum;
import com.gomemyc.gold.enums.InvestStatus;
import com.gomemyc.gold.service.GoldInvestService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldCouponRuleVerifyUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.gold.util.GoldProductRuleVerifyUtil;
import com.gomemyc.gold.util.GoldWalletInfoCode;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.model.user.User;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.user.api.UserService;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.BuyTimeGoldResultDto;
import com.gomemyc.wallet.resp.ConfirmBuyTimeGoldResultDto;
import com.gomemyc.wallet.resp.QueryBuyTimeOrderResultDto;
import com.gomemyc.wallet.walletinter.GoldBuyWallet;
import com.gomemyc.wallet.walletinter.GoldQueryWallet;
import com.google.common.collect.Maps;

/** 
 * ClassName:GoldInvestServiceImpl <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月7日 上午11:31:37 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service(timeout=6000)
public class GoldInvestServiceImpl implements GoldInvestService {
	
	private static final Logger logger = LoggerFactory.getLogger(GoldInvestServiceImpl.class);

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;

	@Autowired
	private GoldProductDao goldProductDao;
	
	@Autowired
	private GoldEarningsDao goldEarningsDao;
	
	@Autowired
	private GoldProductInfoDao goldProductInfoDao;
	
	@Autowired
	private GoldInvestOrderInfoDao goldInvestOrderInfoDao;
	
	@Autowired
	private GoldWalletConstant goldWalletConstant;
	
	@Reference
	private UserService userService;
	
	@Reference
	private AccountService accountService;
	
	@Reference
	private CouponService couponService;
	
	@Reference
	private InvestService investService;
	
	@Autowired
	@Qualifier("redisStringStringTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 
	 * 用户预下单
	 * 
	 * @param amount    (String) 投资金额(必填)
	 * @param userId    (String) 用户id(必填)
	 * @param productId (String) 产品id(必填)
	 * @param couponId  (String) 红包id(选填)
	 * @return GoldInvestOrderDTO
	 * @ServiceException
	 *                30000:参数错误
	 *                50000:操作失败，请重试 
	 *                50001:预下单失败
	 *                60000:产品不存在
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月6日
	 */
	public GoldInvestOrderDTO prePay(BigDecimal amount,
									 String uid,
									 String productId,
									 String couponId) throws ServiceException{
		logger.info("prePay  in  GoldInvestServiceImpl ,the param [amount={}],[uid={},[productId={}]",amount,uid,productId);
		if(amount.compareTo(BigDecimal.ZERO) < 1 || 
		   StringUtils.isBlank(uid) || 
		   StringUtils.isBlank(productId))
			    throw new ServiceException(GoldInfoCode.WRONG_PARAMETERS.getStatus(), GoldInfoCode.WRONG_PARAMETERS.getMsg());
		//根据产品id查询产品编码
		GoldProduct goldProduct = goldProductDao.selectByPrimaryKey(productId);
	    logger.info("prePay  in  GoldInvestServiceImpl ,the goldProduct is [{}]",goldProduct);
		if(goldProduct == null)
			 throw new ServiceException(GoldInfoCode.LOAN_NOT_EXITS.getStatus(), GoldInfoCode.LOAN_NOT_EXITS.getMsg());
		//产品不是开标状态
		if(goldProduct.getStatus() != GoldProductStatusEnum.PRODUCTSTATUS_LAUNCH.getStatus())
			 throw new ServiceException(GoldInfoCode.LOAN_NOT_OPEN.getStatus(), GoldInfoCode.LOAN_NOT_OPEN.getMsg());
		
		//调用账户接口，查询用户账户余额
		AccountDTO accountDTO = null;
		try {
			accountDTO = accountService.getByUserId(uid);
		} catch (Exception e1) {
			logger.info("prePay  in  GoldInvestServiceImpl ,the catchError [账户异常]");
			throw new ServiceException(GoldInfoCode.ACCOUNT_ERROR.getStatus(), GoldInfoCode.ACCOUNT_ERROR.getMsg());
		}
		logger.info("prePay  in  GoldInvestServiceImpl ,the param [accountDTO={}]",accountDTO);
		if(accountDTO == null)
			throw new ServiceException(GoldInfoCode.ACCOUNT_NOT_OPEN.getStatus(), GoldInfoCode.ACCOUNT_NOT_OPEN.getMsg());
		
		//投资规则校验(产品规则)
		User user = null;
		try {
			user = userService.findByUserId(uid);
		} catch (Exception e2) {
			logger.info("prePay  in  GoldInvestServiceImpl ,the catchError [用户异常]");
			throw new ServiceException(GoldInfoCode.USER_ERROR.getStatus(), GoldInfoCode.USER_ERROR.getMsg());
		}
		logger.info("prePay  in  GoldInvestServiceImpl ,the param is [user={}]",user);
		if(user == null)
			throw new ServiceException(GoldInfoCode.USER_NOT_EXITS.getStatus(), GoldInfoCode.USER_NOT_EXITS.getMsg());
		//根据产品id，查询产品规则
		GoldProductInfo goldProductInfo = goldProductInfoDao.selectByPrimaryKey(productId);
		logger.info("prePay  in  GoldInvestServiceImpl ,the goldProductInfo is [{}]",goldProductInfo);
		if(goldProductInfo == null)
			throw new ServiceException(GoldInfoCode.PRODUCT_RULES_NOT_EXIST.getStatus(), GoldInfoCode.PRODUCT_RULES_NOT_EXIST.getMsg());
		//查询一个用户对同一个产品的下单次数
		int investOrderTime = goldInvestOrderDao.getByUserIdAndProductId(uid, productId, GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus()) + goldInvestOrderDao.getByUserIdAndProductId(uid, productId, GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus());
		logger.info("prePay  in  GoldInvestServiceImpl ,the investOrderTime is [{}]",investOrderTime);
		//查询一个产品已经被购买的总金额
		BigDecimal productSumAmount = goldInvestOrderDao.getByProductId(productId, GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus());
		logger.info("prePay  in  GoldInvestServiceImpl ,the productSumAmount is [{}]",productSumAmount);
		boolean goldProductRuleVerifyResultFlag = GoldProductRuleVerifyUtil.goldProductRuleVerify(amount, 
																							      goldProductInfo.getMinInvestAmount(), 
																							      goldProductInfo.getMaxInvestAmount(),
																							      investOrderTime, 
																							      goldProductInfo.getMaxTimes(),
																							      goldProductInfo.getBalance(), 
																							      goldProductInfo.getMaxTotalAmount(),
																							      productSumAmount, 
																							      goldProductInfo.getStepAmount(),
																							      user,
																							      goldProduct.getUserId());
		//如果goldProductRuleVerifyResult为false，预下单失败(投资金额不满足规则)
		if(!goldProductRuleVerifyResultFlag)
			  throw new ServiceException(GoldInfoCode.PRODUCT_NOT_RULES.getStatus(), GoldInfoCode.PRODUCT_NOT_RULES.getMsg());
		
		//红包规则校验(红包规则)
		//计算产品投资天数
		int days = goldProduct.getDays() + goldProduct.getMonths()*30 + goldProduct.getYears()*365;
		//取出红包金额
		BigDecimal couponAmount = BigDecimal.ZERO;
		if(StringUtils.isNotBlank(couponId)){
			try {
				CouponPlacement couponPlacement = couponService.findCouponPlacementbyId("", couponId);
				logger.info("prePay  in  GoldInvestServiceImpl ,the couponPlacement is [{}]",couponPlacement); 
				if(couponPlacement == null)
					 throw new ServiceException(GoldInfoCode.COUPON_NOT_EXIST.getStatus(), GoldInfoCode.COUPON_NOT_EXIST.getMsg());
				boolean goldCouponRuleVerifyResultFlag = GoldCouponRuleVerifyUtil.goldCouponRuleVerify(couponId, 
																									   goldProductInfo.getUseCoupon(),
																									   couponPlacement,
																									   amount, 
																									   goldProduct.getLoanId(),
																									   productId,
																									   days);
				//红包不满足规则
				if(!goldCouponRuleVerifyResultFlag)
					throw new ServiceException(GoldInfoCode.COUPON_NOT_RULES.getStatus(), GoldInfoCode.COUPON_NOT_RULES.getMsg());
				couponAmount = BigDecimal.valueOf(couponPlacement.getCouponPackage().getParValue());
				logger.info("prePay  in  GoldInvestServiceImpl ,the couponAmount is [{}]",couponAmount); 
				// 冻结奖券
				couponPlacement.setStatus(CouponStatus.FROZEN);
		        if (!couponService.updateCouponPlacement(null, couponPlacement)) {
		        	logger.info("prePay  in  GoldInvestServiceImpl ,the CouponError is [{}]","FrozenCouponError"); 
					 throw new ServiceException(GoldInfoCode.COUPON_FREEZE_ERROR.getStatus(), GoldInfoCode.COUPON_FREEZE_ERROR.getMsg());
		        }
			} catch (ServiceException e) {
				logger.info("prePay  in  GoldInvestServiceImpl ,the couponServiceException"); 
				throw e;
			} catch (Exception e) {
				logger.info("prePay  in  GoldInvestServiceImpl ,the couponException"); 
				throw new ServiceException(GoldInfoCode.COUPON_ERROR.getStatus(), GoldInfoCode.COUPON_ERROR.getMsg());
			}
		}

		//生成唯一订单号
		String reqNo = UUIDGenerator.generate();
		logger.info("prePay  in  GoldInvestServiceImpl ,the reqNo is [{}]",reqNo); 
		//key放入redis,并设置过期时间【确认订单时候需要判断是否有效】
		try{
			//将用户id和订单号拼接放入redis中并设置过期时间
			String userIdAndreqNo = uid + "&" + reqNo;
			redisTemplate.opsForValue().set(userIdAndreqNo, userIdAndreqNo, Integer.valueOf(goldWalletConstant.getExpireTime()), TimeUnit.SECONDS);
		}catch (Exception e) {
			String redis = "redisError";
		    logger.info("prePay  in  GoldInvestServiceImpl ,the param [redis={}]",redis);
		}
		BuyTimeGoldResultDto buyTimeGold = null;
		BigDecimal bigAmount = amount.multiply(new BigDecimal(100.00));
		logger.info("prePay  in  GoldInvestServiceImpl ,the param [bigAmount={}]",bigAmount);
		try {
			//调用黄金钱包接口,预下单
			buyTimeGold = GoldBuyWallet.buyTimeGold(goldWalletConstant.getVersion(),
										   			goldWalletConstant.getMerchantCode(),
										   			DateUtil.getDateTime(),
										   			bigAmount,
										   			reqNo,
										   			goldWalletConstant.getBuyType(),
										   			goldProduct.getGoldProductCode(),
										   			productId,
										   			goldWalletConstant.getSign(),
										   			goldWalletConstant.getIp());
		} catch (Exception e) {
			 throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		}
		logger.info("prePay  in  GoldInvestServiceImpl ,the buyTimeGold is [{}]",buyTimeGold);
		if(buyTimeGold == null)
			throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		if(buyTimeGold.getData() == null ||
		   Integer.valueOf(buyTimeGold.getRetCode()).compareTo(GoldWalletInfoCode.SUCCESS.getStatus()) != 0)
			throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		if(StringUtils.isNotBlank(buyTimeGold.getData().getErrCode()) && 
		   StringUtils.isNotBlank(buyTimeGold.getData().getErrMsg())){
			logger.info("prePay  in  GoldInvestServiceImpl ,the param [GolderrCode={}],[GolderrMsg={}",buyTimeGold.getData().getErrCode(),buyTimeGold.getData().getErrMsg());
			 throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		}

		//封装数据的DTO以及实体类
		GoldInvestOrder goldInvestOrder = new GoldInvestOrder();
		GoldInvestOrderDTO goldInvestOrderDTO = new GoldInvestOrderDTO();
		//将调用黄金钱包接口查询回来的dto中的信息设置到订单goldInvestOrder中
		goldInvestOrder.setRealPrice(buyTimeGold.getData().getRealPrice().divide(new BigDecimal(100.00), 2, RoundingMode.CEILING));
		goldInvestOrder.setRealWeight(buyTimeGold.getData().getRealWeight().divide(new BigDecimal(1000.00), 3, RoundingMode.FLOOR));
		goldInvestOrder.setRealAmount(buyTimeGold.getData().getRealAmount().divide(new BigDecimal(100.00), 2, RoundingMode.FLOOR));
		goldInvestOrder.setFinishTime(DateUtil.strToDateLong(buyTimeGold.getData().getFinishTime()));
		goldInvestOrder.setReqNo(buyTimeGold.getData().getReqNo());
		goldInvestOrder.setGoldOrderNo(buyTimeGold.getData().getOrderNo());
		goldInvestOrder.setOrderStatus(Integer.parseInt(buyTimeGold.getData().getStatus()));
		goldInvestOrder.setBuyType(Integer.parseInt(goldWalletConstant.getBuyType()));
		goldInvestOrder.setErrCode(buyTimeGold.getData().getErrCode());
		goldInvestOrder.setErrMsg(buyTimeGold.getData().getErrMsg());
		String id = UUIDGenerator.generate();
		goldInvestOrder.setId(id);
		goldInvestOrder.setUserId(uid);
		goldInvestOrder.setAmount(amount);
		goldInvestOrder.setProductId(productId);
		goldInvestOrder.setProductName(goldProduct.getGoldProcductName());
		if(userService.findByUserId(uid) != null)
			goldInvestOrder.setUserMobile(userService.findByUserId(uid).getMobile());
		//设置过期时间expireTime
		Date expireTime = DateUtil.addSecond(DateUtil.strToDateLong(buyTimeGold.getData().getFinishTime()), Integer.valueOf(goldWalletConstant.getExpireTime()));
		logger.info("prePay  in  GoldInvestServiceImpl ,the expireTime is [{}]",expireTime);
		goldInvestOrder.setExpireTime(expireTime);
		goldInvestOrder.setOrderType(GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus());
		logger.info("prePay  in  GoldInvestServiceImpl ,the goldInvestOrder is [{}]",goldInvestOrder);
		//将产品的剩余可投资金额用乐观锁进行修改
		BigDecimal balance = goldProductInfo.getBalance().subtract(goldInvestOrder.getRealAmount());
		logger.info("prePay  in  GoldInvestServiceImpl ,the balance is [{}]",balance);
		int updateRows = 0;
		logger.info("prePay  in  GoldInvestServiceImpl ,the version is [{}]",goldProductInfo.getVersion());
		if(goldProductInfo.getVersion() == null){
			goldProductInfo.setBalance(balance);
			goldProductInfo.setVersion(1);
			updateRows = goldProductInfoDao.updateByPrimaryKeySelective(goldProductInfo);
		}
		else
			updateRows = goldProductInfoDao.updateByProductIdAndVersion(productId, goldProductInfo.getVersion(), balance);
		logger.info("prePay  in  GoldInvestServiceImpl ,the updateRows is [{}]",updateRows);
		if(updateRows == 0)
			//预下单失败(剩余可投资金额扣除失败)
			throw new ServiceException(GoldInfoCode.BALANCE_ERROR.getStatus(), GoldInfoCode.BALANCE_ERROR.getMsg());
		//将购买黄金预订单信息保存进数据库
		goldInvestOrderDao.insertSelective(goldInvestOrder);
		GoldInvestOrderInfo goldInvestOrderInfo = new GoldInvestOrderInfo();
		goldInvestOrderInfo.setAmount(amount);
		goldInvestOrderInfo.setCouponId(couponId);
		goldInvestOrderInfo.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
		goldInvestOrderInfo.setId(id);
		goldInvestOrderInfo.setReqNo(reqNo);
		//剩余支付金额
		BigDecimal remainAmount = goldInvestOrder.getRealAmount().subtract(couponAmount);
		//红包金额
		goldInvestOrderInfo.setCouponAmount(couponAmount);
	    //如果用户账户的钱不够
	    if(accountDTO.getTotalAmount().compareTo(goldInvestOrder.getRealAmount()) < 0){
	    	//余额支付
			goldInvestOrderInfo.setBalancePaidAmount(accountDTO.getTotalAmount());
			//剩余支付金额
			goldInvestOrderInfo.setRemainAmount(remainAmount.subtract(accountDTO.getTotalAmount()));
		} else{//如果用户账户的钱足够
			//余额支付
			goldInvestOrderInfo.setBalancePaidAmount(remainAmount);
			//剩余支付金额
			goldInvestOrderInfo.setRemainAmount(BigDecimal.ZERO);
		}
		//预下单成功后，保存订单详情信息
		goldInvestOrderInfoDao.insertSelective(goldInvestOrderInfo);
		//封装要返回的数据
		BeanMapper.copy(goldInvestOrder, goldInvestOrderDTO);
		logger.info("prePay  in  GoldInvestServiceImpl ,the goldInvestOrderDTO is [{}]",goldInvestOrderDTO);
		goldInvestOrderDTO.setRemainAmount(goldInvestOrderInfo.getRemainAmount());
		goldInvestOrderDTO.setBalancePaidAmount(goldInvestOrderInfo.getBalancePaidAmount());
		goldInvestOrderDTO.setServerTime(DateUtil.strToDateLong(DateUtil.getDateTime()).getTime());
		goldInvestOrderDTO.setCouponValue(goldInvestOrderInfo.getCouponAmount());
		goldInvestOrderDTO.setTitle(goldProduct.getTitle());
		logger.info("prePay  in  GoldInvestServiceImpl ,the goldInvestOrderDTO is [{}]",goldInvestOrderDTO);
		return goldInvestOrderDTO;
	}

	/**
	 * 
	 * 用户确认下单(查询用户预下单信息，产品信息，产品详情信息)
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @return HashMap<String, Object>
	 * @ServiceException
	 *                30000:参数错误
	 *                30001:预下单信息不存在
	 *                50002:确认下单失败
	 *                60000:产品不存在
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	public HashMap<String, Object> confirmBySelect(String uid,String reqNo) throws ServiceException{
		logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the param [uid={}],[reqNo={}]",uid,reqNo);
		if(StringUtils.isBlank(uid) || 
		   StringUtils.isBlank(reqNo))
			throw new ServiceException(GoldInfoCode.WRONG_PARAMETERS.getStatus(), GoldInfoCode.WRONG_PARAMETERS.getMsg());
		//根据订单号，唯一查询确认下单信息
		GoldInvestOrder goldInvestOrderConfirm = goldInvestOrderDao.selectObjByUserIdReqNo(uid,reqNo,null,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus());
		if(goldInvestOrderConfirm != null)
			//已经确认下单过了
			throw new ServiceException(GoldInfoCode.REPEAT_SUBMIT_ORDERS.getStatus(), GoldInfoCode.REPEAT_SUBMIT_ORDERS.getMsg());
		// 通过订单号和用户id 唯一查询订单信息(预下单)
		GoldInvestOrder goldInvestOrder = goldInvestOrderDao.selectObjByUserIdReqNo(uid,reqNo,GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus());
		logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the goldInvestOrder is [{}]]",goldInvestOrder);
		// 是否有订单信息(预下单)
		if(goldInvestOrder == null)
			 throw new ServiceException(GoldInfoCode.PREPAY_NOT_EXIST.getStatus(), GoldInfoCode.PREPAY_NOT_EXIST.getMsg());
		//根据预下单放入的key,如果能从redis中取出数据，则预下单未过期，如果取不出，则预下单过期
		String userIdAndreqNo = uid + "&" + reqNo;
		try{
			logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the param [currentTime={}],[expireTime={}],[redisValue={}]",DateUtil.strToDateLong(DateUtil.getDateTime()),goldInvestOrder.getExpireTime(),redisTemplate.opsForValue().get(userIdAndreqNo));
			//如果redis中取不出数据，或者当前时间大于过期时间，预下单都超时过期(在redis的基础上多加一层判断，怕redis服务器宕机)
			if(StringUtils.isBlank(redisTemplate.opsForValue().get(userIdAndreqNo)) &&
			   goldInvestOrder.getExpireTime().before(DateUtil.strToDateLong(DateUtil.getDateTime()))){
				//确认下单失败(预下单超时过期)
				throw new ServiceException(GoldInfoCode.PREPAY_ORDER_EXPIRE.getStatus(), GoldInfoCode.PREPAY_ORDER_EXPIRE.getMsg());
			}
		} catch (ServiceException e) {
			logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the 预下单超时过期"); 
			throw e;
		}catch (Exception e) {
		    logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the error is [redis错误]");
		}
		//查询投资订单详情表
		GoldInvestOrderInfo goldInvestOrderInfo = goldInvestOrderInfoDao.selectByPrimaryKey(goldInvestOrder.getId());
	    logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the param [goldInvestOrderInfo={}]",goldInvestOrderInfo);
		if(goldInvestOrderInfo == null)
			throw new ServiceException(GoldInfoCode.INVEST_INFO_ORDER_NOT_EXIST.getStatus(), GoldInfoCode.INVEST_INFO_ORDER_NOT_EXIST.getMsg());
		//根据产品id查询产品编码
		GoldProduct goldProduct = goldProductDao.selectByPrimaryKey(goldInvestOrder.getProductId());
	    logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the goldProduct is [{}]",goldProduct);
	    //是否有该产品
		if(goldProduct == null)
			 throw new ServiceException(GoldInfoCode.LOAN_NOT_EXITS.getStatus(), GoldInfoCode.LOAN_NOT_EXITS.getMsg());
		//确认下单，保存订单信息进invest表
		InvestDTO investDTO = new InvestDTO(); 
		String investId = UUIDGenerator.generate();
		investDTO.setId(investId);
		investDTO.setLoanTypeKey(goldProduct.getTypeKey());
		investDTO.setLoanTypeId(goldProduct.getTypeId());
		investDTO.setUserId(uid);
		investDTO.setLoanId(goldProduct.getLoanId());
		investDTO.setProductId(goldProduct.getId());
		investDTO.setAmount(goldInvestOrder.getRealAmount());
		investDTO.setRate(goldProduct.getRate());
		investDTO.setPlusRate(goldProduct.getRatePlus());
		investDTO.setYears(goldProduct.getYears());
		investDTO.setMonths(goldProduct.getMonths());
		investDTO.setDays(goldProduct.getDays());
		if(RepaymentMethod.BulletRepayment.getIndex() == goldProduct.getMethod())
			investDTO.setRepaymentMethod(RepaymentMethod.BulletRepayment);
		investDTO.setStatus(com.gomemyc.trade.enums.InvestStatus.INITIAL);
		investDTO.setSubmitTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
		investDTO.setSource(InvestSource.INVEST);
		if(goldInvestOrderInfo != null && StringUtils.isNotBlank(goldInvestOrderInfo.getCouponId()))
			investDTO.setCouponPlacememtId(goldInvestOrderInfo.getCouponId());
	    logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the params is [investDTO={}]",investDTO);
		try {
			investService.investmentService(investDTO);
		} catch (Exception e) {
		    logger.info("confirmBySelect  in  GoldInvestServiceImpl ,the error is [保存信息进tbl_invest表出错]");
			throw new ServiceException(GoldInfoCode.INVEST_SAVE_ERROR.getStatus(), GoldInfoCode.INVEST_SAVE_ERROR.getMsg());
		}
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("amount", goldInvestOrder.getAmount());
		map.put("loanId", goldProduct.getLoanId());
		map.put("couponAmount", goldInvestOrderInfo.getCouponAmount());
		map.put("couponId", goldInvestOrderInfo.getCouponId());
		map.put("investId", investId);
		map.put("realAmount", goldInvestOrder.getRealAmount());
		return map;
	}
	
	/**
	 * 
	 * 用户确认下单(调用黄金钱包接口，确认下单)
	 * 
	 * @param uid        (String) 用户id(必填)
	 * @param reqNo      (String) 订单号(必填)
	 * @param investId   (String) 投资id(必填)
	 * @return boolean true确认下单成功  false确认下单失败
	 * @ServiceException
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
	public boolean confirmByGold(String uid,String reqNo,String investId) throws ServiceException{
		// 通过订单号和用户ip 唯一查询订单信息(预下单)
		GoldInvestOrder goldInvestOrder = goldInvestOrderDao.selectObjByUserIdReqNo(uid,reqNo,GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus());
		logger.info("confirmByGold  in  GoldInvestServiceImpl ,the goldInvestOrder is [{}]]",goldInvestOrder);
		// 是否有订单信息(预下单)
		if(goldInvestOrder == null)
			 throw new ServiceException(GoldInfoCode.PREPAY_NOT_EXIST.getStatus(), GoldInfoCode.PREPAY_NOT_EXIST.getMsg());
		//根据产品id查询产品编码
		GoldProduct goldProduct = goldProductDao.selectByPrimaryKey(goldInvestOrder.getProductId());
	    logger.info("confirmByGold  in  GoldInvestServiceImpl ,the goldProduct is [{}]",goldProduct);
	    //是否有该产品
		if(goldProduct == null)
			 throw new ServiceException(GoldInfoCode.LOAN_NOT_EXITS.getStatus(), GoldInfoCode.LOAN_NOT_EXITS.getMsg());
		ConfirmBuyTimeGoldResultDto confirmBuyTimeGold = null;
		try {
			// 调用黄金钱包接口,确认下单
			confirmBuyTimeGold = GoldBuyWallet.confirmBuyTime(goldWalletConstant.getVersion(),
															  goldWalletConstant.getMerchantCode(),
															  DateUtil.getDateTime(),
															  goldInvestOrder.getRealWeight().multiply(new BigDecimal(1000.00)),
															  goldInvestOrder.getRealAmount().multiply(new BigDecimal(100.00)),
															  goldInvestOrder.getRealPrice().multiply(new BigDecimal(100.00)),
															  goldProduct.getGoldProductCode(),
															  reqNo,
															  goldWalletConstant.getSign(),
															  goldWalletConstant.getIp());
		} catch (Exception e) {
			throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		}
		logger.info("confirmByGold  in  GoldInvestServiceImpl ,the confirmBuyTimeGold is [{}]",confirmBuyTimeGold);
		if(confirmBuyTimeGold == null ||
		   confirmBuyTimeGold.getData() == null ||
		   Integer.valueOf(confirmBuyTimeGold.getRetCode()).compareTo(GoldWalletInfoCode.SUCCESS.getStatus()) != 0){
			logger.info("confirmByGold  in  GoldInvestServiceImpl ,the param [GolderrCode={}],[GolderrMsg={}],[confirmBuyTimeGoldData={}]",confirmBuyTimeGold.getRetCode(),confirmBuyTimeGold.getRetMsg(),confirmBuyTimeGold.getData());
			throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		}
		if(StringUtils.isNotBlank(confirmBuyTimeGold.getData().getErrCode()) && 
		   StringUtils.isNotBlank(confirmBuyTimeGold.getData().getErrMsg())){
			logger.info("confirmByGold  in  GoldInvestServiceImpl ,the param [GolderrCode={}],[GolderrMsg={}",confirmBuyTimeGold.getData().getErrCode(),confirmBuyTimeGold.getData().getErrMsg());
			 throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
		}
		//将调用黄金钱包接口查询回来的dto中的信息设置到订单goldInvestOrder中
		goldInvestOrder.setRealPrice(confirmBuyTimeGold.getData().getRealPrice().divide(new BigDecimal(100.00), 2, RoundingMode.CEILING));
		goldInvestOrder.setRealWeight(confirmBuyTimeGold.getData().getRealWeight().divide(new BigDecimal(1000.00), 3, RoundingMode.FLOOR));
		goldInvestOrder.setRealAmount(confirmBuyTimeGold.getData().getRealAmount().divide(new BigDecimal(100.00), 3, RoundingMode.FLOOR));
		goldInvestOrder.setFinishTime(goldInvestOrder.getFinishTime());
		goldInvestOrder.setReqNo(confirmBuyTimeGold.getData().getReqNo());
		goldInvestOrder.setGoldOrderNo(confirmBuyTimeGold.getData().getOrderNo());
		goldInvestOrder.setOrderStatus(Integer.parseInt(confirmBuyTimeGold.getData().getStatus()));
		goldInvestOrder.setBuyType(Integer.parseInt(goldWalletConstant.getBuyType()));
		goldInvestOrder.setErrCode(confirmBuyTimeGold.getData().getErrCode());
		goldInvestOrder.setErrMsg(confirmBuyTimeGold.getData().getErrMsg());
		goldInvestOrder.setId(UUIDGenerator.generate());
		goldInvestOrder.setUserId(uid);
		goldInvestOrder.setAmount(goldInvestOrder.getAmount());
		goldInvestOrder.setProductId(goldInvestOrder.getProductId());
		goldInvestOrder.setProductName(goldProduct.getGoldProcductName());
		goldInvestOrder.setInvestId(investId);
		//将过期时间清空
		goldInvestOrder.setExpireTime(null);
		goldInvestOrder.setRealFinishTime(DateUtil.strToDateLong(confirmBuyTimeGold.getData().getFinishTime()));
		goldInvestOrder.setOrderType(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus());
		logger.info("confirmByGold  in  GoldInvestServiceImpl ,the goldInvestOrder is [{}]",goldInvestOrder);
		// 将购买黄金订单信息保存进数据库
		int insertRows = goldInvestOrderDao.insertSelective(goldInvestOrder);
		logger.info("confirmByGold  in  GoldInvestServiceImpl ,the insertRows is [{}]",insertRows);
		if(insertRows == 0)
			//确认下单失败(保存订单信息失败)
			throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		//确认下单订单状态为处理中，提示确认下单处理中
		if(goldInvestOrder.getOrderStatus().equals(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus()))
		{
			logger.info("confirmByGold  in  GoldInvestServiceImpl ,the confirmOrderStatus is [{}]",goldInvestOrder.getOrderStatus());
			//根据黄金钱包订单号，查询订单信息
			QueryBuyTimeOrderResultDto queryBuyTimeOrder = null;
			try {
				queryBuyTimeOrder = GoldQueryWallet.queryBuyTimeOrder(goldWalletConstant.getVersion(), 
																	  goldWalletConstant.getMerchantCode(), 
																	  goldInvestOrder.getReqNo(),
																	  goldWalletConstant.getSign(), 
																	  goldWalletConstant.getIp());
			} catch (Exception e) {
				logger.info("confirmByGold  in  GoldInvestServiceImpl ,the error is [黄金钱包查询订单信息错误]");
			    throw new ServiceException(GoldInfoCode.GOLD_ERROR.getStatus(), GoldInfoCode.GOLD_ERROR.getMsg());
			}
			logger.info("confirmByGold  in  GoldInvestServiceImpl ,the param [queryBuyTimeOrder={}]",queryBuyTimeOrder);
			if(queryBuyTimeOrder == null || queryBuyTimeOrder.getData() == null)
			    throw new ServiceException(GoldInfoCode.CONFIRM_NOT_EXIST.getStatus(), GoldInfoCode.CONFIRM_NOT_EXIST.getMsg());
			logger.info("confirmByGold  in  GoldInvestServiceImpl ,the param [orderStatus={}]",queryBuyTimeOrder.getData().getStatus());
			//如果订单状态是处理中
			if(queryBuyTimeOrder.getData().getStatus().equals(String.valueOf(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus())))
			{
				logger.info("confirmByGold  in  GoldInvestServiceImpl ,the param [orderStatus={}]",queryBuyTimeOrder.getData().getStatus());
				throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_PROCESSING.getStatus(), GoldInfoCode.AFFIRM_ORDER_PROCESSING.getMsg());
			}
			//如果订单状态是失败
			if(queryBuyTimeOrder.getData().getStatus().equals(String.valueOf(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_FAIL.getStatus())))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * 用户确认下单(更新预下单和确认下单信息)
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @ServiceException
	 *                50002:确认下单失败
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	public void confirmUpdateStatus(String uid,String reqNo) throws ServiceException{
		//更新确认下单订单状态为下单失败
		goldInvestOrderDao.updateByUseridAndReqNo(uid, reqNo, GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_FAIL.getStatus());
		//更新预下单状态为预下单成功，确认下单失败
		goldInvestOrderDao.updateByUseridAndReqNo(uid, reqNo, GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_ERROR.getStatus());
		throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
	}
	
	/**
	 * 
	 * 用户确认下单(更新订单状态，保存订单信息)
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @return  GoldInvestOrderDTO
	 * @ServiceException
	 *                50002:确认下单失败
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	public GoldInvestOrderDTO confirmSaveInvestOrder(String uid,String reqNo) throws ServiceException{
		
		GoldInvestOrderDTO goldInvestOrderDTO = new GoldInvestOrderDTO();
		// 通过订单号和用户ip 唯一查询订单信息为处理中的订单
		GoldInvestOrder goldInvestOrder = goldInvestOrderDao.selectObjByUserIdReqNo(uid,reqNo,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus());
		logger.info("confirmSaveInvestOrder  in  GoldInvestServiceImpl ,the goldInvestOrder is [{}]]",goldInvestOrder);
		//如果确认下单状态为处理中，更新为下单成功
		if(goldInvestOrder!= null && goldInvestOrder.getOrderStatus().equals(GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus()))
			goldInvestOrderDao.updateByUseridAndReqNo(uid, reqNo, GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
		//如果没有处理中订单，查询确认下单成功订单
		if(goldInvestOrder == null)
			goldInvestOrder = goldInvestOrderDao.selectObjByUserIdReqNo(uid,reqNo,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus());
		//确认下单成功，同时保存订单信息成功，将预下单的状态更改为预下单成功，下单成功
		Integer updateRows = goldInvestOrderDao.updateByUseridAndReqNo(uid, reqNo, GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_SUCCESS.getStatus());
		logger.info("confirmSaveInvestOrder  in  GoldInvestServiceImpl ,the updateRows is [{}]",updateRows);
		if(updateRows == 0)
			//确认下单失败(更改预下单订单状态失败)
			throw new ServiceException(GoldInfoCode.AFFIRM_ORDER_ERROR.getStatus(), GoldInfoCode.AFFIRM_ORDER_ERROR.getMsg());
		// 封装要返回的数据
		BeanMapper.copy(goldInvestOrder, goldInvestOrderDTO);
		logger.info("confirmSaveInvestOrder  in  GoldInvestServiceImpl ,the goldInvestOrderDTO is [{}]",goldInvestOrderDTO);
		return goldInvestOrderDTO;
	}
	
	
	
	
	/**
	 *
	 * 查询购买记录
	 *
	 * @param userId  用户id
	 * @param productId  产品id
	 * @param page 当前页
	 * @param pageSize 每页数据量
	 * @return  Page<GoldInvestOrderDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月6日
	 */
	public Page<GoldInvestOrderDTO> listPageByUserIdLoanId(String userId,String productId,int page,
														   int pageSize) throws ServiceException{
		int pageStart = 0;
		if (page != 0 & page != 1)
			pageStart = (page-1)*pageSize;
		List<GoldInvestOrder> goldInvestOrderList = goldInvestOrderDao.listPageByUserIdLoanId(productId,pageStart,pageSize,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
		return new Page<>(BeanMapper.mapList(goldInvestOrderList,GoldInvestOrderDTO.class),page,pageSize, (long) goldInvestOrderDao.getGoldInvestOrderAmount(productId,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus()));
	}

	/**
	 * 查询交易结果
	 * @param userId  用户id
	 * @param reqNo   请求订单号
	 * @return  GoldInvestOrderDTO
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月6日
	 */
	public GoldInvestOrderDTO findResultByUserIdReqNo(String userId,String reqNo){
		GoldInvestOrder goldInvestOrder = goldInvestOrderDao.getResultByUserIdReqNo(userId, reqNo,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
		return goldInvestOrder==null ? new GoldInvestOrderDTO() : BeanMapper.map(goldInvestOrder,GoldInvestOrderDTO.class);
	}

	/**
	 * 统计黄金克数
	 * @param userId  用户id
	 * @param orderStatus 订单状态
	 * @param orderType 交易状态
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月6日
	 */
	@Override
	public BigDecimal statisticsGoldWeight(String userId) {
		return goldInvestOrderDao.getGoidWeight(userId,GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
	}

	/**
	 * 向收益表中插入对象
	 * @param goldEarningsDTO对象
	 * @return 插入的行数，插入为1.
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月23日
	 */
	@Override
	public Integer saveEarnings(GoldEarningsDTO goldEarningsDTO) {
		Integer status = goldEarningsDao.insertGoldEarnings(BeanMapper.map(goldEarningsDTO,GoldEarnings.class));
		return status;
	}

	/**
	 * 查询募集中列表
	 * @param userId  用户id
	 * @param page  页数
	 * @param pageSize  每页显示条数
	 * @return Page<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月13日
	 */
	@Override
	public Page<FinishOrdersDTO>  listPageCollectByUserId(String userId,Integer page,Integer pageSize){
		return 	getIistPageByUserIdUtil( userId,InvestStatus.BJ_SYN_SUCCESS.getIndex(), page,pageSize);
	}
	/**
	 *
	 * 查询收益中列表
	 *
	 * @author liujunhan
	 * @param userId 用户id
	 * @return Page<GoldInvestProductDTO>
	 * @since JDK 1.8
	 */
	@Override
	public Page<FinishOrdersDTO> listPageEarningsByUserId(String userId,Integer page,Integer pageSize) {
		return 	getIistPageByUserIdUtil( userId, InvestStatus.SETTLED.getIndex(), page,pageSize);
	}
	/**
	 *
	 * 查询已完结列表
	 *
	 * @param userId  用户id
	 * @param page  查询的页数
	 * @param pageSize  每页显示条数
	 * @return   Page<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月13日
	 */
	@Override
	public Page<FinishOrdersDTO> listPageClearedByUserId(String  userId, Integer page,Integer pageSize) {
		return 	getIistPageByUserIdUtil( userId, InvestStatus.CLEARED.getIndex(), page,pageSize);
	}
	/**
	 * 查询用户昨日收益
	 * @param userId  用户id
	 * @return  GoldEarnings
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	@Override
	public GoldEarningsDTO getYesterdayEarnings(String userId) {
		DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String date = LocalDate.now().minusDays(1).format(format);
		GoldEarnings goldEarnings = goldEarningsDao.getYesterdayEarnings(userId,date);
		return goldEarnings==null? new GoldEarningsDTO() : BeanMapper.map(goldEarnings,GoldEarningsDTO.class);
	}

	/**
	 * 按照页数查询历史收益
	 * @param userId  用户id
	 * @param page  页码
	 * @param pageSize  显示的条数
	 * @return  List<GoldEarnings>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	@Override
	public List<GoldEarningsDTO> getHistoryEarnings(String userId, int page, int pageSize) {
		int pageStart = 0;
		if (page != 0 && page != 1)
			pageStart = (page - 1) * pageSize;
		List<GoldEarnings> goldEarningsList = goldEarningsDao.getHistoryEarnings(userId, pageStart, pageSize);
		return BeanMapper.mapList(goldEarningsList, GoldEarningsDTO.class);
	}

	/**
	 * 查询历史收益总记录数
	 * @param userId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	@Override
	public int getHistoryEarningrEcord(String userId) {
		return goldEarningsDao.getHistoryEarningrEcord(userId);
	}
	/**
	 * 查询历史总收益
	 * @param userId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	@Override
	public BigDecimal getHistoryTotalEarningrs(String userId){
		return goldEarningsDao.getHistoryTotalEarning(userId);
	}
//	/**
//	 * 公共方法抽取：招募中，收益中，已完结 方法名需要修改
//	 * @param userId  用户id
//	 * @param tblInvestStatus
//	 * @param page
//	 * @param pageSize
//	 * @return  int
//	 * @since JDK 1.8
//	 * @author liujunhan
//	 * @date 2017年3月30日
//	 */
//	public Page<FinishOrdersDTO> getIistPageByUserIdUtil(String  userId,Integer tblInvestStatus, Integer page,Integer pageSize) {
//		int pageStart = page < 2 ? 0 : (page - 1) * pageSize;
//		List<FinishOrders> listFinishOrders = goldInvestOrderDao.listGoldInvestProduct(userId, pageStart, pageSize, tblInvestStatus, GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
//		int totalElements = goldInvestOrderDao.getCollectAmount(userId, tblInvestStatus, GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
//		for (FinishOrders finishOrders : listFinishOrders)
//			finishOrders.setAccruedIncomeAmount(goldEarningsDao.getTotalEarningByInvestOrderId(userId, finishOrders.getId()));
//		return new Page<>(BeanMapper.mapList(listFinishOrders, FinishOrdersDTO.class), page, pageSize, (long) totalElements);
//	}


	/**
	 *
	 * 公共方法抽取：招募中，收益中，已完结 调用标的服务的方法
	 *
	 * @param userId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月30日
	 */
	public Page<FinishOrdersDTO> getIistPageByUserIdUtil(String  userId, Integer tblInvestStatus, Integer page, Integer pageSize){
		List<InvestDTO> investDTOs = null;
		Long totalElements = 0L ;
		if (page<0)
			return new Page<>(null,0,0,0L);
		//调用标的服务，根据用户id，和订单状态查询投资id
		try {
		investDTOs = investService.listInvestByUserIdAndInvestStatus(userId,tblInvestStatus,page,pageSize,"GOLD");
		logger.info("getIistPageByUserIdUtil  in  GoldInvestServiceImpl ,the investDTOs is [{}]",investDTOs);
		}catch (Exception e){
			logger.info("getIistPageByUserIdUtil  in  GoldInvestServiceImpl ,the investDTOs is [{}]","调用myc-trade-api的investService.listInvestByUserIdAndInvestStatus服务失败");
			logger.info("getIistPageByUserIdUtil  in  GoldInvestServiceImpl ,the exception is [{}]",e);
			return null;
		}
		if (investDTOs == null) {
			return new Page<>(null, 0, 0, 0L);
		}
		List<FinishOrdersDTO> finishOrdersDTOs = Lists.newArrayListWithExpectedSize(investDTOs.size());
		//根据投资id查询所需要的展示信息
		for (InvestDTO investDTO : investDTOs){
			FinishOrders finishOrders = goldInvestOrderDao.getFinishOrders(userId,investDTO.getId(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
			if(null != finishOrders) {
				FinishOrdersDTO finishOrdersDTO = BeanMapper.map(finishOrders, FinishOrdersDTO.class);
				finishOrdersDTO.setMethod(GoldMethodStatusEnum.METHOD_STATUS_ONCE.getKey());
				finishOrdersDTOs.add(finishOrdersDTO);
			}
		}
		try {
			totalElements = investService.investCountByUserIdAndInvestStatus(userId,tblInvestStatus,"GOLD").longValue();
			logger.info("getIistPageByUserIdUtil  in  GoldInvestServiceImpl ,the totalElements is [{}]",totalElements);
		}catch (Exception e){
			logger.info("getIistPageByUserIdUtil  in  GoldInvestServiceImpl ,the investDTOs is [{}]","调用myc-trade-api的investService.investCountByUserIdAndInvestStatus()服务失败");
			logger.info("getIistPageByUserIdUtil  in  GoldInvestServiceImpl ,the exception is [{}]",e);
		}
		return new Page<>(finishOrdersDTOs, page, pageSize, totalElements);
}
	/**
	 *
	 * 根据产品id和订单号查询信息
	 *
	 * @param userId  用户id
	 * @param reqNo  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	@Override
	public GoldInvestOrderDTO getResultByUserIdReqNo(String userId, String reqNo) {
		return BeanMapper.map(goldInvestOrderDao.getObjByUserIdReqNo(userId,reqNo,GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus()),GoldInvestOrderDTO.class);
	}

	@Override
	public List<GoldInvestOrderDTO> selectGoldInvestOrderByProductId(String productId) {

		return BeanMapper.mapList(goldInvestOrderDao.selectGoldInvestOrderByProductId(productId,GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus()),GoldInvestOrderDTO.class);

	}

	@Override
	public Integer findRecordAmount(String userId, String productId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 *
	 * 根据产品id和状态，查询所有关联订单的投资总额
	 *
	 * @param productId  商品id
	 * @param orderStatus 订单状态
	 * @param orderType 交易类型
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年4月26日
	 */
	@Override
	public BigDecimal getSumAmountByProductId(String productId, Integer orderType, Integer orderStatus) {
		return goldInvestOrderDao.getByProductId(productId, orderStatus, orderType);
	}
}
  
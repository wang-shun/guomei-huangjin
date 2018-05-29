package com.gomemyc.trade.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.AssignService;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.PayAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.BatchAccountErrorDataDto;
import com.gomemyc.agent.resp.BatchProdTenderDataDto;
import com.gomemyc.agent.resp.BatchProdTenderRSDto;
import com.gomemyc.agent.resp.BatchProdTenderResultDto;
import com.gomemyc.agent.resp.OrderQueryResultDto;
import com.gomemyc.agent.resp.PublishRecallResultDto;
import com.gomemyc.agent.util.DateUtil;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponBucket;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.coupon.model.enums.InvestTimeUnit;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductService;
import com.gomemyc.model.enums.Realm;
import com.gomemyc.model.misc.RealmEntity;
import com.gomemyc.model.user.User;
import com.gomemyc.sms.SMSType;
import com.gomemyc.trade.bridge.InvestBridge;
import com.gomemyc.trade.constant.OrderResultConstant;
import com.gomemyc.trade.constant.SyncResultConstant;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestSource;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.trade.util.DTOUtils;
import com.gomemyc.trade.util.DateUtils;
import com.gomemyc.trade.util.JsonHelper;
import com.gomemyc.trade.util.NumberUtil;
import com.gomemyc.trade.util.RedisScriptUtil;
import com.gomemyc.user.api.UserService;
import com.gomemyc.util.BeanMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class InvestServiceImpl implements InvestService{
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired 
    @Qualifier("redisLoanTemplate")
    private RedisTemplate<String, String> redisTemplate;
    
    @Reference
    LoanService loanService;
    
    @Autowired
    InvestDao investDao;
    
    @Reference
    UserService userService;
    
    @Reference
    CouponService couponService;
    
    @Reference
    com.gomemyc.account.service.InvestService investService;
    
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;
    
    @Autowired
    AgentConfig agentConfig;
    
    @Reference
    AccountService accountService;
    
    @Reference
    private AssignService assignService;
    
    @Autowired
    InvestBridge investBridge;
    
    @Reference
    ProductService productService;

    
    /**
     * 
     */
    @Override
    public CouponBucket invest(String userId, String productId, BigDecimal amount, String placementId, Map<String, String> params)
            throws ServiceException {
        
        // 用户id不能为空
        if (StringUtils.isBlank(userId)) {
            throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
        }
        
        // 产品id不能为空
        if (StringUtils.isBlank(productId)) {
            throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        // 投资金额为正数
        if (null == amount ||amount.signum() <= 0) {
            throw new ServiceException(ExceptionCode.INVEST_AMOUNT_POSITIVE.getIndex(), 
                                       ExceptionCode.INVEST_AMOUNT_POSITIVE.getErrMsg());
        }
        
        // 使用redis分配金额
        String orderId = UUIDGenerator.generate();
        try {
            
            logger.info("ready to excute invest script, the orderId = {}, productId = {}, userId = {}, amount = {} ", 
                                                            orderId, productId, userId, amount.toString());
            
            String scriptResult = redisTemplate.execute(RedisScriptUtil.getInvestScript(), 
                                                        redisTemplate.getStringSerializer(), 
                                                        redisTemplate.getStringSerializer(), 
                                                        Arrays.asList(productId, userId, amount.toString(), orderId, String.valueOf(System.currentTimeMillis())), 
                                                        "");
            
            logger.info("end to excute invest script, the orderId = {}, productId = {}, userId = {}, amount = {}, result = {} ", 
                                                          orderId, productId, userId, amount.toString(), scriptResult);

            
            // 返回如果不是数字,分配金额失败
            if(!NumberUtil.isNumeric(scriptResult)){
                
                ExceptionCode exception = EnumUtils.getEnum(ExceptionCode.class, scriptResult);
                
                if (null != exception) {
                    throw new ServiceException(exception.getIndex(), exception.getErrMsg());
                }
                
                // 执行脚本错误码未找到
                throw new ServiceException(ExceptionCode.INVEST_EXCUTE_SCRIPT_ERROR.getIndex(), 
                                           ExceptionCode.INVEST_EXCUTE_SCRIPT_ERROR.getErrMsg());
            }
            
        } catch (ServiceException ex) {
            
            logger.error("fail to excute invest script, there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            
            logger.error("fail to excute invest script, there is a exception : ", ex);
            
            throw new ServiceException(ExceptionCode.INVEST_EXCUTE_SCRIPT_EXCEPTION.getIndex(), 
                                       ExceptionCode.INVEST_EXCUTE_SCRIPT_EXCEPTION.getErrMsg(),
                                       ex);
        }
        
        
        long startTime = System.currentTimeMillis();    // 记录开始时间
        
        boolean investReuslt = false;   // 是否成功,发送mq消息使用,防止Mq消息接收后,从库没有同步数据
        
        CouponBucket couponBucket = null;   // 投资分享红包
        try {
            
            ProductDTO productDTO = loanService.getProduct(productId);
            if (null == productDTO) {
                
                throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(), 
                                           ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
            }
            
            CouponPlacement couponPlacement = null;
            
            if (!StringUtils.isBlank(placementId)) {
                couponPlacement = couponService.findCouponPlacementbyId(null, placementId);
                if (null == couponPlacement) {
                    
                    throw new ServiceException(ExceptionCode.COUPON_NOT_EXIST.getIndex(), 
                                               ExceptionCode.COUPON_NOT_EXIST.getErrMsg());
                }
            }
            
            User user = userService.findByUserId(userId);
            
            if (null == user) {
                
                throw new ServiceException(ExceptionCode.INVEST_USER_EXIST.getIndex(), 
                                           ExceptionCode.INVEST_USER_EXIST.getErrMsg());
            }
            
            // 检测投资规则
            this.checkInvestRules(productDTO, couponPlacement, amount, user);
            
            logger.info("success to pass invest rules, orderId = {}, productId = {}, userId = {}, amount = {} ", 
                                                            orderId, productId, userId, amount.toString());
            
            // 生成原始投资记录
            Invest invest = new Invest();
            invest.setId(orderId);  // 订单号
            invest.setMobile(user.getMobile()); // 用户手机号
            invest.setName(user.getName()); // 用户姓名
            invest.setLoanTypeKey(productDTO.getTypeKey()); // 标的类型键值
            invest.setLoanTypeId(productDTO.getTypeId());   // 标的类型id
            invest.setUserId(userId);   // 用户id
            invest.setLoanId(productDTO.getLoanId());
            invest.setProductId(productDTO.getId());
            invest.setAmount(amount);
            invest.setRate(productDTO.getRate());
            invest.setPlusRate(productDTO.getPlusRate());
            invest.setYears(productDTO.getYears());
            invest.setMonths(productDTO.getMonths());
            invest.setDays(productDTO.getDays());
            invest.setRepaymentMethod(productDTO.getMethod());
            invest.setStatus(InvestStatus.INITIAL); // 投资初始值
            invest.setSubmitTime(new Date());
            invest.setDebtAmount(BigDecimal.ZERO);
            invest.setSource(InvestSource.INVEST);
            invest.setCouponPlacememtId(placementId);
            invest.setReward(productDTO.isUserReward());
            invest.setDebted(Boolean.TRUE.equals(productDTO.getDebted()) ? true : false);
            
            // 保存投资
            if (1 != investDao.invest(invest)) {
                
                throw new ServiceException(ExceptionCode.INVEST_INITIAL_FAILED.getIndex(), 
                                           ExceptionCode.INVEST_INITIAL_FAILED.getErrMsg());
            } 
            
            logger.info("sucess to initial invest, orderId = {}, productId = {}, userId = {}, amount = {} ", 
                                                            orderId, productId, userId, amount.toString());
            
            // 计算用户真实扣除金额和奖券(抵现券)金额
            BigDecimal placementAmount = BigDecimal.ZERO ;
            if (null != couponPlacement && 
                    null != couponPlacement.getCouponPackage() &&
                    CouponType.CASH.equals(couponPlacement.getCouponPackage().getType())) {
                
                placementAmount = BigDecimal.valueOf(couponPlacement.getCouponPackage().getParValue());
                logger.info("success to calculate placement amount, the placementAmount = {}, orderId = {}, productId = {}, userId = {}, amount = {} ", 
                                                            placementAmount, orderId, productId, userId, amount.toString());
            }
            
            // 冻结奖券
            if (null != couponPlacement && 
                    !couponService.useCouponPlacementById(couponPlacement.getId(), 
                                                          new RealmEntity(Realm.INVEST, invest.getId()),
                                                          amount,
                                                          productId,
                                                          productDTO.getTotalDays(),
                                                          productDTO.getTypeId())) {
                
                throw new ServiceException(ExceptionCode.INVEST_FROZEN_COUPON_FAILED.getIndex(), 
                                           ExceptionCode.INVEST_FROZEN_COUPON_FAILED.getErrMsg());
            }
            
            logger.info("sucess to use coupon, orderId = {}, productId = {}, userId = {}, amount = {} ", 
                                                            orderId, productId, userId, amount.toString());
            
            // 冻结用户资金
            String localFreezeId = investService.investFreeze(invest.getId(), 
                                                              userId, 
                                                              productDTO.getLoanId(), 
                                                              amount.subtract(placementAmount), 
                                                              placementAmount);
            
            if (StringUtils.isBlank(localFreezeId)) {
                
                logger.info("fail to invest, invoke invest freeze has do not return a valiable orderId, orderId = {}, productId = {}, userId = {}, amount = {} ", 
                                                            orderId, productId, userId, amount.toString());
                
                throw new ServiceException(ExceptionCode.INVEST_LOCAL_FORZEN_FAIL.getIndex(), 
                                           ExceptionCode.INVEST_LOCAL_FORZEN_FAIL.getErrMsg());
            }
            
            logger.info("sucess to freeze, orderId = {}, productId = {}, userId = {}, amount = {}, freeOrderId ={}", 
                                                            orderId, productId, userId, amount.toString(), localFreezeId);
            
            // 记录返回流水号,并将状态改为本地冻结成功
            if (1 != investDao.updateLocalFrozenNo(invest.getId(), localFreezeId)) {
                
                throw new ServiceException(ExceptionCode.INVEST_UPDATE_LOCAL_FROZEN_NO_FAIL.getIndex(), 
                                           ExceptionCode.INVEST_UPDATE_LOCAL_FROZEN_NO_FAIL.getErrMsg());
            }
            
            logger.info("sucess to update local frozen no, orderId = {}, productId = {}, userId = {}, amount = {}, freeOrderId ={}", 
                                                           orderId, productId, userId, amount.toString(), localFreezeId);
            
            // 投资成功
            investReuslt = true; 

            // 生成投资分享红包
            couponBucket = couponService.investCouponBucket(orderId, userId);
            return couponBucket;
            
        } catch (ServiceException ex) {
            
            logger.error("fail to invest, there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            
            logger.error("fail to invest, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        } finally {
            
            // 发送投资mq,分两种情况,投资成功: 1.放开该用户该产品的投资限制; 2.同步北京银行投资接口和本地投资接口
            // 投资失败: 1.释放投资金额; 2.解冻用户资金和解冻奖券
            Map<String, String> map = Maps.newHashMap();
            map.put("investId", orderId);
            map.put("userId", userId);
            map.put("amount", amount.toString());
            map.put("productId", productId);
            map.put("success", investReuslt ? "1" : "0");
           
            try {
                logger.info("send invest message, investId = {}, userId = {}, amount = {}, productId = {}, success = {}",
                                                                       orderId, userId, amount, productId, investReuslt);
                Message message = new Message(MQTopic.MYC_INVEST.getValue(), JsonHelper.getJson(map).getBytes());
                SendResult sendResult = mqProducer.send(message);
                logger.info("success send invest message, investId = {}, result = {}", orderId, sendResult);
                
            } catch (Exception ex) {
                logger.error("fail to send invest message, there is a exception : ", ex);
            }
            
            logger.info("end to invest, orderId = {}, costTime = {} ", orderId, System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 投资规则检测
     * @param productDTO
     * @param placement
     * @param amount
     * @return
     * @throws ServiceException
     * @author lujixiang
     * @date 2017年3月13日
     *
     */
    private boolean checkInvestRules(ProductDTO productDTO, 
                                     CouponPlacement placement, 
                                     BigDecimal amount,
                                     User user) throws ServiceException{
        
        // 用户购买规则(状态为启用,不能为企业用户,不能为产品发布者)
        if (null == user || !user.isEnabled() || user.isEnterprise() || 
                user.getId().equals(productDTO.getUserId())) {
            
            throw new ServiceException(ExceptionCode.INVEST_USER_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_USER_LIMIT.getErrMsg());
        }
        
        // 检查奖券使用规则
        if (null != placement) {
            
            // 标的不能使用奖券
            if (!productDTO.isUseCoupon()) {
                throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ABADON_COUPON.getIndex(), 
                                           ExceptionCode.INVEST_PRODUCT_ABADON_COUPON.getErrMsg());
            }
            
            // 奖券状态
            if (!CouponStatus.PLACED.equals(placement.getStatus())) {
                throw new ServiceException(ExceptionCode.INVEST_COUPON_DISABLE.getIndex(), 
                                           ExceptionCode.INVEST_COUPON_DISABLE.getErrMsg());
            }
            
            // 检查奖券的投资额限制(最小投资额、最短期限和最长期限)
            CouponPackage couponPackage = placement.getCouponPackage();
            if (couponPackage != null) {
                
                int minimumDuration=InvestTimeUnit.MONTH.equals(couponPackage.getInvestTimeUnit())?couponPackage.getMinimumDuration() * 30:couponPackage.getMinimumDuration();
                int maximumDuration=InvestTimeUnit.MONTH.equals(couponPackage.getInvestTimeUnit())?couponPackage.getMaximumDuration() * 30:couponPackage.getMaximumDuration();
                
                if ((couponPackage.getMinimumInvest() > 0 && 
                            amount.compareTo(new BigDecimal(couponPackage.getMinimumInvest())) < 0)
                        || (minimumDuration > 0 && productDTO.getTotalDays() < minimumDuration)
                        || (maximumDuration > 0 && productDTO.getTotalDays() > maximumDuration)) {
                    
                    throw new ServiceException(ExceptionCode.INVEST_COUPON_LIMITED_PRODUCT.getIndex(), 
                                               ExceptionCode.INVEST_COUPON_LIMITED_PRODUCT.getErrMsg());
                }
                
                
                //如果该券只能适用于特定的产品
                if (StringUtils.isNotBlank(couponPackage.getLoanId())
                        && !couponPackage.getLoanId().equals(productDTO.getId())) {
                    
                    throw new ServiceException(ExceptionCode.INVEST_COUPON_LIMITED_PRODUCT.getIndex(), 
                                               ExceptionCode.INVEST_COUPON_LIMITED_PRODUCT.getErrMsg());
                }

                //如果该券只能适用于特定的类型（特定的产品类型）
                if (StringUtils.isNotBlank(couponPackage.getProductId())
                        && !couponPackage.getProductId().equals(productDTO.getTypeId())) {
                    
                    throw new ServiceException(ExceptionCode.INVEST_COUPON_LIMITED_LOAN_TYPE.getIndex(), 
                                               ExceptionCode.INVEST_COUPON_LIMITED_LOAN_TYPE.getErrMsg());
                }
                
                //如果该券设置了加息天数（仅在加息券中存在该分支）
                //* 暂规定：加息天数 > 产品天数, 本产品(标)不能用此券,（如果标的单位是月，则认为每个月30天）
                if (CouponType.INTEREST.equals(couponPackage.getType()) && 
                        couponPackage.getRateDays() != 0 &&
                        couponPackage.getRateDays() > productDTO.getTotalDays()) {
                    throw new ServiceException(ExceptionCode.INVEST_COUPON_LIMITED_RATE_DAYS.getIndex(), 
                                               ExceptionCode.INVEST_COUPON_LIMITED_RATE_DAYS.getErrMsg());
                }
            }
        }
        
        
        // 标的规则检测(总金额和总投资次数)
        Map<String, Object> amountAndTimes = investDao.sumAmountAndTimesByLoan(productDTO.getId(), user.getId());
        
        BigDecimal productAmount = null == amountAndTimes || null == amountAndTimes.get("totalAmount") ? BigDecimal.ZERO : new BigDecimal(amountAndTimes.get("totalAmount").toString());
        int times = null == amountAndTimes || null == amountAndTimes.get("num") ? 0 : Integer.parseInt(amountAndTimes.get("num").toString());
        
        // 产品最大投资总额限制
        if (null != productDTO.getMaxTotalAmount() &&
                productDTO.getMaxTotalAmount().signum() > 0 &&
                productAmount.add(amount).compareTo(productDTO.getMaxTotalAmount()) > 0) {
            
            throw new ServiceException(ExceptionCode.INVEST_PRODUCT_TOTAL_AMOUNT_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_PRODUCT_TOTAL_AMOUNT_LIMIT.getErrMsg());
        }
        
        if (productDTO.getInvestMaxTimes() > 0 &&
                times >= productDTO.getInvestMaxTimes() ) {
            
            throw new ServiceException(ExceptionCode.INVEST_PRODUCT_TIMES_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_PRODUCT_TIMES_LIMIT.getErrMsg());
        }
        
        // 标的类型规则检测(总金额和总投资次数)
        Map<String, Object> loanTypeAmountAndTimes = investDao.sumAmountAndTimesByLoanType(productDTO.getTypeKey(), user.getId());
        
        BigDecimal loanTypeAmount = null == loanTypeAmountAndTimes || null == loanTypeAmountAndTimes.get("totalAmount") ? BigDecimal.ZERO : new BigDecimal(loanTypeAmountAndTimes.get("totalAmount").toString());
        int loanTypeTimes = null == loanTypeAmountAndTimes || null == loanTypeAmountAndTimes.get("num") ? 0 : Integer.parseInt(loanTypeAmountAndTimes.get("num").toString());
         
        if (null != productDTO.getTypeMaxTotalAmount() &&
                productDTO.getTypeMaxTotalAmount().signum() > 0 &&
                loanTypeAmount.add(amount).compareTo(productDTO.getTypeMaxTotalAmount()) > 0) {
            
            throw new ServiceException(ExceptionCode.INVEST_LOANTYPE_TOTAL_AMOUNT_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_LOANTYPE_TOTAL_AMOUNT_LIMIT.getErrMsg());
        }
        
        if (productDTO.getTypeMaxTimes() > 0 &&
                loanTypeTimes >= productDTO.getTypeMaxTimes() ) {
            
            throw new ServiceException(ExceptionCode.INVEST_LOANTYPE_TIMES_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_LOANTYPE_TIMES_LIMIT.getErrMsg());
        }
        
        return true;
    }

	@Override
	public InvestDTO findById(String id) throws ServiceException {
		if(StringUtils.isEmpty(id)){
			throw new ServiceException(ExceptionCode.INVEST_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_ID_REQUIRED.getErrMsg());
		}
		Invest invest=investDao.findById(id);
		InvestDTO dto=DTOUtils.toDTO(invest);
		return dto;
	}

	@Override
	public List<InvestDTO> findListByProductIdAndStatus(String productId, InvestStatus... stutus) throws ServiceException{
		if(StringUtils.isEmpty(productId)){
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
		}
		List<Invest> list=investDao.findListByProductIdAndStatus(productId, stutus);
		return DTOUtils.toListDTO(list);
	}
	
	@Override
	public Boolean update(InvestDTO investDTO) throws ServiceException {
		try {
			if(investDTO==null){
				throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(), 
	                    ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
			}
			int count= investDao.update(DTOUtils.toOTD(investDTO));
			if(count>0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(), 
                    ExceptionCode.INVEST_NOT_EXIST.getErrMsg(),e);
		}
	}

    /**
     * 根据订单id，更新订单状态。
     *
     * @param id 订单id
     * @param status 状态
     * @return
     */
    @Override
    public Integer updateStatusById(String id, Integer status) throws ServiceException {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("status", status);
        return investDao.updateStatusById(params);
    }

    @Override
    public Integer updateBjDfCodeById(String id, String bjDfCode) throws ServiceException {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("bjDfCode", bjDfCode);
        return investDao.updateBjDfCodeById(params);
    }

    @Override
    public Integer updateBjSynCodeById(String id, String bjSynCode) throws ServiceException {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("bjSynCode", bjSynCode);
        return investDao.updateBjSynCodeById(params);
    }

    @Override
    public Integer updateLocalDfCodeById(String id, String localDfCode) throws ServiceException {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("localDfCode", localDfCode);
        return investDao.updateLocalDfCodeById(params);
    }

	@Override
	public Boolean checkoutInvestAmount(String userId, String productId, BigDecimal amount, BigDecimal maxTotalAmount, Integer maxTotalTimes) {
		// 标的类型规则检测(总金额和总投资次数)
        Map<String, Object> loanTypeAmountAndTimes = investDao.sumAmountAndTimesByLoan(productId, userId);
        BigDecimal loanTypeAmount = new BigDecimal(loanTypeAmountAndTimes.get("totalAmount").toString());
        int loanTypeTimes = Integer.parseInt(loanTypeAmountAndTimes.get("num").toString());
        if(amount == null){
        	throw new ServiceException(ExceptionCode.INVEST_AMOUNT_POSITIVE.getIndex(), 
                    ExceptionCode.INVEST_AMOUNT_POSITIVE.getErrMsg());
        }
        
        if (maxTotalAmount != null && maxTotalAmount.compareTo(amount.add(loanTypeAmount)) == -1) {
            
            throw new ServiceException(ExceptionCode.INVEST_LOANTYPE_TOTAL_AMOUNT_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_LOANTYPE_TOTAL_AMOUNT_LIMIT.getErrMsg());
        }
        
        if (maxTotalTimes != null && maxTotalTimes > 0 && (loanTypeTimes + 1) > maxTotalTimes ) {
            
            throw new ServiceException(ExceptionCode.INVEST_LOANTYPE_TIMES_LIMIT.getIndex(), 
                                       ExceptionCode.INVEST_LOANTYPE_TIMES_LIMIT.getErrMsg());
        }
        
        return true;
	}
	
	@Override
	public String investmentService(InvestDTO investDto) throws ServiceException {
		Invest invest = BeanMapper.map(investDto, Invest.class);
		String flag="";
		int save=0;
		try {
			save = investDao.save(invest);
			if (save==1) {
				flag=investDto.getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	@Override
	public InvestDTO getInvestment(String investId) throws ServiceException {
		Invest invest=investDao.getInvestment(investId);
		return BeanMapper.map(invest, InvestDTO.class);
	}

	@Override
	public boolean isFirstInvestment(String userId) throws ServiceException {
		// TODO Auto-generated method stub
		return false;
	}


    @Override
    public Page<InvestDTO> findListByLoanAndStatus(String loanId, List<Integer> investStatusList, Integer pageNumber, Integer pageSize) throws ServiceException {
        if (StringUtils.isEmpty(loanId)) {
            throw new ServiceException(ExceptionCode.INVEST_LOAN_ID_NULL.getIndex(),ExceptionCode.INVEST_LOAN_ID_NULL.getErrMsg());
        }
        if (null == investStatusList || investStatusList.size() == 0) {
            throw new ServiceException(ExceptionCode.INVEST_STATUS_NULL.getIndex(),ExceptionCode.INVEST_STATUS_NULL.getErrMsg());
        }
        if (pageNumber == null || pageNumber < 0) {
            throw new ServiceException(ExceptionCode.COMMON_PAGE_NUMBER_INVALID.getIndex(),ExceptionCode.COMMON_PAGE_NUMBER_INVALID.getErrMsg());
        }
        if (pageSize == null || pageSize < 0) {
            throw new ServiceException(ExceptionCode.COMMON_PAGE_SIZE_INVALID.getIndex(),ExceptionCode.COMMON_PAGE_SIZE_INVALID.getErrMsg());
        }
        // 计算开始行数
        int startRow = (pageNumber - 1) * pageSize;
        List<Invest> list = investDao.findListByLoanAndStatus(loanId, investStatusList, startRow, pageSize);
        Long count = investDao.findCountByLoanAndStatus(loanId, investStatusList).longValue();
        if (list.isEmpty()) {
            return new Page<InvestDTO>(Lists.newArrayList(), pageNumber, pageSize, count);
        }
        List<InvestDTO> dtoList = BeanMapper.mapList(list, InvestDTO.class);
        Page<InvestDTO> page = new Page<InvestDTO>(dtoList, pageNumber, pageSize, count);
        return page;
    }

    @Override
    public List<InvestDTO> listInvestByUserIdAndInvestStatus(String userId, Integer investStatus, Integer pageNumber, Integer pageSize,String loanTypeKey) {
    	if (StringUtils.isEmpty(userId)) {
            throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(),ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
        }
    	if (StringUtils.isEmpty(loanTypeKey)) {
    		throw new ServiceException(ExceptionCode.LOAN_TYPE_KEY_NO_EXITS.getIndex(),ExceptionCode.LOAN_TYPE_KEY_NO_EXITS.getErrMsg());
		}
        if (pageNumber == null || pageNumber < 0) {
            throw new ServiceException(ExceptionCode.COMMON_PAGE_NUMBER_INVALID.getIndex(),ExceptionCode.COMMON_PAGE_NUMBER_INVALID.getErrMsg());
        }
        if (pageSize == null || pageSize < 0) {
            throw new ServiceException(ExceptionCode.COMMON_PAGE_SIZE_INVALID.getIndex(),ExceptionCode.COMMON_PAGE_SIZE_INVALID.getErrMsg());
        }
        int startRow = (pageNumber - 1) * pageSize;
        List<Invest> list=null;
        try {
        	list=investDao.findListInvestByUserIdAndInvestStatus(userId,investStatus,startRow,pageSize,loanTypeKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (list==null || list.isEmpty()) {
			return new ArrayList<InvestDTO>();
        }
        List<InvestDTO> dtoList = BeanMapper.mapList(list, InvestDTO.class);
		return dtoList;
    }

    @Override
    public Integer investCountByUserIdAndInvestStatus(String userId, Integer investStatus,String loanTypeKey) {
    	if (StringUtils.isEmpty(userId)) {
            throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(),ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
        }
    	List<Invest> in=null;
    	try {
    		in=investDao.findInvestByUserIdAndStatus(userId,investStatus,loanTypeKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if (in==null || in.isEmpty()) {
			in=new ArrayList<Invest>();
		}
        return in.size();
    }

    @Override
    public Boolean releaseInvest(String investId, String productId, String userId, BigDecimal amount) throws ServiceException {
        
        logger.info("ready to release invest, the investId = {} ", investId);
        
        if (StringUtils.isBlank(investId) ||
                StringUtils.isBlank(productId) ||
                StringUtils.isBlank(userId) ||
                null == amount) {
            throw new ServiceException(ExceptionCode.GLOBAL_PARAM_ERROR.getIndex(), 
                                       ExceptionCode.GLOBAL_PARAM_ERROR.getErrMsg());
        }
        
        Invest invest = null;
        
        try {
            
            // 释放投资金额
            String scriptResult = redisTemplate.execute(RedisScriptUtil.getReleaseScript(), 
                                                        redisTemplate.getStringSerializer(), 
                                                        redisTemplate.getStringSerializer(), 
                                                        Arrays.asList(productId, 
                                                                      userId, 
                                                                      null == amount ? "0" : amount.toString(), 
                                                                      investId), 
                                                        "");
            
            logger.info("end to excute release script, the result = {}, productId = {}, userId = {}, amount = {}, orderId = {}", 
                                                        scriptResult, productId, userId, amount, investId);
            
            // 返回如果不是数字,分配金额失败
            if(!NumberUtil.isNumeric(scriptResult) && !"BID_ALREADY_RELEASED".equals(scriptResult)){
                
                logger.info("fail to release invest, the result = {}, productId = {}, userId = {}, amount = {}, orderId = {}", 
                                                            scriptResult, productId, userId, amount, investId);
                
                throw new ServiceException(ExceptionCode.RELEASE_INVEST_EXCUTE_SCRIPT_FAIL.getIndex(), 
                                           ExceptionCode.RELEASE_INVEST_EXCUTE_SCRIPT_FAIL.getErrMsg());
            }

            
            invest = investDao.findById(investId);
            // 没有生成投资记录,不需要回滚金额
            if (null == invest) {
                
                logger.info("success to release invest, the invest is null, the investId = {} ", investId);
                return true;
                
            }
            
            if (!InvestStatus.INITIAL.equals(invest.getStatus())) {
                
                logger.info("fail to release invest, the invest status is not failed, the investId = {}, status = {} ", investId, invest.getStatus());
                throw new ServiceException(ExceptionCode.RELEASE_INVEST_NOT_FAIL.getIndex(), 
                                           ExceptionCode.RELEASE_INVEST_NOT_FAIL.getErrMsg());
                
            }
            
            // 解冻用户奖券
            if (!StringUtils.isBlank(invest.getCouponPlacememtId())) {
                
                // 判断该奖券是否已被该笔投资占用
                CouponPlacement couponPlacement = couponService.findCouponPlacementbyId(null, invest.getCouponPlacememtId());
                
                if (CouponStatus.USED.equals(couponPlacement.getStatus()) &&
                        null != couponPlacement.getSubject() &&
                        invest.getCouponPlacememtId().equals(couponPlacement.getSubject().getEntityId())) {
                    
                    if (!couponService.unusedCouponPlacementById(invest.getCouponPlacememtId(), 
                                                                 new RealmEntity(Realm.INVEST, invest.getId()))) {
                        
                        logger.info("fail to release invest, the investId = {}, placementId = {} ", 
                                                             invest.getId(), invest.getCouponPlacememtId());
                        throw new ServiceException(ExceptionCode.RELEASE_INVEST_EXCUTE_SCRIPT_FAIL.getIndex(), 
                                                   ExceptionCode.RELEASE_INVEST_EXCUTE_SCRIPT_FAIL.getErrMsg());
                   }
                            
                   logger.info("success to unfroze couponPlacement, the investId = {}, placementId = {} ", 
                                                                invest.getId(), invest.getCouponPlacememtId());
                }
            }
            
        } catch (ServiceException ex) {
            
            logger.error("fail to release invest, there is a serviceException : ", ex);
            throw ex;

        } catch (Exception ex) {
            
            logger.info("fail to release invest, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
        }
        
        boolean unFreezeResult = false; // 解冻金额成功标志
        
        try {
            
            String unFreezeNo = investService.investUnfreezeAfterFreeze(invest.getId(), invest.getLocalFreezeNo());
            logger.info("invest unfreeze fund, the investId = {}, unFreezeNo = {}", invest.getId(), unFreezeNo);
            
            if (!StringUtils.isBlank(unFreezeNo)) {
                unFreezeResult = true;
            }
            
        } catch (ServiceException ex) {
            
            // 如果返回已成功或者未找到该条数据,则认为不需要解冻
            /*if (ex.getErrorCode()) {
                
                unFreezeResult = true;
            }*/
            
            logger.info("fail to release invest, there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            
            logger.info("fail to release invest, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
        }
        
        if (!unFreezeResult) {
            
            logger.info("fail to release invest, cannot unfreeze fund, the investId = {}", invest.getId());
            throw new ServiceException(ExceptionCode.RELEASE_INVEST_NOT_FAIL.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
        }
        
        return true;
    }
    
    /**
     * 一手标同步北京银行资金
     * @param invest
     * @return
     * @throws ServiceException
     * @author lujixiang
     * @date 2017年4月13日
     *
     */
    private Boolean investSuccessFirstHand(Invest invest) throws ServiceException{
        
        logger.info("ready invest success first hand, the investId = {}", invest.getId());
        
        // 已同步成功
        if (InvestStatus.SUCCESS.equals(invest.getStatus())) {
            logger.info("success to invest first hand, invest status is already success, the investId = {}", invest.getId());
            return true;
        }
        
     // 只有本地冻结和北京银行冻结成功的投资记录才能同步北京银行
        if (!InvestStatus.LOCAL_FROZEN_SUCCESS.equals(invest.getStatus()) &&
                !InvestStatus.BJ_SYN_SUCCESS.equals(invest.getStatus())) {
            
            logger.info("fail to invest success sync, the invest status is local frozen, the investId = {}, status = {} ", 
                                                                                            invest.getId(), invest.getStatus());
            throw new ServiceException(ExceptionCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getIndex(), 
                                       ExceptionCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getErrMsg());
        }
        
        boolean bjResult = false;
        
        // 判断是否已和北京银行同步
        if (InvestStatus.LOCAL_FROZEN_SUCCESS.equals(invest.getStatus())) {
            
            // 投资人账户
            AccountDTO accountDTO = accountService.getByUserId(invest.getUserId());
            
            if (null == accountDTO) {
                logger.info("fail to invest success sync, account is not exist, the investId = {}, userId = {} ", 
                                                                                            invest.getId(), invest.getUserId());
                throw new ServiceException(ExceptionCode.USER_ACCOUNT_NOT_EXIST.getIndex(), 
                                           ExceptionCode.USER_ACCOUNT_NOT_EXIST.getErrMsg());
            }
            
            // 获取抵现券金额和加息券加息
            BigDecimal placementAmount = BigDecimal.ZERO ;
            int placementRate = 0;
            
            if (!StringUtils.isBlank(invest.getCouponPlacememtId())) {
                CouponPlacement couponPlacement = couponService.findCouponPlacementbyId(null, invest.getCouponPlacememtId());
                if (null == couponPlacement) {
                    logger.info("fail to invest success sync, couponPlacement is not exist, the investId = {}, placementId = {} ", 
                                                                                            invest.getId(), invest.getCouponPlacememtId());
                    throw new ServiceException(ExceptionCode.COUPON_NOT_EXIST.getIndex(), 
                                               ExceptionCode.COUPON_NOT_EXIST.getErrMsg());
                }
                
                // 抵现券
                if (null != couponPlacement && 
                        null != couponPlacement.getCouponPackage() &&
                        CouponType.CASH.equals(couponPlacement.getCouponPackage().getType())) {
                    
                    placementAmount = BigDecimal.valueOf(couponPlacement.getCouponPackage().getParValue());
                    logger.info("success to calculate placement amount, the investId = {}, placementAmount = {} ", 
                                                                            invest.getId(), placementAmount );
                }
                
                // 加息券
                if (null != couponPlacement && 
                        null != couponPlacement.getCouponPackage() &&
                        CouponType.INTEREST.equals(couponPlacement.getCouponPackage().getType())) {
                    
                    placementRate = couponPlacement.getCouponPackage().getParValue();
                    logger.info("success to calculate placement rate, the investId = {}, rate = {} ", 
                                                                          invest.getId(), placementRate );
                }
            }
            
            
            // 调用北京银行投资接口
            LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);   // 标的发标接口
            Date transDate = new Date();
            BatchProdTenderResultDto result = loanAgent.prodBatchBidd(agentConfig.getPlatNo(), // 平台编号
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
                                                                                                         placementAmount, // 抵现券金额
                                                                                                         invest.getAmount().subtract(placementAmount), // 自费金额
                                                                                                         new BigDecimal(placementRate)
                                                                                                                       .divide(new BigDecimal(10000)).toString(),  // 加息券加息 
                                                                                                         DictionaryEnum.SUBJECTPRIORITY0, // 可提优先
                                                                                                         null, // 手续费入账平台
                                                                                                         null, // 手续费金额
                                                                                                         null)  // 备注信息
                                                                              ).toArray(new BatchProdTenderDataDto[1]));
            
            if (null == result) {
                logger.info("fail to invest success sync, fail to invoke bj bid interface, the invoke result is null, the investId = {} ", 
                                                                                                                          invest.getId());
                throw new ServiceException(ExceptionCode.INVEST_INVOKE_BJ_INVEST_RESULT_NULL.getIndex(), 
                                           ExceptionCode.INVEST_INVOKE_BJ_INVEST_RESULT_NULL.getErrMsg());
            }
            
            // 订单受理成功
            if (SyncResultConstant.SUCCESS_CODE.equals(result.getRecode())) {
                
                // 获取订单成功
                List<BatchProdTenderRSDto> batchProdTenderRSDtos = result.getSuccessData();
                if (null != batchProdTenderRSDtos && !batchProdTenderRSDtos.isEmpty()) {
                   
                    for(BatchProdTenderRSDto batchProdTenderRSDto : batchProdTenderRSDtos){
                        if (invest.getId().equals(batchProdTenderRSDto.getDetailNo())) {
                            logger.info("success to sync invest, the investId = {}", invest.getId());
                            // 更新订单状态
                            if (1 != investDao.updateInvestStatus(invest.getId(), 
                                                                  InvestStatus.BJ_SYN_SUCCESS)) {
                                logger.info("fail to invest success sync, fail to update invest status, the investId = {}, status = {}", 
                                                                                            invest.getId(), InvestStatus.BJ_SYN_SUCCESS);
                                throw new ServiceException(ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getIndex(), 
                                                           ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getErrMsg());
                            }
                            bjResult = true;
                            // 北京银行投资成功
                            logger.info("success to update bj sync status, the investId = {}", invest.getId());
                        }
                    }
                }
                
                // 获取失败订单
                List<BatchAccountErrorDataDto> batchAccountErrorDataDtos = result.getErrorData();
                if (null != batchAccountErrorDataDtos && !batchAccountErrorDataDtos.isEmpty()) {
                    
                    for (BatchAccountErrorDataDto batchAccountErrorDataDto : batchAccountErrorDataDtos) {
                        if (invest.getId().equals(batchAccountErrorDataDto.getDetailNo())) {
                            
                            // 订单重复
                            if (SyncResultConstant.ORDER_REPEAT.equals(batchAccountErrorDataDto.getErrorNo())) {
                                
                                // 查询订单真实状态
                                PayAgent payAgent = PayAgent.getInstance(agentConfig);  
                                OrderQueryResultDto orderQueryResultDto = payAgent.queryOrder(agentConfig.getPlatNo(), 
                                                                                              UUIDGenerator.generate(),
                                                                                              DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
                                                                                              DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间, 
                                                                                              invest.getId());
                                
                                if (null == orderQueryResultDto) {
                                    
                                    logger.info("fail to invoke order query interface, the result is null, the investId = {}", invest.getId());
                                    
                                    throw new ServiceException(ExceptionCode.ORDER_QUERY_FAIL.getIndex(), 
                                                               ExceptionCode.ORDER_QUERY_FAIL.getErrMsg());
                                }
                                
                                // 返回结果成功
                                if (SyncResultConstant.SUCCESS_CODE.equals(orderQueryResultDto.getRecode())) {
                                    
                                    logger.info("success query order by invest interface, the investId = {}, result = {}", 
                                                                                               invest.getId(), orderQueryResultDto.getData());
                                    
                                    // 已同步成功
                                    if (null != orderQueryResultDto.getData() &&
                                            OrderResultConstant.SUCCESS.equals(orderQueryResultDto.getData().getStatus())) {
                                        
                                        if (1 != investDao.updateInvestStatus(invest.getId(), 
                                                                              InvestStatus.BJ_SYN_SUCCESS)) {
                                          logger.info("fail to invest success sync, fail to update invest status, the investId = {}, status = {}", 
                                                                                                      invest.getId(), InvestStatus.BJ_SYN_SUCCESS);
                                          throw new ServiceException(ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getIndex(), 
                                                                     ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_STATUS.getErrMsg());
                                        }
                                        // 北京银行投资成功
                                        logger.info("success to update bj sync status, the investId = {}", invest.getId());
                                    }
                                    
                                    // 已同步失败
                                    if (null != orderQueryResultDto.getData() &&
                                            OrderResultConstant.FAIL.equals(orderQueryResultDto.getData().getStatus())) {
                                        
                                        logger.info("fali to sync invest, the investId = {}, the result = {}", invest.getId(), orderQueryResultDto.getRemsg());
                                        // 更新订单状态
                                        if (1 != investDao.updateInvestStatus(invest.getId(), 
                                                                              InvestStatus.SYNC_FAILED)) {
                                            logger.info("fail to update invest status, the investId = {}, status = {}", invest.getId(), InvestStatus.SYNC_FAILED);
                                            throw new ServiceException(ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getIndex(), 
                                                                       ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getErrMsg());
                                        }
                                        // 北京银行投资失败
                                        throw new ServiceException(ExceptionCode.INVEST_SUCCESS_INVOKE_FAIL.getIndex(), 
                                                                   ExceptionCode.INVEST_SUCCESS_INVOKE_FAIL.getErrMsg());
                                    }
                                    
                                }
                            // 其余情况默认投资失败    
                            }else{
                                
                                logger.info("fali to sync invest, the investId = {}, the result = {}", invest.getId(), batchAccountErrorDataDto.getErrorInfo());
                                // 更新订单状态
                                if (1 != investDao.updateInvestStatus(invest.getId(), 
                                                                      InvestStatus.SYNC_FAILED)) {
                                    logger.info("fail to update invest status, the investId = {}, status = {}", invest.getId(), InvestStatus.SYNC_FAILED);
                                    throw new ServiceException(ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getIndex(), 
                                                               ExceptionCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS.getErrMsg());
                                }
                                // 北京银行投资失败
                                throw new ServiceException(ExceptionCode.INVEST_SUCCESS_INVOKE_FAIL.getIndex(), 
                                                           ExceptionCode.INVEST_SUCCESS_INVOKE_FAIL.getErrMsg());
                            }
                        }
                    }
                }
                
            }
        }
        
        // 北京银行同步成功
        invest = investDao.findById(invest.getId());
        if (bjResult || InvestStatus.BJ_SYN_SUCCESS.equals(invest.getStatus())) {
            
            // 同步本地投资接口
            String localInvestNo = investService.investSuccess(invest.getLocalFreezeNo(), invest.getId());
            
            if (StringUtils.isBlank(localInvestNo)) {
                logger.info("fail to invoke local invest, the result is null, the investId = {}", invest.getId());
                throw new ServiceException(ExceptionCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL.getIndex(), 
                                           ExceptionCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL.getErrMsg());
            }
            
            // 本地投资成功即投资成功
            if (1 != investDao.updateLocalDfCodeAndStatus(invest.getId(), 
                                                          localInvestNo, 
                                                          InvestStatus.SUCCESS)) {
                logger.info("fail to invest success sync, fail to update invest status, the investId = {}", invest.getId());
                throw new ServiceException(ExceptionCode.INVEST_SUCCESS_UPDATE_INVEST_STATUS_FAIL.getIndex(), 
                                           ExceptionCode.INVEST_SUCCESS_UPDATE_INVEST_STATUS_FAIL.getErrMsg());
            }
            
            // 本地同步成功即成功
            return true;
        }
        
        throw new ServiceException(ExceptionCode.INVEST_SUCCESS_FAIL.getIndex(), 
                                   ExceptionCode.INVEST_SUCCESS_FAIL.getErrMsg());
    }
    
    /**
     * 债转标冻结北京银行金额
     * @param invest
     * @return
     * @throws ServiceException
     * @author lujixiang
     * @date 2017年4月13日
     *
     */
    private Boolean investSuccessDebt(Invest invest) throws ServiceException{
        
        logger.info("ready to invest success debt, investId = {}", invest.getId());
        
        if (InvestStatus.BJ_FROZEN_SUCCESS.equals(invest.getStatus())) {
            logger.info("success to invest debt, the invest status is already BJ_FROZEN_SUCCESS, the investId = {}", invest.getId());
            return true;
        }
        
        if (!InvestStatus.LOCAL_FROZEN_SUCCESS.equals(invest.getStatus())) {
            
            logger.info("fail to invest success debt, the invest status is not LOCAL_FROZEN_SUCCESS, investId = {}, status = {}", 
                                                                                            invest.getId(), invest.getStatus());
            throw new ServiceException(ExceptionCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getIndex(), 
                                       ExceptionCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getErrMsg());
        }
        
        try {
            
            // 北京冻结订单号
            logger.info("ready to freeze bj fund, the investId = {}", invest.getId());
            FreezeResultDto freezeResult = assignService.freeze(invest.getLocalFreezeNo());
            
            logger.info("end to freeze bj fund, the investId = {}, result = {}", invest.getId(), freezeResult);
            
            if (null == freezeResult || !freezeResult.getIsSuccess() || StringUtils.isBlank(freezeResult.getOrderNo())) {
                
                logger.info("fail to invest success debt, fail invoke free interface, investId = {}, result = {}", 
                                                                                     invest.getId(), freezeResult);
                throw new ServiceException(ExceptionCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getIndex(), 
                                           ExceptionCode.INVEST_STATUS_NOT_AVALIABLE_SYNC.getErrMsg());
            }
            
            logger.info("ready to update bj freeze code, the investId = {}", invest.getId());
            if (1 != investDao.updateLocalBjDfCodeAndStatus(invest.getId(),
                                                            freezeResult.getOrderNo(), 
                                                            InvestStatus.BJ_FROZEN_SUCCESS)) {
                
                throw new ServiceException(ExceptionCode.INVEST_DEBT_UPDATE_BJ_FREEZE_CODE.getIndex(), 
                                           ExceptionCode.INVEST_DEBT_UPDATE_BJ_FREEZE_CODE.getErrMsg());
            }
            
            return true;
            
        } catch (ServiceException ex) {
            
            logger.error("fail to invest success debt, there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            
            logger.error("fail to invest success debt, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        }
        
        
    }
    

    @Override
    public Boolean investSuccess(String investId, String productId, String userId) throws ServiceException {
        
        logger.info("ready to invest success sync, the investId = {} ", investId);
        
        if (StringUtils.isBlank(investId) ||
                StringUtils.isBlank(productId) ||
                StringUtils.isBlank(userId)) {
            
            throw new ServiceException(ExceptionCode.GLOBAL_PARAM_ERROR.getIndex(), 
                                       ExceptionCode.GLOBAL_PARAM_ERROR.getErrMsg());
        }
        
        // 投资结果
        boolean investResult = false;
        
        Invest invest = null; //发送mq使用
        
        try{
            
         // 释放投资金额
            String scriptResult = redisTemplate.execute(RedisScriptUtil.getSuccessScript(), 
                                                        redisTemplate.getStringSerializer(), 
                                                        redisTemplate.getStringSerializer(), 
                                                        Arrays.asList(productId, 
                                                                      userId,
                                                                      investId), 
                                                        "");
            
            logger.info("end to excute success script, the result = {}, productId = {}, userId = {}, orderId = {}", 
                                                        scriptResult, productId, userId, investId);
            
            // 返回如果不是数字,分配金额失败
            if(!"BID_DELETE_SUCCESS_SREM_SUCCESS".equals(scriptResult) && !"BID_SUCCESS_ALREADY_REMOVE".equals(scriptResult)){
                
                logger.info("fail to success invest, the result = {}, productId = {}, userId = {}, orderId = {}", 
                                                            scriptResult, productId, userId, investId);
                
                throw new ServiceException(ExceptionCode.SUCCESS_INVEST_EXCUTE_SCRIPT_FAIL.getIndex(), 
                                           ExceptionCode.SUCCESS_INVEST_EXCUTE_SCRIPT_FAIL.getErrMsg());
            }
            
            if (StringUtils.isBlank(investId)) {
                
                throw new ServiceException(ExceptionCode.INVEST_ID_REQUIRED.getIndex(), 
                                           ExceptionCode.INVEST_ID_REQUIRED.getErrMsg());
            }
            
            invest = investDao.findById(investId);
            
            if (null == invest) {
                
                logger.info("fail to invest success sync, the invest is null, the investId = {} ", investId);
                throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(), 
                                           ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
            }
            
            // 债转投资,冻结北京银行
            if (invest.isDebted()) {
                investResult = this.investSuccessDebt(invest);
            }else{
                // 一手标同步北京银行
                investResult = this.investSuccessFirstHand(invest);
            }
            
            return investResult;
            
        } catch(ServiceException ex){
            
            logger.info("fail to invest success sync, there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            logger.info("fail to invest success sync, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg());
        } finally {
            
            // 投资成功
            if (investResult) {
                
                // 同步成功发送满标检测mq
                try {
                    
                    logger.info("send to invest finish message, investId = {}, productId = {}", invest.getId(), invest.getProductId());
                    Map<String, String> investFinishMap = Maps.newHashMap();
                    investFinishMap.put("investId", invest.getId());
                    investFinishMap.put("productId", invest.getProductId());
                    Message investFinishMessage = new Message(MQTopic.MYC_CHECK_LOAN_IS_FULL.getValue(), JsonHelper.getJson(investFinishMap).getBytes());
                    mqProducer.send(investFinishMessage);
                    
                } catch (Exception ex) {
                    logger.error("fail to send invest finish message, there is a exception : ", ex);
                }
                
                // 发送短信
                try {
                    
                    ProductDTO productDTO = loanService.getProduct(invest.getProductId());
                    
                    logger.info("send to invest mobile message, investId = {}, productId = {}", invest.getId(), invest.getProductId());
                    Map<String, Object> smsMap = new HashMap<String, Object>();
                    smsMap.put("templateId", SMSType.NOTIFICATION_CREDITMARKET_INVEST.getTemplateId());
                    smsMap.put("mobiles", invest.getMobile());
                    List<String> params = new ArrayList<String>();
                    params.add(DateUtils.formatDate(invest.getSubmitTime(), DateUtils.dateTimeFormatter));
                    params.add(invest.getAmount().toString());
                    params.add(null == productDTO ? "" : productDTO.getTitle());
                    smsMap.put("params", params);
                    // eg:【国美金融】尊敬的美易理财用户，您于2017年03月21日 14:02:30在美易理财购买了20.00元的理财117产品，已成功支付，感谢您对国美的信任。
                    // Templete: 尊敬的美易理财用户，您于{0}在美易理财购买了{1}元的{2}产品，已成功支付，感谢您对国美的信任。
                    // 数据库编辑，template 10.143.81.27 message库，表sms_template,用大括号{0}{1}{2}，表示参数，下标从0开始。
                    Message message = new Message(MQTopic.SMS_SEND.getValue(),  JsonHelper.getJson(smsMap).getBytes());
                    SendResult sendResult = mqProducer.send(message);
                    logger.info("send to invest mobile message, investId = {}, productId = {}, message = {}", invest.getId(), invest.getProductId(), sendResult);
                } catch (Exception e){
                    // 短信发失败，不做任何处理，只记日志
                    logger.error("预约投资发送短信MQ失败，投资id:" + investId, e);
                }
                
                // 同步成功发送满标检测mq
                try {
                    
                    logger.info("send to invest spread message, investId = {}, productId = {}", invest.getId(), invest.getProductId());
                    Map<String, String> investFinishMap = Maps.newHashMap();
                    investFinishMap.put("investList", Arrays.asList(invest.getId()).toString());
                    investFinishMap.put("investType", "0");
                    Message investSpreadMessage = new Message(MQTopic.INVEST_SUCCESS.getValue(), JsonHelper.getJson(investFinishMap).getBytes());
                    mqProducer.send(investSpreadMessage);
                    
                } catch (Exception ex) {
                    logger.error("fail to send invest spread message, there is a exception : ", ex);
                }
                
                // 更新数据库真实投资笔数和金额
                try {
                    logger.info("ready to increase invest amount, investId = {}, productId = {}, amount = {}", 
                                                    invest.getId(), invest.getProductId(), invest.getAmount());
                    Boolean result = productService.increaseInvestAmount(invest.getProductId(), invest.getAmount());
                    logger.info("end to increase invest amount, investId = {}, productId = {}, amount = {}, result = {}", 
                                                    invest.getId(), invest.getProductId(), invest.getAmount(), result);
                    
                } catch (Exception ex) {
                    logger.error("fail to increase invest amount, there is a exception : ", ex);
                }
                
            }
            
        }
        
    }
    
    
    /**
     * 查询投资记录(包含黄金)
     *
     * @param loanId 标id
     * @param investStatusList 投资单状态
     * @param page   页码
     * @param pageSize 页大小
     * @return
     * @throws ServiceException
     */
    public Page<InvestDTO> listInvestByUserIdAndStatus(String userId, List<InvestStatus> investStatus, Integer page, Integer pageSize) throws ServiceException{
        
        if (StringUtils.isBlank(userId) || 
                null  == investStatus || investStatus.isEmpty() ||
                null == page || null == pageSize) {
            
            throw new ServiceException(ExceptionCode.GLOBAL_PARAM_ERROR.getIndex(), 
                                       ExceptionCode.GLOBAL_PARAM_ERROR.getErrMsg());
        }
        
        List<Invest> invests = investDao.listByUserIdAndStatus(userId, investStatus, (page.intValue() - 1) * pageSize.intValue(), pageSize.intValue());
        
        List<InvestDTO> investDTOs = null;
        
        if (null != invests && !invests.isEmpty()) {
            investDTOs = BeanMapper.mapList(invests, InvestDTO.class);
        }
        
        Long count = investDao.countByUserIdAndStatus(userId, investStatus);
        
        return new Page<InvestDTO>(investDTOs, page, pageSize, count);
    }
    
    /**
     * 查询投资记录（不包含黄金）
     *
     * @param loanId 标id
     * @param investStatusList 投资单状态
     * @param page   页码
     * @param pageSize 页大小
     * @return
     * @throws ServiceException
     */
    public Page<InvestDTO> listByUserIdAndStatusNotGold(String userId, List<InvestStatus> investStatus, Integer page, Integer pageSize) throws ServiceException{
    	
    	if (StringUtils.isBlank(userId) || 
    			null  == investStatus || investStatus.isEmpty() ||
    			null == page || null == pageSize) {
    		
    		throw new ServiceException(ExceptionCode.GLOBAL_PARAM_ERROR.getIndex(), 
    				ExceptionCode.GLOBAL_PARAM_ERROR.getErrMsg());
    	}
    	
    	List<Invest> invests = investDao.listByUserIdAndStatusNotGold(userId, investStatus, (page.intValue() - 1) * pageSize.intValue(), pageSize.intValue());
    	
    	List<InvestDTO> investDTOs = null;
    	
    	if (null != invests && !invests.isEmpty()) {
    		investDTOs = BeanMapper.mapList(invests, InvestDTO.class);
    	}
    	
    	Long count = investDao.countByUserIdAndStatusNotGold(userId, investStatus);
    	
    	return new Page<InvestDTO>(investDTOs, page, pageSize, count);
    }
    
    @Override
    public List<InvestDTO> getInvest(String userId, List<String> loanIds) {
        List<Invest> listInvestDto=investDao.finByUserIdAndLoanId(userId,loanIds);
        if (listInvestDto.isEmpty()) {
            return new ArrayList<InvestDTO>();
        }
        List<InvestDTO> dtoList = BeanMapper.mapList(listInvestDto, InvestDTO.class);
        return dtoList;
    }

	@Override
	public int countByUserAndStatus(String userId, List<InvestStatus> investStatus) {
		return investDao.countByUserIdAndStatus(userId, investStatus).intValue();
	}
	
	@Override
	public int countByUserAndStatusNotGold(String userId, List<InvestStatus> investStatus) {
		return investDao.countByUserIdAndStatusNotGold(userId, investStatus).intValue();
	}

    @Override
    public Boolean hasXsbInvest(String userId) {
        Long result = investDao.countUserInvest(userId, "XSB");
        if (null != result && result.compareTo(0L) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal sumInvestAmountByProductIdAndStatus(String productId, InvestStatus... investStatus) throws ServiceException{
        
        try{
            
            return investBridge.sumInvestAmountByProductIdAndStatus(productId, investStatus);
            
        } catch(Exception ex){
            
            logger.error("fail sum invest by product and status, ex ={}", ex);
            
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        }
        
    }

	@Override
	public int countByStatusAndSubmitTime(String userId, List<InvestStatus> statusList, Date endSubmitTime) {
		return investDao.countByStatusAndSubmitTime(userId,statusList,endSubmitTime);
	}

	@Override
	public int countByLoanRewardAndStatusExCludeCuurentInvest(String userId, List<InvestStatus> statusList,
			boolean userIsGetReward, String investId) {
		return investDao.countByLoanRewardAndStatusExCludeCuurentInvest(userId,statusList,userIsGetReward,investId);
	}

	@Override
	public int sumByUserAndDate(Date from, Date to, List<String> userIds, List<InvestStatus> statusList) {
		String startTime = DateUtils.toString(from, "yy-mm-dd hh:mm:ss");
		String endTime = DateUtils.toString(to, "yy-mm-dd hh:mm:ss");
		return investDao.sumByUserAndDate(startTime,endTime,userIds,statusList);
	}
	
	/**
     * 查询投资记录
     *
     * @param loanId 标id
     * @param investStatusList 投资单状态
     * @param page   页码
     * @param pageSize 页大小
     * @return
     * @throws ServiceException
     */
    public Page<InvestDTO> listInvestByProductIdAndStatus(String productId, List<InvestStatus> investStatus, Integer page, Integer pageSize) throws ServiceException{
        
        if (StringUtils.isBlank(productId) || 
                null  == investStatus || investStatus.isEmpty() ||
                null == page || null == pageSize) {
            
            throw new ServiceException(ExceptionCode.GLOBAL_PARAM_ERROR.getIndex(), 
                                       ExceptionCode.GLOBAL_PARAM_ERROR.getErrMsg());
        }
        
        List<Invest> invests = investDao.listByProductIdAndStatus(productId, investStatus, (page.intValue() - 1) * pageSize.intValue(), pageSize.intValue());
        
        List<InvestDTO> investDTOs = null;
        
        if (null != invests && !invests.isEmpty()) {
            investDTOs = BeanMapper.mapList(invests, InvestDTO.class);
        }
        
        Long count = investDao.countByProductIdAndStatus(productId, investStatus);
        
        return new Page<InvestDTO>(investDTOs, page, pageSize, count);
    }

	@Override
	public Map<String, String> sumAndCountByProductAndStatus(String userId, String id,
			InvestStatus... effectiveInvestStatusArray) {
		HashMap<String, String> mapList=new HashMap<String,String>();
		//标的类型下的最大金额
		BigDecimal typeMaxTotalAmount=investDao.getInvestStatusTotalAmount(userId,id,effectiveInvestStatusArray);
		if (typeMaxTotalAmount==null) {
			typeMaxTotalAmount=BigDecimal.ZERO;
		}
		Integer in=investDao.getInvestTimes(userId,id,effectiveInvestStatusArray);
		if (in==null) {
			in=0;
		}
		mapList.put("typeMaxTotalAmount", typeMaxTotalAmount.toString());
		mapList.put("in", in.toString());
		return mapList;
	}
	
	/**
	    * 取消投资
	    * @param investId
	    * @return
	    * @author lujixiang
	    * @date 2017年4月19日
	    *
	    */
	@Override
	public Boolean cancelInvest(String investId) throws ServiceException{
	    
	    Invest invest = investDao.findById(investId);

	    // 债转投资
	    boolean result = false;
	    
	    try {
	        
	        // 债转标取消
	        if (invest.isDebted()) {
	            
	            result = this.unfrozenInvest(invest);
	            
	        }else{
	            
	            result = this.revokeInvest(invest);
	        }
            
        } catch (ServiceException e) {
            
        } catch (Exception e) {
            
        }
	    
	    if (result) {
            
            investDao.updateInvestStatus(invest.getId(), InvestStatus.CANCELED);
        }
	    return false;
	}
	
	/**
	 * 解冻投资
	 * @param invest
	 * @return
	 * @throws ServiceException
	 * @author lujixiang
	 * @date 2017年4月19日
	 *
	 */
	private boolean unfrozenInvest(Invest invest) throws ServiceException{
	    
	    if (InvestStatus.CANCELED.equals(invest.getStatus())) {
	        logger.info("sucess to unfroze invest, the invest status is already canceled, investId = {}", invest.getId());
	        return true;
        }
	    
	    // 如果是非北京银行解冻,不能取消
	    if (!InvestStatus.BJ_FROZEN_SUCCESS.equals(invest.getStatus())) {
            logger.info("fail to unfroze invest, the invest status is not BJ_FROZEN_SUCCESS, investId = {}", invest.getId());
            throw new ServiceException(ExceptionCode.INVEST_UNFROZEN_FAILED_NOT_BJFROZEN.getIndex(), 
                                       ExceptionCode.INVEST_UNFROZEN_FAILED_NOT_BJFROZEN.getErrMsg());
        }
	    
	    boolean result = false;
	    
	    try{
	        
	        // 调用本地解冻和北京银行解冻
	        logger.info("ready to invoke invest cancel inteface, investId = {}", invest.getId());
	        FreezeResultDto freezeResultDto = assignService.investCancel(invest.getLocalFreezeNo());
	        
	        if (null != freezeResultDto && freezeResultDto.getIsSuccess()) {
	            result = true;
	        }
	        
	    } catch(ServiceException ex){
	        
	        // 重复解冻,需要账户服务提供重复异常码
	        if ("xxx".equals(ex.getErrorCode())) {
	            
	            result = true;
            }
	        
	    } catch (Exception e) {
            
        }
	    
	    return result;
	}
	
	/**
	 * 撤销一手投资
	 * @param invest
	 * @return
	 * @throws ServiceException
	 * @author lujixiang
	 * @date 2017年4月19日
	 *
	 */
	private boolean revokeInvest(Invest invest) throws ServiceException{
	    
	    if (InvestStatus.CANCELED.equals(invest.getStatus())) {
            return true;
        }
	    
	    if (!InvestStatus.SUCCESS.equals(invest.getStatus())) {
            
        }
	    
	    // 调用北京银行撤销投资
        LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);   
        
        Date transDate = new Date();
        
        PublishRecallResultDto resultDto = loanAgent.publishRecall(agentConfig.getPlatNo(), // 平台编号
                                                                   UUIDGenerator.generate(), // 订单号
                                                                   DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
                                                                   DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间
                                                                   invest.getLoanId(), // 产品编号
                                                                   invest.getId(),  // 原交易订单号
                                                                   null);
        
        if (null == resultDto ) {
            
        }
        
        if (SyncResultConstant.SUCCESS_CODE.equals(resultDto.getRecode()) && 
                null != resultDto.getData() && 
                "2".equals(resultDto.getData().getOrderStatus()) ) {
            return true;
        }
        
        return false;
	}

    @Override
    public Long countInvestNumByProductAndStatus(String productId, InvestStatus... investStatus) throws ServiceException {
        try{
            
            return investBridge.countInvestNumByProductAndStatus(productId, investStatus);
            
        } catch(Exception ex){
            
            logger.error("fail count invest number by product and status, ex ={}", ex);
            
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        }
    }

	@Override
	public Boolean getFirstInvestment(String investId) throws ServiceException {
		if (StringUtils.isEmpty(investId)) {
            throw new ServiceException(ExceptionCode.INVEST_ID_REQUIRED.getIndex(),ExceptionCode.INVEST_ID_REQUIRED.getErrMsg());
        }
		Invest investment = investDao.getInvestment(investId);
		if (investment==null) {
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(),ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
		}
		String userId = investment.getUserId();
		if (StringUtils.isEmpty(userId)) {
            throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(),ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
        }
		String firstInvestId=investDao.getFirstInvestment(userId);
		if(firstInvestId .equals(investId) ){
			return true;
		}else{
			return false;
		}
	}

    @Override
    public BigDecimal sumAmountByLoanType(String loanType, String userId) throws ServiceException {
        
        try{
            
            return investDao.sumAmountByLoanType(loanType, userId);
            
        } catch(Exception ex){
            
            logger.error("sum amount by loantype failed, there is a serviceException : ", ex);
            
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        }
    }

	@Override
	public BigDecimal sumAmountByStatusAndDebted(String userId) throws ServiceException {
		if (StringUtils.isEmpty(userId)) {
            throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(),ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
        }
		BigDecimal sumAmountByStatusAndDebted = investDao.sumAmountByStatusAndDebted(userId);
		BigDecimal sumAmount = null==sumAmountByStatusAndDebted? BigDecimal.ZERO:sumAmountByStatusAndDebted;
		return sumAmount;
	}
	
}

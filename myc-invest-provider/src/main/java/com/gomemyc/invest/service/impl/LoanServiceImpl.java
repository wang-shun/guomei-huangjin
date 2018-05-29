package com.gomemyc.invest.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gomemyc.invest.enums.*;
import com.gomemyc.trade.enums.InvestStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.BankCardDTO;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.BankCardService;
import com.gomemyc.account.service.LoanAccountService;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.LoanFinancingInfoDto;
import com.gomemyc.agent.resp.ProdPublishResultDto;
import com.gomemyc.agent.util.DateUtil;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.invest.bridge.LoanBridge;
import com.gomemyc.invest.bridge.LoanRedisBridge;
import com.gomemyc.invest.constant.SyncReusltConstant;
import com.gomemyc.invest.dao.LoanDao;
import com.gomemyc.invest.dao.LoanTypeDao;
import com.gomemyc.invest.dao.LoanTypeRulesDao;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dao.ProductRulesDao;
import com.gomemyc.invest.dao.RulesDao;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductBillDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.model.RedisProduct;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.utils.DTOUtils;
import com.gomemyc.util.BeanMapper;


/**
 * 标的服务
 * @author lujixiang
 * @creaTime 2017年3月3日
 */
@Service(timeout=10000)
public class LoanServiceImpl implements LoanService{
    
    @Autowired
    LoanTypeDao loanTypeDao;
    
    @Autowired
    RulesDao rulesDao;
    
    @Autowired
    LoanDao loanDao;
    
    @Autowired
    ProductRegularDao productRegularDao;
    
    @Autowired
    ProductBillDao productBillDao;
    
    @Autowired
    LoanRedisBridge loanRedisBridge;
    
    @Autowired
    LoanTypeRulesDao loanTypeRulesDao;
    
    @Autowired
    ProductRulesDao productRulesDao;
    
    @Autowired
    LoanBridge loanBridge;
    
    @Autowired
    AgentConfig agentConfig;
    
    @Reference
    AccountService accountService;
    
    @Reference
    BankCardService bankCardService;
    
    @Reference
    LoanAccountService loanAccountService;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    

    @Override
    public Boolean createLoan(LoanDTO loanDTO) throws ServiceException {
        try{
            if(loanDTO==null){
                throw new ServiceException(ExceptionCode.PRODCT_NOT_REQUIRED.getIndex(),
                        ExceptionCode.PRODCT_NOT_REQUIRED.getErrMsg());
            }
            if(1 != loanDao.save(BeanMapper.map(loanDTO, Loan.class))){
                throw new ServiceException(ExceptionCode.PRODUCT_SAVE_ERROR.getIndex(),
                        ExceptionCode.PRODUCT_SAVE_ERROR.getErrMsg());
            }
        } catch (Exception ex){

            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),
                    ExceptionCode.EXCEPTION.getErrMsg(),
                    ex);

        }
        return true;
    }

    @Override
    public ProductDTO getProduct(String productId) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        RedisProduct product = null;
        try {
            product = loanRedisBridge.getProduct(productId);
        } catch (Exception ex) {
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
        
        if (null == product) {
            throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                       ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
        }
        
        // 定期理财产品
        if(productId.startsWith("dq-")){
            return BeanMapper.map(product, ProductRegularDTO.class);
        }else if(productId.startsWith("pj-")){
            return BeanMapper.map(product, ProductBillDTO.class);
        }
        
        return null;
    }


	@Override
	public Boolean modifyProductStatus(String productId, ProductStatus status) throws ServiceException {
		if(StringUtils.isEmpty(productId)){
			 throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                     ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg()); 
		}
		int count=0;
		if(productId.startsWith("dq-")){
			count=productRegularDao.updateProductStatus(status, productId);
		}
		if(productId.startsWith("pj-")){
			count=productBillDao.updateProductStatus(status, productId);
		}
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public LoanDTO findById(String id) throws ServiceException {
		if(StringUtils.isEmpty(id)){
			 throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(), 
                                        ExceptionCode.LOAN_NOT_EXIST.getErrMsg()); 
		}
		Loan loan = loanDao.findById(id);
		return DTOUtils.toDTO(loan);
	}


    /**
     * 根据资管产品标号查询标的信息
     *
     * @param portfolioNo
     * @return
     */
    @Override
    public LoanDTO findByPortfolioNo(String portfolioNo) throws ServiceException{
        if(StringUtils.isEmpty(portfolioNo)){
            throw new ServiceException(ExceptionCode.PRODUCT_PORTFOLIONO_REQUIRED.getIndex(),
                    ExceptionCode.PRODUCT_PORTFOLIONO_REQUIRED.getErrMsg());
        }
        Loan loan = loanDao.findByPortfolioNo(portfolioNo);
        if(loan==null){
            return null;
        }
        return BeanMapper.map(loan,LoanDTO.class);
    }

	@Override
	public Boolean modifyProductStatusAndSettleTime(String productId, String loanId, ProductStatus status, Date settleTime,Date valueTime,Date dueDate)
			throws ServiceException {
		if(productId==null||status==null||settleTime==null){
			throw new ServiceException(ExceptionCode.CANSHU_NO.getIndex(),
                    ExceptionCode.CANSHU_NO.getErrMsg());
		}
		
		boolean result = false;
		try {
			 
			result = loanBridge.modifyProductStatusAndSettleTime(productId, loanId, status, settleTime, valueTime,dueDate);
			return result;
		} catch (Exception e) {
			logger.error("modifyProductStatusAndSettleTime error,exception:", e);
			throw new ServiceException(ExceptionCode.CANSHU_NO.getIndex(),
                    ExceptionCode.CANSHU_NO.getErrMsg());
		} finally{
			if(result){
				try {
					loanRedisBridge.clearProductCacheAndList(productId);
				} catch (Exception e) {
					logger.error("fail to clear cache, ex = {}", e);
				}
			}
		}
	}


    /**
     * 票据和定期标的取消
     *
     * @param productId
     * @return
     * @throws ServiceException
     * 60001, "产品id为空"
     * 60002, "定期产品不存在"
     * 60003, "票据产品不存在"
     * 60004, "更新产品状态失败"
     * 60005, "当前的产品状态不可以取消"
     * 60006, "废标失败，请重试"
     * 60007, "清缓存失败"
     *
     */
    @Override
    public Boolean cancelLoan(String productId, Boolean isSendMQ2ZGsys) throws ServiceException {
        // 1、更新状态为 -2 (前提条件) regular 表 status = 1,2
        // 2、调方法清redis
        // 3、如果sync_status = 1 调北京银行废标，4.3.2
        logger.info("进入标的取消方法 ==================>>>>>>>> productId:" + productId);
        if(StringUtils.isBlank(productId)){
            throw new ServiceException(60001, "产品id为空");
        }

        if(productId.startsWith("dq-")){
            // 取消定期产品
            ProductRegular regular = productRegularDao.findById(productId);
            if(null == regular){
                throw new ServiceException(60002, "定期产品不存在");
            }

            loanBridge.cancelDingQi(productId, regular, isSendMQ2ZGsys);


        } else if(productId.startsWith("pj-")){
            // 取消票据
            ProductBill bill = productBillDao.findById(productId);
            if (null == bill) {
                throw new ServiceException(60003, "票据产品不存在");
            }

            loanBridge.cancelPiaoJu(productId, bill, isSendMQ2ZGsys);

        }

        return true;
    }

    @Override
    public Boolean loanFailed(String productId, FailLoanBusinessType businessType) throws ServiceException {
        // 条件： status = 5 && product_switch = 32
        // 1、更新status = -3
        // 2、调账户，传标ID ，loanId
        // 3、更新投资记录为status=-6
        logger.info("进入流标方法 ==================>>>>>>>> productId:" + productId);
        if(StringUtils.isBlank(productId)){
            throw new ServiceException(60001, "产品id为空");
        }
        if(productId.startsWith("dq-")){
            // 定期产品流标
            ProductRegular regular = productRegularDao.findById(productId);
            if(null == regular){
                throw new ServiceException(60002, "产品不存在");
            }
            if(FailLoanBusinessType.FAIL4JOB.equals(businessType)) {
                logger.info("进入定时任务的定期产品流标:" + productId);
                // 定时任务，需要判断业务开关、状态两个条件
                if (ProductStatus.FAILED.getIndex() == regular.getStatus().getIndex() && (regular.getProductSwitch() & 32) == 32) {
                    // 查投资表状态为 1 - 4 取 count 大于 1 ，不允许流标。
                    List<Integer> investStatusList = new ArrayList<Integer>();
                    investStatusList.add(InvestStatus.LOCAL_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_DF_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_SYN_SUCCESS.getIndex());
                    Integer count = loanDao.findInvestStatusCountByProductId(productId, investStatusList);
                    if(null != count && count > 0){
                        logger.info("定期产品：" + productId + "的投资记录中存在1-4的状态条数：" + count);
                        throw new ServiceException(60050, "定期存在中间状态投资记录，不可以流标");
                    }
                    // 更新product状态、退钱（券）、给资管发mq、清理缓存
                    loanBridge.failDingQi(productId, regular);

                    // 发短信、站内信
                    loanBridge.sendSmsAndMessage(productId, regular.getTitle());

                } else {
                    throw new ServiceException(60007, "不符合流标条件，定时任务调用流标需“到期未满标”，业务开关为“到期自动流标”");
                }
            } else if(FailLoanBusinessType.FAIL4CONSOLE.equals(businessType)){
                logger.info("进入管理台的定期产品流标:" + productId);
                // 后台只需要一个条件：状态为 “到期未满标”
                if (ProductStatus.FAILED.getIndex() == regular.getStatus().getIndex()) {
                    // 查投资表状态为 1 - 4 取 count 大于 1 ，不允许流标。
                    List<Integer> investStatusList = new ArrayList<Integer>();
                    investStatusList.add(InvestStatus.LOCAL_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_DF_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_SYN_SUCCESS.getIndex());
                    Integer count = loanDao.findInvestStatusCountByProductId(productId, investStatusList);
                    if(null != count && count > 0){
                        logger.info("定期产品：" + productId + "的投资记录中存在1-4的状态条数：" + count);
                        throw new ServiceException(60050, "定期存在中间状态投资记录，不可以流标");
                    }
                    loanBridge.failDingQi(productId, regular);

                    // 发短信、站内信
                    loanBridge.sendSmsAndMessage(productId, regular.getTitle());
                } else {
                    throw new ServiceException(60027, "不符合流标条件，后台流标需状态为“到期未满标”的产品");
                }

            } else {
                // 未知的业务类型调用
                logger.info("流标未知的业务类型调用");
                throw new ServiceException(60017, "流标未知的业务类型调用");
            }
        } else if(productId.startsWith("pj-")){
            // 票据产品流标
            ProductBill bill = productBillDao.findById(productId);
            if (null == bill) {
                throw new ServiceException(60003, "产品不存在");
            }
            if(FailLoanBusinessType.FAIL4JOB.equals(businessType)) {
                logger.info("进入定时任务的票据产品流标:" + productId);
                // 定时任务，需要判断业务开关、状态两个条件
                if (ProductStatus.FAILED.getIndex() == bill.getStatus().getIndex() && (bill.getProductSwitch() & 32) == 32) {
                    // 查投资表状态为 1 - 4 取 count 大于 1 ，不允许流标。
                    List<Integer> investStatusList = new ArrayList<Integer>();
                    investStatusList.add(InvestStatus.LOCAL_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_DF_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_SYN_SUCCESS.getIndex());
                    Integer count = loanDao.findInvestStatusCountByProductId(productId, investStatusList);
                    if(null != count && count > 0){
                        logger.info("票据产品：" + productId + "的投资记录中存在1-4的状态条数：" + count);
                        throw new ServiceException(60050, "票据存在中间状态投资记录，不可以流标");
                    }
                    loanBridge.failPiaoJu(productId, bill);

                    // 发短信、站内信
                    loanBridge.sendSmsAndMessage(productId, bill.getTitle());
                } else {
                    throw new ServiceException(60007, "不符合流标条件，定时任务调用流标需“到期未满标”，业务开关为“到期自动流标”");
                }
            } else if(FailLoanBusinessType.FAIL4CONSOLE.equals(businessType)){
                logger.info("进入管理后台的票据产品流标:" + productId);
                // 后台只需要一个条件：状态为 “到期未满标”
                if (ProductStatus.FAILED.getIndex() == bill.getStatus().getIndex()) {
                    // 查投资表状态为 1 - 4 取 count 大于 1 ，不允许流标。
                    List<Integer> investStatusList = new ArrayList<Integer>();
                    investStatusList.add(InvestStatus.LOCAL_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_FROZEN_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_DF_SUCCESS.getIndex());
                    investStatusList.add(InvestStatus.BJ_SYN_SUCCESS.getIndex());
                    Integer count = loanDao.findInvestStatusCountByProductId(productId, investStatusList);
                    if(null != count && count > 0){
                        logger.info("票据产品：" + productId + "的投资记录中存在1-4的状态条数：" + count);
                        throw new ServiceException(60050, "票据存在中间状态投资记录，不可以流标");
                    }
                    loanBridge.failPiaoJu(productId, bill);
                    // 发短信、站内信
                    loanBridge.sendSmsAndMessage(productId, bill.getTitle());
                } else {
                    throw new ServiceException(60027, "不符合流标条件，后台流标需状态为“到期未满标”的产品");
                }
            } else {
                // 未知的业务类型调用
                logger.info("流标未知的业务类型调用");
                throw new ServiceException(60017, "流标未知的业务类型调用");
            }
        }

        return true;
    }

    @Override
    public Boolean clearProductCacheAndList(String productId) throws ServiceException {
        try {
            return loanRedisBridge.clearProductCacheAndList(productId);
        } catch (Exception e) {
            // 清缓存失败不做任何处理，只记日志
            logger.error("清缓存失败，" , e);
        }

        return true;
    }


    @Override
    public List<String> listByLoanStatus(LoanStatus status) throws ServiceException{
        
        try {
            return loanDao.listByLoanStatus(status);
            
        } catch (Exception ex) {
            
            logger.error("fail to list loan by status, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
        
        
    }

    /**
     * 标的开户(北京和本地)
     * @param loanId
     * @return
     * @author lujixiang
     * @date 2017年4月17日
     *
     */
    @Override
    public Boolean openLoanAccount(String loanId) throws ServiceException{
        
        if (StringUtils.isBlank(loanId)) {
            
            throw new ServiceException(ExceptionCode.LOAN_ID_REQUIRED.getIndex(),
                                       ExceptionCode.LOAN_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready to open loan account, the loanId = {}", loanId);
        
        // 查询标的
        Loan loan = loanDao.findById(loanId);
        
        if (null == loan) {
            logger.info("fail to open loan account, the loan is null, the loanId = {}", loanId);
            throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(),
                                       ExceptionCode.LOAN_NOT_EXIST.getErrMsg());
        }
        
        boolean bj = false; // 北京银行开户结果
        
        try {
            // 北京银行开户
            logger.info("ready to open loan bj account, the loanId = {}", loanId);
            bj = this.openLoanBjAccount(loan);
            
        } catch (ServiceException ex) {
            
            logger.error("fail to open bj account , there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            logger.error("fail to open bj account , there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getIndex(),
                                       ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getErrMsg(),
                                       ex);
        }
        
        if (!bj) {
            
            logger.info("fail to open bj account, the loanId = {}", loanId);
            throw new ServiceException(ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getIndex(),
                                       ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getErrMsg());
        }
        
        boolean local = false;
        try {
            // 本地开户
            logger.info("ready to open loan local account, the loanId = {}", loanId);
            local = this.openLoanLocalAccount(loan);
            
        } catch (ServiceException ex) {
            
            logger.error("fail to open local account , there is a serviceException : ", ex);
            throw ex;
            
        } catch (Exception ex) {
            logger.error("fail to open local account , there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.PUBLISH_LOAN_LOCAL_FAIL.getIndex(),
                                       ExceptionCode.PUBLISH_LOAN_LOCAL_FAIL.getErrMsg(),
                                       ex);
        }
        
        if (!local) {
            logger.error("fail to open local account , the loanId = {}", loanId);
            throw new ServiceException(ExceptionCode.PUBLISH_LOAN_LOCAL_FAIL.getIndex(),
                                       ExceptionCode.PUBLISH_LOAN_LOCAL_FAIL.getErrMsg());
        }
        
        logger.info("success to open loan account, the loanId = {}", loanId);
        
        // 更新标的开户状态
        if (1 != loanDao.updateStatus(loanId, LoanStatus.PUBLISHED)) {
            
            logger.info("fail to update loan status, the loanId = {}, status = {}", loanId, LoanStatus.PUBLISHED);
            
            throw new ServiceException(ExceptionCode.UPDATE_LOAN_STATUS_FAIL.getIndex(),
                                       ExceptionCode.UPDATE_LOAN_STATUS_FAIL.getErrMsg());
        }
        return true;
    }
    
    
    /**
     * 北京银行开户
     * @param loan
     * @return
     * @author lujixiang
     * @date 2017年4月17日
     *
     */
    private boolean openLoanBjAccount(Loan loan) throws Exception{
        
        if (null == loan) {
            logger.info("fail to publish loan bj account, the loan is null");
            throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(), 
                                       ExceptionCode.LOAN_NOT_EXIST.getErrMsg());
        }
        
        if (LoanSyncStatus.SUCCESS.equals(loan.getSyncStatus())) {
            logger.info("success to publish loan bj account, the loan already has a account, loanId ={}", loan.getId());
            return true;
        }
        
        // 获取借款人账户
        AccountDTO accountDTO = accountService.getByUserId(loan.getUserId());
        if (null == accountDTO) {
            logger.info("fail to publish loan bj account, the user do not has a account, loanId ={}, userId = {}",
                                                                                    loan.getId(), loan.getUserId());
            throw new ServiceException(ExceptionCode.USER_ACCOUNT_NOT_EXIST.getIndex(), 
                                       ExceptionCode.USER_ACCOUNT_NOT_EXIST.getErrMsg());
        }
        
        // 获取借款人银行卡信息
        BankCardDTO bankCardDTO = bankCardService.getBankCardById(loan.getUserId());
        if (null == bankCardDTO) {
            logger.info("fail to publish loan bj account, the user do not has a account, loanId ={}, userId = {}",
                                                                                    loan.getId(), loan.getUserId());
            throw new ServiceException(ExceptionCode.USER_BANKCARD_NOT_EXIST.getIndex(), 
                                       ExceptionCode.USER_BANKCARD_NOT_EXIST.getErrMsg());
        }
        
        // 北京银行同步(此接口是幂等接口,不管之前的开户结果,直接重新生成订单号,根据返回的标的编号获取实际开户结果)        
        String orderId = UUIDGenerator.generate(); // 重新绑定同步订单号
        Date transDate = new Date();    // 交易时间
        if (1 != loanDao.updateSyncOrderNo(loan.getId(), orderId, transDate) ) {
            logger.info("fail to publish loan bj account, the loan cannot bind orderNo, loanId ={}, orderNo = {}",
                                                                                            loan.getId(), orderId);
            throw new ServiceException(ExceptionCode.PRODUCT_ALREADY_PUBLISH.getIndex(), 
                                       ExceptionCode.PRODUCT_ALREADY_PUBLISH.getErrMsg());
        }
        
        // 调用北京银行发标接口
        logger.info("ready to invoke bj publish loan interface, loanId ={}, orderNo = {}",
                                                                   loan.getId(), orderId);
        LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);   // 标的发标接口
        ProdPublishResultDto publishResult = loanAgent.prodPublish(agentConfig.getPlatNo(), // 平台编号
                                                                   orderId, // 订单号
                                                                   DateUtil.dateToStr(transDate, DateUtil.DFS_yyyyMMdd), // 交易日期 
                                                                   DateUtil.dateToStr(transDate, DateUtil.DF_HHmmss), // 交易时间
                                                                   loan.getId(), // 产品编号
                                                                   loan.getTitle(),     // 产品名称
                                                                   DictionaryEnum.PRODUCTTYPE0,  // 产品类型
                                                                   loan.getAmount(),    // 发行总额度
                                                                   DictionaryEnum.PRODUCTINTEREST1, // 起息方式
                                                                   DictionaryEnum.ESTABLISHMENTWAY1, // 成立方式
                                                                   null, // 发行日
                                                                   transDate, // 起息日
                                                                   transDate, // 到期日
                                                                   Integer.valueOf(loan.getYears() * 365 + loan.getMonths() * 30 + loan.getDays()), // 周期
                                                                   DictionaryEnum.PERIODICUNIT1, // 周期单位
                                                                   new BigDecimal(loan.getRate() + loan.getPlusRate())
                                                                                  .divide(new BigDecimal(10000)).toString(), // 年化收益率
                                                                   DictionaryEnum.REPAYTYPE0, // 还款方式
                                                                   Arrays.asList(
                                                                          new LoanFinancingInfoDto(accountDTO.getPlatcust(), // 融资人平台编号
                                                                                                   transDate, // 申请日期
                                                                                                   transDate, // 申请时间
                                                                                                   new BigDecimal(loan.getRate())
                                                                                                                   .divide(new BigDecimal(10000)).toString(),   // 融资利率
                                                                                                   BigDecimal.ZERO, // 手续费
                                                                                                   null, // 借款用途
                                                                                                   transDate, // 用款日期
                                                                                                   bankCardDTO.getBankName(), // 收款人开户行
                                                                                                   bankCardDTO.getBankCard(), // 收款人银行卡号
                                                                                                   DictionaryEnum.CARDTYPE2, // 卡类型
                                                                                                   bankCardDTO.getName(), // 收款人户名
                                                                                                   loan.getAmount())   // 融资金额
                                                                   ).toArray(new LoanFinancingInfoDto[1]),
                                                                   null,    // 风险等级
                                                                   null, // 产品介绍
                                                                   null, // 限制购买人数
                                                                   DictionaryEnum.STATEMENTACCOUNT0, // 出账提示
                                                                   DictionaryEnum.EXCESSLIMIT0, // 超额限制
                                                                   BigDecimal.ZERO, // 超额限制总金额
                                                                   null, // 受托报备
                                                                   null);    // 代偿账户列表
        
        if (null == publishResult) {
            logger.info("fail to publish loan bj account, invoke result is null, loanId ={}, orderNo = {}",
                                                                                    loan.getId(), orderId);
            throw new ServiceException(ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getIndex(), 
                                       ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getErrMsg());
        }
        
        // 第三方开户成功(10000:开户成功; 40008:已成功开户)
        if (SyncReusltConstant.SUCCESS_CODE.equals(publishResult.getRecode()) ||
                SyncReusltConstant.PRODUCT_ID_REPEAT.equals(publishResult.getRecode())) {
            logger.info("success to publish loan bj account, loanId ={}, orderNo = {}, result = {}",
                                                   loan.getId(), orderId, publishResult.getRemsg());
            
            
            
            // 更新标的的第三方同步结果
            if (1 != loanDao.updateSyncStatus(loan.getId(), LoanSyncStatus.SUCCESS, new Date())) {
                
                // 开户成功,更新第三方同步状态失败
                logger.info("fail to edit loan and product syncStatus, loanId ={}, orderNo = {}",
                                                                          loan.getId(), orderId);
                throw new ServiceException(ExceptionCode.LOAN_UPDATE_BJ_SYNC_STATUS_FAIL.getIndex(), 
                                           ExceptionCode.LOAN_UPDATE_BJ_SYNC_STATUS_FAIL.getErrMsg());
            }
            
            logger.info("success to edit loan and product syncStatus, loanId ={}, orderNo = {}",
                                                                         loan.getId(), orderId);
            
            return true;
        }
        
        // 其他情况默认订单处理失败(此处订单号每次重新生成,不用考虑订单号重复的情况)
        logger.info("fail to publish loan bj account, loanId ={}, orderNo = {}, recode = {}, reProductId = {}, result = {}",
                                                            loan.getId(), orderId, publishResult.getRecode(), 
                                                            null == publishResult.getDate() ? "" : publishResult.getDate().getProdId(),
                                                            publishResult.getRemsg());
        // 失败更新状态,不影响标的再次开户
        loanDao.updateSyncStatus(loan.getId(), LoanSyncStatus.FAILED, new Date());

        throw new ServiceException(ExceptionCode.PUBLISH_BJ_SYNC_FAIL.getIndex(), 
                                   publishResult.getRemsg());
    }
    
    
    /**
     * 本地开户
     * @param loan
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年4月17日
     *
     */
    private boolean openLoanLocalAccount(Loan loan) throws Exception{
        
        if (null == loan) {
            logger.info("fail to publish loan local account, the loan is null");
            throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(), 
                                       ExceptionCode.LOAN_NOT_EXIST.getErrMsg());
        }
        
        if (LoanSyncStatus.SUCCESS.equals(loan.getLocalSyncStatus())) {
            logger.info("success to publish loan local account, the loan already has a account, loanId ={}", loan.getId());
            return true;
        }
        
        // 本地账户服务开户
        Boolean resultLocal = Boolean.FALSE;
        try {
            logger.info("ready to publish lcoal loan account, the loanId = {}, amount = {}, userId = {}", 
                                                        loan.getId(), loan.getAmount(), loan.getUserId());
            resultLocal = loanAccountService.generateLoanAccount(loan.getId(), loan.getAmount(), loan.getUserId());
            
            logger.info("end to publish lcoal loan account, the loanId = {}, amount = {}, userId = {}, result = {}",
                                                      loan.getId(), loan.getAmount(), loan.getUserId(), resultLocal);
            
        } catch (ServiceException ex) {
            
            logger.error("fail to publish local loan account, there is a ServiceException : ", ex);
            
            // 10040:本地账户已存在
            if (null != ex.getErrorCode() && ex.getErrorCode().equals(Integer.valueOf(10040))) {
                
                logger.info("success to publish local loan account, the account is exist, loanId = {}", loan.getId());
                resultLocal = Boolean.TRUE;
            }
            
        } catch (Exception ex){
            
            logger.error("fail to publish local loan account, there is a exception : ", ex);
        }
        
        
        if (Boolean.TRUE.equals(resultLocal)) {
            
            logger.info("ready to update loan local sync status, loanId = {}, status = {}", 
                                                     loan.getId(), LoanSyncStatus.SUCCESS);
            // 更新本地开户状态
            if (1 != loanDao.updateLocalSyncStatus(loan.getId(), LoanSyncStatus.SUCCESS)) {
                
                logger.info("fail to update loan local sync status, loanId = {}, status = {}", 
                                                        loan.getId(), LoanSyncStatus.SUCCESS);
                
                throw new ServiceException(ExceptionCode.LOAN_UPDATE_LOCAL_SYNC_STATUS_FAIL.getIndex(), 
                                           ExceptionCode.LOAN_UPDATE_LOCAL_SYNC_STATUS_FAIL.getErrMsg());
            }
            
            logger.info("success to update loan local sync status, loanId = {}, status = {}", 
                                                       loan.getId(), LoanSyncStatus.SUCCESS);
            return true;
        }
        
        // 失败
        logger.info("ready to update loan local sync status, loanId = {}, status = {}", 
                                                  loan.getId(), LoanSyncStatus.FAILED);
        loanDao.updateLocalSyncStatus(loan.getId(), LoanSyncStatus.FAILED);
        
        throw new ServiceException(ExceptionCode.PUBLISH_LOAN_LOCAL_FAIL.getIndex(), 
                                   ExceptionCode.PUBLISH_LOAN_LOCAL_FAIL.getErrMsg());
        
    }

	@Override
	public List<ProductDTO> findProductByLoanId(String loanId) throws ServiceException {
		List<ProductDTO> list=new ArrayList<ProductDTO>();
		List<ProductBill> billList=productBillDao.listByLoanId(loanId);
		if(billList!=null&&billList.size()>0){
			for (ProductBill productBill : billList) {
				ProductDTO dto=BeanMapper.map(productBill, ProductBillDTO.class);
				list.add(dto);
			}
		}
		List<ProductRegular> regularList = productRegularDao.listByLoanId(loanId);
		if(regularList!=null&&regularList.size()>0){
			for (ProductRegular productRegular : regularList) {
				ProductDTO dto=BeanMapper.map(productRegular, ProductRegularDTO.class);
				list.add(dto);
			}
		}
		return list;
	}

	@Override
	public Boolean updateStatus(String id, LoanStatus status) throws ServiceException {
		if(StringUtils.isBlank(id)){
			  throw new ServiceException(ExceptionCode.LOAN_ID_REQUIRED.getIndex(), 
                      ExceptionCode.LOAN_ID_REQUIRED.getErrMsg());
		}
		if(status==null){
			  throw new ServiceException(ExceptionCode.LOAN_STATUS_REQUIRED.getIndex(), 
                      ExceptionCode.LOAN_STATUS_REQUIRED.getErrMsg());
		}
		int updateStatuscount=0;
		updateStatuscount = loanDao.updateStatus(id, status);
		if(updateStatuscount>0){
			return true;
		}
		return false;
	}

}

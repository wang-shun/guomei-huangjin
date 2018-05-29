package com.gomemyc.trade.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.AssignService;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.PayAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.CommontResultDto;
import com.gomemyc.agent.resp.LoanCommissionDto;
import com.gomemyc.agent.resp.LoanCommissionExtDto;
import com.gomemyc.agent.resp.OrderQueryResultDto;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.common.utils.DateUtils;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.invest.constant.NumberConstant;
import com.gomemyc.invest.dto.DebtassignRequestDTO;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.DebtAssignStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.DebtAssignRequestService;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductBillService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.model.user.User;
import com.gomemyc.sms.SMSType;
import com.gomemyc.trade.bridge.DebtAssignBridge;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dao.InvestRepaymentDao;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.DebtAssignTradeService;
import com.gomemyc.trade.util.JsonHelper;
import com.gomemyc.user.api.UserService;

@Service
public class DebtAssignTradeServiceImpl implements DebtAssignTradeService{

	Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference
    private LoanService loanService;
    @Autowired
    private InvestDao investDao;
    @Autowired
    private InvestRepaymentDao investRepaymentDao;
    @Reference
    private DebtAssignRequestService debtAssignRequestService;
    @Reference
    private ProductRegularService productRegularService;
    @Reference
    private ProductBillService productBillService;
    @Autowired
    private DebtAssignBridge debtAssignBridge;
	@Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;
	@Reference
	private AccountService accountService;//账户服务
	@Reference
	private AssignService assignService;
	@Autowired
	private AgentConfig agentConfig;
	@Reference
	private CouponService couponService;
	@Reference
	private UserService userService;
	@Override
	public Boolean settlementDebtAssign(String debtAssignProductId) throws ServiceException {
 		if(StringUtils.isEmpty(debtAssignProductId)){
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getIndex(),ExceptionCode.INVEST_PRODUCT_ID_REQUIRED.getErrMsg());
		}
		ProductDTO productDTO=null;
		try {
			productDTO=loanService.getProduct(debtAssignProductId);
		} catch (Exception e) {
			logger.info("债转结算--- 查询投资产品系统异常  productId={}",debtAssignProductId);
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg(),e);
		}
		if(productDTO==null){
			logger.info("债转结算--- 查询投资产品返回null  productId={}",debtAssignProductId);
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
		}
		if(!productDTO.getStatus().equals(ProductStatus.OPENED)&&!productDTO.getStatus().equals(ProductStatus.FINISHED)
				&&!productDTO.getStatus().equals(ProductStatus.FAILED)){
			logger.info("债转结算--- 结算时产品状态不在返回内  ProductStatus={}",productDTO.getStatus());
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_STATUS_ERROR.getIndex(),ExceptionCode.INVEST_PRODUCT_STATUS_ERROR.getErrMsg());
		}
		
		//债转产品的债转申请
		DebtassignRequestDTO debt=null;
		try {
			 debt=debtAssignRequestService.getByDebtassignProductId(debtAssignProductId);
		} catch (Exception e) {
			logger.info("债转结算--- 根据债转产品ID查询债转申请系统异常  debtAssignProductId={}",debtAssignProductId);
			throw new ServiceException(ExceptionCode.DEBT_NOT_EXIST.getIndex(),ExceptionCode.DEBT_NOT_EXIST.getErrMsg(),e);
		}
		if(debt==null){
			logger.info("债转结算--- 根据债转产品ID查询债转申请返回为null  debtAssignProductId={}",debtAssignProductId);
			throw new ServiceException(ExceptionCode.DEBT_NOT_EXIST.getIndex(),ExceptionCode.DEBT_NOT_EXIST.getErrMsg());
		}
		if(!debt.getStatus().equals(DebtAssignStatus.OPEN)){
			throw new ServiceException(ExceptionCode.DEBT_STATUS_ERROR.getIndex(),ExceptionCode.DEBT_STATUS_ERROR.getErrMsg());
		}
		if(debt.getRequestEndDate()==null||debt.getRequestEndDate().after(new Date())){//结算的时间不在范围内
			logger.info("债转结算--- 结算的时间不在范围内  endDate={} ,nowDate={}",debt.getRequestEndDate(),new Date());
			throw new ServiceException(ExceptionCode.DEBT_ENDTIME_ERROR.getIndex(),ExceptionCode.DEBT_ENDTIME_ERROR.getErrMsg());
		}
		
		ProductDTO rootproductDTO=null;
		try {
			rootproductDTO=loanService.getProduct(debt.getProductId());
		} catch (Exception e) {
			logger.info("债转结算--- 查询原投资产品系统异常  productId={}",debt.getProductId());
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg(),e);
		}
		if(rootproductDTO==null){
			logger.info("债转结算--- 查询原投资产品返回null  productId={}",debt.getProductId());
			throw new ServiceException(ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_PRODUCT_NOT_EXIST.getErrMsg());
		}
		
		//查询转让人的投资记录
		Invest zrinvest=null;
		try {
			zrinvest=investDao.findById(debt.getInvestId());
		} catch (Exception e) {
			logger.info("债转结算--- 通过条件查询投资记录系统异常  productDTO[userId={},rootProductId={},loanId=]"+productDTO.getUserId(), productDTO.getRootProductId(), productDTO.getLoanId());
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(),ExceptionCode.INVEST_NOT_EXIST.getErrMsg(),e);
		}
		
		if(zrinvest==null){
			logger.info("债转结算--- 通过条件查询投资记录返回null  productDTO[userId={},rootProductId={},loanId={}]"+productDTO.getUserId(), productDTO.getRootProductId(), productDTO.getLoanId());
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(),ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
		}
		if(!zrinvest.getStatus().equals(InvestStatus.ASSIGNING)){
			logger.info("债转结算--- 转让人的投资当前状态不符合规则  invest.status={}",zrinvest.getStatus());
			throw new ServiceException(ExceptionCode.DEBT_INVEST_STATUS_ERROR.getIndex(),ExceptionCode.DEBT_INVEST_STATUS_ERROR.getErrMsg());
		}
		User user=null;
		try {
			user=userService.findByUserId(zrinvest.getUserId());
		} catch (Exception e) {
			logger.info("债转结算--- 查询用户系统异常 investID={}  userId={}",zrinvest.getId(),zrinvest.getUserId());
			throw new ServiceException(ExceptionCode.DEBT_USER_SOURCE.getIndex(),ExceptionCode.DEBT_USER_SOURCE.getErrMsg(),e);
		}
		
		if(user==null){
			logger.info("债转结算--- 查询用户返回为null  investID={}  userId={}",zrinvest.getId(),zrinvest.getUserId());
			throw new ServiceException(ExceptionCode.DEBT_USER_SOURCE.getIndex(),ExceptionCode.DEBT_USER_SOURCE.getErrMsg());
		}
		//转让人的还款计划
		InvestRepayment zrInvestRepayment=investRepaymentDao.findByInvestId(zrinvest.getId());
		if(zrInvestRepayment==null){
			logger.info("债转结算--- 通过投资ID查询还款计划返回null  investId={}"+zrinvest.getId());
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_REPAYMENT_NOT_EXIST.getErrMsg());
		}	
		//查询受让人的投资记录
		List<Invest>  srInvestList=investDao.findListByProductIdAndStatus(debtAssignProductId, 
																			InvestStatus.LOCAL_FROZEN_SUCCESS,
																			InvestStatus.BJ_FROZEN_SUCCESS,
																			InvestStatus.BJ_DF_SUCCESS,
																			InvestStatus.BJ_SYN_SUCCESS,
																			InvestStatus.SETTLED
																			);
		List<String> ids=new ArrayList<String>();
		BigDecimal allAmount=BigDecimal.ZERO;
		for (Invest invest : srInvestList) {
			if(invest.getStatus().equals(InvestStatus.LOCAL_FROZEN_SUCCESS)){
				logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 债转撤销时有投资处于本地冻结中状态，不能撤销 investId={},status={}",invest.getId(),invest.getStatus());
				throw new ServiceException(ExceptionCode.INVEST_CLZ_STATUS.getIndex(), 
	                    ExceptionCode.INVEST_CLZ_STATUS.getErrMsg());
			}
			allAmount=allAmount.add(invest.getAmount());
			ids.add(invest.getId());
		}
		//总投资金额为0，或者（总投资金额比转让的金额小，并且募集不足处理方式为全部退回），则直接调用撤销
		if(allAmount.compareTo(BigDecimal.ZERO)==0||//总投资金额为0
				(allAmount.compareTo(debt.getTransferPrice())==-1&&debt.getReturnFullAmount())){//总投资金额比转让的金额小，并且募集不足处理方式为全部退回
			logger.info("债转结算--- 募集不足处理方式为全部退回调用债转撤销开始  debtassignId={}",debt.getId());
			try {
				boolean flag=debtAssignRequestService.cancelDebtAssign(debt.getUserId(), debtAssignProductId, "", "ht");
				logger.info("债转结算--- 募集不足处理方式为全部退回调用债转撤销 ={}",flag);	
				return flag;
			}catch (Exception e) {
				logger.info("债转结算--- 募集不足处理方式为全部退回调用债转撤销异常  debtassignId={}",debt.getId());
	            throw new ServiceException(1010, "募集不足处理方式为全部退回调用债转撤销异常",e);
	        }
		}
		List<InvestRepayment> list=investRepaymentDao.findByInvestIds(ids);
		boolean createFlag=true;//判断是否是第二次请求结算
		if(list!=null&&list.size()>0){
			createFlag=false;//第二次请求结算时还款计划已经更新完毕，所以需要直接调用MQ进行资金划转
		}
		
		LoanDTO loanDTO=loanService.findById(productDTO.getLoanId());
		if(loanDTO==null){
			logger.info("债转结算--- 查询标的返回null  loanId={}",productDTO.getLoanId());
			throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(),ExceptionCode.LOAN_NOT_EXIST.getErrMsg());
		}
		Map<String,BigDecimal> map=new HashMap<String, BigDecimal>();
		if(srInvestList==null||srInvestList.size()==0){
			logger.info("债转结算--- 没有受让人的投资记录  debtAssignProductId={},InvestStatus={}"+debtAssignProductId,InvestStatus.SUCCESS);
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(),ExceptionCode.INVEST_NOT_EXIST.getErrMsg());	
		}
		try {
			if(createFlag){//首次请求
				map = debtAssignBridge.settlementDebt(productDTO,zrInvestRepayment, debt, srInvestList,loanDTO,rootproductDTO);
			}else{
				map.put("sybj", zrInvestRepayment.getPrincipalAmount()==null ? BigDecimal.ZERO : zrInvestRepayment.getPrincipalAmount());//剩余本金
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			logger.info("债转结算--- 系统程序异常      e={}"+e.toString());
			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(),ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),e);	
		}
		//结算
		for (int i = 0; i < srInvestList.size(); i++) {
			Invest invest = srInvestList.get(i);
	        boolean updateStatus=false;
	        if(i==srInvestList.size()-1){
	        	updateStatus=true;
	        }else{
	        	updateStatus=false;
	        }
	        settlementDebtAssignMQ(debt, invest, debt.getDebtAmount(), allAmount, updateStatus, map.get("sybj"));
		}
		
		//转让结算发送短信
		try {
			 logger.info("债转结算通过发送短信MQ, debtId = {}, debtAssignProductId = {}", debt.getId(), debt.getDebtAssignProductId());
            Map<String, Object> smsMap = new HashMap<String, Object>();
            smsMap.put("templateId", SMSType.NOTIFICATION_SETTLE_SUCCESS_REMIND.getTemplateId());
            smsMap.put("mobiles", user.getMobile());
            List<String> params = new ArrayList<String>();
            params.add(DateUtils.format(new Date(), "yyyy年MM月dd日 HH:mm:ss"));
            params.add(debt.getDebtAmount().toString());
            params.add(null == productDTO ? "" : productDTO.getTitle());
            params.add(debt.getBackAmount().toString());
            params.add(debt.getBackAmount().toString());
            smsMap.put("params", params);
            Message message = new Message(MQTopic.SMS_SEND.getValue(),  JsonHelper.getJson(smsMap).getBytes());
            SendResult sendResult = mqProducer.send(message);
            logger.info("债转结算通过发送短信    sendResult={}",sendResult);
		} catch (Exception e) {
			logger.error("债转结算通过发送短信异常, debtId = {}, debtAssignProductId = {}" , debt.getId(), debt.getDebtAssignProductId());
		}
		return true;
	}
	
	private Boolean settlementDebtAssignMQ(DebtassignRequestDTO debtDTO, Invest invest, BigDecimal debtAmount, BigDecimal allAmount,boolean updateStatus,BigDecimal sybj)
			throws ServiceException {
		logger.info("债转结算--- 债转结算调用开始------");
    	InvestRepayment investRepayment=null;
    	try {
    		investRepayment=investRepaymentDao.findByInvestId(invest.getId());
		} catch (Exception e) {
			logger.info("债转结算--- 通过投资ID查询还款计划异常  investId={}",invest.getId());
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_REPAYMENT_NOT_EXIST.getErrMsg());
		}
    	if(investRepayment==null){
    		logger.info("债转结算--- 通过投资ID查询还款计划返回null  investId={}",invest.getId());
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_NOT_EXIST.getIndex(),ExceptionCode.INVEST_REPAYMENT_NOT_EXIST.getErrMsg());
    	}
		AccountDTO zrAccountDTO= null;//转让人账户信息
    	try {
    		zrAccountDTO= accountService.getByUserId(debtDTO.getUserId());
		} catch (Exception e) {
			logger.info("债转结算--- 调用账户服务查询账户信息异常 accountDTO.id ={}",zrAccountDTO.getUserId());
			throw new ServiceException(ExceptionCode.ACCOUNT_ACCOUNT_FAIL.getIndex(),ExceptionCode.ACCOUNT_ACCOUNT_FAIL.getErrMsg(),e);
		}
    	if(zrAccountDTO==null){
    		logger.info("债转结算--- 调用账户服务查询账户信息返回为空 debtDTO.getUserId ={}",debtDTO.getUserId());
    		throw new ServiceException(ExceptionCode.ACCOUNT_ACCOUNT_FIND_NULL.getIndex(),ExceptionCode.ACCOUNT_ACCOUNT_FIND_NULL.getErrMsg());
    	}
    	Invest zrinvest=null;
    	try {
    		zrinvest=investDao.findById(debtDTO.getInvestId());
		} catch (Exception e) {
			logger.info("债转结算--- 查询投资记录列表异常 debtProductId ={}",debtDTO.getDebtAssignProductId());
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(),ExceptionCode.INVEST_NOT_EXIST.getErrMsg(),e);
		}
    	if(zrinvest==null){
    		logger.info("债转结算--- 查询投资记录列表返回为空 debtProductId ={}",debtDTO.getDebtAssignProductId());
			throw new ServiceException(ExceptionCode.INVEST_NOT_EXIST.getIndex(),ExceptionCode.INVEST_NOT_EXIST.getErrMsg());
    	}
		AccountDTO accountDTO= null;//投资人账户信息
    	try {
    		accountDTO= accountService.getByUserId(invest.getUserId());
		} catch (Exception e) {
			logger.info("债转结算--- 调用账户服务查询账户信息异常 accountDTO.id= "+invest.getUserId());
			throw new ServiceException(ExceptionCode.ACCOUNT_ACCOUNT_FAIL.getIndex(),ExceptionCode.ACCOUNT_ACCOUNT_FAIL.getErrMsg(),e);
		}
    	if(accountDTO==null){
    		logger.info("债转结算--- 调用账户服务查询账户信息返回为空 accountDTO.id ={}",invest.getUserId());
    		throw new ServiceException(ExceptionCode.ACCOUNT_ACCOUNT_FIND_NULL.getIndex(),ExceptionCode.ACCOUNT_ACCOUNT_FIND_NULL.getErrMsg());
    	}
    	
    	if(invest.getStatus().equals(InvestStatus.BJ_FROZEN_SUCCESS)){//本地和北京银行冻结成功的投资进行北京银行解冻2
    		logger.info("债转结算---进入北京银行解冻  invest.id ={}",invest.getId());
    		//账户服务解冻北京银行接口
        	try {
        		FreezeResultDto  freezeResultDto=assignService.assignUnfreeze(invest.getBjDfCode());
        		if(freezeResultDto.getIsSuccess()){
        			try {
        				invest.setBjDfCode(freezeResultDto.getOrderNo());
        				invest.setStatus(InvestStatus.BJ_DF_SUCCESS);//解冻北京银行接口成功，准备执行下一步，调用北京银行转让接口 3
        				logger.info("债转结算--- 修改投资记录北京银行冻结流水号 invest={}",invest);
        				int accountflag=investDao.update(invest);
        				if(accountflag==0){
        					logger.info("债转结算--- 修改投资记录账户服务解冻流水号失败 invest= "+invest);
        					throw new ServiceException(ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getErrMsg());	
        				}
        			} catch (ServiceException se) {
        				logger.info("债转结算--- 修改投资记录账户服务解冻流水号异常 invest= "+invest);
        				throw new ServiceException(ExceptionCode.ACCOUNT_DONGJIE_FAIL.getIndex(),ExceptionCode.ACCOUNT_DONGJIE_FAIL.getErrMsg(),se);
        			}
        		}else{
        			throw new ServiceException(ExceptionCode.ACCOUNT_JIEDONG_FAIL.getIndex(),ExceptionCode.ACCOUNT_JIEDONG_FAIL.getErrMsg());
        		}
    		}  catch (Exception e) {
    			e.printStackTrace();
    			logger.info("债转结算--- 调用账户服务解冻北京银行接口异常 investid={},invest.accountSerialNo={},invest.bankSerialNo={}",invest.getId(),invest.getLocalDfCode(),invest.getBjDfCode());
    			throw new ServiceException(ExceptionCode.ACCOUNT_JIEDONG_FAIL.getIndex(),ExceptionCode.ACCOUNT_JIEDONG_FAIL.getErrMsg(),e);
    		}
    	}
    	BigDecimal sxf=debtDTO.getCounterFee().multiply(invest.getAmount().divide(debtAmount,16, RoundingMode.DOWN))
    															.setScale(2, RoundingMode.DOWN);//按比例分配手续费
    	if(invest.getStatus().equals(InvestStatus.BJ_DF_SUCCESS)){//北京银行解冻成功的投资进入北京银行转让接口 3
    		logger.info("债转结算---进入北京银行转让接口  invest.id ={}",invest.getId());
    		//北京银行转让接口
        	String orderNo="zri_"+UUIDGenerator.generate();
    		Date jyDate=invest.getSubmitTime();
    		String YYMMDD=DateUtils.toString(jyDate, "yyyyMMdd");
    		String HHmmSS=DateUtils.toString(jyDate, "HHmmSS");
    		CouponPackage coupon=null;
    		if(StringUtils.isNotBlank(invest.getCouponPlacememtId())){
				CouponPlacement  couponPlacement =couponService.findCouponPlacementbyId(null, invest.getCouponPlacememtId());
				if(couponPlacement.getStatus().equals(CouponStatus.PLACED)){
					try {
		    			coupon=couponService.findCouponPackagebyId(null, invest.getCouponPlacememtId());
		    		} catch (Exception e) {
		    			logger.info("债转结算--- 调用红包接口查询红包异常 investDTO.couponPlacememtId= "+invest.getCouponPlacememtId());
		    			throw new ServiceException(ExceptionCode.DEBT_INVEST_AMOUNT_ZERO.getIndex(),ExceptionCode.DEBT_INVEST_AMOUNT_ZERO.getErrMsg());
		    		}
				}
			}
    		BigDecimal dxj=BigDecimal.ZERO;
    		if(coupon!=null&&coupon.getType().equals(CouponType.CASH)){//抵现卷
    			dxj=BigDecimal.valueOf(coupon.getParValue());
    		}
    		CommontResultDto commontResultDto=null;
    		try {
    			BigDecimal zrsy=invest.getAmount().subtract(investRepayment.getPrincipalAmount());
    			LoanCommissionExtDto loanCommissionDto=new LoanCommissionExtDto(DictionaryEnum.PAYOUTPLATTYPE01, sxf);
    			//北京银行转让接口
    			LoanAgent loanAgent = LoanAgent.getInstance(agentConfig); 
    			commontResultDto=loanAgent.transFerOfTarget(agentConfig.getPlatNo(), orderNo, YYMMDD, HHmmSS,
    					zrAccountDTO.getPlatcust(),//转让人平台客户号
    					investRepayment.getPrincipalAmount(),//转让份额=转让本金
    					debtDTO.getLoanId(),//标的=产品编号
    					invest.getAmount(),//交易金额
    					invest.getAmount().subtract(dxj).subtract(sxf).subtract(zrsy),//自费价格
    					dxj, //抵用劵金额
    					accountDTO.getPlatcust(), //受让人平台客户号
    					null,//受让人手续费
    					loanCommissionDto,//受让人手续费说明  只收取转让人的手续费，但是接口中只能传入受让人手续费，所把手续费按比例分配到受让人的交易金额中
    					debtDTO.getRequestStartDate(),// 发布时间(YYYY-MM-DD HH:mm:ss)
    					debtDTO.getRequestEndDate(),//成交时间(YYYY-MM-DD HH:mm:ss)
    					zrsy,//转让收益 
    					accountDTO.getPlatcust(),//收益出资方账户
    					null,//涉及的标的编号
    					DictionaryEnum.SUBJECTPRIORITY0, //科目优先级0-可提优先1可投优先
    					null//备注
    					);
    		} catch (Exception e) {
    			logger.info("债转结算--- 调用北京银行转让接口异常 queryOrderNo = "+orderNo);
    			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(),"调用北京银行转让接口异常",e);
    		}
    		if(commontResultDto==null){
    			logger.info("债转结算--- 调用北京银行转让接口返回为空 invest.id ={},user.id ={},orderNo =, "+invest.getId(),accountDTO.getId(),orderNo);
    			throw new ServiceException(ExceptionCode.BANK_DEBT_AGENT_FAIL.getIndex(),ExceptionCode.BANK_DEBT_AGENT_FAIL.getErrMsg());
    		}
    		logger.info("债转结算--- 调用北京银行转让接口返回状态 commontResultDto.recode= "+commontResultDto);
    		if (commontResultDto.getRecode().equals("10000")) {
    			try {
    				invest.setBjDfCode(orderNo);
    				invest.setStatus(InvestStatus.BJ_SYN_SUCCESS);//北京银行转让接口调用成功成功，准备执行下一步，调用本地转让接口4
    				logger.info("债转结算--- 修改投资记录北京银行冻结流水号 invest={}",invest);
    				int accountflag=investDao.update(invest);
    				if(accountflag==0){
    					logger.info("债转结算--- 修改投资记录账户服务解冻流水号失败 invest= "+invest);
    					throw new ServiceException(ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getErrMsg());	
    				}
    			} catch (ServiceException se) {
    				logger.info("债转结算--- 修改投资记录账户服务解冻流水号异常 invest= "+invest);
    				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(),"修改投资记录账户服务解冻流水号异常",se);
    			}
    		}else {//20016，需要从新查询订单状态
    			//生成新的订单号，北京银行的订单查询接口会把这个新订单号存入库
    			String neworderno="cxi_"+UUIDGenerator.generate();
    			OrderQueryResultDto queryOrder;
    			try {
    				PayAgent payAgent=PayAgent.getInstance(agentConfig);
    				queryOrder=payAgent.queryOrder(agentConfig.getPlatNo(), neworderno, YYMMDD, HHmmSS, orderNo);
    			} catch (Exception e) {
    				logger.info("债转结算--- 调用北京银行订单查询接口异常 queryOrderNo = "+orderNo);
    				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(),"调用北京银行订单查询接口异常",e);
    			}
    			logger.info("债转结算--- 调用北京银行查询接口返回状态 OrderQueryResultDto= "+queryOrder);
    			if(queryOrder==null){
    				logger.info("债转结算--- 调用北京银行订单查询接口返回为空 queryOrderNo = "+orderNo);
    				throw new ServiceException(ExceptionCode.BANK_QUERY_AGENT_FAIL.getIndex(),ExceptionCode.BANK_QUERY_AGENT_FAIL.getErrMsg());
    			}
    			if(queryOrder.getRecode().equals("10000")&&queryOrder.getData().getStatus().equals("1")){
    				try {
        				invest.setBjDfCode(orderNo);
        				invest.setStatus(InvestStatus.BJ_SYN_SUCCESS);//北京银行转让接口调用成功成功，准备执行下一步，调用本地转让接口 4
        				logger.info("债转结算--- 修改投资记录北京银行冻结流水号 invest={}",invest);
        				int accountflag=investDao.update(invest);
        				if(accountflag==0){
        					logger.info("债转结算--- 修改投资记录账户服务解冻流水号失败 invest= "+invest);
        					throw new ServiceException(ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getErrMsg());	
        				}
        			} catch (ServiceException se) {
        				logger.info("债转结算--- 修改投资记录账户服务解冻流水号异常 invest= "+invest);
        				throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(),"修改投资记录账户服务解冻流水号异常",se);
        			}
    			}else{
    				logger.info("债转结算--- 调用北京银行订单查询接口未返回成功状态 queryOrder ={}",queryOrder);
    				throw new ServiceException(ExceptionCode.BANK_DEBT_AGENT_FAIL.getIndex(),ExceptionCode.BANK_DEBT_AGENT_FAIL.getErrMsg());
    			}
    		}
    	}
		if(invest.getStatus().equals(InvestStatus.BJ_SYN_SUCCESS)){//北京银行转让接口调用成功，准备执行本地转让接口 4
			logger.info("债转结算---进入本地账户服务转让接口  invest.id ={}",invest.getId());
			//平台转让接口
			try {
				logger.info("债转结算--- 本地平台转让家口调用开始--- ");
				FreezeResultDto freezeResultDto=assignService.assignSettle(invest.getLocalFreezeNo(), invest.getBjDfCode(), sxf, debtDTO.getUserId(),debtDTO.getAccountApplyId());
				logger.info("债转结算--- 本地平台转让家口调用结果--- "+freezeResultDto);
				if(freezeResultDto.getIsSuccess()){
					logger.info("债转结算--- 本地平台转让家口调用返回成功结果--- "+freezeResultDto.getIsSuccess());
					try {
        				invest.setLocalDfCode(freezeResultDto.getFundOperateId());
        				invest.setStatus(InvestStatus.SETTLED);//本地转让接口调用成功成功，保存终止状态“已结算” 6
        				logger.info("债转结算--- 修改投资记录北京银行冻结流水号 invest{}"+invest);
        				int accountflag=investDao.update(invest);
        				if(accountflag==0){
        					logger.info("债转结算--- 修改投资记录账户服务解冻流水号失败 invest{} "+invest);
        					throw new ServiceException(ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getErrMsg());	
        				}
        			} catch (ServiceException se) {
        				logger.info("债转结算--- 修改投资记录账户服务解冻流水号失败 invest{} "+invest);
        				throw new ServiceException(ExceptionCode.ACCOUNT_DONGJIE_FAIL.getIndex(),ExceptionCode.ACCOUNT_DONGJIE_FAIL.getErrMsg(),se);
        			}
				}
			} catch (ServiceException se) {
				logger.info("债转结算--- 调用账户服务转让接口异常investid={},investDTO.accountSerialNo={},investDTO.bankSerialNo= "+invest.getId(),invest.getLocalDfCode(),invest.getBjDfCode());
				throw new ServiceException(ExceptionCode.ACCOUNT_ZHUANRANG_FAIL.getIndex(),ExceptionCode.ACCOUNT_ZHUANRANG_FAIL.getErrMsg(),se);
			}
		}
    	
    	boolean flag=true;
    	if (updateStatus) {
    		logger.info("债转结算---进入本地修改原投资数据状态  invest.id ={}",invest.getId());
    		DebtAssignStatus debtStatus=debtDTO.getStatus();
        	try {
        		flag=debtAssignBridge.settlementDebtUpdateStatus(debtDTO.getDebtAssignProductId(), debtDTO, allAmount,zrinvest,sybj);
    		} catch (ServiceException se) {
    			//手动回滚债转服务的债转状态和债转产品状态
    			debtDTO.setStatus(debtStatus);
    			debtDTO.setBackAmount(BigDecimal.ZERO);
    			debtAssignRequestService.update(debtDTO);
    			throw se;
    		} catch (Exception e) {
    			//手动回滚债转服务的债转状态和债转产品状态
    			debtDTO.setStatus(debtStatus);
    			debtDTO.setBackAmount(BigDecimal.ZERO);
    			debtAssignRequestService.update(debtDTO);
    			logger.info("债转结算--- 更新状态系统程序异常      e={}"+e.toString());
    			throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(),"调用债转结算修改产品和债转申请状态异常",e);	
    		}
        	logger.info("债转结算--- 债转结算正常结束------单笔结算结果={} 是否最后更新原投资状态={}",flag,updateStatus);
		}
		return flag;
	}
}

package com.gomemyc.trade.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.LoanRepayDTO;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.LoanRepayService;
import com.gomemyc.agent.BaseAgent;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.LoanCustRepayListDto;
import com.gomemyc.agent.resp.ProdRepayResultDto;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.common.utils.DateUtils;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.LoanStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.trade.dto.LoanRepaymentDTO;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dao.InvestRepaymentDao;
import com.gomemyc.trade.dao.LoanRepaymentDao;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.entity.LoanRepayment;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.enums.RepaymentStatus;
import com.gomemyc.trade.service.LoanRepaymentService;
import com.gomemyc.trade.util.DTOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class LoanRepaymentServiceImpl implements LoanRepaymentService{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private  LoanRepaymentDao loanRepaymentDao;
	@Autowired
	private InvestRepaymentDao investRepaymentDao;
	@Autowired
	private AgentConfig agentConfig;
	@Reference
	private AccountService accountService;//账户服务
	@Reference
	private LoanRepayService loanRepayService;
	@Reference
	private LoanService loanService;
	@Autowired
	private InvestDao investDao;
	
	/**
	 * 标的还款，需要调用北京银行标的还款计划
	 * @param loanRepaymentId 还款计划表ID
	 * @author zhangWei
	 */
	@Override
	public Boolean repayLoan(String loanRepaymentId) throws ServiceException {
		logger.info("标的还款开始-----------------------还款计划ID={}",loanRepaymentId);
		if(loanRepaymentId==null){
			logger.info("标的还款还款计划ID不能为空",loanRepaymentId);
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_REPAYMENT_ID_REQUIRED.getErrMsg());	
		}
		//查询标的还款，融资人的还款计划表
		LoanRepayment loanRepayment=null;
		try {
			loanRepayment=loanRepaymentDao.findById(loanRepaymentId);
		} catch (Exception e) {
			logger.info("查询标的还款计划表异常还款计划ID={}",loanRepaymentId);
			throw new ServiceException(90001,"查询标的还款计划表异常",e);	
		}
		if(loanRepayment==null){
			logger.info("查询标的还款计划表为空还款计划ID={}",loanRepaymentId);
			throw new ServiceException(ExceptionCode.LOAN_REPAYMENT_EXIST.getIndex(), 
                    ExceptionCode.LOAN_REPAYMENT_EXIST.getErrMsg());
		}
		if(loanRepayment.getStatus().equals(RepaymentStatus.REPAYED)){//已还清的标的还款状态不能进行还款
			logger.info("已还清的标的还款状态不能进行还款还款计划ID={}",loanRepaymentId);
			throw new ServiceException(ExceptionCode.LOAN_REPAYMENT_STATUS_ERROR.getIndex(), 
                    ExceptionCode.LOAN_REPAYMENT_STATUS_ERROR.getErrMsg());
		}
		LoanDTO loan=null;
		try {
			 loan=loanService.findById(loanRepayment.getLoanId());
		} catch (Exception e) {
			logger.info("标的还款--查询异常 loanid={}",loanRepayment.getLoanId());
			throw new ServiceException(ExceptionCode.INVEST_NOT_DATE_OPEN.getIndex(), 
                    ExceptionCode.INVEST_NOT_DATE_OPEN.getErrMsg());
		}
		if(loan==null){
			logger.info("标的还款--查询loan为空loanid={}",loanRepayment.getLoanId());
			throw new ServiceException(ExceptionCode.INVEST_NOT_DATE_OPEN.getIndex(), 
                    ExceptionCode.INVEST_NOT_DATE_OPEN.getErrMsg());
		}
		//校验所有标的相关的投资是否有中间状态存在。
		try {
			List<Invest> list= investDao.findByLoanAndStatus(loan.getId(), //标的ID
						InvestStatus.LOCAL_FROZEN_SUCCESS,//本地资金冻结成功
						InvestStatus.BJ_FROZEN_SUCCESS,//北京资金冻结成功,债转
						InvestStatus.BJ_DF_SUCCESS,//北京银行解冻成功
						InvestStatus.BJ_SYN_SUCCESS,//银行资金同步成功
						InvestStatus.SUCCESS); //本地同步成功
			if(list!=null&&list.size()>0){//存在中间状态的投资不能还款
				logger.info("标的还款--标的投资中有处于处理中未完成的投资记录存在不能还款   loanId={}",loan.getId());
				throw new ServiceException(ExceptionCode.INVEST_CLZ_STATUS.getIndex(), 
	                    ExceptionCode.INVEST_CLZ_STATUS.getErrMsg());
			}
		} catch (Exception e) {
			logger.info("标的还款--查询处理中的投资列表异常   loanId={}",loan.getId());
			throw new ServiceException(ExceptionCode.INVEST_List_EXCEPTION.getIndex(), 
                    ExceptionCode.INVEST_List_EXCEPTION.getErrMsg(),e);
		}
		
		
		List<ProductDTO> productList=null;
		try {
			productList = loanService.findProductByLoanId(loanRepayment.getLoanId());
		} catch (Exception e) {
			logger.info("标的还款--查询产品集合异常loanid={}",loanRepayment.getLoanId());
			throw new ServiceException(ExceptionCode.LOAN_PRODUCT_EXIST.getIndex(), 
                    ExceptionCode.LOAN_PRODUCT_EXIST.getErrMsg());
		}
		if(productList==null||productList.size()==0){
			logger.info("标的还款--查询产品集合异常",loanRepayment.getLoanId());
			throw new ServiceException(ExceptionCode.LOAN_PRODUCT_EXIST.getIndex(), 
                    ExceptionCode.LOAN_PRODUCT_EXIST.getErrMsg());
		}
		//查询需要回款的标的回款记录,投资人的回款计划表
		List<InvestRepayment> irList=null;
		try {
			irList=investRepaymentDao.findByLoanIdAndStatuses(loanRepayment.getLoanId(), RepaymentStatus.UNDUE,RepaymentStatus.OVERDUE,RepaymentStatus.BREACH);
		} catch (Exception e) {
			logger.info("标的还款--查询回款集合异常loanid={}",loanRepayment.getLoanId());
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_EXIST.getIndex(), 
                    ExceptionCode.INVEST_REPAYMENT_EXIST.getErrMsg());
		}
		if(irList.isEmpty()){
			logger.info("标的还款--查询回款集合异常loanid={}",loanRepayment.getLoanId());
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_EXIST.getIndex(), 
                    ExceptionCode.INVEST_REPAYMENT_EXIST.getErrMsg());
		}
		//北京银行还款接口
		LoanAgent loanAgent = LoanAgent.getInstance(agentConfig); 
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		BigDecimal allAmount=BigDecimal.ZERO;
		ArrayList<LoanRepayDTO> repayPlanList=new ArrayList<LoanRepayDTO>();
		for (int i = 0; i < irList.size(); i++) {
			InvestRepayment investRepayment = irList.get(i);
			BigDecimal realRepayAmt=investRepayment.getPrincipalAmount().
					add(investRepayment.getInterestAmount()).
					add(investRepayment.getInterestCouponAmount()).
					add(investRepayment.getInterestPlusAmount());
			allAmount=allAmount.add(realRepayAmt);
			LoanRepayDTO loanRepayDTO=new LoanRepayDTO();
			loanRepayDTO.setBorrowerCharge(investRepayment.getInterestAmount());
			loanRepayDTO.setCouponCharge(investRepayment.getInterestCouponAmount());
			loanRepayDTO.setInvestCharge(BigDecimal.ZERO);
			loanRepayDTO.setPlatCharge(investRepayment.getInterestPlusAmount());
			loanRepayDTO.setPrincipalMoney(investRepayment.getPrincipalAmount());
			loanRepayDTO.setTotalAmount(realRepayAmt);
			loanRepayDTO.setUserId(investRepayment.getUserId());
			loanRepayDTO.setInvestId(investRepayment.getInvestId());
			repayPlanList.add(loanRepayDTO);
			AccountDTO accountDTO= null;//投资人账户信息
	    	try {
	    		accountDTO= accountService.getByUserId(investRepayment.getUserId());
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 调用账户服务查询账户信息异常 accountDTO.id={} ",investRepayment.getUserId());
				throw new ServiceException(ExceptionCode.ACCOUNT_ACCOUNT_FAIL.getIndex(),ExceptionCode.ACCOUNT_ACCOUNT_FAIL.getErrMsg(),e);
			}
	    	if(accountDTO==null){
	    		logger.info("LoanRepaymentService.repayLoan 调用账户服务查询账户信息返回为空 accountDTO.id={} ",investRepayment.getUserId());
	    		throw new ServiceException(ExceptionCode.ACCOUNT_ACCOUNT_FIND_NULL.getIndex(),ExceptionCode.ACCOUNT_ACCOUNT_FIND_NULL.getErrMsg());
	    	}
	    	Map<String, String> LoanCustRepayMap=new HashMap<>();
	    	String realRepayVal=investRepayment.getInterestAmount().add(investRepayment.getInterestPlusAmount()).toString();//实际还款利息
			if(accountDTO.getPlatcust()!=null){//投资人平台客户号
				LoanCustRepayMap.put("cust_no", accountDTO.getPlatcust());
			}
			if(realRepayAmt!=null){//实际还款金额（实际还款本金+体验金+加息金+利息+手续费）
				LoanCustRepayMap.put("real_repay_amt", realRepayAmt.toString());	
			}
			if(investRepayment.getPrincipalAmount()!=null){//实际还款本金
				LoanCustRepayMap.put("real_repay_amount", investRepayment.getPrincipalAmount().toString());	
			}
			if(realRepayVal!=null){//实际还款利息
				LoanCustRepayMap.put("real_repay_val", realRepayVal);	
			}
			//体验金
			LoanCustRepayMap.put("experience_amt", "0");	
			//还款期数
			LoanCustRepayMap.put("repay_num", "1");	
			//手续费
			LoanCustRepayMap.put("repay_fee", "0");	
			if(investRepayment.getInterestCouponAmount()!=null){//加息金
				LoanCustRepayMap.put("rates_amt", investRepayment.getInterestCouponAmount().toString());	
			}
			//还款日期
			LoanCustRepayMap.put("repay_date", DateUtils.toString(new Date(), "yyyy-MM-dd"));	
			//实际还款日期
			LoanCustRepayMap.put("real_repay_date", DateUtils.toString(new Date(), "yyyy-MM-dd"));	
			list.add(LoanCustRepayMap);
		}
		JSONObject funddata = new JSONObject();
		funddata.put("custRepayList", list);
		//初始状态下开始调用北京银行标的还款接口   
		String orderNo="hk_"+UUIDGenerator.generate();
		logger.info("LoanRepaymentService.repayLoan 标的还款计划当前状态 loanRepayment.status={} ",loanRepayment.getStatus());
		if(loanRepayment.getStatus().equals(RepaymentStatus.UNDUE)||
				loanRepayment.getStatus().equals(RepaymentStatus.OVERDUE)||
				loanRepayment.getStatus().equals(RepaymentStatus.BREACH)){
			Date hkDate=new Date();
			String YYMMDD=DateUtils.toString(hkDate, "yyyyMMdd");
			String HHmmSS=DateUtils.toString(hkDate, "HHmmSS");
			ProdRepayResultDto  dto=null;
			try {
				logger.info("LoanRepaymentService.repayLoan 北京银行标的还款调用开始-----");
				dto=loanAgent.repayOfTarget(agentConfig.getPlatNo(), orderNo, YYMMDD, HHmmSS, loanRepayment.getLoanId(), 1, DictionaryEnum.IS0, allAmount,DictionaryEnum.IS0, funddata.toString(), null);
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 调用北京银行还款接口异常-----");
				throw new ServiceException(40021,"调用北京银行还款接口异常",e);
			}
			if(dto==null){
				logger.info("LoanRepaymentService.repayLoan 调用北京银行还款接口返回null-----");
				throw new ServiceException(40021,"调用北京银行还款接口返回null");
			}
			if(dto.getRecode().equals("10000")){//调用北京银行还款接口成功
				logger.info("LoanRepaymentService.repayLoan 北京银行标的还款返回成功状态-----ProdRepayResultDto=={}",dto);
				int loanRepaymentCount=0;
				try {
					loanRepayment.setStatus(RepaymentStatus.BJ_REPAYMENT_SUCCESS);//修改成功后当前对象状态更新成最新状态
					loanRepayment.setBjOrderNo(orderNo);
					loanRepaymentCount=loanRepaymentDao.update(loanRepayment);
				} catch (Exception e) {
					logger.info("LoanRepaymentService.repayLoan 更新标的还款计划表异常   loanRepayment={}",loanRepayment);
					throw new ServiceException(40022,"更新标的还款表异常",e);
				}
				if(loanRepaymentCount==0){
					logger.info("LoanRepaymentService.repayLoan 更新标的还款计划表失败 loanRepaymentCount={} ",loanRepaymentCount);
					throw new ServiceException(40023,"更新标的还款表失败");
				}
			}else{
				logger.info("LoanRepaymentService.repayLoan 北京银行标的还款未能返回成功状态-----ProdRepayResultDto={}",dto);
				throw new ServiceException(Integer.valueOf(dto.getRecode()),dto.getRemsg());
			}
		}
		//北京银行标的还款成功返回后调用本地标的还款
		if(loanRepayment.getStatus().equals(RepaymentStatus.BJ_REPAYMENT_SUCCESS)){
			String localLoanRepay=null;
			try {
				logger.info("LoanRepaymentService.repayLoan 本地标的还款调用开始-----");
				localLoanRepay=loanRepayService.loanRapayment(loanRepayment.getUserId(), loanRepayment.getLoanId(), loanRepayment.getId(), repayPlanList,orderNo);
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 本地标的还款调用异常   loanRepayment={}",loanRepayment);
				throw new ServiceException(40024,"本地账户服务标的还款调用异常",e);
			}
			if(localLoanRepay==null){
				logger.info("LoanRepaymentService.repayLoan 本地标的还款调用返回错误   返回字符串={}",localLoanRepay);
				throw new ServiceException(40024,"本地账户服务标的还款调用返回错误 ");
			}
			int loanRepaymentCount2=0;
			try {
				loanRepayment.setStatus(RepaymentStatus.LOCAL_REPAYMENT_SUCCESS);//修改成功后当前对象状态更新成最新状态
				loanRepayment.setLocalOrderNo(localLoanRepay);
				loanRepaymentCount2=loanRepaymentDao.update(loanRepayment);
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 更新标的还款计划表异常   loanRepayment={}",loanRepayment);
				throw new ServiceException(40022,"更新标的还款表异常",e);
			}
			if(loanRepaymentCount2==0){
				logger.info("LoanRepaymentService.repayLoan 更新标的还款计划表失败 loanRepaymentCount= {}",loanRepaymentCount2);
				throw new ServiceException(40023,"更新标的还款表失败");
			}
			logger.info("LoanRepaymentService.repayLoan 本地标的还款调用正常结束-----");
		}
		if(loanRepayment.getStatus().equals(RepaymentStatus.LOCAL_REPAYMENT_SUCCESS)){//本地调用完成后修改后续状态，标示标的还款结束
			logger.info("LoanRepaymentService.repayLoan 更新状态开始-------------");
			try {
				for (InvestRepayment investRepayment : irList) {
					investRepayment.setStatus(RepaymentStatus.REPAYED);
					investRepayment.setRepayDate(new Date());
					investRepaymentDao.update(investRepayment);
					investDao.updateInvestStatus(investRepayment.getInvestId(), InvestStatus.CLEARED);
				}
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 更新investRepayment状态异常-------------");
				throw new ServiceException(40024,"更新investRepayment状态异常",e);
			}
			try {
				loanRepayment.setStatus(RepaymentStatus.REPAYED);//修改成功后当前对象状态更新成最新状态
				loanRepayment.setRepayDate(new Date());
				loanRepaymentDao.update(loanRepayment);
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 更新loanRepayment状态异常-------------id={}",loanRepayment.getId());
				throw new ServiceException(40024,"更新loanRepayment状态异常",e);
			}
			try {
				loanService.updateStatus(loanRepayment.getLoanId(), LoanStatus.REPAYED);
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 更新loan状态异常-------------id={}",loanRepayment.getLoanId());
				throw new ServiceException(40024,"更新loan状态异常",e);
			}
			try {
				for (ProductDTO productDTO : productList) {
					loanService.modifyProductStatus(productDTO.getId(), ProductStatus.CLEARED);
				}
			} catch (Exception e) {
				logger.info("LoanRepaymentService.repayLoan 更新product状态异常-------------");
				throw new ServiceException(40024,"更新product状态异常",e);
			}
			logger.info("LoanRepaymentService.repayLoan 更新状态成功结束-------------");
			
		}
		logger.info("LoanRepaymentService.repayLoan 标的还款成功结束-----");
		return true;
	}

	@Override
	public LoanRepaymentDTO findByLoanId(String loanId) throws ServiceException {
		if(StringUtils.isEmpty(loanId)){
			throw new ServiceException(ExceptionCode.INVEST_LOAN_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_LOAN_ID_REQUIRED.getErrMsg());
		}
		LoanRepayment ment=loanRepaymentDao.findByLoanId(loanId);
		return DTOUtils.toDTO(ment);
	}

	@Override
	public List<LoanRepaymentDTO> findByStatus(Date date,RepaymentStatus... statuss) throws ServiceException {
		if(statuss==null){
			throw new ServiceException(ExceptionCode.LOAN_REPAYMENT_STATUS_REQUIRED.getIndex(), 
                    ExceptionCode.LOAN_REPAYMENT_STATUS_REQUIRED.getErrMsg());
		}
		List<LoanRepayment> list=loanRepaymentDao.findByStatus(date,statuss);
		List<LoanRepaymentDTO> dtoList=new ArrayList<LoanRepaymentDTO>();
		for (LoanRepayment loanRepayment : list) {
			dtoList.add(DTOUtils.toDTO(loanRepayment));
		}
		return dtoList;
	}

}

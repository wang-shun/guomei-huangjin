package com.gomemyc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gomemyc.account.service.AccountService;
import com.gomemyc.common.page.Page;
import org.junit.Test;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.OrderPlan;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductService;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.trade.service.RepaymentService;

public class InvestTest extends BaseFunctionalTestCase {
    
    @Reference
    private InvestService investService;
    
    @Reference
    private LoanService loanService;
    
    @Reference
    private ProductService productService;
    
    @Reference
    private AccountService accountService;
    
    @Test
    public void createLoan() {
        
//        System.out.println(System.currentTimeMillis());
        
//        investService.hasXsbInvest("080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8");
        
//        String SCRIPT_CONTENT_INVEST = "local loanId = KEYS[1];  local userId = KEYS[2];  local amount = KEYS[3];     if not(loanId and string.len(loanId) > 0) then    return 'BID_PARAM_ERROR_LOANKEY'; end;  if not(userId and string.len(userId) > 0) then      return 'BID_PARAM_ERROR_USER'; end;  local recordKey = 'LENDING_LOAN_' .. loanId .. '_USER_' .. userId .. '_RECORD'; if(redis.call('exists', recordKey) == 1) then      return 'BID_PROCESSING'; end;  if not(string.find(amount, '^[0-9]+%.?[0-9]?[0-9]?$')) then      return 'BID_AMOUNT_ONLY_TWO_DECIMAL'; end;  amount = tonumber(amount); if not(amount) then  return 'BID_PARAM_ERROR_AMOUNT'; elseif(amount <= 0) then   return 'BID_AMOUNT_POSITIVE'; end; amount = math.floor(amount*100 + .5);  local loanCacheKey = 'LENDING_NEW_LOAN_INFO_' .. loanId; local loan = redis.call('hmget', loanCacheKey,               'status',               'amount',               'investAmount',                 'investMinAmount',                  'investMaxAmount',                  'investStepAmount',                 'bidNumber');  local status = loan[1];         local loanAmount = loan[2];     local investAmount = loan[3];       local investMinAmount = loan[4];   local investMaxAmount = loan[5];       local investStepAmount = loan[6];  local bidNumber = loan[7];     if not(loan[1]) then     return 'BID_NOT_EXISTS'; end;  if(status ~= 'OPENED') then      if(status == 'INITIATED') then      return 'BID_NOT_SCHEDULED';     elseif(status == 'SCHEDULED') then          return 'BID_NOT_DATE_OPEN';     elseif(status == 'FINISHED') then       return 'BID_ALREADY_FINISHED';  elseif(status == 'FAILED' or status == 'SETTLED' or status == 'CLEARED') then       return 'BID_NOT_OPEN';  else        return 'BID_NOT_OPENED_VISIBLE';    end; end;  loanAmount = tonumber(loanAmount); if not(loanAmount and loanAmount > 0) then    return 'LOAN_AMOUNT_CANNOT_BE_NEGATIVE'; end;  investAmount = tonumber(investAmount); if not(investAmount and investAmount >= 0) then   return 'BID_AMOUNT_CANNOT_BE_NEGATIVE'; end;  loanAmount = math.floor(loanAmount*100 + .5); investAmount = math.floor(investAmount*100 + .5);  local balance = loanAmount - investAmount; if(balance <= 0) then     return 'BID_NO_BALANCE'; end;  if(balance < amount) then    return 'BID_NO_ENOUGH_BALANCE'; end;  investMinAmount = tonumber(investMinAmount); if not(investMinAmount) then     return 'BID_MIN_AMOUNT_ERROR'; end;  investMaxAmount = tonumber(investMaxAmount); if not(investMaxAmount and investMaxAmount >= 0) then     return 'BID_MAX_AMOUNT_ERROR'; end;  investStepAmount = tonumber(investStepAmount); if not(investStepAmount and investStepAmount >= 0) then     return 'BID_STEP_AMOUNT_ERROR'; end;  investMinAmount = math.floor(investMinAmount * 100 + .5); investMaxAmount = math.floor(investMaxAmount * 100 + .5); investStepAmount = math.floor(investStepAmount * 100 + .5);  local realAmount = 0;  if(balance < investMinAmount) then    realAmount = balance; elseif(amount < investMinAmount ) then    realAmount = 0; elseif(0 < investMaxAmount and investMaxAmount < amount ) then  realAmount = investMaxAmount; elseif(0 < investStepAmount) then     realAmount = amount - (amount - investMinAmount)% investStepAmount; else    realAmount = amount; end;  if(realAmount ~= amount) then    return 'INVALID_AMOUNT'; end;  investAmount = investAmount + amount; bidNumber = bidNumber + 1;  local integerAmount = math.floor(investAmount/100);  local floatAmount = investAmount % 100;  floatAmount = (floatAmount < 10 and '0' .. floatAmount or floatAmount);   investAmount = integerAmount .. '.' .. floatAmount;  local result = redis.call('hmset', loanCacheKey,                     'investAmount',                     investAmount,                   'bidNumber',                    bidNumber);  if('OK' == result['ok']) then   redis.call('setex', recordKey,                  10 * 60,                investAmount);  return investAmount; end;  return 'BID_UPDATE_INVEST_FAILED';" ;
//
//        RedisScript<String> INVESTSCRIPT = new DefaultRedisScript<String>(SCRIPT_CONTENT_INVEST, String.class);
//        
////        loanService.createLoan(null);
//        
//        System.out.println(INVESTSCRIPT.getSha1());
        
//        investService.invest(null, null, null, null);
        investService.invest("3333", "dq-558db288-0669-11e7-a905-0050569426e2", new BigDecimal(12), null, null);
//System.out.println(UUIDGenerator.generate());
        
//        ProductDTO productDTO = loanService.getProduct("dq-558db288-0669-11e7-a905-0050569426e2");
//        
//    	System.out.println("result == " + productDTO);
        
//        loanService.getProduct("dq-558db288-0669-11e7-a905-0050569426e2");
//        investService.invest("1231312", "dq-558db288-0669-11e7-a905-0050569426e2", new BigDecimal(12), "12312321", null);
//        loanService.publishLoanAccount("dq-8a18908b5b29139b015b3e3e46ae0010");
//        loanService.publishLoanAccount("dq-8a18908b5b29139b015b3d270344000b");
//        investService.invest("080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8", "dq-558db288-0669-11e7-a905-0050569426e2", new BigDecimal(12), null, null);
//        Page<ProductDTO> xsbLoans = productService.listProductByPages("XSB", 
//                                                                        null, 
//                                                                        1, 
//                                                                        100, 
//                                                                        OrderPlan.OPENTIME_DESC_STATUS_ASC,
//                                                                        1, 
//                                                                        1);
        
//        accountService.getByUserId("23432");
        
        
        //        loanService.publishLoanAccount("dq-558db288-0669-11e7-a905-0050569426e2");
        
//        investService.releaseInvest("8A015E6B5B4664E20015B015B4664E2CF00000", "dq-558db288-0669-11e7-a905-0050569426e2", "080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8", new BigDecimal(12));
    }
    /*@Test
    public void findById(){
    	InvestDTO dto=investService.findById("8A015D0A5ACAA22A0015A015ACAA22A3400000");
    	System.out.println(dto.getId());
	}
    @Test
    public void findListByProductIdAndStatus(){
    	List<InvestDTO> list=investService.findListByProductIdAndStatus("dq-558db288-0669-11e7-a905-0050569426e2", InvestStatus.SETTLED);
    	System.out.println(list.size());
<<<<<<< HEAD
    }*/
    
    @Test
    public void testInvestSuccess(){
        
        investService.investSuccess("8A015E6B5B4756D70015B015B4756D72800000", "dq-558db288-0669-11e7-a905-0050569426e2", "080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8");
        
    }

    @Test
    public void findListByLoanAndStatus(){
        List<Integer> list = new ArrayList<Integer>();
        list.add(6);
        Page<InvestDTO> page = investService.findListByLoanAndStatus("8A015FAC5B1EDE390015B015B1EDF6D5800003", list, 1, 10);
    	System.out.println(page.getPageSize());
    }
    
    
    @Test
    public void publicLocalLoan(){
        
//        loanService.publishLoanAccount("dq-558db288-0669-11e7-a905-0050569426e2");
        
    }
    @Test
    public void testDate(){
		 String userId="00009D74-01BA-4098-9416-B16D02F109BA";
		 Integer investStatus=5;
		 Integer page=1;
		 Integer pageSize=10;
		 String loanTypeKey="1";
		 try {
			 List<InvestDTO> listInvestByUserIdAndInvestStatus = investService.listInvestByUserIdAndInvestStatus(userId, investStatus, page, pageSize, loanTypeKey);
			 System.err.println(listInvestByUserIdAndInvestStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
    @Reference 
    RepaymentService repaymentService;
    @Test
    public void Test(){
    	List<InvestRepaymentDTO> investRepayment=new ArrayList<InvestRepaymentDTO>();
    	InvestRepaymentDTO element=new InvestRepaymentDTO();
    	element.setId("8A0110D85B6B39380015B015B6B3938A400000");
    	element.setInterestAmount(new BigDecimal(8222220));
    	investRepayment.add(element);
    	Boolean updateGoldInvestPayments = repaymentService.updateGoldInvestPayments(null, investRepayment);
    	System.err.println(updateGoldInvestPayments);
    }
    @Test
    public void Test09(){
    	InvestDTO investDTO=new InvestDTO();
    	investService.investmentService(investDTO);
    }
}

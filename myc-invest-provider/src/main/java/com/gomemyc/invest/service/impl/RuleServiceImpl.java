package com.gomemyc.invest.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.bridge.ProductBridge;
import com.gomemyc.invest.dao.LoanTypeRulesDao;
import com.gomemyc.invest.dao.ProductRulesDao;
import com.gomemyc.invest.dto.LoanTypeRulesDTO;
import com.gomemyc.invest.dto.ProductRulesDTO;
import com.gomemyc.invest.dto.RulesDTO;
import com.gomemyc.invest.entity.LoanTypeRules;
import com.gomemyc.invest.entity.ProductRules;
import com.gomemyc.invest.entity.Rules;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.RulesClazz;
import com.gomemyc.invest.enums.RulesType;
import com.gomemyc.invest.service.RuleService;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.util.BeanMapper;
/**
  * @ClassName: RuleServiceImpl
  * @Description: 规则服务业务实现
  * @author lifeifei
  * @date 2017年3月28日
  *
 */
@Service
public class RuleServiceImpl implements RuleService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ProductRulesDao productRulesDao;
	
	@Reference
	private InvestService investService;
	
	@Autowired
	private ProductBridge productBridge;
	
	@Autowired
	private LoanTypeRulesDao loanTypeRulesDao;

	@Override
	public List<ProductRulesDTO> listProductRulesByProductId(String productId) throws ServiceException {
		if(StringUtils.isBlank(productId)){
			throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(),ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
		}
		List<ProductRules> list= productRulesDao.listByProductId(productId);
		List<ProductRulesDTO> dtoList=new ArrayList<ProductRulesDTO>();
		for (ProductRules entity : list) {
			ProductRulesDTO dto=new ProductRulesDTO();
			dto.setProductId(entity.getProductId());
			dto.setRulesId(entity.getRulesId());
			dto.setType(entity.getType());
			dto.setValue(entity.getValue());
			dto.setId(entity.getId());
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public List<LoanTypeRulesDTO> listLoanTypeRulesByTypeId(String typeId) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RulesDTO> listRulesByClazz(RulesClazz clazz) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RulesDTO getRulesByClazzAndType(RulesClazz clazz, RulesType type) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createLoanTypeRules(List<LoanTypeRulesDTO> loanTypeRulesDTOs) throws ServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createProductRules(List<ProductRulesDTO> productRulesDTOs) throws ServiceException {
		if(productRulesDTOs==null||productRulesDTOs.size()==0){
			throw new ServiceException(ExceptionCode.CANSHU_NO.getIndex(),ExceptionCode.CANSHU_NO.getErrMsg());
		}
		boolean flag=false;
		try {
			flag= productBridge.saveProductRule(productRulesDTOs);
		} catch (Exception e) {
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg(),e);
		}
		return flag;
	}

	@Override
	public boolean checkInvestAmountByRule(String userId, String productId, BigDecimal investAmount, BigDecimal balanceAmount) throws ServiceException {
		BigDecimal minAmount = null, maxAmount = null, stepAmount = null, maxTotalAmount = null;
		Integer maxTimes = null;
		List<ProductRules> rules = null;
		
		/****************************获取标的的投资规则*****************************************/
		try {
			rules = productRulesDao.listByProductId(productId);
			if(rules == null || rules.isEmpty()){
				throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg());
			}
			for(ProductRules rule : rules){
				switch (rule.getType()) {
				case MIN_SINGLE_AMOUNT:
					minAmount = new BigDecimal(rule.getValue());
					break;
				case MAX_SINGLE_AMOUNT:
					maxAmount = new BigDecimal(rule.getValue());
					break;
				case STEP_AMOUNT:
					stepAmount = new BigDecimal(rule.getValue());
					break;
				case MAX_TOTAL_AMOUNT:
					maxTotalAmount = new BigDecimal(rule.getValue());
					break;
				case MAX_TIME:
					maxTimes = Integer.parseInt(rule.getValue());
					break;
				default:
					break;
				}
			}
			
			/*********************************校验投资金额是否符合规则********************************************/
			//投资金额大于产品剩余投资金额
			if(investAmount.compareTo(balanceAmount) == 1){
				throw new ServiceException(ExceptionCode.GT_BALANCE_AMOUNT.getIndex(),ExceptionCode.GT_BALANCE_AMOUNT.getErrMsg());
			}
			//调用账户检车总投资额和总投资次数
			investService.checkoutInvestAmount(userId, productId, investAmount, maxTotalAmount, maxTimes);
			
			//如果是尾标：只要判断投资金额不能大于最大单笔投资金额与最小单笔投资额之和就OK
			if(investAmount.compareTo(balanceAmount) == 0){
				if(maxAmount == null || investAmount.compareTo(maxAmount.add(minAmount)) == -1){
					return true;
				}
				throw new ServiceException(ExceptionCode.GT_TAILED_AMOUNT.getIndex(),ExceptionCode.GT_TAILED_AMOUNT.getErrMsg());
			}
			//不是尾标:正常逻辑
			if(investAmount.compareTo(minAmount) == -1){
				throw new ServiceException(ExceptionCode.LT_MIN_AMOUNT.getIndex(),ExceptionCode.LT_MIN_AMOUNT.getErrMsg());
			}
			//判断投资金额是否是投资增量的整数倍
			BigDecimal remainder = (investAmount.subtract(minAmount)).divideAndRemainder(stepAmount)[1];
			if(!"0".equals(remainder.toString())){
				throw new ServiceException(ExceptionCode.INVEST_AMOUNT_ERROR.getIndex(),ExceptionCode.INVEST_AMOUNT_ERROR.getErrMsg());
			}
			//判断投资金额不能大于最大单笔投资额
			if(maxAmount != null && investAmount.compareTo(maxAmount) == 1){
				throw new ServiceException(ExceptionCode.GT_TAILED_AMOUNT.getIndex(),ExceptionCode.GT_TAILED_AMOUNT.getErrMsg());
			}
		}catch(ServiceException e){
			throw e;
		} catch (Exception e2) {
			logger.error("checkInvestAmountByRule error,exception:{}", e2);
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg());
		}
		return true;
	}
	
	@Override
	public LoanTypeRulesDTO getByLoanTypeIdAndRulesType(String loanTypeId, RulesType rulesType) throws ServiceException{
	    
	    try {
	        
	        LoanTypeRules typeRules = loanTypeRulesDao.getByLoanTypeIdAndRulesType(loanTypeId, rulesType);
	        
	        return BeanMapper.map(typeRules, LoanTypeRulesDTO.class);
            
        } catch (Exception ex) {
            
            logger.error("fail to get typeRules, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
	    
	}

}

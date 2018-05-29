package com.gomemyc.trade.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.dto.RepayAmountDTO;
import com.gomemyc.trade.dao.InvestRepaymentDao;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.RepaymentStatus;
import com.gomemyc.trade.service.InvestRepaymentService;
import com.gomemyc.trade.util.DTOUtils;
import com.gomemyc.trade.util.DateUtils;
import com.gomemyc.util.BeanMapper;

@Service
public class InvestRepaymentServiceImpl implements InvestRepaymentService{
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private InvestRepaymentDao investRepaymentDao;
	@Override
	public InvestRepaymentDTO findById(String id) throws ServiceException {
		if(StringUtils.isEmpty(id)){
			throw new ServiceException(ExceptionCode.INVEST_REPAYMENT_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_REPAYMENT_ID_REQUIRED.getErrMsg());
		}
		InvestRepayment ment=investRepaymentDao.findById(id);
		return DTOUtils.toDTO(ment);
	}

	@Override
	public InvestRepaymentDTO findByInvestId(String investId) throws ServiceException {
		if(StringUtils.isEmpty(investId)){
			throw new ServiceException(ExceptionCode.INVEST_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_ID_REQUIRED.getErrMsg());
		}
		InvestRepayment ment=investRepaymentDao.findByInvestId(investId);
		return DTOUtils.toDTO(ment);
	}

	@Override
	public RepayAmountDTO sumByMonth(String userId, int year, int month, List<RepaymentStatus> statusList)
			throws ServiceException {
		if (statusList == null || statusList.size() <= 0) {
            return new RepayAmountDTO(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        String dueDate = null;
        if(0==year){
			  year = Calendar.getInstance().get(Calendar.YEAR);  
		}
		if(0==month){
			  month = Calendar.getInstance().get(Calendar.MONTH)+1; 
		}
        if(month>=1 && month<10 && year>0){
        	dueDate = year+"-"+"0"+month+"%";
         }else{
        	dueDate = year+"-"+month+"%";
        }
	    Map<String, Object> map=investRepaymentDao.sumByMonth(userId, dueDate, statusList);
	    if(map==null){
	    	return new RepayAmountDTO(BigDecimal.ZERO, BigDecimal.ZERO);
	    }else{
	    	return new RepayAmountDTO(BigDecimal.valueOf(Double.valueOf(map.get("principal").toString())), BigDecimal.valueOf(Double.valueOf(map.get("interest").toString())));
	    }
	}

	@Override
	public List<InvestRepaymentDTO> listByInvest(String investId,List<Integer> status) {
		List<InvestRepayment> list=investRepaymentDao.findListByInvest(investId,status);
		List<InvestRepaymentDTO> investRepaymentList =new ArrayList<InvestRepaymentDTO>();
		for (InvestRepayment investRepayment : list) {
			InvestRepaymentDTO map = BeanMapper.map(investRepayment, InvestRepaymentDTO.class);
			investRepaymentList.add(map);
		}
		return investRepaymentList;
	}


	@Override
	public List<InvestRepaymentDTO> getInvestRepaymentDTO(String userId, int year, int month,
			ArrayList<RepaymentStatus> statusList) {
		if (statusList == null || statusList.size() <= 0) {
            return null;
        }
        String dueDate = null;
        if(0==year){
			  year = Calendar.getInstance().get(Calendar.YEAR);  
		}
		if(0==month){
			  month = Calendar.getInstance().get(Calendar.MONTH)+1; 
		}
        if(month>=1 && month<10 && year>0){
        	dueDate = year+"-"+"0"+month+"%";
         }else{
        	dueDate = year+"-"+month+"%";
        }
        List<InvestRepayment> investRepaymentList = investRepaymentDao.getInvestRepayment(userId,dueDate,statusList);
        List<InvestRepaymentDTO> investRepaymentDTOList =new ArrayList<InvestRepaymentDTO>();
        for (InvestRepayment investRepayment : investRepaymentList) {
        	InvestRepaymentDTO map = BeanMapper.map(investRepayment, InvestRepaymentDTO.class);
        	investRepaymentDTOList.add(map);
		}
		return investRepaymentDTOList;
	}

    @Override
    public BigDecimal getYesterdayInterest(String userId) throws ServiceException {
        
        try {
            
            BigDecimal averageInterest = investRepaymentDao.sumAverageDayInterest(userId, DateUtils.dayStart(new Date()));
            
            return null == averageInterest ? BigDecimal.ZERO : averageInterest;
            
        } catch (Exception ex) {
            logger.error("fail to sum average interest, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        }
    }

	@Override
	public BigDecimal getPrincipalPaidByUserId(String userId) {
		if(StringUtils.isEmpty(userId)){
			throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
		}
		return investRepaymentDao.getPrincipalPaidByUserId(userId);
	}

	@Override
	public BigDecimal getIncomeReceivedByUserId(String userId) {
		if(StringUtils.isEmpty(userId)){
			throw new ServiceException(ExceptionCode.INVEST_USER_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_USER_ID_REQUIRED.getErrMsg());
		}
		return investRepaymentDao.getIncomeReceivedByUserId(userId);
	}
	
	/**
     * 累计到昨日回款收益
     * @param userId
     * @return
     * @author lujixiang
     * @date 2017年4月22日
     *
     */
	@Override
	public BigDecimal getTototalYesterdayInterest(String userId) throws ServiceException{
        
        try {
            
            BigDecimal averageInterest = investRepaymentDao.sumTototalAverageDayInterest(userId, DateUtils.dayStart(new Date()));
            
            return null == averageInterest ? BigDecimal.ZERO : averageInterest;
            
        } catch (Exception ex) {
            logger.error("fail to sum total average interest, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.GLOBAL_EXCEPTION.getIndex(), 
                                       ExceptionCode.GLOBAL_EXCEPTION.getErrMsg(),
                                       ex);
        }
        
    }

}

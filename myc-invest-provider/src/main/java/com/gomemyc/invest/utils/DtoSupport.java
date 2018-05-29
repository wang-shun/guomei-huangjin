package com.gomemyc.invest.utils;

import java.util.ArrayList;
import java.util.List;

import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.dto.ReserveInvestPeriodDTO;
import com.gomemyc.invest.dto.ReserveRateRangeDTO;
import com.gomemyc.invest.entity.LoanType;
import com.gomemyc.invest.entity.ReserveInvestPeriod;
import com.gomemyc.invest.entity.ReserveRate;

public class DtoSupport {
	

    public static List<ReserveInvestPeriodDTO> toReserveInvestPeriodDTOList(List<ReserveInvestPeriod> periodList) {
    	List<ReserveInvestPeriodDTO> investPeriodDTOs = new ArrayList<>();
        for(ReserveInvestPeriod period : periodList){
        	ReserveInvestPeriodDTO dto = new ReserveInvestPeriodDTO();
        	dto.setId(period.getId());
        	dto.setInvestPeriodDesc(period.getInvestPeriodDesc());
        	dto.setMaxInvestPeriod(period.getMaxInvestPeriod());
        	dto.setMinInvestPeriod(period.getMinInvestPeriod());
        	dto.setState(period.isState());
        }
        
        return investPeriodDTOs;
    }
    
    public static List<ReserveRateRangeDTO> toReserveRateDTOList(List<ReserveRate> rateList) {
    	List<ReserveRateRangeDTO> rateRangeDTOs = new ArrayList<>();
        for(ReserveRate rate : rateList){
        	ReserveRateRangeDTO dto = new ReserveRateRangeDTO();
        	dto.setId(rate.getId());
        	dto.setEnable(rate.getEnable());
        	dto.setMinDate(rate.getMinDate());
        	dto.setMaxDate(rate.getMaxDate());
        	dto.setRatePeriodDesc(rate.getRateDesc());
        	
        }
        
        return rateRangeDTOs;
    }

}

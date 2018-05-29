package com.gomemyc.trade.util;

import com.gomemyc.trade.dto.ReserveInvestPeriodDTO;
import com.gomemyc.trade.dto.ReserveRateRangeDTO;
import com.gomemyc.trade.entity.ReserveInvestPeriod;
import com.gomemyc.trade.entity.ReserveRate;
import java.util.ArrayList;
import java.util.List;

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

			// 加入返回集合
			investPeriodDTOs.add(dto);
        }
        
        return investPeriodDTOs;
    }
    
    public static List<ReserveRateRangeDTO> toReserveRateDTOList(List<ReserveRate> rateList) {
    	List<ReserveRateRangeDTO> rateRangeDTOs = new ArrayList<>();
        for(ReserveRate rate : rateList){
        	ReserveRateRangeDTO dto = new ReserveRateRangeDTO();
        	dto.setId(rate.getId());
        	dto.setEnable(rate.getEnable());
			// 防空指针
			Boolean coupon = rate.getCoupon();
			if(null == coupon){
				coupon = false;
			}
			dto.setCoupon(coupon ? 1 : 0);
        	dto.setMinDate(rate.getMinRate());
        	dto.setMaxDate(rate.getMaxRate());
        	dto.setRatePeriodDesc(rate.getRateDesc());

			// 加入返回集合
        	rateRangeDTOs.add(dto);
        }
        
        return rateRangeDTOs;
    }

}

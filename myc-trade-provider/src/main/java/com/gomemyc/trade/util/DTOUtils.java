package com.gomemyc.trade.util;

import java.util.ArrayList;
import java.util.List;

import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.dto.LoanRepaymentDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.entity.LoanRepayment;

public class DTOUtils {

	public static InvestDTO toDTO(Invest invest){
		InvestDTO dto=new InvestDTO();
		if(invest!=null){
			dto.setId(invest.getId());
			dto.setLoanTypeKey(invest.getLoanTypeKey());
			dto.setLoanTypeId(invest.getLoanTypeId());
			dto.setUserId(invest.getUserId());
			dto.setLoanId(invest.getLoanId());
			dto.setProductId(invest.getProductId());
			dto.setAmount(invest.getAmount());
			dto.setRate(invest.getRate());
			dto.setPlusRate(invest.getPlusRate());
			dto.setYears(invest.getYears());
			dto.setMonths(invest.getMonths());
			dto.setDays(invest.getDays());
			dto.setRepaymentMethod(invest.getRepaymentMethod());
			dto.setStatus(invest.getStatus());
			dto.setSubmitTime(invest.getSubmitTime());
			dto.setDebtAmount(invest.getDebtAmount());
			dto.setSource(invest.getSource());
			dto.setEquipmentChannel(invest.getEquipmentChannel());
			dto.setSourceChannel(invest.getSourceChannel());
			dto.setCouponPlacememtId(invest.getCouponPlacememtId());
			dto.setRootInvestId(invest.getRootInvestId());
			dto.setBjDfCode(invest.getBjDfCode());
			dto.setBjSynCode(invest.getBjSynCode());
			dto.setLocalDfCode(invest.getLocalDfCode());
			dto.setLocalFreezeNo(invest.getLocalFreezeNo());
		}else{
			return null;
		}
		return dto;
	}
	public static List<InvestDTO> toListDTO(List<Invest> list){
		List<InvestDTO> listDTO=new ArrayList<InvestDTO>();
		for (Invest invest : list) {
			InvestDTO dto=toDTO(invest);
			listDTO.add(dto);
		}
		return listDTO;
	}
	
	public static InvestRepaymentDTO toDTO(InvestRepayment ment){
		InvestRepaymentDTO dto=new InvestRepaymentDTO();
		if(ment!=null){
			dto.setId(ment.getId());
			dto.setDueDate(ment.getDueDate());
			dto.setInterestAmount(ment.getInterestAmount());
			dto.setInterestCouponAmount(ment.getInterestCouponAmount());
			dto.setInterestPlusAmount(ment.getInterestPlusAmount());
			dto.setInvestId(ment.getInvestId());
			dto.setLoanId(ment.getLoanId());
			dto.setOutstandingAmount(ment.getOutstandingAmount());
			dto.setPrincipalAmount(ment.getPrincipalAmount());
			dto.setRepayAmount(ment.getRepayAmount());
			dto.setRepayDate(ment.getRepayDate());
			dto.setStatus(ment.getStatus());
			dto.setSurplusAmount(ment.getSurplusAmount());
		}else{
			return null;
		}
		return dto;
	}
	
	public static LoanRepaymentDTO toDTO(LoanRepayment ment){
		LoanRepaymentDTO dto=new LoanRepaymentDTO();
		if(ment!=null){
			dto.setCurrentPeriod(ment.getCurrentPeriod());
			dto.setDueDate(ment.getDueDate());
			dto.setId(ment.getId());
			dto.setInterestAmount(ment.getInterestAmount());
			dto.setInterestPlusAmount(ment.getInterestPlusAmount());
			dto.setLoanId(ment.getLoanId());
			dto.setOutstandingAmount(ment.getOutstandingAmount());
			dto.setPrincipalAmount(ment.getPrincipalAmount());
			dto.setRepayAmount(ment.getRepayAmount());
			dto.setRepayDate(ment.getRepayDate());
			dto.setStatus(ment.getStatus());
			dto.setTimeCreatime(ment.getTimeCreatime());
		}else{
			return null;
		}
		return dto;
	}
	public static Invest toOTD(InvestDTO dto){
		Invest invest=new Invest();
		if(dto!=null){
			invest.setId(dto.getId());
			invest.setLoanTypeKey(dto.getLoanTypeKey());
			invest.setLoanTypeId(dto.getLoanTypeId());
			invest.setUserId(dto.getUserId());
			invest.setLoanId(dto.getLoanId());
			invest.setProductId(dto.getProductId());
			invest.setAmount(dto.getAmount());
			invest.setRate(dto.getRate());
			invest.setPlusRate(dto.getPlusRate());
			invest.setYears(dto.getYears());
			invest.setMonths(dto.getMonths());
			invest.setDays(dto.getDays());
			invest.setRepaymentMethod(dto.getRepaymentMethod());
			invest.setStatus(dto.getStatus());
			invest.setSubmitTime(dto.getSubmitTime());
			invest.setDebtAmount(dto.getDebtAmount());
			invest.setSource(dto.getSource());
			invest.setEquipmentChannel(dto.getEquipmentChannel());
			invest.setSourceChannel(dto.getSourceChannel());
			invest.setCouponPlacememtId(dto.getCouponPlacememtId());
			invest.setRootInvestId(dto.getRootInvestId());
			invest.setBjDfCode(dto.getBjDfCode());
			invest.setBjSynCode(dto.getBjSynCode());
			invest.setLocalDfCode(dto.getLocalDfCode());
			invest.setLocalFreezeNo(dto.getLocalFreezeNo());
		}else{
			return null;
		}
		return invest;
	}
}

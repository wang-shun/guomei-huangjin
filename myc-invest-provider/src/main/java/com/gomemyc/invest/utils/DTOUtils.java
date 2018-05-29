package com.gomemyc.invest.utils;

import java.util.ArrayList;
import java.util.List;

import com.gomemyc.invest.dto.DebtPlanDTO;
import com.gomemyc.invest.dto.DebtassignCancelLogDTO;
import com.gomemyc.invest.dto.DebtassignProductDTO;
import com.gomemyc.invest.dto.DebtassignRequestDTO;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductBillDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.entity.DebtPlan;
import com.gomemyc.invest.entity.DebtassignRequest;
import com.gomemyc.invest.entity.DebtassigncancleLog;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;

public class DTOUtils {
 
	public static DebtPlanDTO toDTO(DebtPlan debtPlan){
		DebtPlanDTO dto=new DebtPlanDTO();
		if(debtPlan!=null){
			dto.setId(debtPlan.getId());
	    	dto.setPlanName(debtPlan.getPlanName());
	    	dto.setApplyRedPacket(debtPlan.getApplyRedPacket());
	    	dto.setManualAuditFlag(debtPlan.getManualAuditFlag());
	    	dto.setPreferentialDebt(debtPlan.getPreferentialDebt());
	    	dto.setMaxCollectDeadline(debtPlan.getMaxCollectDeadline());
	    	dto.setMinCollectTimeLimit(debtPlan.getMinCollectTimeLimit());
	    	dto.setMaxCancelCount(debtPlan.getMaxCancelCount());
	    	dto.setFirstDebtDate(debtPlan.getFirstDebtDate());
	    	dto.setSecondDebtDate(debtPlan.getSecondDebtDate());
	    	dto.setOverplusDeadline(debtPlan.getOverplusDeadline());
	    	dto.setTransferRate(debtPlan.getTransferRate());
	    	dto.setMaxPriceRate(debtPlan.getMaxPriceRate());
	    	dto.setMinPriceRate(debtPlan.getMinPriceRate());
	    	dto.setPeopleCount(debtPlan.getPeopleCount());
	    	dto.setCreateTime(debtPlan.getCreateTime());
	    	dto.setUpdateTime(debtPlan.getUpdateTime());
	    	dto.setOprId(debtPlan.getOprId());
	    	dto.setDebtCount(debtPlan.getDebtCount());
	    	dto.setMinExpectedRate(debtPlan.getMinExpectedRate());
	    	dto.setMaxPriceRate(debtPlan.getMaxPriceRate());
		}else{
			return null;
		}
    	return dto;
	}
	
	public static List<DebtPlanDTO> toDebtPlanDTOList(List<DebtPlan> list){
		List<DebtPlanDTO> DTOList=new ArrayList<DebtPlanDTO>();
		if(list!=null&&list.size()>0){
			for (DebtPlan debtPlan : list) {
				DTOList.add(toDTO(debtPlan));
			}
		}
		return DTOList;
	}
	public static DebtassignCancelLogDTO toDTO(DebtassigncancleLog log){
		DebtassignCancelLogDTO dto=new DebtassignCancelLogDTO();
		if(log!=null){
			dto.setId(log.getId());
			dto.setLoanId(log.getLoanId());
			dto.setProductId(log.getProductId());
			dto.setDebtassignId(log.getDebtassignId());
			dto.setDebtassignProductId(log.getDebtassignProductId());
			dto.setInvestId(log.getInvestId());
			dto.setAssignApplyUserId(log.getAssignApplyUserId());
			dto.setAssignApplyUserMobile(log.getAssignApplyUserMobile());
			dto.setAssignLoanCancelType(log.getAssignLoanCancelType());
			dto.setExpactRollBackMoney(log.getExpactRollBackMoney());
			dto.setOperateTime(log.getOperateTime());
			dto.setOperatorId(log.getOperatorId());
			dto.setOperatorName(log.getOperatorName());
			dto.setResult(log.getResult());
			dto.setCancleReason(log.getCancleReason());
			dto.setDescription(log.getDescription());	
		}else{
			return null;
		}
		return dto;
	}
	
	public static ProductRegularDTO toDTO(ProductRegular pr){
		ProductRegularDTO dto=new ProductRegularDTO();
		if(pr!=null){
			dto.setId(pr.getId());
			dto.setTitle(pr.getTitle());
			dto.setLoanId(pr.getLoanId());
			dto.setAmount(pr.getAmount());
			dto.setInvestAmount(pr.getInvestAmount());
			dto.setStatus(pr.getStatus());
			dto.setRate(pr.getRate());
			dto.setMethod(pr.getMethod());
			dto.setYears(pr.getYears());
			dto.setMonths(pr.getMonths());
			dto.setDays(pr.getDays());
			dto.setUserId(pr.getUserId());
			dto.setValueTime(pr.getValueTime());
			dto.setDebtPlanId(pr.getDebtPlanId());
		}else{
			return null;
		}
		return dto;
	}
	
	public static List<ProductDTO> toListDTO(List<ProductRegular> list){
		List<ProductDTO> ListDTO=new ArrayList<ProductDTO>();
		for (ProductRegular productRegular : list) {
			ListDTO.add(toDTO(productRegular));
		}
		return ListDTO;
	}
	
	public static ProductBillDTO toDTO(ProductBill pr){
		ProductBillDTO dto=new ProductBillDTO();
		if(pr!=null){
			dto.setId(pr.getId());
			dto.setTitle(pr.getTitle());
			dto.setLoanId(pr.getLoanId());
			dto.setAmount(pr.getAmount());
			dto.setInvestAmount(pr.getInvestAmount());
			dto.setStatus(pr.getStatus());
			dto.setRate(pr.getRate());
			dto.setMethod(pr.getMethod());
			dto.setYears(pr.getYears());
			dto.setMonths(pr.getMonths());
			dto.setDays(pr.getDays());
			dto.setUserId(pr.getUserId());
			dto.setValueTime(pr.getValueTime());
			dto.setDebtPlanId(pr.getDebtPlanId());
		}else{
			return null;
		}
		return dto;
	}
	
	public static List<ProductDTO> toBillListDTO(List<ProductBill> list){
		List<ProductDTO> ListDTO=new ArrayList<ProductDTO>();
		for (ProductBill productBill : list) {
			ListDTO.add(toDTO(productBill));
		}
		return ListDTO;
	}
	
	
	public static List<DebtassignRequestDTO> toDebtListDTO(List<DebtassignRequest> list){
		List<DebtassignRequestDTO> ListDTO=new ArrayList<DebtassignRequestDTO>();
		for (DebtassignRequest productRegular : list) {
			ListDTO.add(toDTO(productRegular));
		}
		return ListDTO;
	}
	
	public static DebtassignRequestDTO toDTO(DebtassignRequest debt){
		DebtassignRequestDTO dto=new DebtassignRequestDTO();
		if(debt!=null){
			dto.setId(debt.getId());
			dto.setInvestId(debt.getInvestId());
			dto.setLoanId(debt.getLoanId());
			dto.setProductId(debt.getProductId());
			dto.setDebtPlanId(debt.getDebtPlanId());
			dto.setDebtAssignProductId(debt.getDebtAssignProductId());
			dto.setProductTitle(debt.getProductTitle());
			dto.setUserId(debt.getUserId());
			dto.setOriginalAmount(debt.getOriginalAmount());
			dto.setOriginalRate(debt.getOriginalRate());
			dto.setDebtAmount(debt.getDebtAmount());
			dto.setDebtMaxAmount(debt.getDebtMaxAmount());
			dto.setAccountApplyId(debt.getAccountApplyId());
			dto.setCounterFee(debt.getCounterFee());
			dto.setTransferPrice(debt.getTransferPrice());
			dto.setBackAmount(debt.getBackAmount());
			dto.setDiscountAmount(debt.getDiscountAmount());
			dto.setExpectedReturnAmount(debt.getExpectedReturnAmount());
			dto.setDebtDealRate(debt.getDebtDealRate());
			dto.setReturnFullAmount(debt.getReturnFullAmount());
			dto.setRequestStartDate(debt.getRequestStartDate());
			dto.setRequestEndDate(debt.getRequestEndDate());
			dto.setValidDate(debt.getValidDate());
			dto.setProductDueDate(debt.getProductDueDate());
			dto.setLastOperationTime(debt.getLastOperationTime());
			dto.setCancelDate(debt.getCancelDate());
			dto.setCancelCount(debt.getCancelCount());
			dto.setOriginalInvestAmount(debt.getOriginalInvestAmount());
			dto.setOriginalTotalInterestAmount(debt.getOriginalTotalInterestAmount());
			dto.setOriginalDubeInterestAmount(debt.getOriginalDubeInterestAmount());
			dto.setInvestInTotalScale(debt.getInvestInTotalScale());
			dto.setOriginalRemainTotalAmount(debt.getOriginalRemainTotalAmount());
			dto.setOriginalRemainAmount(debt.getOriginalRemainAmount());
			dto.setOriginalRemainInterestAmount(debt.getOriginalRemainInterestAmount());
			dto.setDebtExpectedAmount(debt.getDebtExpectedAmount());
			dto.setDebtExpectedRate(debt.getDebtExpectedRate());
			dto.setDebtPreciseExpectedRate(debt.getDebtPreciseExpectedRate());
			dto.setUserInfo(debt.getUserInfo());
			dto.setPurchasedRate(debt.getPurchasedRate());
			dto.setDebtRealAmount(debt.getDebtRealAmount());
			dto.setFloatingRate(debt.getFloatingRate());
			dto.setRootPurchasedRate(debt.getRootPurchasedRate());
			dto.setRootFloatingRate(debt.getRootFloatingRate());
			dto.setFirstDebtAssignRequestId(debt.getFirstDebtAssignRequestId());
			dto.setDays(debt.getDays());
			dto.setMonths(debt.getMonths());
			dto.setYears(debt.getYears());
			dto.setPricingMethod(debt.getPricingMethod());
			dto.setMethod(debt.getMethod());
			dto.setStatus(debt.getStatus());
			dto.setCancelType(debt.getCancelType());
			dto.setStrfield1(debt.getStrfield1());
			dto.setStrfield2(debt.getStrfield2());
			dto.setStrfield3(debt.getStrfield3());
			dto.setIntfield1(debt.getIntfield1());
			dto.setIntfield2(debt.getIntfield2());
			dto.setIntfield3(debt.getIntfield3());
		}else{
			return null;
		}
		return dto;
	}
	
	public static DebtassignRequest toOTD(DebtassignRequestDTO dto){
		if(dto!=null){
			DebtassignRequest debt=new DebtassignRequest();
			debt.setId(dto.getId());
			debt.setInvestId(dto.getInvestId());
			debt.setLoanId(dto.getLoanId());
			debt.setProductId(dto.getProductId());
			debt.setDebtPlanId(dto.getDebtPlanId());
			debt.setDebtAssignProductId(dto.getDebtAssignProductId());
			debt.setProductTitle(dto.getProductTitle());
			debt.setUserId(dto.getUserId());
			debt.setOriginalAmount(dto.getOriginalAmount());
			debt.setOriginalRate(dto.getOriginalRate());
			debt.setDebtAmount(dto.getDebtAmount());
			debt.setDebtMaxAmount(dto.getDebtMaxAmount());
			debt.setCounterFee(dto.getCounterFee());
			debt.setTransferPrice(dto.getTransferPrice());
			debt.setAccountApplyId(dto.getAccountApplyId());
			debt.setBackAmount(dto.getBackAmount());
			debt.setDiscountAmount(dto.getDiscountAmount());
			debt.setExpectedReturnAmount(dto.getExpectedReturnAmount());
			debt.setDebtDealRate(dto.getDebtDealRate());
			debt.setReturnFullAmount(dto.getReturnFullAmount());
			debt.setRequestStartDate(dto.getRequestStartDate());
			debt.setRequestEndDate(dto.getRequestEndDate());
			debt.setValidDate(dto.getValidDate());
			debt.setProductDueDate(dto.getProductDueDate());
			debt.setLastOperationTime(dto.getLastOperationTime());
			debt.setCancelDate(dto.getCancelDate());
			debt.setCancelCount(dto.getCancelCount());
			debt.setOriginalInvestAmount(dto.getOriginalInvestAmount());
			debt.setOriginalTotalInterestAmount(dto.getOriginalTotalInterestAmount());
			debt.setOriginalDubeInterestAmount(dto.getOriginalDubeInterestAmount());
			debt.setInvestInTotalScale(dto.getInvestInTotalScale());
			debt.setOriginalRemainTotalAmount(dto.getOriginalRemainTotalAmount());
			debt.setOriginalRemainAmount(dto.getOriginalRemainAmount());
			debt.setOriginalRemainInterestAmount(dto.getOriginalRemainInterestAmount());
			debt.setDebtExpectedAmount(dto.getDebtExpectedAmount());
			debt.setDebtExpectedRate(dto.getDebtExpectedRate());
			debt.setDebtPreciseExpectedRate(dto.getDebtPreciseExpectedRate());
			debt.setUserInfo(dto.getUserInfo());
			debt.setPurchasedRate(dto.getPurchasedRate());
			debt.setDebtRealAmount(dto.getDebtRealAmount());
			debt.setFloatingRate(dto.getFloatingRate());
			debt.setRootPurchasedRate(dto.getRootPurchasedRate());
			debt.setRootFloatingRate(dto.getRootFloatingRate());
			debt.setFirstDebtAssignRequestId(dto.getFirstDebtAssignRequestId());
			debt.setDays(dto.getDays());
			debt.setMonths(dto.getMonths());
			debt.setYears(dto.getYears());
			debt.setPricingMethod(dto.getPricingMethod());
			debt.setMethod(dto.getMethod());
			debt.setStatus(dto.getStatus());
			debt.setCancelType(dto.getCancelType());
			debt.setStrfield1(dto.getStrfield1());
			debt.setStrfield2(dto.getStrfield2());
			debt.setStrfield3(dto.getStrfield3());
			debt.setIntfield1(dto.getIntfield1());
			debt.setIntfield2(dto.getIntfield2());
			debt.setIntfield3(dto.getIntfield3());
			return debt;
		}else{
			return null;
		}
	}
	
	public static LoanDTO toDTO(Loan loan){
		LoanDTO dto=new LoanDTO();
		if(loan!=null){
			dto.setId(loan.getId());
			dto.setAmount(loan.getAmount());
			dto.setCompanyName(loan.getCompanyName());
			dto.setDays(loan.getDays());
			dto.setDueTime(loan.getDueTime());
			dto.setLoanTypeId(loan.getLoanTypeId());
			dto.setLoanTypeKey(loan.getLoanTypeKey());
			dto.setLoginName(loan.getLoginName());
			dto.setMethod(loan.getMethod());
			dto.setMonths(loan.getMonths());
			dto.setPlusRate(loan.getPlusRate());
			dto.setPortfolioNo(loan.getPortfolioNo());
			dto.setPurpose(loan.getPurpose());
			dto.setRate(loan.getRate());
			dto.setSource(loan.getSource());
			dto.setStatus(loan.getStatus());
			dto.setSyncOrderNo(loan.getSyncOrderNo());
			dto.setSyncReturnTime(loan.getSyncReturnTime());
			dto.setSyncStatus(loan.getSyncStatus());
			dto.setTitle(loan.getTitle());
			dto.setUserId(loan.getUserId());
			dto.setValueTime(loan.getValueTime());
			dto.setYears(loan.getYears());
			return dto;
		}else{
			return null;
		}
	}
	
	public static DebtassignProductDTO toDebtProductDTO(DebtassignRequest debt){
		if(debt!=null){
			DebtassignProductDTO dto=new DebtassignProductDTO();
			dto.setId(debt.getDebtAssignProductId());
			dto.setNewLoanId(debt.getDebtAssignProductId());
			dto.setInvestId(debt.getInvestId());
			dto.setLoanId(debt.getLoanId());
			dto.setLoanTitle(debt.getProductTitle());
			dto.setDebtPlanId(debt.getDebtPlanId());
			dto.setUserId(debt.getUserId());
			dto.setOriginalAmount(debt.getOriginalAmount());
			dto.setOriginalRate(debt.getOriginalRate());
			dto.setDebtAmount(debt.getDebtAmount());
			dto.setCounterFee(debt.getCounterFee());
			dto.setTransferPrice(debt.getTransferPrice());
			dto.setDiscountAmount(debt.getDiscountAmount());
			dto.setExpectedReturnAmount(debt.getExpectedReturnAmount());
			dto.setDebtDealRate(debt.getDebtDealRate());
			dto.setReturnFullAmount(debt.getReturnFullAmount());
			dto.setRequestStartDate(DateUtils.toString(debt.getRequestStartDate(), "yyyy-MM-dd"));
			dto.setValidDate(debt.getValidDate());
			dto.setCancelCount(debt.getCancelCount());
			dto.setMaxCancelCount(debt.getMaxCancelCount());
			dto.setPricingMethod(debt.getPricingMethod());
			dto.setMethod(debt.getMethod());
			dto.setStatus(debt.getStatus());
			dto.setCreditAmount(debt.getDebtAmount());
			dto.setLoanDueDate(DateUtils.toString(debt.getProductDueDate(), "yyyy-MM-dd"));
			dto.setRequestEndDate(DateUtils.toString(debt.getRequestEndDate(), "yyyy-MM-dd"));
			dto.setMaxCancelCount(debt.getMaxCancelCount());
			dto.setProductKey(debt.getProductKey());
			dto.setAssignLoan(debt.getAssignLoan());
			dto.setOriginalDuration(debt.getOriginalDuration());
			dto.setRemainderDays(DateUtils.getTotalDays(debt.getYears(),debt.getMonths(),debt.getDays()));
			return dto;
		}else{
			return null;
		}
	}
	public static List<DebtassignProductDTO> toDebtProductListDTO(List<DebtassignRequest> list){
		List<DebtassignProductDTO> ListDTO=new ArrayList<DebtassignProductDTO>();
		for (DebtassignRequest debt : list) {
			ListDTO.add(toDebtProductDTO(debt));
		}
		return ListDTO;
	}
}

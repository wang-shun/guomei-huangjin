package com.gomemyc.trade.bridge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.invest.dto.DebtassignRequestDTO;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.DebtAssignStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.DebtAssignRequestService;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductBillService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.invest.utils.FormulaUtil;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dao.InvestRepaymentDao;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.util.DateUtils;

@Component
public class DebtAssignBridge {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Reference
	private LoanService loanService;
    @Autowired
    private InvestRepaymentDao investRepaymentDao;
    @Reference
    private DebtAssignRequestService debtAssignRequestService;
    @Reference
	private CouponService couponService;
    @Reference
    private ProductRegularService productRegularService;
    @Reference
    private ProductBillService productBillService;
    @Autowired
    private InvestDao investDao;
    
    @Transactional
	public  Map<String,BigDecimal> settlementDebt(ProductDTO productDTO, InvestRepayment zrInvestRepayment,
			DebtassignRequestDTO debt, List<Invest> srIncestList,LoanDTO loanDTO,ProductDTO rootproductDTO)  throws Exception{
			BigDecimal tzzbj=BigDecimal.ZERO;//投资的总本金
			BigDecimal tzze=BigDecimal.ZERO;//投资的总额
			//受让人的新加还款计划
			int totaldays=DateUtils.getTotalDays(rootproductDTO.getYears(), rootproductDTO.getMonths(), rootproductDTO.getDays());
			//债转申请截止日期的起息时间
	    	Date requestEndDate = debt.getRequestEndDate();
			//转让人获利天数=转让有效期时间-起息日
	    	int holdingDays = DateUtils.countDays(rootproductDTO.getValueTime(), requestEndDate);
	    	BigDecimal sytzbj=BigDecimal.ZERO;//剩余投资本金，为了抹平因小数点导致的本金余钱
	    	for (Invest invest : srIncestList) {
	    		//投资的本金
				BigDecimal principal = invest.getAmount().divide(BigDecimal.valueOf(1)
						.add(FormulaUtil.dayRate(BigDecimal.valueOf(debt.getPurchasedRate()).divide(BigDecimal.valueOf(10000), 10, RoundingMode.DOWN).intValue()).multiply(BigDecimal.valueOf(holdingDays))),2, RoundingMode.DOWN);
				tzzbj=tzzbj.add(principal);
				tzze=tzze.add(invest.getAmount());
	    	}
	    	//投资的本金
			BigDecimal yprincipal = zrInvestRepayment.getPrincipalAmount();
	    	sytzbj=yprincipal.subtract(tzzbj);//计算出本金中的几分钱没有分配出去，按投资顺利分配给投资人
	    	
	    	tzzbj=BigDecimal.ZERO;//投资的总本金
			for (Invest invest : srIncestList) {
				//投资的本金
				BigDecimal principal = invest.getAmount().divide(BigDecimal.valueOf(1)
						.add(FormulaUtil.dayRate(BigDecimal.valueOf(debt.getPurchasedRate()).divide(BigDecimal.valueOf(10000), 10, RoundingMode.DOWN).intValue()).multiply(BigDecimal.valueOf(holdingDays))),2, RoundingMode.DOWN);
				if(sytzbj.compareTo(BigDecimal.ZERO)==1){
					principal=principal.add(BigDecimal.valueOf(0.01));//本金里加1分钱
					sytzbj=sytzbj.subtract(BigDecimal.valueOf(0.01));//剩余的本金里减一分钱
				}
				tzzbj=tzzbj.add(principal);
				InvestRepayment sr=new InvestRepayment();
				sr.setId(UUIDGenerator.generate());
				sr.setLoanId(invest.getLoanId());
				sr.setInvestId(invest.getId());
				sr.setPrincipalAmount(principal);
				//应还总利息计算
				int syqx=totaldays-holdingDays;
				//剩余利息
				BigDecimal syzlx=invest.getAmount().multiply(FormulaUtil.dayRate(productDTO.getRate()+productDTO.getPlusRate())).multiply(BigDecimal.valueOf(syqx))
																													.setScale(2, RoundingMode.DOWN);
				
				BigDecimal yhzlx=invest.getAmount().add(syzlx).subtract(principal);
				//应还基础利息计算
				BigDecimal yhjclx=principal.multiply(FormulaUtil.dayRate(loanDTO.getRate())).multiply(BigDecimal.valueOf(totaldays))
																							.setScale(2, RoundingMode.DOWN);
				sr.setInterestAmount(yhjclx);//应还基础利息
				sr.setInterestPlusAmount(yhzlx.subtract(yhjclx));//应还加息利息
				//-------------调用红包服务计算加息利息start
				BigDecimal interestCouponAmount=BigDecimal.ZERO;
				CouponPackage coupon=couponService.findCouponPackagebyId(null, invest.getCouponPlacememtId());
				if(coupon!=null&&coupon.getType().equals(CouponType.INTEREST)){//加息卷
					interestCouponAmount=principal.multiply(FormulaUtil.dayRate(loanDTO.getRate())).multiply(BigDecimal.valueOf(syqx));
				}
				sr.setInterestCouponAmount(interestCouponAmount);//应还加息劵利息
				//---------------end--------------------------
				sr.setDueDate(zrInvestRepayment.getDueDate());//应还款日跟原来的一样
				sr.setStatus(zrInvestRepayment.getStatus());
				sr.setRepayAmount(sr.getPrincipalAmount().add(sr.getInterestAmount()).add(sr.getInterestPlusAmount()).add(sr.getInterestCouponAmount()));
				sr.setRepayDate(zrInvestRepayment.getRepayDate());
				sr.setUserId(invest.getUserId());
				sr.setYears(productDTO.getYears());
				sr.setMonths(productDTO.getMonths());
				sr.setDays(productDTO.getDays());
				sr.setValueTime(productDTO.getValueTime());
				int irscount=investRepaymentDao.insert(sr);//保存
				if(irscount==0){
					logger.info("DebtAssignBridge.settlementDebt 保存还款计划失败  zrInvestRepayment[id={},status={}]"+zrInvestRepayment.getId(),zrInvestRepayment.getStatus());
					throw new ServiceException(ExceptionCode.DEBT_INVEST_REPAY_SAVE_ERROR.getIndex(),ExceptionCode.DEBT_INVEST_REPAY_SAVE_ERROR.getErrMsg());
				}
			}
			
			//剩余金额更新
			zrInvestRepayment.setRepayAmount(debt.getOriginalRemainTotalAmount());
			//剩余本金
			BigDecimal sybj=BigDecimal.ZERO;
			if(tzze.compareTo(debt.getTransferPrice())==-1){//不是全部转让的计算剩余的本金
				sybj=debt.getOriginalInvestAmount().subtract(yprincipal);//债转前原始投资本金-实际投资的本金
			}
			zrInvestRepayment.setPrincipalAmount(sybj);
			//基本利息、加息利息--start----------------------
			//基础利息
			BigDecimal baseInterest = sybj.multiply(BigDecimal.valueOf(totaldays)).multiply(FormulaUtil.dayRate(loanDTO.getRate()))
																					 .setScale(2, RoundingMode.DOWN);
			//总利息
			BigDecimal zInterest = sybj.multiply(BigDecimal.valueOf(totaldays))
										.multiply(FormulaUtil.dayRate(loanDTO.getPlusRate()+loanDTO.getRate()))
					 					.setScale(2, RoundingMode.DOWN);
			//基础利息
			zrInvestRepayment.setInterestAmount(baseInterest);
			//加息利息
			zrInvestRepayment.setInterestPlusAmount(zInterest.subtract(baseInterest));
			zrInvestRepayment.setInterestCouponAmount(BigDecimal.ZERO);//应还加息劵利息归零
			zrInvestRepayment.setRepayAmount(zrInvestRepayment.getPrincipalAmount()
										.add(zrInvestRepayment.getInterestAmount())
										.add(zrInvestRepayment.getInterestPlusAmount())
										.add(zrInvestRepayment.getInterestCouponAmount()));
			//----------end-------------------
			//保存已修改的转让人还款计划
			int irucount=investRepaymentDao.update(zrInvestRepayment);//保存
			if(irucount==0){
				throw new ServiceException(ExceptionCode.DEBT_INVEST_REPAY_UPDATE_ERROR.getIndex(),ExceptionCode.DEBT_INVEST_REPAY_UPDATE_ERROR.getErrMsg());
			}
			debt.setDebtRealAmount(tzzbj);
			debtAssignRequestService.update(debt);
			Map<String,BigDecimal> map=new HashMap<String, BigDecimal>();
			map.put("sybj", sybj);//剩余本金
			return map;
	}
    
    @Transactional
   	public  Boolean settlementDebtUpdateStatus(String productId, 
   			DebtassignRequestDTO debt, BigDecimal allAmount,Invest invest,BigDecimal sybj)  throws Exception{
    	logger.info("DebtAssignHandler.debtassignCancel 债转结算修改状态开始 ------------");
    	logger.info("DebtAssignHandler.debtassignCancel 债转结算转让人剩余本金="+sybj);
    	if(sybj.compareTo(BigDecimal.ZERO)==0){//剩余本金为零
    		investDao.updateInvestStatus(invest.getId(), InvestStatus.ASSIGNED);
    	}else{
    		BigDecimal ybj=invest.getAmount();
    		invest.setAmount(sybj);//更新当前剩余本金的投资
    		invest.setDebtAmount(ybj.subtract(sybj));
    		investDao.update(invest);
    		Invest zri=new Invest();
    		zri.setId(UUIDGenerator.generate());
    		zri.setStatus(InvestStatus.ASSIGNED);
    		zri.setAmount(ybj.subtract(sybj));//已转让本金
    		zri.setMobile(invest.getMobile());
    		zri.setName(invest.getName());
    		zri.setLoanTypeKey(invest.getLoanTypeKey());
    		zri.setLoanTypeId(invest.getLoanTypeId());
    		zri.setUserId(invest.getUserId());
    		zri.setLoanId(invest.getLoanId());
    		zri.setProductId(invest.getProductId());
    		zri.setRate(invest.getRate());
    		zri.setPlusRate(invest.getPlusRate());
    		zri.setYears(invest.getYears());
    		zri.setMonths(invest.getMonths());
    		zri.setDays(invest.getDays());
    		zri.setRepaymentMethod(invest.getRepaymentMethod());
    		zri.setSubmitTime(invest.getSubmitTime());
    		zri.setDebtAmount(BigDecimal.ZERO);
    		zri.setSource(invest.getSource());
    		zri.setEquipmentChannel(invest.getEquipmentChannel());
    		zri.setSourceChannel(invest.getSourceChannel());
    		zri.setCouponPlacememtId(invest.getCouponPlacememtId());
    		zri.setRootInvestId(invest.getId());
    		zri.setLocalFreezeNo(invest.getLocalFreezeNo());
    		zri.setBjDfCode(invest.getBjDfCode());
    		zri.setBjSynCode(invest.getBjSynCode());
    		zri.setLocalDfCode(invest.getLocalDfCode());
    		zri.setReward(invest.isReward());
    		zri.setDebted(invest.isDebted());
    		investDao.save(zri);
    	}
		debt.setStatus(DebtAssignStatus.ALREADY_TRANSFERRED);
   		debt.setBackAmount(allAmount);
		boolean debtupdateflag=debtAssignRequestService.update(debt);
		if(!debtupdateflag){
			logger.debug("DebtAssignBridge.settlementDebt 更新债转申请状态失败  debt[id={}]"+debt.getId());
			throw new ServiceException(ExceptionCode.DEBT_PRODUCT_UPDATE_FAIL.getIndex(),ExceptionCode.DEBT_PRODUCT_UPDATE_FAIL.getErrMsg());
		}
		int productupdatcount=0;
		if(productId.startsWith("dq-")){
			productupdatcount=productRegularService.updateStatusById(ProductStatus.SETTLED, productId);
		}else if(productId.startsWith("pj-")){
			productupdatcount=productBillService.updateStatusById(ProductStatus.SETTLED, productId);
		}
		if(productupdatcount==0){
			logger.debug("DebtAssignBridge.settlementDebt 更新债转产品失败  product[id={}]"+productId);
			throw new ServiceException(ExceptionCode.DEBT_PRODUCT_UPDATE_ERROR.getIndex(),ExceptionCode.DEBT_PRODUCT_UPDATE_ERROR.getErrMsg());
		}
		logger.info("DebtAssignHandler.debtassignCancel 债转结算更新状态结束------");
		return true;
   	}
}

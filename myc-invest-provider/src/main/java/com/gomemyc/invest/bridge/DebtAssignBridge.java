package com.gomemyc.invest.bridge;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AssignService;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.invest.dao.DebtAssignRequestDao;
import com.gomemyc.invest.dao.DebtassigncancleLogDao;
import com.gomemyc.invest.dao.DebtcheckRecordDao;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dao.ProductRulesDao;
import com.gomemyc.invest.dto.ProductRulesDTO;
import com.gomemyc.invest.entity.DebtPlan;
import com.gomemyc.invest.entity.DebtassignRequest;
import com.gomemyc.invest.entity.DebtassigncancleLog;
import com.gomemyc.invest.entity.DebtcheckRecord;
import com.gomemyc.invest.entity.Product;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.entity.ProductRules;
import com.gomemyc.invest.enums.AssignLoanCancelStatus;
import com.gomemyc.invest.enums.CheckState;
import com.gomemyc.invest.enums.CheckStep;
import com.gomemyc.invest.enums.CheckType;
import com.gomemyc.invest.enums.DebtAssignCancelType;
import com.gomemyc.invest.enums.DebtAssignStatus;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.ProductSwitch;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.invest.enums.RulesType;
import com.gomemyc.invest.service.RuleService;
import com.gomemyc.invest.utils.DateUtils;
import com.gomemyc.model.user.User;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.InvestService;

@Component
public class DebtAssignBridge {
	@Autowired
	private DebtAssignRequestDao debtAssignRequestDao;
	@Autowired
	private ProductRegularDao productRegularDao;
	@Autowired
	private ProductBillDao productBillDao;
	@Autowired
	private DebtcheckRecordDao debtcheckRecordDao;
	@Reference
	private InvestService investService;
	@Reference
	private AssignService assignService;
	@Autowired
	private DebtassigncancleLogDao debtassigncancleLogDao;
	@Autowired
	private ProductRulesDao productRulesDao;
	
	@Transactional(rollbackFor=Exception.class)
	public Boolean saveDebtProdect(Object product, DebtPlan debtPlan,
			DebtassignRequest debtAssignRequest,InvestDTO invest,List<ProductRules> list) throws Exception{
		int count=debtAssignRequestDao.add(debtAssignRequest);
		if(count==0){
			throw new ServiceException(ExceptionCode.DEBT_CREATE_FAIL.getIndex(),ExceptionCode.DEBT_CREATE_FAIL.getErrMsg());	
		}
		String productId=null;
		//--------------------创建债转产品-------------------------------
		if(invest.getProductId().startsWith("dq-")){//非定期产品不能转让
			ProductRegular productRegular=(ProductRegular)product;
			//定期产品调用定期产品的保存
			ProductRegular debtAssignProduct=new ProductRegular();
			debtAssignProduct.setId("dq-"+UUIDGenerator.generate());
			debtAssignProduct.setRate(debtAssignRequest.getDebtExpectedRate());//年化利率
			debtAssignProduct.setAmount(debtAssignRequest.getTransferPrice());//募集金额=转让价格
			debtAssignProduct.setRootProductId(productRegular.getId());//原产品ID
			debtAssignProduct.setStatus(ProductStatus.READY);//准备
			debtAssignProduct.setLoanId(productRegular.getLoanId());
			debtAssignProduct.setMethod(RepaymentMethod.BulletRepayment);
			debtAssignProduct.setTitle(productRegular.getTitle());
			debtAssignProduct.setCreateTime(new Date());
			debtAssignProduct.setDays(debtAssignRequest.getDays());
			debtAssignProduct.setMonths(debtAssignRequest.getMonths());
			debtAssignProduct.setYears(debtAssignRequest.getYears());
			debtAssignProduct.setDebtPlanId(debtPlan.getId());
			debtAssignProduct.setValueTime(DateUtils.offsetDays(debtAssignRequest.getRequestStartDate(), debtAssignRequest.getValidDate()+1));
			debtAssignProduct.setUserId(debtAssignRequest.getUserId());
			debtAssignProduct.setTypeId(productRegular.getTypeId());
			debtAssignProduct.setTypeKey(productRegular.getTypeKey());
			debtAssignProduct.setTypeName(productRegular.getTypeName());
			debtAssignProduct.setOpenTime(new Date());
			debtAssignProduct.setEndTime(debtAssignRequest.getRequestEndDate());
			debtAssignProduct.setDebted(true);//二手标
			debtAssignProduct.setLocalSyncStatus(productRegular.getLocalSyncStatus());
			debtAssignProduct.setLoginName(productRegular.getLoginName());
			debtAssignProduct.setRiskType(productRegular.getRiskType());
			debtAssignProduct.setSettleTime(productRegular.getSettleTime());
			if(debtPlan.getApplyRedPacket()){
				debtAssignProduct.setProductSwitch(productRegular.getProductSwitch() | ProductSwitch.IS_COUPON.getIndex().intValue());
			}else{
				debtAssignProduct.setProductSwitch((productRegular.getProductSwitch() & ProductSwitch.IS_COUPON.getIndex().intValue() ) ^ productRegular.getProductSwitch());
			}
			
			debtAssignProduct.setSyncStatus(LoanSyncStatus.SUCCESS);
			//合同  协议
			int procount=productRegularDao.save(debtAssignProduct);
			if(procount==0){
				throw new ServiceException(ExceptionCode.DEBT_PRODUCT_CREATE_FAIL.getIndex(),ExceptionCode.DEBT_PRODUCT_CREATE_FAIL.getErrMsg());	
			}
			productId=debtAssignProduct.getId();
		}else if(invest.getProductId().startsWith("pj-")){
			ProductBill productBill=(ProductBill)product;
			ProductBill debtAssignProduct=new ProductBill();
			debtAssignProduct.setId("pj-"+UUIDGenerator.generate());
			debtAssignProduct.setRate(debtAssignRequest.getDebtExpectedRate());//年化利率
			debtAssignProduct.setAmount(debtAssignRequest.getTransferPrice());//募集金额=转让价格
			debtAssignProduct.setRootProductId(productBill.getId());//原产品ID
			debtAssignProduct.setStatus(ProductStatus.READY);//准备
			debtAssignProduct.setLoanId(productBill.getLoanId());
			debtAssignProduct.setMethod(RepaymentMethod.BulletRepayment);
			debtAssignProduct.setTitle(productBill.getTitle());
			debtAssignProduct.setCreateTime(new Date());
			debtAssignProduct.setDays(debtAssignRequest.getDays());
			debtAssignProduct.setMonths(debtAssignRequest.getMonths());
			debtAssignProduct.setYears(debtAssignRequest.getYears());
			debtAssignProduct.setDebtPlanId(debtPlan.getId());
			debtAssignProduct.setValueTime(DateUtils.offsetDays(debtAssignRequest.getRequestStartDate(), debtAssignRequest.getValidDate()+1));
			debtAssignProduct.setUserId(debtAssignRequest.getUserId());
			debtAssignProduct.setTypeId(productBill.getTypeId());
			debtAssignProduct.setTypeKey(productBill.getTypeKey());
			debtAssignProduct.setTypeName(productBill.getTypeName());
			debtAssignProduct.setOpenTime(new Date());
			debtAssignProduct.setEndTime(debtAssignRequest.getRequestEndDate());
			debtAssignProduct.setDebted(true);//二手标
			debtAssignProduct.setLocalSyncStatus(productBill.getLocalSyncStatus());
			debtAssignProduct.setLoginName(productBill.getLoginName());
			debtAssignProduct.setRiskType(productBill.getRiskType());
			if(debtPlan.getApplyRedPacket()){
				debtAssignProduct.setProductSwitch(productBill.getProductSwitch() | ProductSwitch.IS_COUPON.getIndex().intValue());
			}else{
				debtAssignProduct.setProductSwitch((productBill.getProductSwitch() & ProductSwitch.IS_COUPON.getIndex().intValue() ) ^ productBill.getProductSwitch());
			}
			debtAssignProduct.setSyncStatus(LoanSyncStatus.SUCCESS);
			debtAssignProduct.setBillType(productBill.getBillType());
			debtAssignProduct.setAcceptanceBank(productBill.getAcceptanceBank());
			debtAssignProduct.setExtendDeadline(productBill.getExtendDeadline());
			debtAssignProduct.setExtendExpireDate(productBill.getExtendExpireDate());
			debtAssignProduct.setSettleTime(productBill.getSettleTime());
			//合同  协议
			int procount=productBillDao.save(debtAssignProduct);
			if(procount==0){
				throw new ServiceException(ExceptionCode.DEBT_PRODUCT_CREATE_FAIL.getIndex(),ExceptionCode.DEBT_PRODUCT_CREATE_FAIL.getErrMsg());	
			}
			productId=debtAssignProduct.getId();
		}
		if(productId==null){
			throw new ServiceException(ExceptionCode.DEBT_PRODUCT_CREATE_FAIL.getIndex(),ExceptionCode.DEBT_PRODUCT_CREATE_FAIL.getErrMsg());
		}
		try {
			//保存产品规则
			for (ProductRules dto : list) {
				dto.setProductId(productId);
				if(dto.getType().equals(RulesType.MAX_SINGLE_AMOUNT)){
					dto.setValue(debtAssignRequest.getTransferPrice().toString());
				}else if(dto.getType().equals(RulesType.MAX_TOTAL_AMOUNT)){
					dto.setValue(debtAssignRequest.getTransferPrice().toString());
				}
				ProductRules rule=new ProductRules();
				rule.setProductId(dto.getProductId());
				rule.setRulesId(dto.getRulesId());
				rule.setType(dto.getType());
				rule.setValue(dto.getValue());
				productRulesDao.save(rule);
			}
		} catch (Exception e) {
			throw new ServiceException(30080,"保存债转产品规则失败",e);	
		}
		
		
		//提交申请后，记录申请转让操作记录
		int recordCount = saveDebtCheckRecord(debtAssignRequest, CheckState.SUCCESS, CheckStep.APPLY, CheckType.PERSON, "债转申请成功",productId);
		if(recordCount==0){
			throw new ServiceException(ExceptionCode.DEBT_SHSB_CHECK.getIndex(),ExceptionCode.DEBT_SHSB_CHECK.getErrMsg());	
		}
		if(debtPlan.getManualAuditFlag()){//系统自动审核
			int zdrecordcount=saveDebtCheckRecord(debtAssignRequest, CheckState.PASS, CheckStep.SECOND, CheckType.SYSTEM, "系统自动审核成功",productId);
			if(zdrecordcount==0){
				saveDebtCheckRecord(debtAssignRequest, CheckState.FAIL, CheckStep.SECOND, CheckType.SYSTEM, "系统自动审核失败",productId);
				throw new ServiceException(ExceptionCode.DEBT_SHSB_CHECK.getIndex(),ExceptionCode.DEBT_SHSB_CHECK.getErrMsg());	
			}
			//回写债转产品
			debtAssignRequest.setStatus(DebtAssignStatus.OPEN);
			int productUpdatcount=0;
			if(productId.startsWith("dq-")){
				productUpdatcount=productRegularDao.updateProductStatus(ProductStatus.OPENED, productId);
			}else if(productId.startsWith("pj-")){
				productUpdatcount=productBillDao.updateProductStatus(ProductStatus.OPENED, productId);
			}
			if(productUpdatcount==0){
				throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(),ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
			}
		}else{
		}
		FreezeResultDto  freezeResultDto=assignService.assignApply(productId, debtAssignRequest.getInvestId(), debtAssignRequest.getDebtAmount(), debtAssignRequest.getUserId(), debtAssignRequest.getLoanId());
		debtAssignRequest.setAccountApplyId(freezeResultDto.getFundOperateId());
		//回写债转产品的ID
		debtAssignRequest.setDebtAssignProductId(productId);
		debtAssignRequestDao.update(debtAssignRequest);
		if(debtPlan.getManualAuditFlag()){
			investService.updateStatusById(invest.getId(), InvestStatus.ASSIGNING.getIndex());
		}
		return true;
		
		
	}
	/**
	 * 记录申请转让操作记录
	 * @param userId 申请人
	 * @param debtAssignRequest 债转申请
	 * @return
	 */
	private int saveDebtCheckRecord(DebtassignRequest debtAssignRequest,CheckState state,CheckStep step,CheckType type,String desc,String debtproductId) {
		DebtcheckRecord debtCheckRecord = new DebtcheckRecord();
		debtCheckRecord.setId(UUIDGenerator.generate());
		debtCheckRecord.setCheckState(state);
		debtCheckRecord.setCheckStep(step);
		debtCheckRecord.setCheckType(type);
		debtCheckRecord.setCreateTime(new Date());
		debtCheckRecord.setDescription(desc);
		debtCheckRecord.setSourceId(debtAssignRequest.getId());
		debtCheckRecord.setSourceType(0);
		debtCheckRecord.setUserId(debtAssignRequest.getUserId());
		debtCheckRecord.setUserInfo(debtAssignRequest.getUserInfo());
		debtCheckRecord.setDebtAssignProductId(debtproductId);
		int recordCount=debtcheckRecordDao.add(debtCheckRecord);
		return recordCount;
	}
	@Transactional(rollbackFor=Exception.class)
	public Boolean saveCancel(DebtassignRequest debtAssignRequest, User user, Product product,String sysSign) throws Exception {
    	//审核操作----Start
    	DebtcheckRecord debtCheckRecord = new DebtcheckRecord();
    	debtCheckRecord.setId(UUIDGenerator.generate());
    	debtCheckRecord.setUserId(user.getId());
		debtCheckRecord.setCheckStep(CheckStep.CANCLE);
		if(sysSign!=null&&sysSign.equals("ht")){
			debtCheckRecord.setCheckType(CheckType.SYSTEM);
			debtCheckRecord.setDescription("后台操作出让人债权转让撤销");
		}else{
			debtCheckRecord.setCheckType(CheckType.PERSON);
			debtCheckRecord.setDescription("出让人债权转让撤销");
			debtCheckRecord.setUserInfo(user.getMobile());
		}
		debtCheckRecord.setCreateTime(new Date());
		debtCheckRecord.setSourceId(debtAssignRequest.getId());
		debtCheckRecord.setSourceType(0);
		debtCheckRecord.setCheckState(CheckState.SUCCESS);
		debtCheckRecord.setDebtAssignProductId(debtAssignRequest.getDebtAssignProductId());
		//-------end
		//---------记录撤销日志-----------------
		DebtassigncancleLog log=new DebtassigncancleLog();
		log.setId(UUIDGenerator.generate());
		log.setLoanId(product.getLoanId());
		log.setAssignApplyUserId(debtAssignRequest.getUserId());
		log.setAssignApplyUserMobile(debtAssignRequest.getUserInfo());
		log.setOperatorId(user.getId());
		log.setOperatorName(user.getMobile());
		log.setOperateTime(new Date());
		log.setAssignLoanCancelType(DebtAssignCancelType.SELLER_CANCLE);
		if(sysSign!=null&&sysSign.equals("ht")){
			log.setCancleReason("后台操作出让人撤销债转！");
		}else{
			log.setCancleReason("前台出让人撤销债转！");
		}
		log.setProductId(product.getRootProductId());
		log.setDebtassignId(debtAssignRequest.getId());
		log.setDebtassignProductId(product.getId());
		log.setInvestId(debtAssignRequest.getInvestId());
		//----------end-------------------------
	//修改债转申请信息
		debtAssignRequest.setCancelType(DebtAssignCancelType.SELLER_CANCLE);
		debtAssignRequest.setStatus(DebtAssignStatus.CANCELED);
		debtAssignRequest.setCancelDate(new Date());
		int count=debtAssignRequestDao.update(debtAssignRequest);
		if(count<=0){
			log.setResult(AssignLoanCancelStatus.FAIL);
			throw new ServiceException(ExceptionCode.DEBT_GGZZSQZT_CHECK.getIndex(),ExceptionCode.DEBT_GGZZSQZT_CHECK.getErrMsg());
		}
		//更新产品状态为取消
		int markStatus_result=0;
		if(product.getId().startsWith("dq-")){
			markStatus_result=productRegularDao.updateProductStatus(ProductStatus.CANCELED, debtAssignRequest.getDebtAssignProductId());
		}else if(product.getId().startsWith("pj-")){
			markStatus_result=productBillDao.updateProductStatus(ProductStatus.CANCELED, debtAssignRequest.getDebtAssignProductId());
		}
		if(markStatus_result==0){
			throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(),ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
		}
		if(markStatus_result>0){
			log.setResult(AssignLoanCancelStatus.SUCCESS);
			debtCheckRecord.setCheckState(CheckState.SUCCESS);
		}else{
			debtCheckRecord.setCheckState(CheckState.FAIL);
			log.setResult(AssignLoanCancelStatus.FAIL);
		}
		int recordcount=debtcheckRecordDao.add(debtCheckRecord);
		if(recordcount==0){
			throw new ServiceException(ExceptionCode.DEBT_LOG_CREATE_FAIL.getIndex(),ExceptionCode.DEBT_LOG_CREATE_FAIL.getErrMsg());
		}
		int logcount=debtassigncancleLogDao.add(log);
		if(logcount==0){
			throw new ServiceException(ExceptionCode.DEBT_LOG_CREATE_FAIL.getIndex(),ExceptionCode.DEBT_LOG_CREATE_FAIL.getErrMsg());
		}
		return true;
	}
}

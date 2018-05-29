package com.gomemyc.invest.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AssignService;
import com.gomemyc.agent.AccountAgent;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.invest.bridge.DebtAssignBridge;
import com.gomemyc.invest.bridge.LoanRedisBridge;
import com.gomemyc.invest.constant.NumberConstant;
import com.gomemyc.invest.constant.TimeConstant;
import com.gomemyc.invest.dao.DebtAssignRequestDao;
import com.gomemyc.invest.dao.DebtPlanDao;
import com.gomemyc.invest.dao.DebtStatisticsDao;
import com.gomemyc.invest.dao.DebtassigncancleLogDao;
import com.gomemyc.invest.dao.LoanDao;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dao.ProductRulesDao;
import com.gomemyc.invest.dto.DebtPlanDTO;
import com.gomemyc.invest.dto.DebtassignCancelLogDTO;
import com.gomemyc.invest.dto.DebtassignProductDTO;
import com.gomemyc.invest.dto.DebtassignRequestDTO;
import com.gomemyc.invest.dto.ProductRulesDTO;
import com.gomemyc.invest.entity.DebtPlan;
import com.gomemyc.invest.entity.DebtStatistics;
import com.gomemyc.invest.entity.DebtassignRequest;
import com.gomemyc.invest.entity.DebtassigncancleLog;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.entity.Product;
import com.gomemyc.invest.entity.ProductRules;
import com.gomemyc.invest.enums.AssignLoanCancelStatus;
import com.gomemyc.invest.enums.DebtAssignCancelType;
import com.gomemyc.invest.enums.DebtAssignPricingMethod;
import com.gomemyc.invest.enums.DebtAssignStatus;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.invest.service.DebtAssignRequestService;
import com.gomemyc.invest.service.RuleService;
import com.gomemyc.invest.utils.DTOUtils;
import com.gomemyc.invest.utils.DateUtils;
import com.gomemyc.invest.utils.FormulaUtil;
import com.gomemyc.invest.utils.JsonHelper;
import com.gomemyc.model.enums.Realm;
import com.gomemyc.model.misc.RealmEntity;
import com.gomemyc.model.user.User;
import com.gomemyc.sms.SMSType;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.InvestRepaymentService;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.user.api.UserService;
import com.google.common.collect.Lists;
/**
 * 债转申请
 * @author zhangWei
 *
 */
@Service
public class DebtAssignRequestServiceImpl implements DebtAssignRequestService{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DebtAssignRequestDao debtAssignRequestDao;
	@Autowired
	private DebtassigncancleLogDao debtassigncancleLogDao;
	@Autowired
	private DebtPlanDao debtPlanDao;
	@Autowired
	private LoanDao loanDao;
	@Autowired
	private ProductRegularDao productRegularDao;
	@Autowired
	private ProductBillDao productBillDao;
	@Autowired
	private DebtAssignBridge debtAssignBridge;
	@Reference
	private InvestService investService;
	@Reference
	private InvestRepaymentService investRepaymentService;
	@Reference
	private AccountAgent accountAgent;
	@Reference
	private CouponService couponService;
	@Reference
	private AssignService assignService;
	@Reference
	private UserService userService;
	@Autowired
	private DebtStatisticsDao debtStatisticsDao;
	@Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;
	@Autowired
	private RuleService ruleService;
	@Autowired
    private LoanRedisBridge loanRedisBridge;
	@Autowired
	private ProductRulesDao productRulesDao;
	@Override
	public Boolean applyDebtAssign(String userId, String investId, BigDecimal debtAmount, 
			DebtAssignPricingMethod pricingMethod, BigDecimal discountAmount, Boolean raiseType,
			Integer validDate) throws ServiceException {
		
	
//		//userId不能为空
		if(StringUtils.isEmpty(userId)){
			throw new ServiceException(ExceptionCode.DEBT_USER_REQUIRED.getIndex(),ExceptionCode.DEBT_USER_REQUIRED.getErrMsg());
		}
		
		//investId 不能为空
		if(StringUtils.isEmpty(investId)){
			throw new ServiceException(ExceptionCode.DEBT_TZ_REQUIRED.getIndex(),ExceptionCode.DEBT_USER_REQUIRED.getErrMsg());
		}
		//定价方式不能为空
		if(pricingMethod==null){ 
			throw new ServiceException(ExceptionCode.DEBT_DJFS_REQUIRED.getIndex(),ExceptionCode.DEBT_DJFS_REQUIRED.getErrMsg());
		}
		
		if(null==debtAmount){
			throw new ServiceException(ExceptionCode.DEBT_ZRJ_REQUIRED.getIndex(),ExceptionCode.DEBT_ZRJ_REQUIRED.getErrMsg());
		}
		if(null==discountAmount){
			throw new ServiceException(ExceptionCode.DEBT_INVEST_CHECK.getIndex(),ExceptionCode.DEBT_INVEST_CHECK.getErrMsg());
		}
		if(null==raiseType){
			throw new ServiceException(ExceptionCode.DEBT_MJBZCLFS_REQUIRED.getIndex(),ExceptionCode.DEBT_MJBZCLFS_REQUIRED.getErrMsg());
		}
		if(null==validDate){
			throw new ServiceException(ExceptionCode.DEBT_ZRYXQ_CHECK.getIndex(),ExceptionCode.DEBT_ZRYXQ_CHECK.getErrMsg());
		}
		//手机验证码校验-----start
		
		//---------------end
		
		//------------------------------校验产品投资start
		//判断投资信息 根据投资ID查询
		InvestDTO invest=null;
		try {
			invest=investService.findById(investId);
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询投资记录系统异常 investId={} ",investId);
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg(),e);
		}
		if(invest==null){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询投资记录返回null investId={} ",investId);
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg());
		}
		//操作人必须是投资人
		if(!invest.getUserId().equalsIgnoreCase(userId)){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 投资人和债转申请人不一致  investUserId={} , operUserId={}",invest.getUserId(),userId);
			throw new ServiceException(ExceptionCode.DEBT_TZRSHR_CHECK.getIndex(),ExceptionCode.DEBT_TZRSHR_CHECK.getErrMsg());
		}
		//投资状态必须是"已结算"
		if(!invest.getStatus().equals(InvestStatus.SETTLED)){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 投资状态必须是'已结算'  invest.status={}",invest.getStatus().getKey());
			throw new ServiceException(ExceptionCode.DEBT_TZZT_CHECK.getIndex(),ExceptionCode.DEBT_TZZT_CHECK.getErrMsg());
		}
		//只支持"一次性还本付息"的投资
		if(!invest.getRepaymentMethod().equals(RepaymentMethod.BulletRepayment)){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 只支持'一次性还本付息'的投资  invest.repaymentMethod={}",invest.getRepaymentMethod().getKey());
			throw new ServiceException(ExceptionCode.DEBT_HKFS_CHECK.getIndex(),ExceptionCode.DEBT_HKFS_CHECK.getErrMsg());
		}
		User user=null;
		try {
			user=userService.findByUserId(userId);
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询用户系统异常  userId={}",userId);
			throw new ServiceException(ExceptionCode.DEBT_USER_SOURCE.getIndex(),ExceptionCode.DEBT_USER_SOURCE.getErrMsg(),e);
		}
		
		if(user==null){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询用户返回为null  userId={}",userId);
			throw new ServiceException(ExceptionCode.DEBT_USER_SOURCE.getIndex(),ExceptionCode.DEBT_USER_SOURCE.getErrMsg());
		}
		/**
		 * 查询投资记录后，由投资记录里的产品编号查询得到invest.getProductId()
		 */
		Object productObject=null;
		try {
			if(invest.getProductId().startsWith("dq-")){
				productObject=productRegularDao.findById(invest.getProductId());
			}else if (invest.getProductId().startsWith("pj-")){
				productObject=productBillDao.findById(invest.getProductId());
			}
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询投资产品系统异常  productId={}",invest.getProductId());
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg(),e);
		}
		if(productObject==null){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询投资产品返回null  productId={}",invest.getProductId());
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg());
		}
		Product product=(Product)productObject;
		DebtPlan debtPlan = null;
		try {
			debtPlan = debtPlanDao.findById(product.getDebtPlanId());
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询债转方案系统异常  debtplanId={}",product.getDebtPlanId());
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg(),e);
		}
		if(null==debtPlan){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询债转方案返回null  debtplanId={}",product.getDebtPlanId());
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg());
		}
		if (validDate < debtPlan.getMinCollectTimeLimit() || validDate > debtPlan.getMaxCollectDeadline()) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 转让有效期不符合债转方案要求  validDate={},debtPlan.minCollectTimeLimit={},debtPlan.maxCollectDeadline={}",validDate,debtPlan.getMinCollectTimeLimit(),debtPlan.getMaxCollectDeadline());
			throw new ServiceException(ExceptionCode.DEBT_ZRYXQ_CHECK.getIndex(),ExceptionCode.DEBT_ZRYXQ_CHECK.getErrMsg());
		}
		if(!debtPlan.getPreferentialDebt()&&StringUtils.isNotBlank(invest.getCouponPlacememtId())){//方案中转让优惠债权标识为禁止使用转让
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 禁止使用优惠劵的投资转让  debtPlan.getPreferentialDebt={},invest.getCouponPlacememtId={}",debtPlan.getPreferentialDebt(),invest.getCouponPlacememtId());
			throw new ServiceException(ExceptionCode.DEBT_PLAN_PREFERENTIAL_REQUIRED.getIndex(),ExceptionCode.DEBT_PLAN_PREFERENTIAL_REQUIRED.getErrMsg());
		}
		//校验是否已经存在债转
		List<DebtassignRequest> debtList =null;
		try {
			debtList =debtAssignRequestDao.listByInvestIdsAndUserIdAndStatusSection(userId, investId, DebtAssignStatus.CLOSED_ACCOUNT,DebtAssignStatus.OPEN);
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询债转列表系统异常");
    		throw new ServiceException(ExceptionCode.DEBT_ZRZT_CHECK.getIndex(),ExceptionCode.DEBT_ZRZT_CHECK.getErrMsg(),e);
		}
    	if(!debtList.isEmpty()){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 已存在债转申请，不能重复申请  size={}",debtList.size());
    		throw new ServiceException(ExceptionCode.DEBT_ZRZT_CHECK.getIndex(),ExceptionCode.DEBT_ZRZT_CHECK.getErrMsg());
    	}
    	//回款计划的应还利息
    	Loan loan=null;
    	try {
    		loan=loanDao.findById(product.getLoanId());//产品中的标的ID查询而来
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询标的系统异常  loanId={}",product.getLoanId());
    		throw new ServiceException(ExceptionCode.DEBT_LOAN_SOURCE.getIndex(),ExceptionCode.DEBT_LOAN_SOURCE.getErrMsg(),e);
		}
    	if(loan==null){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询标的返回null  loanId={}",product.getLoanId());
    		throw new ServiceException(ExceptionCode.DEBT_LOAN_SOURCE.getIndex(),ExceptionCode.DEBT_LOAN_SOURCE.getErrMsg());
    	}
    	//查询未到期的还款计划  校验是否有可转让的金额-----start---------------------------
    	InvestRepaymentDTO investRepayment =null;
    	try {
    		investRepayment = investRepaymentService.findByInvestId(investId);//通过投资ID查询未到期的投资记录
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 通过投资ID查询还款计划系统异常  investId={}",investId);
    		throw new ServiceException(ExceptionCode.DEBT_HKJH_SOURCE.getIndex(),ExceptionCode.DEBT_HKJH_SOURCE.getErrMsg(),e);
		}
		if(investRepayment==null){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 通过投资ID查询还款计划返回null  investId={}",investId);
    		throw new ServiceException(ExceptionCode.DEBT_HKJH_SOURCE.getIndex(),ExceptionCode.DEBT_HKJH_SOURCE.getErrMsg());
		}
		BigDecimal unpayedPrincipal = BigDecimal.ZERO;
		BigDecimal amountInvest=new BigDecimal(0); //转让人应还利息
		switch (investRepayment.getStatus()) {
		case UNDUE:
			unpayedPrincipal = unpayedPrincipal.add(investRepayment.getPrincipalAmount());
			amountInvest=investRepayment.getInterestAmount();//转让人应还利息=还款计划中的应还基础利息
			break;
		case REPAYED:
			//有提前还款的暂时不让转让
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 还款计划状态不能申请债转  investRepayment.status={}",investRepayment.getStatus().getKey());
			throw new ServiceException(ExceptionCode.DEBT_YQWYHK_CHECK.getIndex(),ExceptionCode.DEBT_YQWYHK_CHECK.getErrMsg());
		default:
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 还款计划状态不能申请债转  investRepayment.status={}",investRepayment.getStatus().getKey());
			throw new ServiceException(ExceptionCode.DEBT_YQWYHK_CHECK.getIndex(),ExceptionCode.DEBT_YQWYHK_CHECK.getErrMsg());
		}
		//查看是否有可转让的金额
		if (unpayedPrincipal.compareTo(BigDecimal.valueOf(0))==0) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign  还款计划中没有查询到可转让的金额  unpayedPrincipal={}",unpayedPrincipal);
			throw new ServiceException(ExceptionCode.DEBT_YQWYHK_CHECK.getIndex(),ExceptionCode.DEBT_YQWYHK_CHECK.getErrMsg());
		}
		//------------------------end--------------------------------------
		//-----------------------------------end
		//判断转让金额是否超过最大可转金额
		BigDecimal debtMaxAmount =this.queryDebtMaxAmount(product, investId, validDate);
		if(debtAmount.compareTo(debtMaxAmount)==1){//小于最大可转让金额
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 转让金额超过最大可转金额  转让金额：debtAmount={},最大可转金额：debtMaxAmount={}",debtAmount,debtMaxAmount);
			throw new ServiceException(ExceptionCode.DEBT_ZRJE_CHECK.getIndex(),ExceptionCode.DEBT_ZRJE_CHECK.getErrMsg());
		}
		Date nowDate = new Date();
		//查看产品是否已经计息
		Date qixiDate = product.getValueTime();// 起息日
		//起息日到申请日的天数
		int qixitonowDays=DateUtils.countDays(qixiDate, nowDate);
		//校验是否第二次债转
		if(product.isDebted()){
			if(qixitonowDays<debtPlan.getSecondDebtDate()){//起息日到申请日不能小于第二次债转要求天数
				logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 起息日到申请日不能小于第二次债转要求天数  获息天数：qixitonowDays={},二次债转要求天数：secondDebtDate={}",qixitonowDays,debtPlan.getSecondDebtDate());
				throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg());
			}
		}else{//第一次债转
			if(qixitonowDays<debtPlan.getFirstDebtDate()){
				logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 起息日到申请日不能小于第二次债转要求天数  获息天数：qixitonowDays={},二次债转要求天数：firstDebtDate={}",qixitonowDays,debtPlan.getFirstDebtDate());
				throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg());
			}
		}
		
		
    	//---------------------start----------------------------------
    	//判断此用户撤销次数是否超过债转方案的最大值
    	int debtCancelCount=debtassigncancleLogDao.countByOperatorType(product.getId(), investId, userId, DebtAssignCancelType.SELLER_CANCLE, AssignLoanCancelStatus.SUCCESS);
    	if(debtCancelCount>=debtPlan.getMaxCancelCount()){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 用户撤销次数超过债转方案的最大值  已转入次数：debtCancelCount={},方案要求最大次数：maxCancelCount={}",debtCancelCount,debtPlan.getMaxCancelCount());
    		throw new ServiceException(ExceptionCode.DEBT_CXCS_MAX_CHECK.getIndex(),ExceptionCode.DEBT_CXCS_MAX_CHECK.getErrMsg());	
    	}
    	//---------------------end-----------------------------------
    	
    	//---------------------start----------------------------------
		//债转申请截止日期的起息时间
    	Date requestEndDate = DateUtils.offsetDays(nowDate, validDate);
    	//转让人获利天数=转让有效期时间-起息日
    	int holdingDays = DateUtils.countDays(qixiDate, requestEndDate);
    	//获取产品期限
    	int totalDays = DateUtils.getTotalDays(product.getYears(),product.getMonths(),product.getDays());//产品期限换算
    	//剩余期限 = 总天数-(起息日 到 当前时间 总天数)
		int syqx = totalDays - holdingDays;
		if(syqx<debtPlan.getOverplusDeadline()){
			if(syqx<=0){
				logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 剩余期限必须大于零      syqx={}",syqx);
	    		throw new ServiceException(ExceptionCode.DEBT_ZRSYQX_CHECK.getIndex(),ExceptionCode.DEBT_ZRSYQX_CHECK.getErrMsg());
	    	}
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 剩余期限必须大于债转方案要求的最小剩余期限      剩余期限：syqx={},债转方案要求最小剩余期限：debtPlan.overplusDeadline={}",syqx,debtPlan.getOverplusDeadline());
			throw new ServiceException(ExceptionCode.DEBT_SYQX_MIN_CHECK.getIndex(),ExceptionCode.DEBT_SYQX_MIN_CHECK.getErrMsg());	
		}
		//折价/溢价后的转让金额
		BigDecimal zydebtAmount  = debtAmount;
		if(pricingMethod.equals(DebtAssignPricingMethod.DISCOUNT)){
			zydebtAmount = debtAmount.subtract(discountAmount);
		}
		if(pricingMethod.equals(DebtAssignPricingMethod.PREMIUM)){
			zydebtAmount = debtAmount.add(discountAmount);
		}
		//债权转让手续费率
		int transferRate = debtPlan.getTransferRate();
		//手续费公式=转让金额*手续费率/10000
		BigDecimal counterFee = zydebtAmount.divide(BigDecimal.valueOf(10000))//转让金额除10000
				.multiply(BigDecimal.valueOf(transferRate), new MathContext(16, NumberConstant.ROUNDING_MODE))//乘手续费率（整数）
				.setScale(2, RoundingMode.DOWN);
		//转让成功预计回款 折价溢价后转让金额-手续费
		BigDecimal expectedReturnAmount = zydebtAmount.subtract(counterFee);
		
		//---------start------------------------------
    	//产品年利率
    	int Rate=product.getPlusRate()+product.getRate();
    	BigDecimal dayRate = FormulaUtil.dayRate(Rate);
		//principal本金 = 输入金额/(1+(日利率*转让截止持有天数))
		BigDecimal principal = debtAmount.divide(BigDecimal.valueOf(1)
				.add(dayRate.multiply(BigDecimal.valueOf(holdingDays))),16, NumberConstant.ROUNDING_MODE).setScale(16, RoundingMode.DOWN);
		//转让本金的剩余期限的利息=本金*（剩余期限*日利率）
		BigDecimal sylx = principal.multiply(BigDecimal.valueOf(syqx)).multiply(dayRate).setScale(16, RoundingMode.DOWN);
		//---------------------------end-----------------------------------
		
		//转让本金的剩余期限的剩余利息 =转让金额-转让价格+债权到期剩余未返利息
		BigDecimal debtExpectedAmount=sylx;
		if(pricingMethod.equals(DebtAssignPricingMethod.DISCOUNT)){//折价时加上折价金额
			debtExpectedAmount = debtExpectedAmount.add(discountAmount);
		}
		if(pricingMethod.equals(DebtAssignPricingMethod.PREMIUM)){//溢价时减去溢价金额
			debtExpectedAmount = debtExpectedAmount.subtract(discountAmount);
		}
		//转让预期年化率=转让成功预计回款 * 365/债权标持有期限*转让价格/10000
		BigDecimal debtDealRate_bigdecimal=debtExpectedAmount.multiply(BigDecimal.valueOf(TimeConstant.DAYS_PER_YEAR))
				                             .divide((BigDecimal.valueOf(syqx).multiply(zydebtAmount)), new MathContext(16, RoundingMode.DOWN));
		//转让本金的剩余期限年化利率
		int debtExpectedRate = debtDealRate_bigdecimal.multiply(BigDecimal.valueOf(10000)).intValue();
		//判断折溢价之后的总年化利率 是否在债转方案的范围之内
		if(debtExpectedRate<debtPlan.getMinExpectedRate()||debtExpectedRate>debtPlan.getMaxExpectedRate()){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 折价溢价后的年化利率必须符合债转方案要求范围      debtExpectedRate={},方案要求最小年化率:{},方案要求最大年化率：{}",debtExpectedRate,debtPlan.getMinExpectedRate(),debtPlan.getMaxExpectedRate());
			throw new ServiceException(ExceptionCode.DEBT_ZRYQ_CHECK.getIndex(),ExceptionCode.DEBT_ZRYQ_CHECK.getErrMsg());	
		}
//			if(debtExpectedRate>debtPlan.getMaxExpectedRate()){
//				logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 剩余期限必须大于零      syqx={}",syqx);
//				throw new ServiceException(ExceptionCode.DEBT_ZRYQ_MAX_CHECK.getIndex(),ExceptionCode.DEBT_ZRYQ_MAX_CHECK.getErrMsg());	
//			}
		
		//计算募集终止时间
		Date endDate = DateUtils.weeHours(requestEndDate, 1);
		//---------计算剩余期限start---------------------
		Map<String,Integer> ymdmap=DateUtils.getYearMonthsDays(syqx);
		//转让时转让人转让金额所产生的利率（折溢价会变化）
		int purchasedRate=0;
		BigDecimal zsylx=zydebtAmount.subtract(unpayedPrincipal);
		purchasedRate=zsylx.divide(unpayedPrincipal.multiply(BigDecimal.valueOf(holdingDays)),16, RoundingMode.DOWN).multiply(BigDecimal.valueOf(TimeConstant.DAYS_PER_YEAR)).multiply(BigDecimal.valueOf(100000000)).intValue();
		
		DebtassignRequest debtAssignRequest = getDebtAssignRequest(debtAmount, pricingMethod, discountAmount,
				raiseType, validDate, invest, product, debtPlan, nowDate, loan, debtMaxAmount, zydebtAmount,
				counterFee, expectedReturnAmount, endDate, amountInvest, ymdmap,unpayedPrincipal,sylx,principal,debtExpectedRate,purchasedRate);
		//--------------------------------end-----------------------
		//转让人应收利息 时间截止到募集截止日  ------------------------start-----------------------
		BigDecimal yingShouamountInterest=new BigDecimal(0);
		switch (debtAssignRequest.getMethod()) {//还款方式
  		case BulletRepayment://一次性还本付息
  			yingShouamountInterest=principal.multiply(BigDecimal.valueOf(holdingDays)).multiply(dayRate).setScale(2, RoundingMode.DOWN);
  			break; 
  		default:
  			break;
		}
		debtAssignRequest.setOriginalDubeInterestAmount(yingShouamountInterest);
		//------------------------------end------------------------
		debtAssignRequest.setUserInfo(user.getMobile());
		debtAssignRequest.setUserId(userId);
		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询产品规则开始      条件productid={}",invest.getProductId());
		List<ProductRules> list=null;
		try {
			list= productRulesDao.listByProductId(invest.getProductId());
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 产品规则条数异常     条件productid={},e={}",invest.getProductId(),e);
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg(),e);	
		}
		
		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 产品规则条数      size={}",list.size());
		if(list==null||list.size()==0){
			throw new ServiceException(30080,"没有查到产品规则productID");	
		}
		try {
			debtAssignBridge.saveDebtProdect(productObject, debtPlan, debtAssignRequest, invest,list);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 系统程序异常  ");
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg(),e);	
		}
		// 清除缓存列表
        logger.info("clear product cache and list, the productId = {} ", product.getId());
        loanRedisBridge.clearProductListCache();
		if(debtAssignRequest.getStatus().equals(DebtAssignStatus.OPEN)){
			try {
				  logger.info("债转申请自动审核通过发送短信, debtId = {}, debtAssignProductId = {}", debtAssignRequest.getId(), debtAssignRequest.getDebtAssignProductId());
	              Map<String, Object> smsMap = new HashMap<String, Object>();
	              smsMap.put("templateId", SMSType.NOTIFICATION_CREDIT_EXAMINE_SUCCESS_REMIND.getTemplateId());
	              smsMap.put("mobiles", user.getMobile());
	              List<String> params = new ArrayList<String>();
	              params.add(DateUtils.format(new Date(), DateUtils.dateTimeFormatter));
	              params.add(debtAssignRequest.getDebtAmount().toString());
	              params.add(null == product ? "" : product.getTitle());
	              params.add("0");
	              params.add("0");
	              smsMap.put("params", params);
	              Message message = new Message(MQTopic.SMS_SEND.getValue(),  JsonHelper.getJson(smsMap).getBytes());
	              SendResult sendResult = mqProducer.send(message);
	              logger.info("债转申请自动审核通过发送短信    sendResult={}",sendResult);
			} catch (Exception e) {
				 logger.info("债转申请自动审核通过发送短信异常, debtId = {}, debtAssignProductId = {}", debtAssignRequest.getId(), debtAssignRequest.getDebtAssignProductId());
			}
		}
		return true;
	}




	@Override
	public Boolean cancelDebtAssign(String userId, String debtAssignProductId,String operatorId,String sysSign) throws ServiceException {
		//userId不能为空
		if(StringUtils.isEmpty(userId)&&StringUtils.isEmpty(operatorId)){
			throw new ServiceException(ExceptionCode.DEBT_USER_REQUIRED.getIndex(),ExceptionCode.DEBT_USER_REQUIRED.getErrMsg());
		}
		//债转产品ID不能为空
		if(StringUtils.isEmpty(debtAssignProductId)){
			throw new ServiceException(ExceptionCode.DEBT_ZZCP_REQUIRED.getIndex(),ExceptionCode.DEBT_ZZCP_REQUIRED.getErrMsg());
		}
		//查询债转申请
		DebtassignRequest  debtAssignRequest = debtAssignRequestDao.getByDebtassignProductId(debtAssignProductId);
		if(debtAssignRequest==null){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 根据债转产品ID查询债转申请返回为null  debtAssignProductId={}",debtAssignProductId);
			throw new ServiceException(ExceptionCode.DEBT_ZZCP_SOURCE.getIndex(),ExceptionCode.DEBT_ZZCP_SOURCE.getErrMsg());
		}
		if(sysSign==null||!sysSign.equals("ht")){//不是后台撤销，校验申请人和操作人
			//前台撤销：只有申请人才能撤销
			if(!userId.equals(operatorId)){
				logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 前台撤销时撤销人和投资人不一致 userId={}，operatorId={}",userId,operatorId);
				throw new ServiceException(ExceptionCode.DEBT_CXRZQR_CHECK.getIndex(),ExceptionCode.DEBT_CXRZQR_CHECK.getErrMsg());
			}
		}
		List<InvestDTO> investDTOs=null;
		try {
			investDTOs=investService.findListByProductIdAndStatus(debtAssignRequest.getDebtAssignProductId(), InvestStatus.LOCAL_FROZEN_SUCCESS,InvestStatus.BJ_FROZEN_SUCCESS);
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 查询投资列表异常");
			throw new ServiceException(40018,"查询投资列表异常");
		}
		if(investDTOs!=null&&investDTOs.size()>0){
			for (InvestDTO investDTO : investDTOs) {
				if(investDTO.getStatus().equals(InvestStatus.LOCAL_FROZEN_SUCCESS)){
					logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 债转撤销时有投资处于本地冻结中状态，不能撤销 investId={},status={}",investDTO.getId(),investDTO.getStatus());
					throw new ServiceException(40019,"债转撤销时有投资处于未完结状态");
				}
			}
		}
		User user=userService.findByUserId(userId);
		if(user==null){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 查询用户返回为null  userId={}",userId);
			throw new ServiceException(ExceptionCode.DEBT_USER_SOURCE.getIndex(),ExceptionCode.DEBT_USER_SOURCE.getErrMsg());
		}
		if(StringUtils.isEmpty(debtAssignRequest.getInvestId())){
			throw new ServiceException(ExceptionCode.DEBT_TZ_SOURCE.getIndex(),ExceptionCode.DEBT_TZ_SOURCE.getErrMsg());
		}
		/**
		 * 查询投资记录后，由投资记录里的产品编号查询得到debtAssignProductId
		 */
		Object productObject=null;
		if(debtAssignProductId.startsWith("dq-")){
			productObject=productRegularDao.findById(debtAssignProductId);
		}else if (debtAssignProductId.startsWith("pj-")){
			productObject=productBillDao.findById(debtAssignProductId);
		}
		
		if(productObject==null){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 查询投资产品返回null  productId={}",debtAssignProductId);
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg());
		}
		Product product=(Product)productObject;
		if(product.getStatus()==ProductStatus.CLEARED){//已还清的产品是不能取消的
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 已还清的产品是不能取消的  product.status={}",product.getStatus());
			throw new ServiceException(ExceptionCode.DEBT_ZQZT_CHECK.getIndex(),ExceptionCode.DEBT_ZQZT_CHECK.getErrMsg());
		}
		DebtPlan debtPlan = debtPlanDao.findById(product.getDebtPlanId());
		if(debtPlan == null){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 查询债转方案返回null  debtplanId={}",product.getDebtPlanId());
			throw new ServiceException(ExceptionCode.DEBT_ZZFA_SOURCE.getIndex(),ExceptionCode.DEBT_ZZFA_SOURCE.getErrMsg());
		}
		//开标和到期未满标可以撤销
		if(!product.getStatus().equals(ProductStatus.OPENED)  &&!product.getStatus().equals(ProductStatus.FINISHED)  && !product.getStatus().equals(ProductStatus.FAILED)){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 开标和到期未满标可以撤销 product.status",product.getStatus());
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg());
		}
		//判断撤销次数
		//判断此用户撤销次数是否超过债转方案的最大值
    	int debtCancelCount=debtassigncancleLogDao.countByOperatorType(product.getId(), debtAssignRequest.getInvestId(), userId, DebtAssignCancelType.SELLER_CANCLE, AssignLoanCancelStatus.SUCCESS);
    	if(debtCancelCount>=debtPlan.getMaxCancelCount()){//债转方案次数限制
    		logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 用户撤销次数超过债转方案的最大值  已转入次数：debtCancelCount={},方案要求最大次数：maxCancelCount={}",debtCancelCount,debtPlan.getMaxCancelCount());
    		throw new ServiceException(ExceptionCode.DEBT_CXCS_MAX_CHECK.getIndex(),ExceptionCode.DEBT_CXCS_MAX_CHECK.getErrMsg());	
    	}
    	try {
			debtAssignBridge.saveCancel(debtAssignRequest, user, product, sysSign);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 系统程序异常   ");
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg(),e);	
		}
		//撤销MQ
		try {
			 Map<String, Object> mqMap = new HashMap<String, Object>();
			// 债转产品id
	        mqMap.put("debtProductId", debtAssignProductId);
	        // 投资记录ID
	        mqMap.put("investId", debtAssignRequest.getInvestId());
			Message message = new Message("myc_debtassign","debtassignCancel",JsonHelper.getJson(mqMap).getBytes());
			SendResult sendResult = mqProducer.send(message);
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 债转撤销MQ调用返回     sendResult={}",sendResult); 
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 调用债转撤销MQ异常 ");
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg(),e);
		}
		
		return true;
	}



	@Override
	public Page<DebtassignProductDTO> findPageByUserIdAndStatus(String userId, DebtAssignStatus status, Integer pageNumber,
			Integer pageSize) throws ServiceException {
		//userId不能为空
		if(StringUtils.isEmpty(userId)){
			throw new ServiceException(ExceptionCode.DEBT_USER_REQUIRED.getIndex(),ExceptionCode.DEBT_USER_REQUIRED.getErrMsg());
		}
		if(pageNumber==null||pageNumber<0){
			throw new ServiceException(ExceptionCode.DEBT_FYCS_CHECK.getIndex(),ExceptionCode.DEBT_FYCS_CHECK.getErrMsg());
		}
		if(pageSize==null||pageSize<0){
			throw new ServiceException(ExceptionCode.DEBT_ZZCP_SOURCE.getIndex(),ExceptionCode.DEBT_ZZCP_SOURCE.getErrMsg());
		}
		int startRow=(pageNumber-1)*pageSize;//计算开始行数
		List<DebtassignRequest> list=debtAssignRequestDao.findPageByByUserIdAndStatus(userId, status, startRow, pageSize);
		for (DebtassignRequest debtassignRequest : list) {
			int debtCancelCount=debtassigncancleLogDao.countByOperatorType(debtassignRequest.getProductId(), debtassignRequest.getInvestId(), userId, DebtAssignCancelType.SELLER_CANCLE, AssignLoanCancelStatus.SUCCESS);
			debtassignRequest.setCancelCount(debtCancelCount);
		}
		Long count=debtAssignRequestDao.getCountByByUserIdAndStatus(userId, status).longValue();
		if(list.isEmpty()){
			return new Page<DebtassignProductDTO>(Lists.newArrayList(),pageNumber,pageSize,count);
        }
		List<DebtassignProductDTO> ListDTO=DTOUtils.toDebtProductListDTO(list);
		Page<DebtassignProductDTO> page=new Page<DebtassignProductDTO>(ListDTO,pageNumber,pageSize,count);
        return page;
	}

	@Override
	public List<DebtassignCancelLogDTO> findListByUserIdAndProductId(String userId,
			String productId) throws ServiceException {
		//userId不能为空
		if(StringUtils.isEmpty(userId)){
			throw new ServiceException(ExceptionCode.DEBT_USER_REQUIRED.getIndex(),ExceptionCode.DEBT_USER_REQUIRED.getErrMsg());
		}
		//产品ID不能为空
		if(StringUtils.isEmpty(productId)){
			throw new ServiceException(ExceptionCode.DEBT_CP_REQUIRED.getIndex(),ExceptionCode.DEBT_CP_REQUIRED.getErrMsg());
		}
		List<DebtassigncancleLog> list= debtassigncancleLogDao.findListByUserIdAndProductId(userId,productId);
		List<DebtassignCancelLogDTO> dtoList=new ArrayList<DebtassignCancelLogDTO>();
		for (DebtassigncancleLog log : list) {
			DebtassignCancelLogDTO dto=DTOUtils.toDTO(log);
			dtoList.add(dto);
		}
		return dtoList;
		
	}

	@Override
	public BigDecimal queryDebtMaxAmount(String productId,String investId, Integer validDate)
			throws ServiceException {
		//产品ID不能为空
		if(StringUtils.isEmpty(productId)){
			throw new ServiceException(ExceptionCode.DEBT_CP_REQUIRED.getIndex(),ExceptionCode.DEBT_CP_REQUIRED.getErrMsg());
		}
		if(validDate<0){
			throw new ServiceException(ExceptionCode.DEBT_ZRYXQ_CHECK.getIndex(),ExceptionCode.DEBT_ZRYXQ_CHECK.getErrMsg());
		}
		if(StringUtils.isEmpty(investId)){
			throw new ServiceException(ExceptionCode.DEBT_TZ_REQUIRED.getIndex(),ExceptionCode.DEBT_TZ_REQUIRED.getErrMsg());
		}
		//查询用户在该投资中的还款计划
		InvestRepaymentDTO investRepayment=investRepaymentService.findByInvestId(investId);
		if(investRepayment==null){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 通过投资ID查询还款计划返回null  investId={}",investId);
			throw new ServiceException(ExceptionCode.DEBT_HKJH_SOURCE.getIndex(),ExceptionCode.DEBT_HKJH_SOURCE.getErrMsg());
		}
		
		//查询投资记录
		InvestDTO invest=null;
		try {
			invest=investService.findById(investId);
		} catch (Exception e) {
			logger.info(" 查询投资记录系统异常 investId={} ",investId);
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg(),e);
		}
		if(invest==null){
			logger.info(" 查询投资记录返回null investId={} ",investId);
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg());
		}
		//查询产品详细
		/**
		 * 查询投资记录后，由投资记录里的产品编号查询得到debtAssignProductId
		 */
		Object productObject=null;
		if(productId.startsWith("dq-")){
			productObject=productRegularDao.findById(productId);
		}else if (productId.startsWith("pj-")){
			productObject=productBillDao.findById(productId);
		}
		
		if(productObject==null){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 查询投资产品返回null  productId={}",productId);
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg());
		}
		Product product=(Product)productObject;
		//获取总利息	=基础利息+标的加息利息
    	BigDecimal amountInterest = investRepayment.getInterestAmount().add(investRepayment.getInterestPlusAmount());
		//获取应还本金 
		BigDecimal amountPrincipal=investRepayment.getPrincipalAmount();
		BigDecimal tze=invest.getAmount();
		//重新计算利息    债转投资的回款表中存的本金，所以要换算出来利息
		amountInterest=amountInterest.add(amountPrincipal).subtract(tze);
		//获取起息日
    	Date valueDate = product.getValueTime();
    	//获取借款期限 产品中获取
    	//封装产品年月日Duration
//		Duration prioductDuration=new Duration(product.getYears(),product.getMonths(),product.getDays());
    	int totalDays =DateUtils.getTotalDays(product.getYears(),product.getMonths(),product.getDays());
    	//获取每日利息 （总利息/借款期限）
    	BigDecimal dayInterest = amountInterest.divide(new BigDecimal(totalDays),16, BigDecimal.ROUND_HALF_EVEN);
    	//计算转让人持有债权天数
    	Date nowDate = new Date();
    	Date requestEndDate = DateUtils.offsetDays(nowDate, validDate);
    	//获取持有期限
    	int holdingDays = DateUtils.countDays(valueDate, requestEndDate);
    	//获取持有天数的总利息
    	BigDecimal totalInterest = BigDecimal.valueOf(holdingDays<0?0:holdingDays).multiply(dayInterest);
    	//获取最大可转金额=持有天数总利息+本金
    	BigDecimal debtMaxAmount =  tze.add(totalInterest).setScale(2, BigDecimal.ROUND_DOWN);
		return debtMaxAmount;
	}

	@Override
	public DebtPlanDTO getDebtPlan(String userId, String investId, String productId) throws ServiceException {
		//产品ID不能为空
		if(StringUtils.isEmpty(productId)){
			throw new ServiceException(ExceptionCode.DEBT_CP_REQUIRED.getIndex(),ExceptionCode.DEBT_CP_REQUIRED.getErrMsg());
		}
		if(StringUtils.isEmpty(userId)){
			throw new ServiceException(ExceptionCode.DEBT_USER_REQUIRED.getIndex(),ExceptionCode.DEBT_USER_REQUIRED.getErrMsg());
		}
		if(StringUtils.isEmpty(investId)){
			throw new ServiceException(ExceptionCode.DEBT_TZ_REQUIRED.getIndex(),ExceptionCode.DEBT_TZ_REQUIRED.getErrMsg());
		}
    	List<DebtassignRequest> debtList =debtAssignRequestDao.listByInvestIdsAndUserIdAndStatusSection(userId, investId, DebtAssignStatus.WAIT_FIRST_CHECK,DebtAssignStatus.OPEN);
    	if(!debtList.isEmpty()){//判断投资是否有债转申请存在
    		throw new ServiceException(ExceptionCode.DEBT_ZRZT_CHECK.getIndex(),ExceptionCode.DEBT_ZRZT_CHECK.getErrMsg());
    	}
    	//判断投资产品是否存在   需要查询
    	Object productObject=null;
		if(productId.startsWith("dq-")){
			productObject=productRegularDao.findById(productId);
		}else if (productId.startsWith("pj-")){
			productObject=productBillDao.findById(productId);
		}
		if(productObject==null){
			logger.info("DebtAssignRequestServiceImpl.cancelDebtAssign 查询投资产品返回null  productId={}",productId);
			throw new ServiceException(ExceptionCode.DEBT_CP_SOURCE.getIndex(),ExceptionCode.DEBT_CP_SOURCE.getErrMsg());
		}
		Product product=(Product)productObject;
    	DebtPlan debtPlan=debtPlanDao.findById(product.getDebtPlanId());//产品里有债转方案
    	if(debtPlan==null){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 查询债转方案返回null  debtplanId={}",product.getDebtPlanId());
    		throw new ServiceException(ExceptionCode.DEBT_ZZFA_SOURCE.getIndex(),ExceptionCode.DEBT_ZZFA_SOURCE.getErrMsg());	
    	}
    	//判断此用户撤销次数是否超过债转方案的最大值
    	int debtCancelCount=debtassigncancleLogDao.countByOperatorType(productId, investId, userId, DebtAssignCancelType.SELLER_CANCLE, AssignLoanCancelStatus.SUCCESS);
    	if(debtCancelCount>=debtPlan.getMaxCancelCount()){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 用户撤销次数超过债转方案的最大值  已转入次数：debtCancelCount={},方案要求最大次数：maxCancelCount={}",debtCancelCount,debtPlan.getMaxCancelCount());
    		throw new ServiceException(ExceptionCode.DEBT_CXCS_MAX_CHECK.getIndex(),ExceptionCode.DEBT_CXCS_MAX_CHECK.getErrMsg());	
    	}
    	//获取可转金额，如果可转金额小于最低转让金额 则说明这个投资是购买尾标    需要查还款计划
    	BigDecimal debtMaxAmount = this.queryDebtMaxAmount(product, investId, debtPlan.getMinCollectTimeLimit());
    	if(debtMaxAmount==null||debtMaxAmount.compareTo(BigDecimal.ZERO)==0){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 可转金额不能为零 debtMaxAmount={}",debtMaxAmount);
    		throw new ServiceException(ExceptionCode.DEBT_BJ_CHECK.getIndex(),ExceptionCode.DEBT_BJ_CHECK.getErrMsg());	
    	}
    	
    	//最小可投金额(对于一对一全部转让的标，再次转让时涉及最小可投金额问题，需进行判断)
//    	if(StringUtils.isEmpty(productRegular.getRootProductId())){
//    		debtPlan.setDebtMinAmount(BigDecimal.valueOf(10000));//product.getLoanRequest().getInvestRule().getMinAmount()
//    	}else{
//    		ProductRegular rootProduct = new ProductRegular();//loanService.find(loan.getRootLoanId());
//    		debtPlan.setDebtMinAmount(rootProduct);//rootProduct.getLoanRequest().getInvestRule().getMinAmount()
//    	}
    	//转让截止日期  产品计息日+（产品期限-债转方案最少剩余期限）product.getLoanRequest().getValueDate()  product.getDuration().getTotalDays()
//    	Duration prioductDuration=new Duration(productRegular.getYears(),productRegular.getMonths(),productRegular.getDays());
    	int totalDays=DateUtils.getTotalDays(product.getYears(),product.getMonths(),product.getDays());
    	Date deadline = DateUtils.dayEnd(DateUtils.offsetDays(product.getValueTime(), totalDays-debtPlan.getOverplusDeadline()));
    	if(new Date().compareTo(deadline)>=0){
    		logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 债转申请日必须早于最少剩余期限要求的日期  当前时间nowdate={},起息日到最少剩余期限算出的日期deadline={}",new Date(),deadline);
    		throw new ServiceException(ExceptionCode.DEBT_SYQX_MIN_CHECK.getIndex(),ExceptionCode.DEBT_SYQX_MIN_CHECK.getErrMsg());	
    	}
    	//获取起息日
    	Date valueDate = product.getValueTime();
    	//获取持有期限
    	int holdingDays = DateUtils.countDays(valueDate, new Date());
    	
    	DebtPlanDTO dto=DTOUtils.toDTO(debtPlan);
    	//查询理财产品是否使用了加息券--------start-------------------------------------
    	InvestDTO invest=new InvestDTO();//根据investId查询
    	if(StringUtils.isEmpty(invest.getCouponPlacememtId())){
    		dto.setUsedInterestCoupon(false);
    	}else{
    		dto.setUsedInterestCoupon(true);
    	}
    	//---------------------------end----------------------------------------
    	dto.setNowDate(new Date());
    	dto.setDebtCount(debtCancelCount);
    	//可转金额金额
    	dto.setDebtMaxAmount(debtMaxAmount);
    	dto.setLoanRate(product.getRate()+product.getPlusRate());
    	logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 基础利率={}，加息利率={}，年化利率={}",product.getRate(),product.getPlusRate(),dto.getLoanRate());
    	dto.setHoldingDays(holdingDays);
    	dto.setTotalDays(totalDays);
    	return dto;
	}

	@Override
	public DebtassignRequestDTO getByDebtassignProductId(String debtAssignProductId) throws ServiceException {
		if(StringUtils.isEmpty(debtAssignProductId)){
			throw new ServiceException(ExceptionCode.DEBT_ZZCP_REQUIRED.getIndex(),ExceptionCode.DEBT_ZZCP_REQUIRED.getErrMsg());	
		}
		DebtassignRequest debt=debtAssignRequestDao.getByDebtassignProductId(debtAssignProductId);
		DebtassignRequestDTO dto=DTOUtils.toDTO(debt);
		return dto;
	}
	@Override
	public Boolean updateDebtStatus(String id, DebtAssignStatus status) throws ServiceException {
		if(StringUtils.isEmpty(id)){
			throw new ServiceException(ExceptionCode.DEBT_ID_REQUIRED.getIndex(),ExceptionCode.DEBT_ID_REQUIRED.getErrMsg());
		}
		if(status==null){
			throw new ServiceException(ExceptionCode.DEBT_STATUS_REQUIRED.getIndex(),ExceptionCode.DEBT_STATUS_REQUIRED.getErrMsg());
		}
		try {
			int count=debtAssignRequestDao.updateStatusById(id, status,new Date());
			if(count>0){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			throw new ServiceException(ExceptionCode.DEBT_UPDATE_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_FAIL.getErrMsg(),e);
		}
	}
	
	@Override
	public List<DebtassignRequestDTO> findByStatusAndEndTime(DebtAssignStatus status, Date time)
			throws ServiceException {
		if(status==null){
			throw new ServiceException(ExceptionCode.DEBT_STATUS_REQUIRED.getIndex(),ExceptionCode.DEBT_STATUS_REQUIRED.getErrMsg());
		}
		if(time==null){
			throw new ServiceException(ExceptionCode.DEBT_END_TIME_REQUIRED.getIndex(),ExceptionCode.DEBT_END_TIME_REQUIRED.getErrMsg());
		}
		String timeStr=DateUtils.format(time, "yyyy-MM-dd HH:mm:ss");
		return debtAssignRequestDao.findByStatusAndEndTime(status, timeStr);
	}
	
	/**
	 * 债转申请参数写入
	 * @param userId 申请人
	 * @param debtAmount 转让金额
	 * @param pricingMethod 定价方式
	 * @param discountAmount 折价金额
	 * @param returnFullAmount 募集不足处理方式(全部退回/不足部分退回)
	 * @param validDate 转让有效期
	 * @param invest 投资
	 * @param product 产品
	 * @param debtPlan 债转方案
	 * @param nowDate 当前时间
	 * @param loan 标的
	 * @param debtMaxAmount 最大可转金额
	 * @param zydebtAmount 折价/溢价后的转让金额
	 * @param counterFee 手续费:具体金额
	 * @param expectedReturnAmount 转让成功预计回款 折价溢价后转让金额-手续费
	 * @param endDate 募集终止时间
	 * @param amountInvest 回款计划的应还利息
	 * @param debtsyDuration 剩余期限
	 * @param unpayedPrincipal 原始投资本金
	 * @param surInterest 剩余基础利息
	 * @param surplusInterest 剩余加息利息
	 * @param principal 转让本金
	 * @param purchasedRate 转让时转让人转让金额所产生的利率（折溢价会变化）
	 * @return
	 */
	private DebtassignRequest getDebtAssignRequest(BigDecimal debtAmount,
			DebtAssignPricingMethod pricingMethod, BigDecimal discountAmount, Boolean returnFullAmount,
			Integer validDate, InvestDTO invest, Product product, DebtPlan debtPlan, Date nowDate, Loan loan,
			BigDecimal debtMaxAmount, BigDecimal zydebtAmount, BigDecimal counterFee, BigDecimal expectedReturnAmount,
			Date endDate, BigDecimal amountInvest, Map<String,Integer> ymdmap,BigDecimal unpayedPrincipal,BigDecimal sylx,BigDecimal principal,int debtExpectedRate,int purchasedRate){
		DebtassignRequest debtAssignRequest = new DebtassignRequest();
		debtAssignRequest.setInvestId(invest.getId());
		debtAssignRequest.setLoanId(loan.getId());
		debtAssignRequest.setDebtPlanId(debtPlan.getId());
		debtAssignRequest.setProductId(product.getId());
		debtAssignRequest.setProductTitle(product.getTitle());
		
		debtAssignRequest.setDebtExpectedRate(debtExpectedRate);//转让本金剩余期限利率
		debtAssignRequest.setId(UUIDGenerator.generate());
		//转让金额
		debtAssignRequest.setDebtAmount(debtAmount);
		//最大可转金额
		debtAssignRequest.setDebtMaxAmount(debtMaxAmount);
		//转让价格
		debtAssignRequest.setTransferPrice(zydebtAmount);
		//原产品年化收益
		debtAssignRequest.setOriginalRate(product.getRate()+product.getPlusRate());
		//定价方式
		debtAssignRequest.setPricingMethod(pricingMethod);
		//折价金额（折价金额为正数）
		debtAssignRequest.setDiscountAmount(discountAmount);
		//手续费
		debtAssignRequest.setCounterFee(counterFee);
		//转让成功预计回款
		debtAssignRequest.setExpectedReturnAmount(expectedReturnAmount);
		debtAssignRequest.setStatus(DebtAssignStatus.WAIT_FIRST_CHECK);
		debtAssignRequest.setReturnFullAmount(returnFullAmount);
		debtAssignRequest.setRequestStartDate(nowDate);
		debtAssignRequest.setValidDate(validDate);
		debtAssignRequest.setLastOperationTime(nowDate);
		debtAssignRequest.setCancelType(DebtAssignCancelType.NOT_CANCEL);
		debtAssignRequest.setCancelCount(0);
		debtAssignRequest.setProductDueDate(product.getSettleTime());//原标结标时间  
		debtAssignRequest.setMethod(product.getMethod());//还款方式 和债转标相同
		//获得原始本金 从投资记录中获得 invest.getAmount()
		debtAssignRequest.setOriginalAmount(invest.getAmount());//原始产品价格
		debtAssignRequest.setOriginalInvestAmount(unpayedPrincipal);
		debtAssignRequest.setRequestEndDate(endDate);
		//债转标剩余期限
		debtAssignRequest.setYears(ymdmap.get("y"));
		debtAssignRequest.setMonths(ymdmap.get("m"));
		debtAssignRequest.setDays(ymdmap.get("d"));
		debtAssignRequest.setOriginalTotalInterestAmount(amountInvest);//原始本金对应的总利息 可能包括加息券
		debtAssignRequest.setOriginalRemainTotalAmount(debtMaxAmount.subtract(debtAmount));//剩余金额(可转让金额-转让金额)
		debtAssignRequest.setOriginalRemainInterestAmount(sylx.setScale(16, RoundingMode.DOWN));// 剩余总利息
		debtAssignRequest.setDebtRealAmount(principal.setScale(16, RoundingMode.DOWN));//转让本金
		//剩余本金(原始本金-转让本金)
		debtAssignRequest.setOriginalRemainAmount(unpayedPrincipal.subtract(principal.setScale(16, RoundingMode.DOWN)));
		//债权预期到期收益 =转让金额-转让价格+债权到期剩余未返利息
		debtAssignRequest.setDebtExpectedAmount(debtAmount.subtract(zydebtAmount).add(sylx.setScale(16, RoundingMode.DOWN)));
		debtAssignRequest.setMaxCancelCount(debtPlan.getMaxCancelCount());
		debtAssignRequest.setOriginalDuration(DateUtils.getTotalDays(loan.getYears(),loan.getMonths(),loan.getDays()));
		debtAssignRequest.setAssignLoan(product.isDebted());
		//转让时折溢价会变化利率
		debtAssignRequest.setPurchasedRate(purchasedRate);
		debtAssignRequest.setProductKey(product.getTypeKey());
		return debtAssignRequest;
	}

	private BigDecimal queryDebtMaxAmount(Product product,String investId, Integer validDate)
			throws ServiceException {
		//查询用户在该投资中的还款计划
		InvestRepaymentDTO investRepayment=investRepaymentService.findByInvestId(investId);
		if(investRepayment==null){
			logger.info("DebtAssignRequestServiceImpl.applyDebtAssign 通过投资ID查询还款计划返回null  investId={}",investId);
			throw new ServiceException(ExceptionCode.DEBT_HKJH_SOURCE.getIndex(),ExceptionCode.DEBT_HKJH_SOURCE.getErrMsg());
		}
		//查询投资记录
		InvestDTO invest=null;
		try {
			invest=investService.findById(investId);
		} catch (Exception e) {
			logger.info(" 查询投资记录系统异常 investId={} ",investId);
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg(),e);
		}
		if(invest==null){
			logger.info(" 查询投资记录返回null investId={} ",investId);
			throw new ServiceException(ExceptionCode.DEBT_CPBZCZZ_CHECK.getIndex(),ExceptionCode.DEBT_CPBZCZZ_CHECK.getErrMsg());
		}
		//获取总利息	=基础利息+标的加息利息
    	BigDecimal amountInterest = investRepayment.getInterestAmount().add(investRepayment.getInterestPlusAmount());
		//获取应还本金 
		BigDecimal amountPrincipal=investRepayment.getPrincipalAmount();
		BigDecimal tze=invest.getAmount();
		//重新计算利息    债转投资的回款表中存的本金，所以要换算出来利息
		amountInterest=amountInterest.add(amountPrincipal).subtract(tze);
		//获取起息日
    	Date valueDate = product.getValueTime();
    	//获取借款期限 产品中获取
    	//封装产品年月日Duration
//		Duration prioductDuration=new Duration(product.getYears(),product.getMonths(),product.getDays());
    	int totalDays =DateUtils.getTotalDays(product.getYears(),product.getMonths(),product.getDays());
    	//获取每日利息 （总利息/借款期限）
    	BigDecimal dayInterest = amountInterest.divide(new BigDecimal(totalDays),16, BigDecimal.ROUND_HALF_EVEN);
    	//计算转让人持有债权天数
    	Date nowDate = new Date();
    	Date requestEndDate = DateUtils.offsetDays(nowDate, validDate);
    	//获取持有期限
    	int holdingDays = DateUtils.countDays(valueDate, requestEndDate);
    	//获取持有天数的总利息
    	BigDecimal totalInterest = BigDecimal.valueOf(holdingDays<0?0:holdingDays).multiply(dayInterest);
    	//获取最大可转金额=持有天数总利息+本金
    	BigDecimal debtMaxAmount =  tze.add(totalInterest).setScale(2, BigDecimal.ROUND_DOWN);
		return debtMaxAmount;
	}




	@Override
	public Boolean debtStatistics(Date date) throws ServiceException {
		if(date==null){
			logger.info("DebtAssignRequestServiceImpl.debtStatistics date不能传入null ");
			throw new ServiceException(ExceptionCode.DEBT_DATE_REQUIRED.getIndex(),ExceptionCode.DEBT_DATE_REQUIRED.getErrMsg());
		}
		//申请数
		List<Map<String, Object>> listsq=debtAssignRequestDao.statistiscDebtRequest(DebtAssignStatus.WAIT_FIRST_CHECK, DebtAssignStatus.OPEN, DateUtils.getStartTime(date),DateUtils.getEndTime(date));
		int sqzqs=0;//申请债权数
		int sqzqrs=0;//申请债权人数
		BigDecimal allamount=BigDecimal.ZERO;
		for (Map<String, Object> map : listsq) {
			sqzqs+=Integer.valueOf(map.get("zqshu").toString());
			sqzqrs++;
			allamount=allamount.add(BigDecimal.valueOf(Double.valueOf(map.get("amount").toString())));
		}
		//成功数
		List<Map<String, Object>> listcg=debtAssignRequestDao.statistiscDebtRequest(DebtAssignStatus.CLOSED_ACCOUNT, DebtAssignStatus.ALREADY_TRANSFERRED, DateUtils.getStartTime(date),DateUtils.getEndTime(date));
		int cgzqs=0;//成功债权数
		int cgzqrs=0;//成功债权人数
		for (Map<String, Object> map : listcg) {
			cgzqs+=Integer.valueOf(map.get("zqshu").toString());
			cgzqrs++;
		}
		//成功的投资总额需要去投资表算去
		BigDecimal cgallamount= debtAssignRequestDao.statistiscDebtSuccess(DebtAssignStatus.CLOSED_ACCOUNT, DebtAssignStatus.ALREADY_TRANSFERRED, DateUtils.getStartTime(date),DateUtils.getEndTime(date));
		if(cgallamount==null){
			cgallamount=BigDecimal.ZERO;
		}
		DebtStatistics sta=new DebtStatistics();
		sta.setId(UUIDGenerator.generate());
		sta.setRequestCount(sqzqs);
		sta.setRequestSum(allamount);
		sta.setRequestUserCount(sqzqrs);
		sta.setSuccessCount(cgzqs);
		sta.setSuccessSum(cgallamount);
		sta.setSuccessUserCount(cgzqrs);
		sta.setTime(date);
		debtStatisticsDao.save(sta);
		return true;
	}




	@Override
	public Boolean update(DebtassignRequestDTO dto) throws ServiceException {
		if(dto==null){
			logger.info("DebtAssignRequestServiceImpl.update 传入债转申请为null");
			throw new ServiceException(ExceptionCode.DEBT_ZZCP_SOURCE.getIndex(),ExceptionCode.DEBT_ZZCP_SOURCE.getErrMsg());
		}
		int count=debtAssignRequestDao.update(DTOUtils.toOTD(dto));
		if(count==0){
			return false;
		}else{
			return true;
		}
	}




	@Override
	public List<DebtassignRequestDTO> listByInvestIdsAndUserIdAndStatus(String userId, List<String> investIds,
			DebtAssignStatus... statusList) throws ServiceException {
		List<DebtassignRequest> list=debtAssignRequestDao.listByInvestIdsAndUserIdAndStatus(userId, investIds, statusList);
		return DTOUtils.toDebtListDTO(list);
	}




	@Override
	public Map countConcelLogByUserIdAndRootLoanIds(Map<String, String> loan_invest_map, String operatorId,
			DebtAssignCancelType assignLoanCancelType, AssignLoanCancelStatus result, Date beginDate, Date endDate) {
		List<String> tmp_rootLoanIds = new ArrayList<>();
		List<String> tmp_investIds = new ArrayList<>();
		for(Map.Entry<String, String> entry : loan_invest_map.entrySet()){
			tmp_investIds.add(entry.getKey());
			tmp_rootLoanIds.add(entry.getValue());
		}
		return debtassigncancleLogDao.countConcelLogByUserIdAndRootLoanIds(tmp_investIds, tmp_rootLoanIds, operatorId, assignLoanCancelType, result, beginDate, endDate);
	}




	@Override
	public Integer countByUserIdAndStatus(@Param("userId")String userId, @Param("debtAssignStatus")DebtAssignStatus debtAssignStatus) {
		return debtAssignRequestDao.countByUserIdAndStatus(userId,debtAssignStatus);
	}




	@Override
	public List<DebtPlanDTO> findDebtPlanByIds(List<String> ids) throws ServiceException {
		if(ids==null){
			logger.info("DebtAssignRequestServiceImpl.findDebtPlanByIds 传入债转方案IDS为null");
			throw new ServiceException(ExceptionCode.DEBT_PLAN_ID_REQUIRED.getIndex(),ExceptionCode.DEBT_PLAN_ID_REQUIRED.getErrMsg());
		}
		List<DebtPlan> list=debtPlanDao.findByIds(ids);
		return DTOUtils.toDebtPlanDTOList(list);
	}




	@Override
	public Boolean debtassignCancelMQ(String debtProductId, String investId) throws ServiceException {
    	logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 转让人债转投资撤销执行开始，投资ID={} debtProductId={} ",investId,debtProductId);
    	if(debtProductId==null){
			throw new ServiceException(ExceptionCode.DEBT_ZZCP_REQUIRED.getIndex(),ExceptionCode.DEBT_ZZCP_REQUIRED.getErrMsg());
    	}
    	//通过债转产品ID查询符合状态的
    	List<InvestDTO> investList=null;
    	try {
    		investList=investService.findListByProductIdAndStatus(debtProductId, InvestStatus.BJ_FROZEN_SUCCESS);
		} catch (Exception e) {
			logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 查询投资记录列表异常 debtProductId{} ",debtProductId);
			throw new ServiceException(ExceptionCode.DEBT_INVEST_SOURCE.getIndex(),ExceptionCode.DEBT_INVEST_SOURCE.getErrMsg(),e);
		}
    	if(investList!=null&&investList.size()>0){
    		for (InvestDTO invest : investList) {
    			//解冻红包
    			if(StringUtils.isNotBlank(invest.getCouponPlacememtId())){
    				CouponPlacement  couponPlacement =couponService.findCouponPlacementbyId(null, invest.getCouponPlacememtId());
    				if(couponPlacement!=null&&couponPlacement.getStatus().equals(CouponStatus.PLACED)){
    					boolean couponRollBack_flag=false;
    					try {
    						couponRollBack_flag=couponService.couponPlacementUnFrozen(invest.getCouponPlacememtId(),RealmEntity.of(Realm.INVEST, invest.getId()));
    					} catch (Exception e) {
    						logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 调用红包接口解冻红包异常 invest.couponPlacememtId {} ",invest.getCouponPlacememtId());
    						throw new ServiceException(ExceptionCode.COUPON_JIEDONG_FAIL.getIndex(),ExceptionCode.COUPON_JIEDONG_FAIL.getErrMsg(),e);
    					}
//    					if(couponRollBack_flag==false){
//    						logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 调用红包接口解冻红包返回失败 invest.couponPlacememtId {} ",invest.getCouponPlacememtId());
//    						throw new ServiceException(ExceptionCode.COUPON_JIEDONG_FAIL.getIndex(),ExceptionCode.COUPON_JIEDONG_FAIL.getErrMsg());
//    					}
    				}
    			}
    			//----------------取消受让人投资记录start------------------
    			try {
    				logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 调用账户接口解冻接口 invest.id{},accountSerialNo{}",invest.getId(),invest.getLocalFreezeNo());
    				FreezeResultDto  freezeResultDto=assignService.investCancel(invest.getLocalFreezeNo());
    				invest.setLocalDfCode(freezeResultDto.getFundOperateId());
    				invest.setBjDfCode(freezeResultDto.getOrderNo());
    				invest.setStatus(InvestStatus.CANCELED);
    			} catch (Exception e) {
    				logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 修改投资记录北京银行冻结流水号 invest.accountSerialNo{},invest.bankSerialNo{}",invest.getLocalDfCode(),invest.getBjDfCode());
    				throw new ServiceException(ExceptionCode.ACCOUNT_JIEDONG_FAIL.getIndex(),ExceptionCode.ACCOUNT_JIEDONG_FAIL.getErrMsg());
    			}
    			try {
    				boolean accountflag=investService.update(invest);
    				if(!accountflag){
    					logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 修改投资记录账户服务解冻流水号失败 invest.id {} ",invest.getId());
    					throw new ServiceException(ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getErrMsg());	
    				}
    			} catch (ServiceException se) {
    				logger.info("DebtAssignHandler.debtassignCancel 修改投资记录账户服务解冻流水号失败 invest.id {} ",invest.getId());
    				throw new ServiceException(ExceptionCode.ACCOUNT_DONGJIE_FAIL.getIndex(),ExceptionCode.ACCOUNT_DONGJIE_FAIL.getErrMsg(),se);
    			}
    			//-----------------------end----------------
    		}
    	}
		investService.updateStatusById(investId, InvestStatus.SETTLED.getIndex());
		logger.info("DebtAssignRequestServiceImpl.debtassignCancelMQ 转让人债转投资撤销执行完成，投资ID{} debtProductId{} ",debtProductId);
		return true;
	
	}

}

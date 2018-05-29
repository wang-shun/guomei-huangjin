package com.gomemyc.invest.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.invest.dao.LoanTypeDao;
import com.gomemyc.invest.dao.ReserveConfigDao;
import com.gomemyc.invest.dao.ReserveInvestPeriodDao;
import com.gomemyc.invest.dao.ReserveQueueDao;
import com.gomemyc.invest.dao.ReserveQueueDetailDao;
import com.gomemyc.invest.dao.ReserveRateDao;
import com.gomemyc.invest.dao.ReserveRequestDao;
import com.gomemyc.invest.dto.AllReserveInfoDTO;
import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.entity.LoanType;
import com.gomemyc.invest.entity.ReserveConfig;
import com.gomemyc.invest.entity.ReserveInvestPeriod;
import com.gomemyc.invest.entity.ReserveQueue;
import com.gomemyc.invest.entity.ReserveQueueDetail;
import com.gomemyc.invest.entity.ReserveRate;
import com.gomemyc.invest.entity.ReserveRequest;
import com.gomemyc.invest.enums.ReserveParameterEnum;
import com.gomemyc.invest.enums.ReserveQueueStatusEnum;
import com.gomemyc.invest.enums.ReserveRequestStatusEnum;
import com.gomemyc.invest.service.ReserveRequestService;
import com.gomemyc.invest.utils.DtoSupport;
import com.gomemyc.invest.utils.ReserveUtils;

@Service
public class ReserveRequestServiceImpl implements ReserveRequestService{

//	/** 日志 */
//	Logger logger = LoggerFactory.getLogger(this.getClass());
//
//	/** 标的类型表-业务总开关2：是否允许预约 */
//	public static final Integer CAN_RESERVE = 2;
//
//    /**
//     * 开启只显示”不限“预约产品类型功能
//     */
//    final String RESERVE_PRODUCT_NOLIMIT_ENABLE = "TRUE";
//
//    /**
//     * 关闭只显示”不限“预约产品类型功能
//     */
//    final String RESERVE_PRODUCT_NOLIMIT_DISABLE = "FALSE";
//
//	/** 标的类型表DAO */
//    @Autowired
//    LoanTypeDao loanTypeDao;
//
//    /** 预约队列明细 */
//    @Autowired
//    ReserveQueueDetailDao reserveQueueDetailDao;
//
//    /** 利率区间 */
//    @Autowired
//    ReserveRateDao reserveRateDao;
//
//    /** 投资期限 */
//    @Autowired
//    ReserveInvestPeriodDao reserveInvestPeriodDao;
//
//    /** 配置信息 */
//    @Autowired
//    ReserveConfigDao reserveConfigDao;
//
//    /** 投资队列DAO */
//    @Autowired
//    ReserveQueueDao reserveQueueDao;
//
//	/** 预约申请单DAO  */
//    @Autowired
//    ReserveRequestDao reserveRequestDao;
//
//    /** 账户相关 */
///*    @Autowired
//    AccountService accountService;*/
//
//	/**
//	 * 显示预约申请单。
//	 *
//	 * @param userId 用户ID
//	 * @return
//	 */
//	@Override
//	public AllReserveInfoDTO getReserveInfo(String userId) throws ServiceException {
//
//		// 显示预约投资产品、利率、期限信息。
//		AllReserveInfoDTO allReserveInfoDTO = new AllReserveInfoDTO();
//	    // 校验传过来的
//        if (StringUtils.isBlank(userId)) {
//            logger.error("get reserve info params error,userId:{}", userId);
//        	throw new ServiceException(1000, "userId参数为空");
//        }
//        // 针对老代码做了优化，先查询用户是否存在-->
//        BigDecimal userAvailableAmount = BigDecimal.ZERO;
//        /*List<AccountDTO> accountDTOList = accountService.getByUserId(userId);
//        if (null == accountDTOList){
//            logger.error("get reserve info result error, userFund is null,userId:{}", userId);
//            throw new ServiceException(1002, "用户不存在");
//        } else {
//            for (AccountDTO accountDTO : accountDTOList){
//                // 这两种账户的余额之和就是可用余额
//                // FUND_INVESTMENT("现金投资子账户", 1)
//                if(accountDTO.getAccountType() == AccountType.FUND_INVESTMENT.getIndex()){
//                    userAvailableAmount = userAvailableAmount.add(accountDTO.getBalanceAmount());
//                }
//                // INVESTMENT_TRANSIT ("在途投资子账户", 2)
//                if(accountDTO.getAccountType() == AccountType.INVESTMENT_TRANSIT.getIndex()){
//                    userAvailableAmount = userAvailableAmount.add(accountDTO.getBalanceAmount());
//                }
//            }
//        }*/
//
//        // 新版本查询标的类型表tbl_loan_type （旧版本查询预约产品类型列表tb_reserve_product）-->
//        List<LoanType> loanTypeList = loanTypeDao.findListByTypeSwitch(CAN_RESERVE);
//        // 查询预约利率区间
//        List<ReserveRate> rateList = reserveRateDao.findByEnableList();
//        // 查询投资期限
//        List<ReserveInvestPeriod> periodList = reserveInvestPeriodDao.findByEnableList();
//        // 查询预约配置表(一次性查出来)
//        List<ReserveConfig> configList = reserveConfigDao.findByList();
//        if(null == configList || configList.size() == 0) {
//        	throw new ServiceException(1003, "预约配置列表为空");
//        }
//        // 定义参数变量，遍历上述一次性查出的集合。
//        // ("预约有效期下限"),
//        ReserveConfig minLine = null;
//        // ("预约投资上限"),
//        ReserveConfig maxLine = null;
//        // ("预约投资默认有效期"),
//        ReserveConfig defaulLine = null;
//        // ("预约起投金额"),
//        ReserveConfig minAmtlimit = null;
//        // ("预约最大金额"),
//        ReserveConfig maxAmtlimit = null;
//        // ("只显示不限产品类型");
//        ReserveConfig onlyShowNoLimit = null;
//        // 参数值为空校验
//        for (ReserveConfig config : configList) {
//        	if(StringUtils.isBlank(config.getParameterValue())){
//        		logger.error("reverseConfig parameterName {0} : value is blank", config.getParameterName());
//        		throw new ServiceException(1004, "预约配置项：" + config.getParameterName() + "值为空");
//        	}
//
//        	if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_MINDEADLINE.name())) {
//        		minLine = config;
//        	}
//        	if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_MAXDEADLINE.name())) {
//        		maxLine = config;
//        	}
//        	if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_DEFAULTDEADLINE.name())) {
//        		defaulLine = config;
//        	}
//        	if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_AMOUNT_MINLIMIT.name())) {
//        		minAmtlimit = config;
//        	}
//        	if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_AMOUNT_MAXLIMIT.name())) {
//        		maxAmtlimit = config;
//        	}
//        	if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_PRODUCT_ONLYSHOW_NOLIMIT.name())) {
//        		onlyShowNoLimit = config;
//        	}
//
//        }
//
//        // 预约起投金额, 预约最大金额
//        String amountMinLimit = minAmtlimit.getParameterValue();
//        String amountMaxLimit = maxAmtlimit.getParameterValue();
//
//        // 预约产品信息
//        StringBuffer stringBuffer = new StringBuffer();
//        for (LoanType loanType : loanTypeList) {
//            stringBuffer.append(loanType.getId()).append(",");
//        }
//        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//
//        // 最终向外输出预约产品信息集合
//        List<LoanType> products = new ArrayList<>();
//        // 可预约产品列表添加产品类型为不限的产品，用于前端显示，数据库并没有该条数据
//        LoanType anyProduct = new LoanType();
//        anyProduct.setId(stringBuffer.toString());
//        anyProduct.setName("不限");
//        anyProduct.setTypeSwitch(2);
//        products.add(anyProduct);
//        if (onlyShowNoLimit.getParameterValue().equals(RESERVE_PRODUCT_NOLIMIT_DISABLE)) {
//            products.addAll(loanTypeList);
//        }
//        // 将预约产品信息集合转换为 DTO 集合。
//        List<LoanTypeDTO> loanTypeDtoList = new ArrayList<>();
//        for(LoanType loanType : products){
//        	LoanTypeDTO dto = new LoanTypeDTO();
//        	dto.setId(loanType.getName());
//        	dto.setProductName(loanType.getName());
//        	dto.setEnable(loanType.getTypeSwitch() == 2 ? true : false);
//        }
//        // 转换后的预约起投金额, 预约最大金额
//        String amountMinDisplay = ReserveUtils.getReserveAmountLimitDisplay(amountMinLimit);
//        String amountMaxDisplay = ReserveUtils.getReserveAmountLimitDisplay(amountMaxLimit);
//        String reserveAmountTip = amountMinDisplay + "-" + amountMaxDisplay;
//
//        // 设置预约产品信息
//        allReserveInfoDTO.setReserveProducts(loanTypeDtoList);
//        // 预约利率信息
//        allReserveInfoDTO.setReserveRateRanges(DtoSupport.toReserveRateDTOList(rateList));
//        // 预约期限信息
//        allReserveInfoDTO.setReserveInvestPeriods(DtoSupport.toReserveInvestPeriodDTOList(periodList));
//        // 用户可用余额
//        allReserveInfoDTO.setAvailableAmount(userAvailableAmount);
//        // 最小预约期限
//        allReserveInfoDTO.setMinDeadLine(minLine.getParameterValue());
//        // 最大预约期限
//        allReserveInfoDTO.setMaxDeadLine(maxLine.getParameterValue());
//        // 预约起投金额
//        allReserveInfoDTO.setReserveAmountLimit(BigDecimal.valueOf(Double.valueOf(amountMinLimit)));
//        // 预约起投金额提示
//        allReserveInfoDTO.setAmountMinLimitDisplay(amountMinDisplay);
//        // 预约最大金额
//        allReserveInfoDTO.setReserveAmountMaxLimit(BigDecimal.valueOf(Double.valueOf(amountMaxLimit)));
//        // 预约起投金额文本信息
//        allReserveInfoDTO.setAmountMaxLimitDisplay(amountMaxDisplay);
//        // 预约最大金额文本信息
//        allReserveInfoDTO.setReserveAmountTip(reserveAmountTip);
//
//		return allReserveInfoDTO;
//	}
//
//	/**
//	 * 提交预约名单，生成投资队列。
//	 *
//	 * @param productId   可预约产品ID
//	 * @param rateRangeId 可预约利率区间ID
//	 * @param periodId    可预约投资期限ID
//	 * @param matchedAmount  总撮合金额
//	 * @param loanTypeName    可预约产品中文名称
//	 * @param ratePeriodDesc  可预约利率区间描述
//	 * @param investPeriodDesc 可预约投资期限描述
//	 * @param requestIdList    预约ID集合
//	 * @return
//	 */
//	@Override
//	public Integer reserveQueue(String productId, String rateRangeId, String periodId,
//			BigDecimal matchedAmount, String loanTypeName, String ratePeriodDesc, String investPeriodDesc,
//			List<String> requestIdList) {
//
//		ReserveQueue reserveQueue = new ReserveQueue();
//		reserveQueue.setProductId(productId);
//		reserveQueue.setLoanTypeName(loanTypeName);
//		reserveQueue.setRateRangeId(rateRangeId);
//		reserveQueue.setRatePeriodDesc(ratePeriodDesc);
//		reserveQueue.setInvestPeriodId(periodId);
//		reserveQueue.setInvestPeriodDesc(investPeriodDesc);
//		reserveQueue.setMatchedAmount(matchedAmount);
//		reserveQueue.setStatus(ReserveQueueStatusEnum.UNENTERED.getIndex());
//		reserveQueue.setCreateTime(new Date());
//
//		// 记录入参
//		logger.info("add ReserveQueue param productId {}, rateRangeId {}, "
//		 		    + "periodId {},matchedAmount {},productName {},ratePeriodDesc {},"
//		 		    + "investPeriodDesc {},requestIds {}", productId, rateRangeId, periodId, matchedAmount,
//		 		   loanTypeName, ratePeriodDesc, investPeriodDesc, requestIdList);
//
//
//		Map<String,Object> map = new HashMap<String, Object>();
//		try {
//			// 持久化投资队列、预约队列明细。
//			map = createReserveQueue(requestIdList, reserveQueue);
//		} catch (Exception e) {
//			map.put("flag", "1");
//			map.put("msg", "增加失败");
//		}
//
//		return null;
//	}
//
//	/**
//	 * 持久化投资队列、预约队列明细。
//	 *
//	 * @param requestIdList 预约队列ID集合
//	 * @param reserveQueue 投资队列
//	 * @return
//	 * @throws Exception
//	 */
//    public Map<String, Object> createReserveQueue(List<String> requestIdList, ReserveQueue reserveQueue) throws Exception{
//
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        // 判断预约申请单能否加到预约投资队列表。
//        if (addReserveQueue(requestIdList)) {
//        	reserveQueue.setId(UUIDGenerator.generate());
//        	reserveQueueDao.insert(reserveQueue);
//        	String reserveQueueId = reserveQueue.getId();
//        	System.out.println(reserveQueueId);
//            for (String requestId : requestIdList) {
//                ReserveRequest reserveRequest = reserveRequestDao.findByRequestId(requestId);
//                ReserveQueueDetail reserveQueueDetail = new ReserveQueueDetail();
//                reserveQueueDetail.setId(null);
//                // 投资队列id
//                reserveQueueDetail.setInvestQueueId(reserveQueueId);
//                // 预约申请id
//                reserveQueueDetail.setReserveApplyId(requestId);
//                // 撮合资金
//                reserveQueueDetail.setMatchingFunds(reserveRequest.getBalanceAmount());
//                // 预约产品名称
//                reserveQueueDetail.setLoanTypeName(reserveRequest.getProductName());
//                reserveQueueDetail.setRateDate(reserveRequest.getRatePeriods());
//                reserveQueueDetail.setInvestmentInterval(reserveRequest.getInvestPeriods());
//                reserveQueueDetailDao.insert(reserveQueueDetail);
//
//                reserveRequest.setStatus(ReserveRequestStatusEnum.INVESTED.getIndex());
//                // 这里要核对库表设计！！
//                reserveRequest.setEnableCancelled(1);
//
//                reserveRequestDao.updateByPrimaryKeySelective(reserveRequest);
//
//            }
//            // 修改状态
//            reserveQueue.setStatus(ReserveQueueStatusEnum.UNENTERED.getIndex());
//            reserveQueueDao.updateByPrimaryKeySelective(reserveQueue);
//
//
//            resultMap.put("flag", "0");
//            resultMap.put("msg", "新建投资单成功");
//
//        } else {
//            resultMap.put("flag", "1");
//            resultMap.put("msg", "所选预约单有误，请重新选择");
//        }
//
//        return resultMap;
//    }
//
//	 /**
//     * 判断预约申请单能否加到预约投资队列表。
//     *
//     * @param requestIdList
//     *
//     * @return
//     */
//    private Boolean addReserveQueue(List<String> requestIdList) {
//
//    	// 所选择的预约申请单可加入投资队列。
//        Boolean canAddtoQueue = true;
//
//        /*// 遍历预约申请单id集合
//        for (String requestId : requestIdList) {
//        	// 需要确认requestId，是否是新表中的 reserve_apply_id
//            List<ReserveQueueDetail> reserveQueueDetails = reserveQueueDetailDao.listByRequestId(requestId);
//            logger.debug("ReserveBridge addReserveQueue:判断提交的预约申请单能否加进队列中 requestId {}, resultSize {},"+requestId,reserveQueueDetails.size());
//            // 判断在其他队列中的情况
//            if (reserveQueueDetails.size() != 0) {
//            	// 查询预约申请单
//                ReserveRequest reserveRequest = reserveRequestDao.findByRequestId(requestId);
//                // 判断不是部分投资成功切余额不为0的情况
//                if (!ReserveRequestStatusEnum.PARTLY_SUCCESSFUL.equals(reserveRequest.getStatus())
//                    && (0 == reserveRequest.getBalanceAmount().compareTo(BigDecimal.ZERO))) {
//                    // 判断在队列的情况是否是待录入状态，如果是则正常,不是则不能加入
//                    for (ReserveQueueDetail reserveQueueDetail : reserveQueueDetails) {
//                        String queueId = reserveQueueDetail.getId();
//                        ReserveQueue reserveQueue = reserveQueueDao.findByQueueId(queueId);
//                        if (!(ReserveQueueStatusEnum.UNENTERED).equals(reserveQueue.getStatus())) {
//                        	canAddtoQueue = false;
//                            break;
//                        }
//                    }
//
//                }
//            }
//        }*/
//
//        return canAddtoQueue;
//
//    }
	
    
}

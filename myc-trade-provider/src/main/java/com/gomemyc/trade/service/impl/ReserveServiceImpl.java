package com.gomemyc.trade.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.AppointmentService;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.common.utils.DateUtils;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.model.enums.Realm;
import com.gomemyc.trade.util.*;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.LoanTypeService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.model.misc.RealmEntity;
import com.gomemyc.trade.bridge.ReserveBridge;
import com.gomemyc.trade.dto.AllReserveInfoDTO;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.dto.ReserveRequestShowDTO;
import com.gomemyc.trade.dao.*;
import com.gomemyc.trade.entity.*;
import com.gomemyc.trade.enums.*;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.trade.service.ReserveService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.*;

/**
 * 预约投资接口实现类。
 *
 * @author 何健
 * @creaTime 2017年3月10日
 */
@Service
public class ReserveServiceImpl implements ReserveService {

    /** 日志 */
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 标的类型表-业务总开关2：是否允许预约 */
    public static final Integer CAN_RESERVE = 2;


    /** 启用预约功能 */
    public static final String RESERVE_ENABLE = "TRUE";

    /**
     * 只显示“不限”
     */
    final String RESERVE_PRODUCT_NOLIMIT_DISABLE = "FALSE";

    /**
     * 预约产品键值
     */
    final String YUYUE_PRODUCT_KEY = "YUYUE";

    /** 申请单DAO  */
    @Autowired
    private ReserveRequestDao reserveRequestDao;

    /** 预约队列DAO */
    @Autowired
    private ReserveQueueDao reserveQueueDao;

    /** 预约队列明细 */
    @Autowired
    private ReserveQueueDetailDao reserveQueueDetailDao;

    /** 预约桥接 */
    @Autowired
    private ReserveBridge reserveBridge;

    /** 利率区间 */
    @Autowired
    private ReserveRateDao reserveRateDao;

    /** 投资期限 */
    @Autowired
    private ReserveInvestPeriodDao reserveInvestPeriodDao;

    /** 配置信息 */
    @Autowired
    private ReserveConfigDao reserveConfigDao;

    /** 账户相关 */
    @Reference
    AccountService accountService;

    /** 产品相关 */
    @Reference
    ProductRegularService productRegularService;

    /** 红包服务 */
    @Reference
    private CouponService couponService;

    /** 标的类型表服务 */
    @Reference
    LoanTypeService loanTypeService;

    @Reference(timeout = 10000)
    AppointmentService appointmentService;
    
    @Reference
    InvestService investService;

    @Reference
    LoanService loanService;

    /** 7.MQ发送者  */
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;

    /**
     * 显示预约申请单。
     *
     * @param userId  用户id
     * @return 预约产品DTO
     * @throws ServiceException
     * 1000, "userId参数为空"
     * 1002, "当前用户无账户"
     * 1003, "预约配置列表为空"
     * 1004, "预约配置项值为空"
     */
    @Override
    public AllReserveInfoDTO getReserveInfo(String userId) throws ServiceException {

        // 显示预约投资产品、利率、期限信息。
        AllReserveInfoDTO allReserveInfoDTO = new AllReserveInfoDTO();
        // 校验传过来的
        if (StringUtils.isBlank(userId)) {
            logger.error("get reserve info params error,userId:{}", userId);
            throw new ServiceException(ExceptionCode.USER_ID_BLANK.getIndex(), ExceptionCode.USER_ID_BLANK.getErrMsg());
        }
        // 新版本开发优化，不判断用户是否存在，在账户不存在时异常返回。
        BigDecimal userAvailableAmount = BigDecimal.ZERO;
        AccountDTO accountDTO = accountService.getByUserId(userId);
        if(null == accountDTO){
            // 用户账户未创建
            throw new ServiceException(ExceptionCode.ACCOUNT_NOT_CREATE.getIndex(), ExceptionCode.ACCOUNT_NOT_CREATE.getErrMsg());
        }
        // 现金金额
        userAvailableAmount = userAvailableAmount.add(accountDTO.getBalanceAmount() == null ? BigDecimal.ZERO : accountDTO.getBalanceAmount());
        // 在途金额
        userAvailableAmount = userAvailableAmount.add(accountDTO.getTransitAmount() == null ? BigDecimal.ZERO : accountDTO.getTransitAmount());
        // 新版本查询标的类型表tbl_loan_type （旧版本查询预约产品类型列表tb_reserve_product）-->
        List<LoanTypeDTO> loanTypeList = loanTypeService.findListByTypeSwitch(CAN_RESERVE);
        // 查询预约利率区间
        List<ReserveRate> rateList = reserveRateDao.findByEnableList();
        // 查询投资期限
        List<ReserveInvestPeriod> periodList = reserveInvestPeriodDao.findByEnableList();
        // 查询预约配置表(一次性查出来)
        List<ReserveConfig> configList = reserveConfigDao.findByList();
        if(null == configList || configList.size() == 0) {
            throw new ServiceException(ExceptionCode.RESERVE_CONFIG_EMPTY.getIndex(), ExceptionCode.RESERVE_CONFIG_EMPTY.getErrMsg());
        }
        // 定义参数变量，遍历上述一次性查出的集合。
        // ("预约有效期下限"),
        ReserveConfig minLine = null;
        // ("预约投资上限"),
        ReserveConfig maxLine = null;
        // ("预约投资默认有效期"),
        ReserveConfig defaulLine = null;
        // ("预约起投金额"),
        ReserveConfig minAmtlimit = null;
        // ("预约最大金额"),
        ReserveConfig maxAmtlimit = null;
        // ("只显示不限产品类型");
        ReserveConfig onlyShowNoLimit = null;
        // 参数值为空校验
        for (ReserveConfig config : configList) {
            if(StringUtils.isBlank(config.getParameterValue())){
                logger.error("reverseConfig parameterName {0} : value is blank", config.getParameterName());
                throw new ServiceException(ExceptionCode.RESERVE_CONFIG_ITEM_EMPTY.getIndex(), ExceptionCode.RESERVE_CONFIG_ITEM_EMPTY.getErrMsg() + config.getParameterName());
            }
            // 预约有效期下限
            if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_MINDEADLINE.name())) {
                minLine = config;
            }
            // 预约投资上限
            if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_MAXDEADLINE.name())) {
                maxLine = config;
            }
            // 预约投资默认有效期
            if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_DEFAULTDEADLINE.name())) {
                defaulLine = config;
            }
            // 预约起投金额
            if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_AMOUNT_MINLIMIT.name())) {
                minAmtlimit = config;
            }
            // 预约最大金额
            if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_AMOUNT_MAXLIMIT.name())) {
                maxAmtlimit = config;
            }
            // 只显示不限产品类型
            if (null != config && config.getParameterName().equals(ReserveParameterEnum.RESERVE_PRODUCT_ONLYSHOW_NOLIMIT.name())) {
                onlyShowNoLimit = config;
            }
        }
        // 预约起投金额, 预约最大金额
        String amountMinLimit = minAmtlimit.getParameterValue();
        String amountMaxLimit = maxAmtlimit.getParameterValue();

        // 预约产品信息
        StringBuffer stringBuffer = new StringBuffer();
        for (LoanTypeDTO loanType : loanTypeList) {
            stringBuffer.append(loanType.getId()).append(",");
        }
        if(stringBuffer.length() != 0) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }

        // 最终向外输出预约产品信息集合
        List<LoanTypeDTO> products = new ArrayList<>();
        // 可预约产品列表添加产品类型为不限的产品，用于前端显示，数据库并没有该条数据
        LoanTypeDTO anyProduct = new LoanTypeDTO();
        anyProduct.setId(stringBuffer.toString());
        anyProduct.setProductName("不限");
        // “不限”产品设置为可预约
        anyProduct.setEnable(true);

        // 输出增加“不限”产品类型
        products.add(anyProduct);

        // 关闭只显示“不限”产品时，将所有产品加入进来
        if (onlyShowNoLimit.getParameterValue().equals(RESERVE_PRODUCT_NOLIMIT_DISABLE)) {
            products.addAll(loanTypeList);
        }

        // 转换后的预约起投金额, 预约最大金额
        String amountMinDisplay = ReserveUtils.getReserveAmountLimitDisplay(amountMinLimit);
        String amountMaxDisplay = ReserveUtils.getReserveAmountLimitDisplay(amountMaxLimit);
        String reserveAmountTip = amountMinDisplay + "-" + amountMaxDisplay;

        // 设置预约产品信息
        allReserveInfoDTO.setReserveProducts(products);
        // 预约利率信息
        allReserveInfoDTO.setReserveRateRanges(DtoSupport.toReserveRateDTOList(rateList));
        // 预约期限信息
        allReserveInfoDTO.setReserveInvestPeriods(DtoSupport.toReserveInvestPeriodDTOList(periodList));
        // 用户可用余额
        allReserveInfoDTO.setAvailableAmount(userAvailableAmount);
        // 最小预约期限
        allReserveInfoDTO.setMinDeadLine(minLine.getParameterValue());
        // 最大预约期限
        allReserveInfoDTO.setMaxDeadLine(maxLine.getParameterValue());
        // 预约默认有效期
        allReserveInfoDTO.setDefaultDeadLine(defaulLine.getParameterValue());
        // 预约起投金额
        allReserveInfoDTO.setReserveAmountLimit(BigDecimal.valueOf(Double.valueOf(amountMinLimit)));
        // 预约起投金额提示
        allReserveInfoDTO.setAmountMinLimitDisplay(amountMinDisplay);
        // 预约最大金额
        allReserveInfoDTO.setReserveAmountMaxLimit(BigDecimal.valueOf(Double.valueOf(amountMaxLimit)));
        // 预约起投金额文本信息
        allReserveInfoDTO.setAmountMaxLimitDisplay(amountMaxDisplay);
        // 预约最大金额文本信息
        allReserveInfoDTO.setReserveAmountTip(reserveAmountTip);

        return allReserveInfoDTO;
    }

    /**
     * 提交预约名单，生成投资队列。
     *
     * @param productId   可预约产品ID
     * @param rateRangeId 可预约利率区间ID
     * @param periodId    可预约投资期限ID
     * @param matchedAmount  总撮合金额
     * @param loanTypeName    可预约产品中文名称
     * @param ratePeriodDesc  可预约利率区间描述
     * @param investPeriodDesc 可预约投资期限描述
     * @param requestIdList    预约ID集合
     * @return
     * 10000：成功
     * 10002：所选预约单有误，请重新选择
     * @throws ServiceException
     * 1000, "插入队列数据失败"
     * 1002, "插入预约队列明细数据失败"
     * 1003, "更新申请单为“投资中”失败"
     * 1004, "更新队列状态为“待录入产品”失败"
     */
    @Override
    public Integer reserveQueue(String productId, String rateRangeId, String periodId,
                                BigDecimal matchedAmount, String loanTypeName, String ratePeriodDesc, String investPeriodDesc,
                                List<String> requestIdList) throws ServiceException{

        // 检查入参，检查传入的申请单id 集合是否合法，能否加入队列。
        if( !reserveBridge.isValidToQueue(requestIdList) ){
            throw new ServiceException(10002, "插入队列数据失败");
        }

        ReserveQueue reserveQueue = new ReserveQueue();
        // 可预约产品id
        reserveQueue.setLoanTypeId(productId);
        // 可预约产品中文名称
        reserveQueue.setLoanTypeName(loanTypeName);
        // 可预约利率区间id
        reserveQueue.setRateRangeId(rateRangeId);
        // 可预约利率区间描述
        reserveQueue.setRatePeriodDesc(ratePeriodDesc);
        // 可预约投资期限id
        reserveQueue.setInvestPeriodId(periodId);
        // 可预约利率区间描述
        reserveQueue.setInvestPeriodDesc(investPeriodDesc);
        // 总撮合金额
        reserveQueue.setMatchedAmount(matchedAmount);
        // 预约状态设置为“待录入产品”
        reserveQueue.setStatus(ReserveQueueStatusEnum.UNENTERED.getIndex());
        // 创建时间
        reserveQueue.setCreateTime(new Date());
        // 日志记录入参
        logger.info("add ReserveQueue param productId {}, rateRangeId {}, "
                        + "periodId {},matchedAmount {},productName {},ratePeriodDesc {},"
                        + "investPeriodDesc {},requestIds {}", productId, rateRangeId, periodId, matchedAmount,
                loanTypeName, ratePeriodDesc, investPeriodDesc, requestIdList);

        // 持久化投资队列、预约队列明细。
        return reserveBridge.createQueueAndDetail(requestIdList, reserveQueue);
    }

    /**
     * 预约队列投资服务，后台点投资后调用此方法。
     *
     * @param queueId 投资队列id
     * @return
     * @throws ServiceException
     */
    @Override
    public Boolean submitReserveInvest(String queueId) throws ServiceException {
        logger.info("进入预约投资方法，队列id {}", queueId);
        if(StringUtils.isBlank(queueId)){
            throw new ServiceException(ExceptionCode.NO_VALID_QUEUE.getIndex(), ExceptionCode.NO_VALID_QUEUE.getErrMsg());
        }
        ReserveQueue reserveQueue = reserveQueueDao.findById(queueId);
        if (reserveQueue == null || StringUtils.isBlank(reserveQueue.getLoanName())) {
            throw new ServiceException(ExceptionCode.NO_VALID_QUEUE.getIndex(), ExceptionCode.NO_VALID_QUEUE.getErrMsg());
        }

        // 直接根据队列中的 ‘理财产品名称’product_name 查询产品。
        ProductRegularDTO regularDTO = productRegularService.findByTitle(reserveQueue.getLoanName());
        if(null == regularDTO){
            throw new ServiceException(ExceptionCode.NO_VALID_QUEUE.getIndex(), ExceptionCode.NO_VALID_QUEUE.getErrMsg());
        }
        // 标利率不一致等校验。
        beforeSubmitInvestCheck(reserveQueue, regularDTO);
        // 查询预约队列明细
        List<ReserveQueueDetail> queueDetails = reserveQueueDetailDao.findListByQueueId(queueId);
        for (ReserveQueueDetail reserveQueueDetail : queueDetails) {
            ReserveRequest reserveRequest = reserveRequestDao.findById(reserveQueueDetail.getReserveApplyId());
            String userId = reserveRequest.getUserId();
            // 处理单笔队列明细，完成投资动作。
            String mqStr = reserveBridge.tenderOneDetail(userId, regularDTO, reserveQueueDetail, reserveRequest);
            try {
                logger.info("预约mq消息单发，requestId:" + reserveRequest.getId());
                logger.info("要发送预约投资的mq消息：" + mqStr);
                // 通知消费端做 1、解冻北京；2、同步投资（同步）北京（转账）；3、解冻本地转账（本地）；4、更新状态
                Message message = new Message(MQTopic.MYC_RESERVE_INVEST.getValue(), mqStr.getBytes());
                SendResult sendResult = mqProducer.send(message);
                logger.info("发送预约消费MQ结果：" + sendResult);
            } catch (Exception e) {
                logger.info("发送预约消费MQ失败，申请单id" + reserveRequest.getId() + ", ");
                throw new ServiceException(ExceptionCode.SEND_INVEST_MQ_FAIL.getIndex(), ExceptionCode.SEND_INVEST_MQ_FAIL.getErrMsg());
            }
        }

        // 投资成功后直接将状态改掉，不再等结标消息。结标消息只作为记录日志用。
        Boolean settleResult = settleReserve(queueId, true);
        logger.info("更新队列、申请单状态结果：" + settleResult + ", 队列id:" + queueId);

        // 清缓存失败后不做任何处理
        // 成功与否只记日志
        try{
            // 清除redis
            boolean clearIsSuccess = loanService.clearProductCacheAndList(regularDTO.getId());
            // 成功与否只记日志
            logger.info("清理缓存,productId:" + regularDTO.getId() + ", clearIsSuccess" + clearIsSuccess);
        }catch (Exception e){
            logger.info("清理缓存,productId:" + regularDTO.getId() + ", 失败", e);
        }


        logger.info("预约投资执行成功==============================>>>>>>>>>>");
        // 如果上述步骤没抛出异常，预约投资返回成功
        return true;
    }

    /**
     * 投资前检查。
     *
     * @param reserveQueue 队列
     * @param regularDTO 产品
     * @throws ServiceException
     */
    private void beforeSubmitInvestCheck (ReserveQueue reserveQueue, ProductRegularDTO regularDTO) throws ServiceException {
        // 总撮合金额
        BigDecimal matchAmount = reserveQueue.getMatchedAmount();
        if (matchAmount == null || matchAmount.compareTo(BigDecimal.ZERO) < 1) {
            // 无效的投资队列
            throw new ServiceException(ExceptionCode.NO_VALID_QUEUE.getIndex(), ExceptionCode.NO_VALID_QUEUE.getErrMsg());
        }

        // 只有预约标才能进行预约投资
        String productKey = regularDTO.getTypeKey();
/*        if(!YUYUE_PRODUCT_KEY.equals(productKey)){
            throw new ServiceException(ExceptionCode.ONLY_FOR_YUYUE_2_INVEST.getIndex(), ExceptionCode.ONLY_FOR_YUYUE_2_INVEST.getErrMsg());
        }*/

        logger.info("打印产品已投资金额：" + regularDTO.getInvestAmount());
        // 已满标
        if(regularDTO.getStatus() == ProductStatus.FINISHED ||
                regularDTO.getAvailable().compareTo(BigDecimal.ZERO) <= 0){
            throw new ServiceException(ExceptionCode.FULLED_LOAN.getIndex(), ExceptionCode.FULLED_LOAN.getErrMsg());
        }

        // 未开放投标
        if (regularDTO.getStatus() != ProductStatus.OPENED) {
            throw new ServiceException(ExceptionCode.NO_OPEN_FOR_INVEST.getIndex(), ExceptionCode.NO_OPEN_FOR_INVEST.getErrMsg());
        }
        // 预约投资金额（队列金额）与产品金额不匹配
        if (matchAmount.compareTo(regularDTO.getAmount()) != 0) {
            throw new ServiceException(ExceptionCode.QUEUEAMT_NOT_MATCH_LOANAMT.getIndex(), ExceptionCode.QUEUEAMT_NOT_MATCH_LOANAMT.getErrMsg());
        }

        // 判断预约投资期限是否有效
        ReserveInvestPeriod investPeriod = reserveInvestPeriodDao.findById(reserveQueue.getInvestPeriodId());
        if(null == investPeriod) {
            throw new ServiceException(ExceptionCode.NOT_FOUND_INVEST_PERIOD.getIndex(), ExceptionCode.NOT_FOUND_INVEST_PERIOD.getErrMsg());
        }

        if (investPeriod.getMinInvestPeriod() != 0 && investPeriod.getMaxInvestPeriod() != 0) {
            if ((LoanDaysUtil.getTotalDays(regularDTO) < investPeriod.getMinInvestPeriod())
                    || (LoanDaysUtil.getTotalDays(regularDTO) > investPeriod.getMaxInvestPeriod())) {

                throw new ServiceException(ExceptionCode.INVEST_PERIOD_NOTMATCH_LOANDAYS.getIndex(), ExceptionCode.INVEST_PERIOD_NOTMATCH_LOANDAYS.getErrMsg());
            }
        }

        if (investPeriod.getMinInvestPeriod() == 0 && investPeriod.getMaxInvestPeriod() != 0) {
            if (LoanDaysUtil.getTotalDays(regularDTO) > investPeriod.getMaxInvestPeriod()) {
                throw new ServiceException(ExceptionCode.INVEST_PERIOD_NOTMATCH_LOANDAYS.getIndex(), ExceptionCode.INVEST_PERIOD_NOTMATCH_LOANDAYS.getErrMsg());
            }
        }
        if (investPeriod.getMinInvestPeriod() != 0 && investPeriod.getMaxInvestPeriod() == 0) {
            if (LoanDaysUtil.getTotalDays(regularDTO) < investPeriod.getMinInvestPeriod()) {
                throw new ServiceException(ExceptionCode.INVEST_PERIOD_NOTMATCH_LOANDAYS.getIndex(), ExceptionCode.INVEST_PERIOD_NOTMATCH_LOANDAYS.getErrMsg());
            }
        }

        // 判断预约投资利率是否有效
        ReserveRate reserveRate = reserveRateDao.findById(reserveQueue.getRateRangeId());
        if (reserveRate == null) {
            throw new ServiceException(ExceptionCode.NOT_FOUND_RATE.getIndex(), ExceptionCode.NOT_FOUND_RATE.getErrMsg());
        }

       if (reserveRate.getMinRate() != 0 && reserveRate.getMaxRate() != 0) {
            if ((regularDTO.getRate() < reserveRate.getMinRate())
                    || (regularDTO.getRate() > reserveRate.getMaxRate())) {
                throw new ServiceException(ExceptionCode.RATE_NOTMATCH_LOANRATE.getIndex(), ExceptionCode.RATE_NOTMATCH_LOANRATE.getErrMsg());
            }
        }
        if (reserveRate.getMinRate() == 0 && reserveRate.getMaxRate() != 0) {
            if (regularDTO.getRate() > reserveRate.getMaxRate()) {
                throw new ServiceException(ExceptionCode.RATE_NOTMATCH_LOANRATE.getIndex(), ExceptionCode.RATE_NOTMATCH_LOANRATE.getErrMsg());
            }
        }
        if (reserveRate.getMinRate() != 0 && reserveRate.getMaxRate() == 0) {
            if (regularDTO.getRate() < reserveRate.getMinRate()) {
                throw new ServiceException(ExceptionCode.RATE_NOTMATCH_LOANRATE.getIndex(), ExceptionCode.RATE_NOTMATCH_LOANRATE.getErrMsg());
            }
        }
    }

    /**
     * 更新预约队列、申请单状态。
     *
     * @Description 更新预约队列状态
     * @param queueId 投资队列id
     * @return 更新是否成功
     * @throws ServiceException
     */
    @Override
    public Boolean settleReserve(String queueId, Boolean isSuccess) throws ServiceException{
        return reserveBridge.settleReserve(queueId, isSuccess);
    }


    /**
     * 检查状态为预约中的预约申请单并解冻预约资金，给定时任务用。
     *
     * @throws ServiceException
     */
    public void checkReserveRequests() throws ServiceException{
        logger.info("检查预约申请单定时任务开始");
        // 已优化：直接查询状态为预约中且已过期的数据
        List<ReserveRequest> reserveRequests = reserveRequestDao.findListByStatusAndIsExpired(ReserveRequestStatusEnum.RESERVED.getIndex());
        logger.info("检查预约单总数{}", reserveRequests.size());
        for (ReserveRequest reserveRequest : reserveRequests){
            // 判断预约是否已过期
            if (new DateTime().getMillis() > new DateTime(reserveRequest.getExpireTime()).getMillis()) {
                logger.info("准备处理过期申请单, [reserveRequest={}]", reserveRequest);
                boolean checkAndUnFreezeIsSuccess = false;
                try {
                    checkAndUnFreezeIsSuccess = reserveBridge.checkReserveRequest(reserveRequest);
                } catch (Exception e){
                    logger.warn("处理过期申请单【解冻操作】失败, [reserveRequest={}]异常[exception={}]", reserveRequest, e);
                }
                if(checkAndUnFreezeIsSuccess){
                    try {
                        reserveBridge.doUpdateReserveRequest(reserveRequest);
                    }catch(Exception e){
                        // 捕获一条申请单的异常，防止影响所有用户申请单
                        logger.warn("处理过期申请单【更新申请单状态】失败, [reserveRequest={}]异常[exception={}]", reserveRequest, e);
                    }
                } else {
                    logger.warn("处理过期申请单【解冻操作】失败, [reserveRequest={}]", reserveRequest);
                }
            }
        }
    }

    /**
     * 提供给后台用于取消投资队列的操作资金步骤。
     *
     * @param requestIdList
     * @return
     * @throws ServiceException
     */
    public Boolean updateReserveRequestsStatus4Console(List<String> requestIdList) throws ServiceException{
        if (null == requestIdList) {
            throw new ServiceException(1008, "所传id集合为空");
        }
        int updateRequestCount = 0;
        for(String requestId : requestIdList){
            ReserveRequest reserveRequest = reserveRequestDao.findById(requestId);
            if (new DateTime().getMillis() > new DateTime(reserveRequest.getExpireTime()).getMillis()){
                boolean checkAndUnFreezeIsSuccess = false;
                boolean updateReserveRequestIsSuccess = false;
                try {
                    checkAndUnFreezeIsSuccess = reserveBridge.checkReserveRequest(reserveRequest);
                } catch (Exception e){
                    logger.warn("后台取消队列处理过期申请单【checkAndUnFreeze】异常, [reserveRequest={}]异常[exception={}]", reserveRequest, e);
                }
                if(checkAndUnFreezeIsSuccess){
                    try {
                        updateReserveRequestIsSuccess = reserveBridge.doUpdateReserveRequest(reserveRequest);
                        logger.info("后台取消队列处理过期申请单【更新申请单状态】更新是否成功？：" + updateReserveRequestIsSuccess);
                    }catch(Exception e){
                        // 捕获一条申请单的异常，防止影响所有用户申请单
                        logger.warn("后台取消队列处理过期申请单【更新申请单状态】失败, [reserveRequest={}]异常[exception={}]", reserveRequest, e);
                    }
                } else {
                    logger.warn("后台取消队列处理过期申请单【checkAndUnFreeze】false, [reserveRequest={}]", reserveRequest);
                }
                return (checkAndUnFreezeIsSuccess && updateReserveRequestIsSuccess) ? true : false;
            } else {
                // 未过期 只修改状态 可继续预约
                ReserveRequest updateRequest = new ReserveRequest();
                updateRequest.setId(requestId);
                updateRequest.setStatus(ReserveRequestStatusEnum.RESERVED.getIndex());
                updateRequest.setEnableCancelled(ReserveRequestCancelEnum.TRUE.getIndex());
                updateRequestCount = reserveRequestDao.updateStatusAndCancelled(updateRequest);
                logger.info("后台取消队列，申请单未过期，更新为RESERVED、可取消，更新条数：" + updateRequestCount);
            }
        }
        return updateRequestCount == 1 ? true : false;
    }

    /**
     * 提供给后台移出队列的操作资金步骤。
     *
     * @return
     * @throws ServiceException
     */
    public Boolean checkReserveRequestStatusAndUpdateStatus(String requestId) throws ServiceException{
        ReserveRequest reserveRequest = reserveRequestDao.findById(requestId);
        int updateRequestCount = 0;
        if (new DateTime().getMillis() > new DateTime(reserveRequest.getExpireTime()).getMillis()){
            boolean checkAndUnFreezeIsSuccess = false;
            boolean updateReserveRequestIsSuccess = false;
            try {
                checkAndUnFreezeIsSuccess = reserveBridge.checkReserveRequest(reserveRequest);
            } catch (Exception e){
                logger.warn("后台移出队列处理过期申请单【checkAndUnFreeze】异常, [reserveRequest={}]异常[exception={}]", reserveRequest, e);
            }
            if(checkAndUnFreezeIsSuccess){
                try {
                    updateReserveRequestIsSuccess = reserveBridge.doUpdateReserveRequest(reserveRequest);
                    logger.info("后台移出队列处理过期申请单【更新申请单状态】更新是否成功？：" + updateReserveRequestIsSuccess);
                }catch(Exception e){
                    // 捕获一条申请单的异常，防止影响所有用户申请单
                    logger.warn("后台移出队列处理过期申请单【更新申请单状态】异常, [reserveRequest={}]异常[exception={}]", reserveRequest, e);
                }
            } else {
                logger.warn("后台移出队列处理过期申请单【checkAndUnFreeze】失败, [reserveRequest={}]", reserveRequest);
            }
            return (checkAndUnFreezeIsSuccess && updateReserveRequestIsSuccess) ? true : false;
        } else {
            // 未过期 只修改状态 可继续预约
            ReserveRequest updateRequest = new ReserveRequest();
            updateRequest.setId(requestId);
            updateRequest.setStatus(ReserveRequestStatusEnum.RESERVED.getIndex());
            updateRequest.setEnableCancelled(ReserveRequestCancelEnum.TRUE.getIndex());
            updateRequestCount = reserveRequestDao.updateStatusAndCancelled(updateRequest);
            logger.info("后台移出队列，申请单未过期，更新为RESERVED、可取消，更新条数：" + updateRequestCount);
        }
        return updateRequestCount == 1 ? true : false;
    }



    @Override
	public Boolean submitReserveRequest(String userId, String productIds, String periodIds, String rateIds,
			BigDecimal amount, int waitPeriod, String reserveCoupon) throws ServiceException {

        logger.info("进入预约申请单方法=============================>>>>>>>>>");

        // 判断预约功能是否打开
        ReserveConfig reserveEnableConfig = reserveConfigDao.findByName("RESERVE_ENABLE");
        logger.info("查询到的启用预约功能配置值：" + reserveEnableConfig.getParameterValue());
        if (reserveEnableConfig == null || !RESERVE_ENABLE.equals(reserveEnableConfig.getParameterValue())) {
            throw new ServiceException(ExceptionCode.RESERVE_DISABLE.getIndex(), ExceptionCode.RESERVE_DISABLE.getErrMsg());
        }

		//预约期限下限
        ReserveConfig minDeadLineConfig = reserveConfigDao.findByName("RESERVE_MINDEADLINE");
        //预约期限上限
        ReserveConfig maxDeadLineConfig = reserveConfigDao.findByName("RESERVE_MAXDEADLINE");
        //起投金额
        ReserveConfig reserveAmountLimitConfig = reserveConfigDao.findByName("RESERVE_AMOUNT_MINLIMIT");
        //最大金额
        ReserveConfig reserveAmountMaxLimitConfig = reserveConfigDao.findByName("RESERVE_AMOUNT_MAXLIMIT");
        if (minDeadLineConfig == null){
        	throw new ServiceException(ExceptionCode.MIN_DEAD_LINE.getIndex(), ExceptionCode.MIN_DEAD_LINE.getErrMsg());
        }
        if (StringUtils.isBlank(minDeadLineConfig.getParameterValue())) {
        	throw new ServiceException(ExceptionCode.MIN_DEAD_LINE_BLANK.getIndex(),ExceptionCode.MIN_DEAD_LINE_BLANK.getErrMsg());
        }
        if (maxDeadLineConfig == null) {
        	throw new ServiceException(ExceptionCode.MAX_DEAD_LINE_BLANK.getIndex(),ExceptionCode.MAX_DEAD_LINE_BLANK.getErrMsg());
		}
        if (StringUtils.isBlank(maxDeadLineConfig.getParameterValue())) {
        	throw new ServiceException(ExceptionCode.MAX_DEAD_LINE.getIndex(),ExceptionCode.MAX_DEAD_LINE_BLANK.getErrMsg());
		}
        if(reserveAmountLimitConfig == null){
        	throw new ServiceException(ExceptionCode.RESERVE_AMOUNT_LIMIT_BLANK.getIndex(),ExceptionCode.RESERVE_AMOUNT_LIMIT_BLANK.getErrMsg());
        }
        if (StringUtils.isBlank(reserveAmountLimitConfig.getParameterValue())) {
        	throw new ServiceException(ExceptionCode.RESERVE_AMOUNT_LIMIT.getIndex(),ExceptionCode.RESERVE_AMOUNT_LIMIT.getErrMsg());
		}
        if (reserveAmountMaxLimitConfig == null) {
        	throw new ServiceException(ExceptionCode.AMOUNT_MAX_LIMIT_BLANK.getIndex(),ExceptionCode.AMOUNT_MAX_LIMIT_BLANK.getErrMsg());
		}
        if (StringUtils.isBlank(reserveAmountMaxLimitConfig.getParameterValue())) {
        	throw new ServiceException(ExceptionCode.AMOUNT_MAX_LIMIT.getIndex(),ExceptionCode.AMOUNT_MAX_LIMIT.getErrMsg());
        }
        // 抵现券金额
        BigDecimal couponAmount = BigDecimal.ZERO;
        CouponPlacement couponPlacement = null;
        // 预约的金额
        if (!StringUtils.isBlank(reserveCoupon) && !reserveCoupon.equals("")) {
        	 couponPlacement = couponService.findCouponPlacementbyId(null, reserveCoupon);
        	 if(null == couponPlacement){
        		 throw new ServiceException(ExceptionCode.COUPON_NOT_EXIST.getIndex(),ExceptionCode.COUPON_NOT_EXIST.getErrMsg()); 
        	 }
        	 RealmEntity owner = couponPlacement.getOwner();
        	 String entityId = owner.getEntityId();
             logger.info("红包上的entityId:" + entityId + "用户id：" + userId);
        	 if (StringUtils.isNotBlank(entityId)) {
        		 CouponPackage coupon = couponPlacement.getCouponPackage();
                 logger.info("CouponPackage类型getType()：" + coupon.getType());
                 // 抵现券
    			 if( coupon.getType().equals( CouponType.CASH ) ){
    				couponAmount = BigDecimal.valueOf(coupon.getParValue());
                    logger.info("本次所使用的抵现券金额：" + couponAmount);
    			 }
			 }
		}
        // 预约金额必须小于等于预约最大金额
        if (amount.compareTo(BigDecimal.valueOf(Double.valueOf(reserveAmountMaxLimitConfig.getParameterValue()))) == 1) {
        	throw new ServiceException(ExceptionCode.AMOUNT.getIndex(),ExceptionCode.AMOUNT.getErrMsg());
        }
        // 使用红包后用户支付的金额。
        BigDecimal afterUseCouponUserPayAmount = amount.subtract(couponAmount);
        // 检查预约期限
        if ((waitPeriod < Integer.valueOf(minDeadLineConfig.getParameterValue()))
            || (waitPeriod > Integer.valueOf(maxDeadLineConfig.getParameterValue()))) {
        	throw new ServiceException(ExceptionCode.WAIT_PERIOD.getIndex(),ExceptionCode.WAIT_PERIOD.getErrMsg());
        }
        //检查可预约产品是否存在
        Map<String, LoanTypeDTO> reserveLoanTypeMap = this.mapReserveProductByEnable(CAN_RESERVE);
        String[] productArray = productIds.split(",");
        if (productArray.length == 0) {
        	throw new ServiceException(ExceptionCode.PRODUCT_ARRAY.getIndex(),ExceptionCode.PRODUCT_ARRAY.getErrMsg());
        }
        logger.info("检查可预约产品是否存在结束=============================>>>>>>>>>");
        StringBuffer productNameBuffer = new StringBuffer();
        List<LoanTypeDTO> loanTypeList = new ArrayList<>();
        for (String productId : productArray) {
        	if (!reserveLoanTypeMap.containsKey(productId)) {
        		throw new ServiceException(ExceptionCode.PRODUCT_ARRAY.getIndex(),ExceptionCode.PRODUCT_ARRAY.getErrMsg());
        	}
            productNameBuffer.append(reserveLoanTypeMap.get(productId).getProductName()).append(",");
            loanTypeList.add(reserveLoanTypeMap.get(productId));
        }
        productNameBuffer.deleteCharAt(productNameBuffer.length() - 1);
		//检查可预约投资期限是否存在
        Map<String, ReserveInvestPeriod> investPeriodMap = this.mapReserveInvestPeriodByEnable();
        String[] periodArray = periodIds.split(",");
        if (periodArray.length == 0) {
        	throw new ServiceException(ExceptionCode.PERIOD_ARRAY.getIndex(),ExceptionCode.PERIOD_ARRAY.getErrMsg());
        }
        StringBuffer periodNameBuffer = new StringBuffer();
        List<ReserveInvestPeriod> investPeriodList = new ArrayList<>();
        for (String periodId : periodArray) {
            if (!investPeriodMap.containsKey(periodId)) {
            	throw new ServiceException(ExceptionCode.PERIOD_ARRAY.getIndex(),ExceptionCode.PERIOD_ARRAY.getErrMsg());
            }
            periodNameBuffer.append(investPeriodMap.get(periodId).getInvestPeriodDesc()).append(",");
            investPeriodList.add(investPeriodMap.get(periodId));
        }
        periodNameBuffer.deleteCharAt(periodNameBuffer.length() - 1);
        
        //检查可预约利率区间是否存在
        Map<String, ReserveRate> rateRangeMap = this.mapReserveRateRangeByEnable();
        String[] rateArray = rateIds.split(",");
        if (rateArray.length == 0) {
        	throw new ServiceException(ExceptionCode.RATE_ARRAY.getIndex(),ExceptionCode.RATE_ARRAY.getErrMsg());
        }
        StringBuffer rateRangeNameBuffer = new StringBuffer();
        List<ReserveRate> rateRangeList = new ArrayList<>();
        for (String rateId : rateArray) {
            if (!rateRangeMap.containsKey(rateId)) {
            	throw new ServiceException(ExceptionCode.RATE_ARRAY.getIndex(),ExceptionCode.RATE_ARRAY.getErrMsg());
            }
            rateRangeNameBuffer.append(rateRangeMap.get(rateId).getRateDesc()).append(",");
        }
        rateRangeNameBuffer.deleteCharAt(rateRangeNameBuffer.length() - 1);
        //以下为正常的业务逻辑
        //保存预约申请
		ReserveRequest reserveRequest = new ReserveRequest();
		//保存预约申请
		reserveRequest.setId(UUIDGenerator.generate());
		reserveRequest.setUserId(userId);
		reserveRequest.setStatus(ReserveRequestStatusEnum.FAILED.getIndex());//预约失败
		reserveRequest.setEnableCancelled(ReserveRequestCancelEnum.FALSE.getIndex());//
		reserveRequest.setSubmitTime(new Date());//不可以取消
		reserveRequest.setWaitPeriod(waitPeriod);
		reserveRequest.setExpireTime(DateUtils.offsetDaysByInt(new Date(), waitPeriod));
		String productNames=productNameBuffer.toString();
		reserveRequest.setProductNames(productNames);//	预约产品类型名称
		reserveRequest.setRatePeriods(rateRangeNameBuffer.toString());//利率区间
		reserveRequest.setReserveAmount(amount);
		reserveRequest.setInvestPeriods(periodNameBuffer.toString());//投资期限区间
		reserveRequest.setInvestedAmount(BigDecimal.ZERO);
		reserveRequest.setBalanceAmount(amount);
		reserveRequest.setReserveCoupon(reserveCoupon);
        //保存已预约产品
		List<ReserveAlready> reserveAlreadyList = new ArrayList<ReserveAlready>();
        for (LoanTypeDTO loan : loanTypeList) {
        	ReserveAlready reserveAlready=new ReserveAlready();
        	reserveAlready.setId(UUIDGenerator.generate());
        	reserveAlready.setReserveId(reserveRequest.getId());
        	reserveAlready.setLoanTypeKey(loan.getEnable().toString());
        	reserveAlready.setLoanTypeName(loan.getProductName());
        	reserveAlreadyList.add(reserveAlready);
        }
        List<ReserveAlreadyRate> reserveAlreadyRateList=new ArrayList<ReserveAlreadyRate>();
       //保存已预约利率区间
       for (ReserveRate rateRange : rateRangeList) {
    	   ReserveAlreadyRate reserveAlreadyRate = new ReserveAlreadyRate();
    	   reserveAlreadyRate.setId(UUIDGenerator.generate());
    	   reserveAlreadyRate.setRateId(rateRange.getId());
    	   reserveAlreadyRate.setReserveId(reserveRequest.getId());
    	   reserveAlreadyRate.setMaxRate(rateRange.getMaxRate());
    	   reserveAlreadyRate.setMinRate(rateRange.getMinRate());
    	   reserveAlreadyRate.setRateDesc(rateRange.getRateDesc());
    	   reserveAlreadyRateList.add(reserveAlreadyRate);
        }
       List<ReserveAlreadyDate> reserveDateList=new ArrayList<ReserveAlreadyDate>();
        //保存已预约投资期限
        for (ReserveInvestPeriod investPeriod : investPeriodList) {
        	ReserveAlreadyDate reserveDate = new ReserveAlreadyDate();
        	reserveDate.setId(UUIDGenerator.generate());
        	reserveDate.setReserveDateId(investPeriod.getId());
        	reserveDate.setTermDesc(investPeriod.getInvestPeriodDesc());
        	reserveDate.setMinDate(String.valueOf(investPeriod.getMaxInvestPeriod()));
        	reserveDate.setMaxDate(String.valueOf(investPeriod.getMaxInvestPeriod()));
        	reserveDateList.add(reserveDate);
        }

        Boolean flag = false;
        FreezeResultDto ptOrderNo = null;
        try {
            logger.info("准备保存预约申请=============================>>>>>>>>>");
        	// 1、保存申请单
        	Boolean reserveRequestSave = reserveBridge.reserveRequestSave(reserveRequest, reserveAlreadyList, reserveAlreadyRateList, reserveDateList, couponAmount);
            logger.info("保存预约申请结果：" + reserveRequestSave);
        	if (reserveRequestSave) {
                // 2.冻结金额
        		// 冻结红包
        		String reserveCouponId = reserveRequest.getReserveCoupon();
        		if(reserveCouponId!=null&&!reserveCouponId.equals("")){

                    logger.info("进入冻结红包方法，预约单id:" + reserveRequest.getId());
                    boolean gobalFrozenCouponFlag = couponService.couponPlacementFrozen(reserveRequest.getReserveCoupon(),
                            RealmEntity.of(Realm.RESERVE, reserveRequest.getId()));
        			if ( !gobalFrozenCouponFlag ) {
        				throw new ServiceException(ExceptionCode.USE_COUPON_PLACEMENT.getIndex(),
        						ExceptionCode.USE_COUPON_PLACEMENT.getErrMsg());
        			}
                    logger.info("冻结红包成功，预约单id：" + reserveRequest.getId());
        		}
                logger.info("预约单id:" + reserveRequest.getId() + ", 用户自费金额：" + afterUseCouponUserPayAmount + ", 抵现券金额:" + couponAmount);
        		// 调用本地账户服务的预约接口
        		ptOrderNo = appointmentService.ApplyAppointment(reserveRequest.getId(),	reserveRequest.getUserId(), afterUseCouponUserPayAmount, couponAmount);
        		Boolean isSuccess = ptOrderNo.getIsSuccess();   //是否成功
                logger.info("调用本地账户服务的冻结接口结果：" + isSuccess);
        		if (isSuccess) {
        			String fundOperateId = ptOrderNo.getFundOperateId();
        			reserveRequest.setId(reserveRequest.getId());
        			reserveRequest.setFrozenCode(fundOperateId);//账户系统冻结返回code
        			reserveRequest.setStatus(ReserveRequestStatusEnum.RESERVED.getIndex());//更新状态为预约中。
        			reserveRequest.setEnableCancelled(ReserveRequestCancelEnum.TRUE.getIndex());//预约申请单可以取消。
        			int updateReserveRequest = reserveRequestDao.updateReserveRequest(reserveRequest);
        			// 3.获取结果更新单子状态
        			if(updateReserveRequest!=1){
        				throw new ServiceException(ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getIndex(),ExceptionCode.DEBT_UPDATE_INVEST_FAIL.getErrMsg());	
        			}
        			flag=true;
    			} else {
    				logger.info("调用账户预约接口，账户返回失败，预约单id:" + reserveRequest.getId());
    	    		logger.info("冻结时，FundOperate" + ptOrderNo.getFundOperateId());
    	    		reserveRequest.setFrozenCode(ptOrderNo.getFundOperateId()); //账户系统冻结返回code
    				int updateReserveRequest = reserveRequestDao.updateReserveRequest(reserveRequest);
    				if (updateReserveRequest != 1) {
    					throw new ServiceException(ExceptionCode.LOCA_UPDATE_FAIL.getIndex(),ExceptionCode.LOCA_UPDATE_FAIL.getErrMsg());
    				}
    			}
			} else {
	        	logger.info("保存预约申请单失败！！！");
			}
		} catch (Exception e) {
			logger.error("保存预约申请单进入异常" , e);
			logger.error("保存预约申请单进入异常,reserveRequestId：" + reserveRequest.getId() );
    		reserveRequest.setStatus(ReserveRequestStatusEnum.FAILED.getIndex());//更新状态为预约失败
			int updateReserveRequest = reserveRequestDao.updateReserveRequest(reserveRequest);
			logger.info("更新预约状态 是否成功（1为成功，0为失败）" + updateReserveRequest + "，reserveRequestId:" + reserveRequest.getId());
			if (updateReserveRequest!=1) {
				throw new ServiceException(ExceptionCode.LOCA_UPDATE_FAIL.getIndex(),ExceptionCode.LOCA_UPDATE_FAIL.getErrMsg());
			}

            // 异常时红包需要解冻
            // 红包状态应为冻结状态
            couponPlacement = couponService.findCouponPlacementbyId(null, reserveCoupon);
            if(CouponStatus.FROZEN.equals(couponPlacement.getStatus())){
                boolean unFrozenResult = couponService.couponPlacementUnFrozen(reserveCoupon, RealmEntity.of(Realm.RESERVE, reserveRequest.getId()));
                logger.info("预约失败后，解冻红包结果：" + unFrozenResult);
            }

            // 查账户表
            List<String> frozenCodeList = reserveRequestDao.findFundOperate(reserveRequest.getId());
            // 只有一条记录
            if(null != frozenCodeList && frozenCodeList.size() == 1) {
                String frozencode = frozenCodeList.get(0);
                try {
                    logger.info("申请时失败，调账户取消方法前打印参数，frozenCode" + reserveRequest.getFrozenCode() + ",userId:" + reserveRequest.getUserId() + ",afterUseCouponUserPayAmount:" + afterUseCouponUserPayAmount + "couponAmount:" + couponAmount);
                    FreezeResultDto cancelAppointment = appointmentService.cancelAppointment(
                            frozencode, reserveRequest.getUserId(), afterUseCouponUserPayAmount, couponAmount);
                    String orderNo = cancelAppointment.getOrderNo();
                    logger.info("申请时失败，调账户系统取消返回orderNo:" + orderNo);
                    Boolean isSuccess = cancelAppointment.getIsSuccess();
                    logger.info("申请时失败，账户系统取消返回结果isSuccess：" + isSuccess);
                } catch (Exception ex) {
                    logger.error("预约失败后，调账户取消异常,requestId" + reserveRequest.getId(), e);
                }
            }

		}
		return flag;
	}
	
    /**
     * 可预约利率区间表
     * @return
     */
	private Map<String, ReserveRate> mapReserveRateRangeByEnable() {
		Map<String, ReserveRate> mapReserveRate=new HashMap<String, ReserveRate>();
		List<ReserveRate> findByEnableList = reserveRateDao.findByEnableList();
		for (ReserveRate reserveRate : findByEnableList) {
			mapReserveRate.put(reserveRate.getId(), reserveRate);
		}
		return mapReserveRate;
	}
	
	/**
	 * 获取可预约投资期限
	 * @return
	 */
	
	private Map<String, ReserveInvestPeriod> mapReserveInvestPeriodByEnable() {
		Map<String, ReserveInvestPeriod>  mapReserveInvestPeriod=new HashMap<String, ReserveInvestPeriod>();
		List<ReserveInvestPeriod> findByEnableList = reserveInvestPeriodDao.findByEnableList();
		for (ReserveInvestPeriod reserveInvestPeriod : findByEnableList) {
			mapReserveInvestPeriod.put(reserveInvestPeriod.getId(), reserveInvestPeriod);
		}
		return mapReserveInvestPeriod;
	}
	
	/**
	 * 获取可预约产品列表。
	 * @param typeSwitch
	 * @return
	 */
	private Map<String, LoanTypeDTO> mapReserveProductByEnable(int typeSwitch) {
		Map<String,LoanTypeDTO> mapLoanType=new HashMap<String, LoanTypeDTO>();
		List<LoanTypeDTO> findListByTypeSwitch = loanTypeService.findListByTypeSwitch(typeSwitch);
		for (LoanTypeDTO loanType : findListByTypeSwitch) {
			mapLoanType.put(loanType.getId(), loanType);
		}
		return mapLoanType;
	}



    /**
     * 生成前端显示用的预约信息
     *
     * @param reserveRequest 数据库中的预约信息
     *
     * @return 前端显示用的预约信息
     */
    private ReserveRequestShowDTO convertReserveRequestInfo(ReserveRequest reserveRequest) {
        ReserveRequestShowDTO info = new ReserveRequestShowDTO();
        String tip = "";
        String lastInvestDate = "";
        switch (ReserveRequestStatusEnum.getReserveRequestStatusEnum(reserveRequest.getStatus())) {
            case RESERVED:
                if (reserveRequest.getInvestedAmount().compareTo(BigDecimal.ZERO) == 0) {
                    info.setStatus("RESERVED");
                    tip = "有效期至:" + DateUtils.format(reserveRequest.getExpireTime(), "yyyy-MM-dd");
                    info.setTip(tip);
                    info.setLastInvestDate(lastInvestDate);
                } else {
                    // 部分投资成功
                    info.setStatus("PARTLY_SUCCESSFUL");
                    if (ReserveRequestCancelEnum.TRUE.getIndex() == reserveRequest.getEnableCancelled()) {
                        tip = "剩余部分继续预约中";
                    } else {
                        tip = "剩余的" + getAmountDisplay(reserveRequest.getBalanceAmount()) + "已退回";
                    }
                    info.setTip(tip);
                    info.setLastInvestDate(getLastInvestDate(reserveRequest));
                }
                break;
            case INVESTED:
                info.setStatus("RESERVED");
                tip = "有效期至:" + DateUtils.format(reserveRequest.getExpireTime(), "yyyy-MM-dd");
                info.setTip(tip);
                info.setLastInvestDate(lastInvestDate);
                break;

            // 投资失败时，状态显示为预约中，需显示使用预约功能最后投资成功的时间
            case INVEST_FAILED:
                info.setStatus("RESERVED");
                tip = "有效期至:" + DateUtils.format(reserveRequest.getExpireTime(), "yyyy-MM-dd");
                info.setTip(tip);
                info.setLastInvestDate(getLastInvestDate(reserveRequest));
                break;
            case PARTLY_SUCCESSFUL:
                info.setStatus("PARTLY_SUCCESSFUL");
                tip = "剩余的" + getAmountDisplay(reserveRequest.getBalanceAmount()) + "已退回";
                info.setTip(tip);
                info.setLastInvestDate(getLastInvestDate(reserveRequest));
                break;
            case SUCCESSFUL:
                info.setStatus("SUCCESSFUL");
                info.setTip(tip);
                info.setLastInvestDate(getLastInvestDate(reserveRequest));
                break;
            case CANCELLED:
                info.setStatus("CANCELLED");
                tip = "预约投资金额已退回";
                info.setTip(tip);
                info.setLastInvestDate(lastInvestDate);
                break;
            case FAILED:
                info.setStatus("FAILED");
                tip = "预约投资金额已退回";
                info.setTip(tip);
                info.setLastInvestDate(lastInvestDate);
                break;
            default:
                // nothing
        }
        info.setReserveRequestId(reserveRequest.getId());
        info.setUserId(reserveRequest.getUserId());
        info.setReserveAmount(reserveRequest.getReserveAmount());
        info.setReserveAmountDisplay(getAmountDisplay(reserveRequest.getReserveAmount()));
        info.setInvestedAmount(reserveRequest.getInvestedAmount());
        info.setInvestedAmountDisplay(getAmountDisplay(reserveRequest.getInvestedAmount()));
        info.setBalanceAmount(reserveRequest.getBalanceAmount());
        info.setBalanceAmountDisplay(getAmountDisplay(reserveRequest.getBalanceAmount()));
        info.setEnableCancelled(ReserveRequestCancelEnum.TRUE.getIndex() == reserveRequest.getEnableCancelled() ? true : false );
        info.setSubmitDate(DateUtils.format(reserveRequest.getExpireTime(), "yyyy-MM-dd HH:mm"));
        return info;
    }


	@Override
	public Page<ReserveRequestShowDTO> findReserveRequests(String userId, Integer pageNumber, Integer pageSize)
			throws ServiceException {
        List<ReserveRequestShowDTO> rrDtoList = new ArrayList<ReserveRequestShowDTO>();
        int startRow = (pageNumber - 1) * pageSize;
        List<ReserveRequest> reserveRequests = reserveRequestDao.findReserveRequests(userId, startRow, pageSize);
        Long count = reserveRequestDao.findReserveRequestsCount(userId).longValue();
        if (reserveRequests.isEmpty()) {
            return new Page<ReserveRequestShowDTO>(Lists.newArrayList(), pageNumber, pageSize, count);
        }
        for (ReserveRequest reserveRequest : reserveRequests) {
            rrDtoList.add(convertReserveRequestInfo(reserveRequest));
        }
        Page<ReserveRequestShowDTO> page = new Page<ReserveRequestShowDTO>(rrDtoList, pageNumber, pageSize, count);
        return page;
    }


	private String getLastInvestDate(ReserveRequest reserveRequest) {
		String lastInvestDate = "";
	    //获取最近一次投资成功的时间
	    List<ReserveQueue> reserveQueues = reserveQueueDao.listByRequestIdAndStatus(reserveRequest.getId(),ReserveQueueStatusEnum.SUCCESSED.getIndex());
	    List<String> loanIds = new ArrayList<>();
	    for (ReserveQueue reserveQueue : reserveQueues) {
	        loanIds.add(reserveQueue.getLoanId());
	    }
	    if(loanIds!=null && !loanIds.isEmpty()){
	    	List<InvestDTO> invests = investService.getInvest(reserveRequest.getUserId(), loanIds);
	        if (invests != null && !invests.isEmpty()) {
	            lastInvestDate = DateUtils.format(invests.get(0).getSubmitTime(), "yyyy-MM-dd HH:mm");
	        }
	    }
	    return lastInvestDate;
	}

	private String getAmountDisplay(BigDecimal amount) {
		if (isDecimal(amount)) {
            return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "元";
        }

        int modLeft = amount.intValue() % 10000;
        int value = amount.intValue() / 10000;
        if (modLeft == 0 && value != 0) {
            return value + "万元";
        } else {
            return amount.setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "元";
        }
	}

	private boolean isDecimal(BigDecimal amount) {
		int inputInt = amount.intValue();
        BigDecimal intDecimal = new BigDecimal(inputInt);

        if (amount.compareTo(intDecimal) != 0) {
            return true;
        } else {
            return false;
        }
	}


	@Override
	public Boolean userCancelReserve(String requestId) throws ServiceException {
		logger.info("cancel reserve request params,[requestId={}]", requestId);
        logger.info("交易系统用户预约取消，requestId:" + requestId);
		
		if (StringUtils.isBlank(requestId)) {
			 throw new ServiceException(ExceptionCode.REQUESTID_NOT_EXIST.getIndex(), ExceptionCode.REQUESTID_NOT_EXIST.getErrMsg());
        }
        ReserveRequest reserveRequest = reserveRequestDao.findById(requestId);

        if (reserveRequest == null) {
        	throw new ServiceException(ExceptionCode.RESERVE_REQUEST_NO_NULL.getIndex(),ExceptionCode.RESERVE_REQUEST_NO_NULL.getErrMsg());
        }
        // 如下条件必须放在 reserveRequest不为 null 条件验证后的第一行。请勿移动代码。
        // 存管版发现原来的bug,新加一个目前在投资中的状态，不可以取消。
        if(reserveRequest.getStatus() == ReserveRequestStatusEnum.INVESTED.getIndex()){
            // 该预约单状态为“投资中”暂不可以取消
            throw new ServiceException(ExceptionCode.REQUEST_STATUS_INVESTED.getIndex(),ExceptionCode.REQUEST_STATUS_INVESTED.getErrMsg());
        }

        // 业务规则校验，只有预约中且可以取消预约的申请单才能取消。
        if( ! (reserveRequest.getStatus() == ReserveRequestStatusEnum.RESERVED.getIndex() && reserveRequest.getEnableCancelled() == ReserveRequestCancelEnum.TRUE.getIndex()) ){
            throw new ServiceException(ExceptionCode.RESERVE_REQUEST_CANNOT_CANCEL.getIndex(),ExceptionCode.RESERVE_REQUEST_CANNOT_CANCEL.getErrMsg());
        }

        boolean checkAndUnFreezeIsSuccess = reserveBridge.checkReserveAmountBeforeRelease(reserveRequest);
        if(!checkAndUnFreezeIsSuccess){
            logger.info("用户取消预约申请单调用检查方法 checkReserveAmountBeforeRelease ，检查结果不通过,requestId:" + requestId);
            throw new ServiceException(ExceptionCode.USER_CANCEL_RESERVE_CHECK_ERROR.getIndex(),ExceptionCode.USER_CANCEL_RESERVE_CHECK_ERROR.getErrMsg());
        }

        boolean cancel = reserveBridge.cancelReserve(reserveRequest);

		return cancel;
	}

    @Override
    public BigDecimal reserveAmountByUserId(String userId) throws ServiceException {
        if(StringUtils.isBlank(userId)){
            throw new ServiceException(ExceptionCode.GLOBAL_PARAM_ERROR.getIndex(),ExceptionCode.GLOBAL_PARAM_ERROR.getErrMsg());
        }
        List<Integer> requestStatusList = new ArrayList<Integer>();
        requestStatusList.add(ReserveRequestStatusEnum.RESERVED.getIndex());
        requestStatusList.add(ReserveRequestStatusEnum.INVESTED.getIndex());
        BigDecimal result = reserveRequestDao.reserveAmountByUserId(userId, requestStatusList);

        return result == null ? BigDecimal.ZERO : result;
    }

}

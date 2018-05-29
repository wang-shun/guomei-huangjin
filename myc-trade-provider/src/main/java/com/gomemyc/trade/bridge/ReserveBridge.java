package com.gomemyc.trade.bridge;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.FreezeRecordDTO;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.AppointmentService;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponBucket;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.model.enums.Realm;
import com.gomemyc.model.misc.RealmEntity;
import com.gomemyc.model.user.User;
import com.gomemyc.sms.SMSType;
import com.gomemyc.trade.dao.*;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.ReserveAlready;
import com.gomemyc.trade.entity.ReserveAlreadyDate;
import com.gomemyc.trade.entity.ReserveAlreadyRate;
import com.gomemyc.trade.entity.ReserveQueue;
import com.gomemyc.trade.entity.ReserveQueueDetail;
import com.gomemyc.trade.entity.ReserveRequest;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestSource;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.enums.ReserveQueueStatusEnum;
import com.gomemyc.trade.enums.ReserveRequestStatusEnum;
import com.gomemyc.trade.enums.*;
import com.gomemyc.trade.util.DateUtils;
import com.gomemyc.trade.util.JsonHelper;
import com.gomemyc.trade.util.LoanDaysUtil;
import com.gomemyc.user.api.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 预约投资事务桥接
 *
 * @author 何健
 */
@Component
public class ReserveBridge {

    /** 日志 */
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 1.申请单DAO  */
    @Autowired
    private ReserveRequestDao reserveRequestDao;

    /** 2.预约队列DAO */
    @Autowired
    private ReserveQueueDao reserveQueueDao;

    /** 3.预约队列明细 */
    @Autowired
    private ReserveQueueDetailDao reserveQueueDetailDao;

    /** 4.订单DAO  */
    @Autowired
    private InvestDao investDao;

    /** 5.产品相关 */
    @Reference
    private ProductRegularService productRegularService;

    /** 6.红包服务 */
    @Reference
    private CouponService couponService;

    /** 7.MQ发送者  */
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;

    /** 8、账户相关 */
    @Reference
    private AccountService accountService;

    /** 9、账户预约相关 */
    @Reference
    private AppointmentService appointmentService;

    /** 10.原始产品DAO  */
    @Autowired
    private OriginProductDao originProductDao;

    /** 11.用户  */
    @Reference
    private UserService userService;

    @Autowired
    private ReserveAlreadyDao reserveAlreadyDao;

    /** 预约利率相关*/
    @Autowired
    private ReserveAlreadyRateDao reserveAlreadyRateDao;

    /** 预约投资期限相关*/
    @Autowired
    private ReserveDateDao reserveDateDao;

    @Autowired
    private ReserveAlreadyDateDao reserveAlreadyDateDao;

    /**
     * 持久化投资队列、预约队列明细。
     *
     * @param requestIdList 预约队列ID集合
     * @param reserveQueue 投资队列
     * @return   10000：成功
     *            10002：所选预约单有误，请重新选择
     * @throws ServiceException
     *            1000, "插入队列数据失败"
     *            1002, "插入预约队列明细数据失败"
     *            1003, "更新申请单为“投资中”失败"
     *            1004, "更新队列状态为“待录入产品”失败"
     *
     */
    @Transactional
    public Integer createQueueAndDetail(List<String> requestIdList, ReserveQueue reserveQueue) throws ServiceException{
        try {
            // 判断预约申请单能否加到预约投资队列表。
            reserveQueue.setId(UUIDGenerator.generate());
            int insertCount = reserveQueueDao.insert(reserveQueue);
            if (1 != insertCount) {
                throw new ServiceException(1000, "插入队列数据失败");
            }
            String reserveQueueId = reserveQueue.getId();
            for (String requestId : requestIdList) {
                ReserveRequest reserveRequest = reserveRequestDao.findById(requestId);
                ReserveQueueDetail reserveQueueDetail = new ReserveQueueDetail();
                reserveQueueDetail.setId(UUIDGenerator.generate());
                // 投资队列id
                reserveQueueDetail.setInvestQueueId(reserveQueueId);
                // 预约申请id
                reserveQueueDetail.setReserveApplyId(requestId);
                // 撮合资金
                reserveQueueDetail.setMatchingFunds(reserveRequest.getBalanceAmount());
                // 预约产品名称
                reserveQueueDetail.setLoanTypeName(reserveRequest.getProductNames());
                reserveQueueDetail.setRateDate(reserveRequest.getRatePeriods());
                reserveQueueDetail.setInvestmentInterval(reserveRequest.getInvestPeriods());
                insertCount = reserveQueueDetailDao.insert(reserveQueueDetail);
                if (1 != insertCount) {
                    throw new ServiceException(1002, "插入预约队列明细数据失败");
                }
                // 更新预约单状态
                ReserveRequest request = new ReserveRequest();
                request.setId(reserveRequest.getId());
                // 申请单设置为“投资中”
                request.setStatus(ReserveRequestStatusEnum.INVESTED.getIndex());
                // 设置为 false(0)，不可以取消。
                request.setEnableCancelled(0);
                int updateCount = reserveRequestDao.updateStatusAndCancelled(request);
                if (1 != updateCount) {
                    throw new ServiceException(1003, "更新申请单为“投资中”失败");
                }
            }

            // 修改状态为“待录入产品”
            Map<String,Object> params = new HashMap<String, Object>();
            params.put("id", reserveQueue.getId());
            params.put("status", ReserveQueueStatusEnum.UNENTERED.getIndex());
            int updateQueueCount = reserveQueueDao.updateStatusById(params);
            if (1 != updateQueueCount) {
                throw new ServiceException(1004, "更新队列状态为“待录入产品”失败");
            }
        } catch (Exception e){
            throw new ServiceException(1005, "创建队列和队列明细失败");
        }

        // 成功
        return 10000;
    }

    /**
     * 判断预约申请单能否加到预约投资队列表。
     *
     * @param requestIdList 勾选的预约单ID集合
     * @return boolean 入参如果可加入同一个队列返回 true，否则返回 false。
     */
    public Boolean isValidToQueue(List<String> requestIdList) {
        // 所选择的预约申请单可加入投资队列。
        Boolean canAddToQueue = true;
        // 遍历预约申请单id集合
        for (String requestId : requestIdList) {
            // 需要确认requestId，是否是新表中的 reserve_apply_id
            List<ReserveQueueDetail> reserveQueueDetails = reserveQueueDetailDao.findListByApplyId(requestId);
            logger.debug("ReserveBridge isValidToQueue:判断提交的预约申请单能否加进队列中 requestId {}, resultSize {},"+requestId,reserveQueueDetails.size());
            // 如果在其他队列
            if ( null != reserveQueueDetails && reserveQueueDetails.size() != 0 ) {
                // 查询预约申请单
                ReserveRequest reserveRequest = reserveRequestDao.findById(requestId);
                // 如果单个申请单是部分申请成功，可以加入队列，否则不可以加入队列。 -- 该步骤和郭鸿志确认
                if ( ReserveRequestStatusEnum.PARTLY_SUCCESSFUL.equals(reserveRequest.getStatus()) ) {
                    canAddToQueue = true;
                } else {
                    canAddToQueue = false;
                    break;
                }
            }
        }

        return canAddToQueue;
    }


    /**
     * 检查状态为预约中的预约申请单并解冻预约资金，目前给定时任务用，取消队列用
     * 原系统中该方法给定时任务用时，方法返回void。后台取消队列，方法体和改方法内容一致，为了合并通用，这里将返回值改为boolean。
     *
     * 方法作用：解冻资金、解冻红包，设置申请单状态。
     *
     * @param reserveRequest 预约申请单
     */
    public synchronized Boolean checkReserveRequest(ReserveRequest reserveRequest) throws RuntimeException{
        // 解冻预约投资金额前检查数据一致性，确保解冻操作没有问题
        if (!checkReserveAmountBeforeRelease(reserveRequest)) {
            return false;
        }
        String userId = reserveRequest.getUserId();
        String reserveRequestId = reserveRequest.getId();
        // 红包金额，账户服务用
        BigDecimal couponCashValue = BigDecimal.ZERO;
        if(StringUtils.isNotBlank(reserveRequest.getReserveCoupon())) {
            CouponPlacement coupon = couponService.findCouponPlacementbyId("", reserveRequest.getReserveCoupon());
            if (null == coupon) {
                throw new ServiceException(1002, "红包不存在");
            }
            if (coupon.getStatus() == CouponStatus.FROZEN) {
                // 抵现券类型
                if (null != coupon.getCouponPackage() && coupon.getCouponPackage().getType() == CouponType.CASH) {
                    // 获得红包金额
                    couponCashValue = BigDecimal.valueOf(coupon.getCouponPackage().getParValue());
                }
            }
        }
        BigDecimal balanceAmount = reserveRequest.getBalanceAmount() == null ? BigDecimal.ZERO : reserveRequest.getBalanceAmount();
        BigDecimal afterUseCoupon = balanceAmount.subtract(couponCashValue);
        logger.info("交易服务checkReserveRequest调用账户取消预约方法【前】打印参数：requestId: " + reserveRequest.getId()  + ",frozenCode:" + reserveRequest.getFrozenCode() + ",userId:" + userId + ",afterUseCoupon:" + afterUseCoupon + ",couponCashValue" + couponCashValue);
        // 解冻资金并记录资金解冻操作 （账户系统提供）
        FreezeResultDto freezeResultDto = appointmentService.cancelAppointment(reserveRequest.getFrozenCode(), userId, afterUseCoupon, couponCashValue);
        // 解冻成功
        int updateRequestCount = 0;
        if ( freezeResultDto.getIsSuccess() ) {

            logger.info("交易服务checkReserveRequest调用账户取消预约方法成功：requestId:" + reserveRequest.getId() );
            // 解冻成功返回true,否则返回false
            return true;
        } else {
            logger.info("交易服务checkReserveRequest调用账户取消预约方法成功失败：requestId:" + reserveRequest.getId());
            logger.error("reserve amount release fail, [reserveRequest={}]", reserveRequest);
            return false;
        }

        // 更新成功返回true，否则返回false。
//        return updateRequestCount == 1 ? true : false;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean doUpdateReserveRequest (ReserveRequest reserveRequest) throws RuntimeException{
        // 资金解冻成功后，更改预约申请单状态
        boolean markCouponStatusResult = false;
        int updateRequestCount = 0;
        String couponPlacementId = reserveRequest.getReserveCoupon();
        if (reserveRequest.getInvestedAmount().compareTo(BigDecimal.ZERO) == 0) {
            // 没有投资时，置为预约失败，并且不能取消该预约
            ReserveRequest updateRequest = new ReserveRequest();
            updateRequest.setId(reserveRequest.getId());
            updateRequest.setStatus(ReserveRequestStatusEnum.FAILED.getIndex());
            updateRequest.setEnableCancelled(ReserveRequestCancelEnum.FALSE.getIndex());
            updateRequestCount = reserveRequestDao.updateStatusAndCancelled(updateRequest);
            // 判断此次预约如果有奖券则返还奖券
            if (StringUtils.isNotBlank(couponPlacementId)) {
                CouponPlacement couponPlacement = couponService.findCouponPlacementbyId("", couponPlacementId);
                // 红包状态应为冻结状态
                if(CouponStatus.FROZEN.equals(couponPlacement.getStatus())){
                    markCouponStatusResult = couponService.couponPlacementUnFrozen(couponPlacementId, RealmEntity.of(Realm.RESERVE, reserveRequest.getId()));
                }
            } else {
                markCouponStatusResult = true;
            }
        } else {
            // 有投资时，置为部分投资成功，并且不能取消该预约
            ReserveRequest updateRequest = new ReserveRequest();
            updateRequest.setId(reserveRequest.getId());
            updateRequest.setStatus(ReserveRequestStatusEnum.PARTLY_SUCCESSFUL.getIndex());
            updateRequest.setEnableCancelled(ReserveRequestCancelEnum.FALSE.getIndex());
            updateRequestCount = reserveRequestDao.updateStatusAndCancelled(updateRequest);
        }

        if (1 != updateRequestCount) {
            logger.error("reserve amount release success, but mark reserveRequestStatus fail.[reserveRequest={}]", reserveRequest);
        }else if (!markCouponStatusResult) {
            logger.error("reserve amount release success, but mark markCouponStatus fail.[couponPlacementId={}]", reserveRequest.getReserveCoupon());
        } else {
            // 记录预约申请取消信息（系统自动取消）
            int cancelMessageFlag = reserveRequestDao.updateCancelTypeAndCancelTime(reserveRequest.getId(), ReserveCancelType.AUTO_CANCELED.getIndex(), new Date());
            if(1 != cancelMessageFlag){
                logger.error("reserve amount release success, but updateCancelTypeAndCancelTime fail.[reserveRequest={}]", reserveRequest);
                throw new RuntimeException("更新申请单状态为自动取消失败");
            }
        }

        return 1 == updateRequestCount ? true : false;
    }

    /**
     * 解冻预约投资金额前检查数据一致性，确保解冻操作没有问题。
     * 备注：该方法只做检查，不做任何数据操作。
     *
     * @param reserveRequest 预约申请单
     *
     * @return 可以解冻：true 不能解冻：false
     */
    public boolean checkReserveAmountBeforeRelease(ReserveRequest reserveRequest){
        BigDecimal balanceAmount = reserveRequest.getBalanceAmount();
        String userId = reserveRequest.getUserId();
        String reserveRequestId = reserveRequest.getId();
        // 获得账户
        AccountDTO accountDTO = accountService.getByUserId(userId);
        // 判断冻结金额是否大于预约剩余金额(账户系统提供)
        if (balanceAmount.compareTo(accountDTO.getFreezeAmount()) == 1) {
            logger.error("reserve request balance amount more than frozen amount, [reserveRequest={}]", reserveRequest);
            return false;
        }

        // 判断是否有预约冻结记录（账户系统提供）
        String frozenCode = reserveRequest.getFrozenCode();
        if (StringUtils.isBlank(frozenCode)){
            logger.error("fail to find reserve freeze record,[userId:{},reserveRequestId:{}]",
                    userId, reserveRequestId);
            return false;
        }

        return true;
    }

    /**
     * 单笔队列明细完成投资动作，受事务控制。
     *
     * @param userId 引用问题，用户参数暂时略去
     * @param productRegularDTO   产品信息
     * @param reserveQueueDetail
     */
    @Transactional
    public String tenderOneDetail(String userId, ProductRegularDTO productRegularDTO, ReserveQueueDetail reserveQueueDetail, ReserveRequest reserveRequestCoupon) throws ServiceException{
        String mqString = "";
        // 和辉哥确认，不用做"不允许企业用户、借款用户投标；不允许用户重复投标"校验。
        // 撮合资金
        BigDecimal investAmount = reserveQueueDetail.getMatchingFunds();
        String investId = UUIDGenerator.generate();
        // 构建投资记录并入库。
        Invest invest = new Invest();
        invest.setId(investId);
        User user = userService.findByUserId(userId);
        invest.setMobile(user.getMobile());
        invest.setName(user.getName());
        // 产品类型键值
        invest.setLoanTypeKey(productRegularDTO.getTypeKey());
        // 产品类型id
        invest.setLoanTypeId(productRegularDTO.getTypeId());
        // 用户id
        invest.setUserId(userId);
        // 标的id
        invest.setLoanId(productRegularDTO.getLoanId());
        // 产品id
        invest.setProductId(productRegularDTO.getId());
        // 投资金额
        invest.setAmount(investAmount);
        // 利率
        invest.setRate(productRegularDTO.getRate());
        // 加息利率
        invest.setPlusRate(productRegularDTO.getPlusRate());
        // 来源
        invest.setSource(InvestSource.RESERVE);
        // 年月日
        invest.setYears(productRegularDTO.getYears());
        invest.setMonths(productRegularDTO.getMonths());
        invest.setDays(productRegularDTO.getDays());
        // 还款方式
        invest.setRepaymentMethod(productRegularDTO.getMethod());
        // 状态，因为预约成功时已经冻结成功，所以入库时状态值为 LOCAL_FROZEN_SUCCESS(1, "本地资金冻结成功（待北京解冻，北京银行解冻中）")
        invest.setStatus(InvestStatus.LOCAL_FROZEN_SUCCESS);
        Date submitTime = new Date();
        // 提交时间
        invest.setSubmitTime(submitTime);
        // 使用的红包id
        if(null != reserveRequestCoupon && StringUtils.isNotBlank(reserveRequestCoupon.getReserveCoupon())){
            invest.setCouponPlacememtId(reserveRequestCoupon.getReserveCoupon());
        }

        // 投资记录入库
        int insertCount = investDao.save(invest);
        if (1 != insertCount) {
            throw new ServiceException(ExceptionCode.SAVE_INVEST_FAIL.getIndex(), ExceptionCode.SAVE_INVEST_FAIL.getErrMsg());
        }

        // 投资金额大于预约剩余投资额报错
        String reserveRequestId = reserveQueueDetail.getReserveApplyId();
        ReserveRequest reserveRequest = reserveRequestDao.findById(reserveRequestId);
        if(investAmount.compareTo(reserveRequest.getBalanceAmount()) == 1){
            throw new ServiceException(ExceptionCode.INVALID_INVEST_AMT.getIndex(), ExceptionCode.INVALID_INVEST_AMT.getErrMsg());
        }

        // 判断是否有预约冻结记录（账户系统提供）
        String frozenCode = reserveRequest.getFrozenCode();
        if (StringUtils.isBlank(frozenCode)){
            logger.error("fail to find reserve freeze record,[userId:{},reserveRequestId:{}]",
                    userId, reserveRequestId);
            throw new ServiceException(ExceptionCode.NOT_FOUND_FROZEN.getIndex(), ExceptionCode.NOT_FOUND_FROZEN.getErrMsg());
        }
        // 查询账户系统冻结记录
        List<FreezeRecordDTO> freezeRecordDTOList = appointmentService.findFreezeRecord(frozenCode);
        if(null != freezeRecordDTOList){
            BigDecimal frozenAmt = BigDecimal.ZERO;
            BigDecimal unFrozenAmt = BigDecimal.ZERO;
            for(FreezeRecordDTO freezeRecordDTO : freezeRecordDTOList){
                // 冻结记录的总金额
                if(freezeRecordDTO.getFreezeType() == 1){
                    frozenAmt = frozenAmt.add(freezeRecordDTO.getAmount());
                } else if(freezeRecordDTO.getFreezeType() == 2){
                    // 解冻记录总金额
                    unFrozenAmt = unFrozenAmt.add(freezeRecordDTO.getAmount());
                }
            }
            // 冻结金额比解冻金额少，数据异常
            if(frozenAmt.compareTo(unFrozenAmt) == -1){
                throw new ServiceException(ExceptionCode.INVALID_ACCOUNT_DATA.getIndex(), ExceptionCode.INVALID_ACCOUNT_DATA.getErrMsg());
            }
        } else {
            throw new ServiceException(ExceptionCode.NOT_FOUND_FROZEN.getIndex(), ExceptionCode.NOT_FOUND_FROZEN.getErrMsg());
        }

        // 判断冻结金额（账户系统提供）
        AccountDTO accountDTO = accountService.getByUserId(userId);
        BigDecimal frozenAmount = accountDTO.getFreezeAmount();
        if(investAmount.compareTo(frozenAmount) == 1){
            logger.error("invest amount more than frozen amount,[reserveQueueDetail:{},frozenAmount:{}]",
                    reserveQueueDetail, frozenAmount);
            throw new ServiceException(ExceptionCode.INVALID_INVEST_AMT.getIndex(), ExceptionCode.INVALID_INVEST_AMT.getErrMsg());
        }

        // 修改产品金额
        ProductRegularDTO regularDTO = productRegularService.findByTitle(productRegularDTO.getTitle());
        // 产品实际已投金额，产品申购数
        BigDecimal regularInvestAmount = regularDTO.getInvestAmount() == null ? BigDecimal.ZERO : regularDTO.getInvestAmount();

        // 本地dao 更新产品状态，保证事务
        int updateRegularCount = productRegularService.updateInvestById(regularInvestAmount.add(investAmount), regularDTO.getInvestNum() + 1,
                regularDTO.getId());
        if(1 != updateRegularCount){
            throw new ServiceException(ExceptionCode.UPDATE_REGULAR_INVEST_AMOUNT_COUNT_FAIL.getIndex(), ExceptionCode.UPDATE_REGULAR_INVEST_AMOUNT_COUNT_FAIL.getErrMsg());
        }

        // 修改用户预约已投资额和剩余可投资额
        reserveRequest.setInvestedAmount(reserveRequest.getInvestedAmount().add(investAmount));
        reserveRequest.setBalanceAmount(reserveRequest.getBalanceAmount().subtract(investAmount));
        int updateCount = reserveRequestDao.updateAmoutById(reserveRequest);
        if(1 != updateCount){
            throw new ServiceException(ExceptionCode.UPDATE_REQUEST_INVESTAMT_BALANCEAMT_FAIL.getIndex(), ExceptionCode.UPDATE_REQUEST_INVESTAMT_BALANCEAMT_FAIL.getErrMsg());
        }

        // 更新预约投资队列明细表中的投资ID字段
        reserveQueueDetail.setInvestId(investId);
        reserveQueueDetailDao.updateBySelective(reserveQueueDetail);

        // 查询下红包是否被使用，如果没使用把金额查出来，用于解冻。
        String placementId = reserveRequest.getReserveCoupon();
        // 抵现券金额
        BigDecimal couponCashValue = BigDecimal.ZERO;
        // 加息
        BigDecimal interestRate = BigDecimal.ZERO;

        if(StringUtils.isNotBlank(placementId)) {
            CouponPlacement coupon = couponService.findCouponPlacementbyId("", placementId);
            if (null == coupon) {
                throw new ServiceException(ExceptionCode.COUPON_NOT_FOUND.getIndex(), ExceptionCode.COUPON_NOT_FOUND.getErrMsg());
            }
            if (coupon.getStatus() == CouponStatus.FROZEN) {
                // 抵现券类型
                if (null != coupon.getCouponPackage() && coupon.getCouponPackage().getType() == CouponType.CASH) {
                    // 获得红包金额
                    couponCashValue = BigDecimal.valueOf(coupon.getCouponPackage().getParValue());
                }
                // 加息券，北京银行要求：加息券加息 eg:1% 传 0.01
                if (null != coupon.getCouponPackage() && coupon.getCouponPackage().getType() == CouponType.INTEREST) {
                    // 获得加息利率
                    interestRate = BigDecimal.valueOf(coupon.getCouponPackage().getParValue());

                    if(interestRate.compareTo(BigDecimal.ZERO) == 1){
                        // 加息券利率折算公式：最终利率 = 加息券利率（万分位表示）/ 标天数 * 加息券天数 / 10000
                        // 北京银行：  加息 eg:1% 传 0.01
                        int totalDays = LoanDaysUtil.getTotalDays(productRegularDTO);
                        interestRate = interestRate.divide(new BigDecimal(totalDays), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(coupon.getCouponPackage().getRateDays()));
                        interestRate = interestRate.divide(new BigDecimal(10000), 4, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
            }
            // 把红包状态置为使用（只能使用加息券、抵现券）
            if (coupon.getCouponPackage().getType() == CouponType.CASH || coupon.getCouponPackage().getType() == CouponType.INTEREST) {
                // 置为已使用，参数传：业务类型INVEST(投资)，investId, couponId
                boolean isSuccess = couponService.useCouponPlacementById(placementId, RealmEntity.of(Realm.RESERVE, invest.getId()),
                                                                         investAmount, productRegularDTO.getId(),
                                                                         productRegularDTO.getTotalDays(),
                                                                         productRegularDTO.getTypeId());
                if (!isSuccess) {
                    throw new ServiceException(ExceptionCode.USE_COUPON_FAIL.getIndex(), ExceptionCode.USE_COUPON_FAIL.getErrMsg());
                }
            }
        }

        // 生成投资分享红包
        CouponBucket investCouponBucket = couponService.investCouponBucket(investId, userId);
        logger.info("invest couponBucket info,[investCouponBucket={}]", investCouponBucket);
        // 构建单笔投资MQ消息
        Map<String, Object> mqMap = new HashMap<String, Object>();
        // 用户id
        mqMap.put("userId", userId);
        // 队列id
        mqMap.put("queueId", reserveQueueDetail.getInvestQueueId());
        // 抵现券金额
        mqMap.put("couponCashValue", couponCashValue == null ? "" : couponCashValue + "");
        // 加息券加息 eg:1% 传 0.01
        mqMap.put("interestRate", interestRate == null ? "" : interestRate + "");
        // 投资单金额（撮合资金）
        mqMap.put("investAmount", investAmount == null ? "" : investAmount + "");
        // 投资订单id
        mqMap.put("investId", investId);
        // 调账户冻结是返回的操作流水号
        mqMap.put("applyFundOperateId", frozenCode);
        // 标id
        mqMap.put("prodId", productRegularDTO.getLoanId());
        // 产品id
        mqMap.put("productId", productRegularDTO.getId());


        try {
            mqString = JsonHelper.getJson(mqMap);
            /*logger.info("要发送预约投资的mq消息：" + JsonHelper.getJson(mqMap));
            // 通知消费端做 1、解冻北京；2、同步投资（同步）北京（转账）；3、解冻本地转账（本地）；4、更新状态
            Message message = new Message(MQTopic.MYC_RESERVE_INVEST.getValue(), JsonHelper.getJson(mqMap).getBytes());
            SendResult sendResult = mqProducer.send(message);
            logger.info("发送预约消费MQ结果：" + sendResult);*/
        } catch (JsonProcessingException e) {
            logger.error("预约投资发送MQ消息转换 JSON 时出错{}", e);
        } catch (Exception e) {
            throw new ServiceException(ExceptionCode.SEND_INVEST_MQ_FAIL.getIndex(), ExceptionCode.SEND_INVEST_MQ_FAIL.getErrMsg());
        }

        // 投资成功后，统计推广收益及发送短信提示
        // 这里发两个mq,一个mq负责短信，另外一个mq负责处理三件事情(见job)
        try {
            Map<String, Object> fsMap = new HashMap<String, Object>();
            fsMap.put("userId", userId);
            logger.info("要发送预约投资【附属任务】的mq消息：" + JsonHelper.getJson(fsMap));
            Message message = new Message(MQTopic.AFTER_RESERVE_INVEST.getValue(), JsonHelper.getJson(mqMap).getBytes());
//            Message message = new Message(MQTopic.MYC_RESERVE_INVEST.getValue(), JsonHelper.getJson(mqMap).getBytes());
            SendResult sendResult = mqProducer.send(message);
            logger.info("发送预约【附属任务】MQ结果：" + sendResult);
        } catch (JsonProcessingException e) {
            logger.error("预约投资附属任务转换 JSON 时出错{}", e);
        } catch (Exception e) {
            throw new ServiceException(ExceptionCode.SEND_INVEST_MQ_FAIL.getIndex(), ExceptionCode.SEND_INVEST_MQ_FAIL.getErrMsg());
        }

        try {
            Map<String, Object> smsMap = new HashMap<String, Object>();
            smsMap.put("templateId", SMSType.NOTIFICATION_CREDITMARKET_INVEST.getTemplateId());
            smsMap.put("mobiles", user.getMobile());
            List<String> params = new ArrayList<String>();
            params.add(DateUtils.formatDate(submitTime, DateUtils.dateTimeFormatter));
            params.add(investAmount.add(couponCashValue).setScale(2, BigDecimal.ROUND_DOWN) + "");
            params.add(productRegularDTO.getTitle());
            smsMap.put("params", params);
            // eg:【国美金融】尊敬的美易理财用户，您于2017年03月21日 14:02:30在美易理财购买了20.00元的理财117产品，已成功支付，感谢您对国美的信任。
            // Templete: 尊敬的美易理财用户，您于{0}在美易理财购买了{1}元的{2}产品，已成功支付，感谢您对国美的信任。
            // 数据库编辑，template 10.143.81.27 message库，表sms_template,用大括号{0}{1}{2}，表示参数，下标从0开始。
            Message message = new Message(MQTopic.SMS_SEND.getValue(),  JsonHelper.getJson(smsMap).getBytes());
            SendResult sendResult = mqProducer.send(message);
            System.out.println(sendResult);
        } catch (Exception e){
            // 短信发失败，不做任何处理，只记日志
            logger.error("预约投资发送短信MQ失败，投资id:" + investId, e);
        }

        return mqString;

    }

    /**
     * 投资后设置队列、申请单状态
     *
     * @param queueId 队列id
     * @param isSuccess 结标成功
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean settleReserve(String queueId, Boolean isSuccess) throws RuntimeException{
        // 如果结标成功
        if (isSuccess) {
            // 更新队列状态
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", queueId);
            map.put("status", ReserveQueueStatusEnum.SUCCESSED.getIndex());
            int updateQueueCount = reserveQueueDao.updateStatusById(map);
            if (1 != updateQueueCount) {
                logger.error("update reserveQueue success fail!,[queueId={}]", queueId);
                throw new ServiceException(ExceptionCode.UPDATE_QUEUE_SUCCESSFUL_FAIL.getIndex(), ExceptionCode.UPDATE_QUEUE_SUCCESSFUL_FAIL.getErrMsg());
            }
            // 更新预约申请单状态
            List<ReserveQueueDetail> queueDetails = reserveQueueDetailDao.findListByQueueId(queueId);
            for (ReserveQueueDetail detail : queueDetails) {
                ReserveRequest reserveRequest = reserveRequestDao.findById(detail.getReserveApplyId());
                int updateRequestCount = 0;
                // 全部投资成功
                if (reserveRequest.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0) {
                    ReserveRequest needUpdateObj = new ReserveRequest();
                    needUpdateObj.setId(reserveRequest.getId());
                    needUpdateObj.setStatus(ReserveRequestStatusEnum.SUCCESSFUL.getIndex());
                    needUpdateObj.setEnableCancelled(ReserveRequestCancelEnum.FALSE.getIndex());
                    updateRequestCount = reserveRequestDao.updateStatusAndCancelled(needUpdateObj);
                } else if (reserveRequest.getBalanceAmount().compareTo(BigDecimal.ZERO) == 1) {
                    // 部分投资成功, 状态还是预约中，可取消该预约
                    ReserveRequest needUpdateObj = new ReserveRequest();
                    needUpdateObj.setId(reserveRequest.getId());
                    needUpdateObj.setStatus(ReserveRequestStatusEnum.RESERVED.getIndex());
                    needUpdateObj.setEnableCancelled(ReserveRequestCancelEnum.TRUE.getIndex());
                    updateRequestCount = reserveRequestDao.updateStatusAndCancelled(needUpdateObj);
                }
                if (1 != updateRequestCount) {
                    logger.error("update reserveRequest balance amount fail!,[reserveRequest={}]", reserveRequest);
                    throw new ServiceException(ExceptionCode.UPDATE_REQUEST_STATUS_FAIL.getIndex(), ExceptionCode.UPDATE_REQUEST_STATUS_FAIL.getErrMsg());
                }

            }
        } else {
            // 更新队列状态
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", queueId);
            map.put("status", ReserveQueueStatusEnum.FAILED.getIndex());
            int updateQueueCount = reserveQueueDao.updateStatusById(map);
            if (1 != updateQueueCount) {
                logger.error("update reserveQueue success fail!,[queueId={}]", queueId);
                throw new ServiceException(ExceptionCode.UPDATE_QUEUE_FAILED_FAIL.getIndex(), ExceptionCode.UPDATE_QUEUE_FAILED_FAIL.getErrMsg());
            }

            // 更新预约申请单状态
            List<ReserveQueueDetail> queueDetails = reserveQueueDetailDao.findListByQueueId(queueId);
            for (ReserveQueueDetail detail : queueDetails) {
                ReserveRequest reserveRequest = reserveRequestDao.findById(detail.getReserveApplyId());
                ReserveRequest needUpdateObj = new ReserveRequest();
                needUpdateObj.setId(reserveRequest.getId());
                needUpdateObj.setStatus(ReserveRequestStatusEnum.INVEST_FAILED.getIndex());
                needUpdateObj.setEnableCancelled(ReserveRequestCancelEnum.TRUE.getIndex());
                int updateRequestCount = reserveRequestDao.updateStatusAndCancelled(needUpdateObj);
                if (1 != updateRequestCount) {
                    logger.error("update reserveRequest balance amount fail!,[reserveRequest={}]", reserveRequest);
                    throw new ServiceException(ExceptionCode.UPDATE_REQUEST_STATUS_FAIL.getIndex(), ExceptionCode.UPDATE_REQUEST_STATUS_FAIL.getErrMsg());
                }
            }
        }

        // 无论结标是否成功，只要投资成功，都要更新原始产品状态为已使用
        int updateCount = originProductDao.updateStatusByQueueId(OriginProductStatusEnum.USED.getIndex(), queueId);
        if (1 != updateCount) {
            logger.error("update reserveOriginProduct status fail!,[queueId={}]", queueId);
            throw new ServiceException(ExceptionCode.UPDATE_ORIGIN_PRODUCT_STATUS_USED_FAIL.getIndex(), ExceptionCode.UPDATE_ORIGIN_PRODUCT_STATUS_USED_FAIL.getErrMsg());
        }

        // 更新定期产品状态为满标，2分钟一次任务，会将满标做结标处理。
        ReserveQueue queue = reserveQueueDao.findById(queueId);
        logger.info("查询到的队列中产品productId:" + queue.getProductId());
        int updateRegularCount = reserveRequestDao.updateRegularStatus(queue.getProductId(), ProductStatus.FINISHED.getIndex());
        if(1 != updateRegularCount){
            logger.info("更新产品状态为满标失败，productId:" + queue.getProductId());
            throw new ServiceException(ExceptionCode.UPDATE_REGULAR_STATUS_FINISH_FAIL.getIndex(), ExceptionCode.UPDATE_REGULAR_STATUS_FINISH_FAIL.getErrMsg());
        }



        return true;

    }
	
    /**
     * 保存预约申请单。
     * @param reserveRequest 预约申请单相关
     * @param reserveAlreadyList  预约产品相关
     * @param reserveAlreadyRateList 已预约利率相关
     * @param reserveDateList 已预约期限相关
     * @param packetAmount
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
	public Boolean reserveRequestSave(ReserveRequest reserveRequest,
									List<ReserveAlready> reserveAlreadyList,
									List<ReserveAlreadyRate> reserveAlreadyRateList,
									List<ReserveAlreadyDate> reserveDateList,BigDecimal packetAmount) throws Exception{

    	int  i=reserveRequestDao.saveReserveRequest(reserveRequest);
    	if (i!=1) {
			throw new ServiceException(ExceptionCode.RESERVE_REQUEST_SAVE.getIndex(),ExceptionCode.RESERVE_REQUEST_SAVE.getErrMsg());
		}
		for (ReserveAlready reserveAlready : reserveAlreadyList) {
			int i1=reserveAlreadyDao.save(reserveAlready);
			if (i1!=1) {
				throw new ServiceException(ExceptionCode.RESERVE_ALREADY_SAVE.getIndex(),ExceptionCode.RESERVE_ALREADY_SAVE.getErrMsg());
			}
		}
		for (ReserveAlreadyRate reserveAlreadyRate : reserveAlreadyRateList) {
			int in=reserveAlreadyRateDao.insert(reserveAlreadyRate);
			if (in!=1) {
				throw new ServiceException(ExceptionCode.RESERVE_ALREADY_RATE_SAVE.getIndex(),ExceptionCode.RESERVE_ALREADY_RATE_SAVE.getErrMsg());
			}
		}
		for (ReserveAlreadyDate reserveDate: reserveDateList) {
			int reserveInv=reserveAlreadyDateDao.save(reserveDate);
			if (reserveInv!=1) {
				throw new ServiceException(ExceptionCode.RESERVE_RESERVE_INV_SAVE.getIndex(),ExceptionCode.RESERVE_RESERVE_INV_SAVE.getErrMsg());
			}
		}
		return true;
    }

    @Transactional(rollbackFor = Exception.class)
   	public boolean cancelReserve(ReserveRequest reserveRequest) {
       BigDecimal balanceAmount = reserveRequest.getBalanceAmount();
       // 解冻用户资金。
       logger.info("用户冻结时，返回的id", reserveRequest.getFrozenCode());
       logger.info("预约申请单的id", reserveRequest.getId());


        // 抵现券金额
        BigDecimal couponAmount = BigDecimal.ZERO;
        // 预约的金额
        if (StringUtils.isNotBlank(reserveRequest.getReserveCoupon())) {
            CouponPlacement couponPlacement = couponService.findCouponPlacementbyId(null, reserveRequest.getReserveCoupon());
            if(null == couponPlacement){
                throw new ServiceException(ExceptionCode.COUPON_NOT_EXIST.getIndex(),ExceptionCode.COUPON_NOT_EXIST.getErrMsg());
            }
            CouponPackage coupon = couponPlacement.getCouponPackage();
            logger.info("CouponPackage类型getType()：" + coupon.getType());
            // 抵现券
            if( coupon.getType().equals( CouponType.CASH ) ){
                couponAmount = BigDecimal.valueOf(coupon.getParValue());
                logger.info("本次所使用的抵现券金额：" + couponAmount);
            }
        }

        // 这块有问题，注意 balance Amout 和红包金额的关系。
        balanceAmount = balanceAmount.subtract(couponAmount);


       logger.info("调账户取消方法前打印参数，frozenCode" + reserveRequest.getFrozenCode() + ",userId:" + reserveRequest.getUserId() + ",balanceAmount:" + balanceAmount + "couponAmount:" + couponAmount );
       FreezeResultDto cancelAppointment = appointmentService.cancelAppointment(
    		   reserveRequest.getFrozenCode(), reserveRequest.getUserId(), balanceAmount, couponAmount);
       String orderNo = cancelAppointment.getOrderNo();
        logger.info("调账户系统取消返回orderNo:" + orderNo);
       Boolean isSuccess = cancelAppointment.getIsSuccess();
       logger.info("账户系统取消返回结果isSuccess：" + isSuccess);

       // 将用户资金返还给用户，并将申请单的状态置为已取消。
       if (isSuccess) {
           // 资金解冻成功后，更改预约申请单状态
           boolean markStatusResult = false;
           boolean markCouponStatusResult = false;
           String couponPlacementId = reserveRequest.getReserveCoupon();
           logger.info("投资金额：" + reserveRequest.getInvestedAmount());

           if (reserveRequest.getInvestedAmount().compareTo(BigDecimal.ZERO) == 0) {
               // 没有投资时，置为已取消，并且不能取消该预约
        	   int mak = reserveRequestDao.updateStatusCancelById(reserveRequest.getId(), ReserveRequestStatusEnum.CANCELLED.getIndex(), ReserveRequestCancelEnum.FALSE.getIndex());
        	   if (mak == 1) {
                   markStatusResult = true;
               }

               // add by mzl start 增加取消返回奖券
               // 判断此次预约是否奖券，如果有奖券则返还奖券
               if (StringUtils.isNotBlank(couponPlacementId)) {
                   CouponPlacement couponPlacement = couponService.findCouponPlacementbyId(null, couponPlacementId);
                   if(CouponStatus.FROZEN.equals(couponPlacement.getStatus())){
                       // 解冻红包状态
                       markCouponStatusResult = couponService.couponPlacementUnFrozen(couponPlacementId, RealmEntity.of(Realm.RESERVE, reserveRequest.getId()));
                       logger.info("预约失败后，解冻红包结果：" + markCouponStatusResult);
                   }
               } else {
                   markCouponStatusResult = true;
               }
           } else {
               // 有投资时，置为部分投资成功，并且不能取消该预约
               int mak = reserveRequestDao.updateStatusCancelById(reserveRequest.getId(),
                       ReserveRequestStatusEnum.PARTLY_SUCCESSFUL.getIndex(), ReserveRequestCancelEnum.TRUE.getIndex());
               if( mak == 1 ){
            	   markStatusResult = true;
               }
           }
           if (!markStatusResult) {
              logger.error("reserve amount release success, but mark reserveRequestStatus fail.[reserveRequest={}]", reserveRequest);
              throw new ServiceException(ExceptionCode.CANCLE_UPDATERESERVE_FAILED.getIndex(), ExceptionCode.CANCLE_UPDATERESERVE_FAILED.getErrMsg());
           } else if (!markCouponStatusResult) {
        	  logger.error("reserve amount release success, but mark markCouponStatus fail.[couponPlacementId={}]", reserveRequest.getReserveCoupon());
              throw new ServiceException(ExceptionCode.CANCLE_UPDATE_COUPONSTATUS_FAILED.getIndex(),ExceptionCode.CANCLE_UPDATE_COUPONSTATUS_FAILED.getErrMsg());
           } else{
           	    // 记录预约申请取消信息（用户手动取消）
        	    ReserveRequest reserveCancel=new ReserveRequest();
               reserveCancel.setId(reserveRequest.getId());
        	    reserveCancel.setCancelTime(new Date());
        	    reserveCancel.setReserveCancelType(ReserveCancelType.USER_CANCELED.getIndex());
                // 	只用于取消时账户系统返回code
        	    reserveCancel.setUnFrozenCode(orderNo);
	          	int update = reserveRequestDao.updateReserveRequest(reserveCancel);
               if(1 != update){
                   logger.error("reserve amount release success, but updateCancelMessage fail.[reserveRequest={}]", reserveRequest);
                   throw new ServiceException(ExceptionCode.UPDATE_REQUEST_USER_CANCEL_FAIL.getIndex(),ExceptionCode.UPDATE_REQUEST_USER_CANCEL_FAIL.getErrMsg());
               }
           }

           logger.info("用户取消预约申请单成功，userId" + reserveRequest.getUserId() + ",申请单id:" + reserveRequest.getId() );
           return true;
       } else {
           // 调账户系统解冻失败，需要抛出异常
           logger.error("调账户系统取消方法失败, [reserveRequest={}]", reserveRequest);
           throw new ServiceException(ExceptionCode.ACCOUNT_CANCEL_REQUEST_ERROR.getIndex(),ExceptionCode.ACCOUNT_CANCEL_REQUEST_ERROR.getErrMsg());
       }
    }


}

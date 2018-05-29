package com.gomemyc.invest.bridge;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gomemyc.account.service.InvestService;
import com.gomemyc.agent.LoanAgent;
import com.gomemyc.agent.config.AgentConfig;
import com.gomemyc.agent.enums.DictionaryEnum;
import com.gomemyc.agent.resp.ProdFoundAbandonResultDto;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.common.utils.DateUtils;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.enums.*;
import com.gomemyc.invest.util.JodaDateUtils;
import com.gomemyc.invest.utils.JsonHelper;
import com.gomemyc.message.MessageTemplate;
import com.gomemyc.message.api.MessageService;
import com.gomemyc.model.client.Client;
import com.gomemyc.model.enums.Realm;
import com.gomemyc.model.misc.RealmEntity;
import com.gomemyc.model.user.User;
import com.gomemyc.sms.SMSType;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.user.api.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.dao.LoanDao;
import com.gomemyc.invest.dao.ProductRegularDao;

@Component
public class LoanBridge {
    
    @Autowired
    private LoanDao loanDao;
    
    @Autowired
    private ProductRegularDao productRegularDao;

    @Autowired
    private ProductBillDao productBillDao;

    @Autowired
    LoanRedisBridge loanRedisBridge;

    @Autowired
    AgentConfig agentConfig;

    @Reference
    InvestService investService;

    @Reference
    com.gomemyc.trade.service.InvestService tradeInvestService;

    /** 6.红包服务 */
    @Reference
    private CouponService couponService;

    @Reference
    private MessageService messageService;

    @Reference
    private UserService userService;


    /** 7.MQ发送者  */
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;

    /** 日志 */
    Logger logger = LoggerFactory.getLogger(this.getClass());

    

    @Transactional(rollbackFor = {Exception.class})
    public boolean cancelDingQi(String productId, ProductRegular regular, Boolean isSendMQ2ZGsys) throws ServiceException{
        // 补充： -1,0 的状态也需要更新状态，但是不用和北京银行同步
        // 判断如果是资管类型（tbl_loan source字段为1时），给资管推mq。

        // 1、更新状态为 -2 (前提条件) regular 表 status = 1,2
        // 2、调方法清redis
        // 3、如果sync_status = 1 调北京银行废标，4.3.2
        logger.info("进入取消定期产品方法，productId:" + productId);
        if (regular.getStatus().getIndex() == 1 || regular.getStatus().getIndex() == 2 || regular.getStatus().getIndex() == -1 || regular.getStatus().getIndex() == 0) {
            int updateCount = productRegularDao.updateProductStatus(ProductStatus.CANCELED, productId);
            if (1 != updateCount) {
                throw new ServiceException(60004, "更新产品状态失败");
            }
        } else {
            throw new ServiceException(60005, "当前的产品状态不可以取消");
        }

        // 成功与否只记日志
        try{
            // 清除redis
            boolean isSuccess = loanRedisBridge.clearProductCacheAndList(productId);
            // 成功与否只记日志
            logger.info("清理缓存,productId:" + productId + ", isSuccess" + isSuccess);
        }catch (Exception e){
            logger.info("清理缓存,productId:" + productId + ", 失败", e);
        }

        Loan loan = loanDao.findById(regular.getLoanId());
        if(null != loan) {
            logger.info("废标前标id:" + loan.getId() + ",regular的status:" + regular.getStatus().getIndex() + ", getLocalSyncStatus状态：" + loan.getLocalSyncStatus() + "SyncStatus:" + loan.getSyncStatus());
        }
        // 只有状态 1(北京银行开标) 和 2 时，需要调北京银行废标
        if((null != loan && null != regular.getStatus() && regular.getStatus().getIndex() == 1 && loan.getSyncStatus() == LoanSyncStatus.SUCCESS && loan.getLocalSyncStatus() == LoanSyncStatus.SUCCESS) ||
                (null != regular.getStatus() && regular.getStatus().getIndex() == 2)) {
            LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);
            Date date = new Date();
            // 商商户交易日期,格式：YYYYMMDD(必填)
            String partnerTransDate = JodaDateUtils.formatshortDate(date);
            // 商户交易时间,格式：HHmmSS(必填)
            String partnerTransTime = JodaDateUtils.formatTime(date);
            int randNum = mtRand(1, 200);
            String retryOrderNo = productId + "RETRY" + randNum;
            logger.info("废标时标的loanId:" + regular.getLoanId() + ", 发往北京银行单号 orderNo:" + retryOrderNo);
            ProdFoundAbandonResultDto dto =
                    loanAgent.publishFoundAbandon(agentConfig.getPlatNo(), retryOrderNo, partnerTransDate, partnerTransTime, regular.getLoanId(), DictionaryEnum.WASTELABEL3, null, null, null, "定期产品标的取消，productId" + productId);
            logger.info("废标时标的loanId:" + regular.getLoanId() + "发往北京银行后返回结果：" + dto); // 对象已重写toString

            if ("10000".equals(dto.getRecode())) {
                /*北京银行-汤学涛(1978969810) 10:12:36
                @李非非-国美  刚看了，目前就返回这2个字段，这个接口返回是同步的，返回10000表示成功了*/
                // 成功
                logger.info("废标标的loanId:" + regular.getLoanId() + "废标成功！！");
            } else {
                logger.error("废标失败，标的loanId:" + regular.getLoanId());
                throw new ServiceException(60006, "废标失败，请重试");
            }
        }

        // 如果标的数据（tbl_loan）来源于资产系统（source = 1）,给资产系统发 MQ
        // 以下是【定期产品】取消 MQ
        if(null != loan){
            logger.info("取消标是否需要给资管发送isSendMQ2ZGsys：" + isSendMQ2ZGsys + "资产编号 portfolioNo:" + loan.getPortfolioNo() + ",资产来源source:" + loan.getSource());
        }
        if(isSendMQ2ZGsys && null != loan && StringUtils.isNotBlank(loan.getPortfolioNo()) && LoanSource.ASSET == loan.getSource()){
            try {
                Map<String, String> mqMap = new HashMap<String, String>();
                mqMap.put("type", "productStatus");
                mqMap.put("id", loan.getPortfolioNo());
                mqMap.put("status", "11");
                logger.info("要发送定期取消的mq消息：" + JsonHelper.getJson(mqMap));
                String key = UUIDGenerator.generate();
                Message message = new Message("zgsystem", "baoli", key, JsonHelper.getJson(mqMap).getBytes());
                SendResult sendResult = mqProducer.send(message);
                logger.info("发送取消标的，产品productId:{},发送MQ结果：{}", productId, sendResult);
            } catch (JsonProcessingException e) {
                logger.error("发送取消标的mq时解析时出错，productId:" + productId, e);
            } catch (Exception e) {
                logger.error("发送取消【定期产品】标的mq时出错，productId:" + productId , e);
                throw new ServiceException(60012, "给资产系统发撤销mq失败");
            }
        }

        logger.info("取消定期产品执行完成,productId" + productId);
        return true;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean cancelPiaoJu(String productId, ProductBill bill, Boolean isSendMQ2ZGsys)  throws ServiceException {

        // 补充： -1,0 的状态也需要更新状态，但是不用和北京银行同步
        // 判断如果是资管类型（tbl_loan source字段为1时），给资管推mq。

        // 1、更新状态为 -2 (前提条件) regular 表 status = 1,2
        // 2、调方法清redis
        // 3、如果sync_status = 1 调北京银行废标，4.3.2
        logger.info("进入取消定期产品方法，productId:" + productId);
        if (bill.getStatus().getIndex() == 1 || bill.getStatus().getIndex() == 2 || bill.getStatus().getIndex() == -1 || bill.getStatus().getIndex() == 0) {
            int updateCount = productBillDao.updateProductStatus(ProductStatus.CANCELED, productId);
            if (1 != updateCount) {
                throw new ServiceException(60004, "更新产品状态失败");
            }
        } else {
            throw new ServiceException(60005, "当前的产品状态不可以取消");
        }

        try{
            // 清除redis
            boolean isSuccess = loanRedisBridge.clearProductCacheAndList(productId);
            // 成功与否只记日志
            logger.info("清理缓存,productId:" + productId + ", isSuccess" + isSuccess);
        }catch (Exception e){
            logger.info("清理缓存,productId:" + productId + ", 失败", e);
        }

        Loan loan = loanDao.findById(bill.getLoanId());
        if (null != loan){
            logger.info("废标前标id:" + loan.getId() + ",bill 的status" + bill.getStatus().getIndex() + ", getLocalSyncStatus状态：" + loan.getLocalSyncStatus() + "SyncStatus:" + loan.getSyncStatus());
        }

        // 只有状态 1 和 2 时，需要调北京银行废标
        if( (bill.getStatus().getIndex() == 1 && null != loan && loan.getLocalSyncStatus() == LoanSyncStatus.SUCCESS && loan.getSyncStatus() == LoanSyncStatus.SUCCESS)
                || bill.getStatus().getIndex() == 2) {
            LoanAgent loanAgent = LoanAgent.getInstance(agentConfig);
            Date date = new Date();
            // 商商户交易日期,格式：YYYYMMDD(必填)
            String partnerTransDate = JodaDateUtils.formatshortDate(date);
            // 商户交易时间,格式：HHmmSS(必填)
            String partnerTransTime = JodaDateUtils.formatTime(date);
            int randNum = mtRand(1, 200);
            String retryOrderNo = productId + "RETRY" + randNum;
            logger.info("废标时标的loanId:" + bill.getLoanId() + ", 发往北京银行单号 orderNo:" + retryOrderNo);
            ProdFoundAbandonResultDto dto =
                    loanAgent.publishFoundAbandon(agentConfig.getPlatNo(), retryOrderNo, partnerTransDate, partnerTransTime, bill.getLoanId(), DictionaryEnum.WASTELABEL3, null, null, null, "定期产品标的取消，productId" + productId);
            logger.info("废标时标的loanId:" + bill.getLoanId() + "发往北京银行后返回结果：" + dto); // 对象已重写toString

            if ("10000".equals(dto.getRecode())) {
                 /*北京银行-汤学涛(1978969810) 10:12:36
                @李非非-国美  刚看了，目前就返回这2个字段，这个接口返回是同步的，返回10000表示成功了*/
                // 成功
                logger.info("废标标的loanId:" + bill.getLoanId() + "废标成功！！");
            } else {
                logger.error("废标失败，标的loanId:" + bill.getLoanId());
                throw new ServiceException(60006, "废标失败，请重试");
            }
        }

        // 如果标的数据（tbl_loan）来源于资产系统（source = 1）,给资产系统发 MQ
        // 以下是【票据产品】取消 MQ
        if(null != loan){
            logger.info("取消标是否需要给资管发送isSendMQ2ZGsys：" + isSendMQ2ZGsys + "资产编号 portfolioNo:" + loan.getPortfolioNo() + ",资产来源source:" + loan.getSource());
        }
        if(isSendMQ2ZGsys && null != loan && StringUtils.isNotBlank(loan.getPortfolioNo()) && LoanSource.ASSET == loan.getSource()){
            try {
                Map<String, String> mqMap = new HashMap<String, String>();
                mqMap.put("type", "productStatus");
                mqMap.put("id", loan.getPortfolioNo());
                mqMap.put("status", "11");
                logger.info("要发票据取消的mq消息：" + JsonHelper.getJson(mqMap));
                String key = UUIDGenerator.generate();
                Message message = new Message("zgsystem", "bill", key, JsonHelper.getJson(mqMap).getBytes());
                SendResult sendResult = mqProducer.send(message);
                logger.info("发送取消标的，产品productId:{},发送MQ结果：{}", productId, sendResult);
            } catch (JsonProcessingException e) {
                logger.error("发送取消标的mq时解析时出错，productId:" + productId, e);
            } catch (Exception e) {
                logger.error("发送取消【票据产品】标的mq时出错，productId:" + productId , e);
                throw new ServiceException(60012, "给资产系统发撤销mq失败");
            }
        }

        logger.info("取消票据产品执行完成,productId" + productId);
        return true;
    }

    /**
     * 无论定期产品、票据产品的流标，都可以调用这个方法
     *
     * @param productId
     */
    public void sendSmsAndMessage(String productId, String title){
        // 流标发短信站内信
        // 根据产品id查询所有投资记录
        List<InvestDTO> investDTOList = tradeInvestService.findListByProductIdAndStatus(productId, InvestStatus.SUCCESS);
        if(null != investDTOList) {
            for (InvestDTO invest : investDTOList) {
                try {
                    // 查询订单的用户
                    User user = userService.findByUserId(invest.getUserId());
                    if (null != user) {
                        try {
                            Map<String, Object> smsMap = new HashMap<String, Object>();
                            smsMap.put("templateId", SMSType.NOTIFICATION_LOAN_BILL_FAILED.getTemplateId());
                            smsMap.put("mobiles", user.getMobile());
                            List<String> params = new ArrayList<String>();
                            params.add(DateUtils.formatDate(invest.getSubmitTime(), DateUtils.dateTimeFormatter));
                            params.add(invest.getAmount().toString());
                            params.add(title);
                            smsMap.put("params", params);
                            Message message = new Message(MQTopic.SMS_SEND.getValue(),  JsonHelper.getJson(smsMap).getBytes());
                            SendResult sendResult = mqProducer.send(message);
                            logger.info("流标发送短信mq:" + sendResult);
                        } catch (Exception e){
                            // 短信发失败，不做任何处理，只记日志
                            logger.error("流标发送短信MQ失败，投资id:" + invest.getId(), e);
                        }

                        // 发站内信
                        messageService.sendMessageByTemplate(new Client(), MessageTemplate.NOTIFICATION_LOAN_BILL_FAILED,
                                user.getMobile(),
                                JodaDateUtils.formatDate(invest.getSubmitTime(), JodaDateUtils.dateYear),
                                JodaDateUtils.formatDate(invest.getSubmitTime(), JodaDateUtils.dateYear),
                                JodaDateUtils.formatDate(invest.getSubmitTime(),JodaDateUtils.dateYear),
                                invest.getAmount().toString(),
                                title);
                        logger.info("流标发送站内信成功，投资id:" + invest.getId());
                    }
                } catch (Exception e){
                    // 给某一个人发送短信、站内信异常记录日志即可。
                    logger.error("流标productId:" + productId + ",发送短信站内信失败，订单investId:" + invest.getId());
                }
            }
        }

    }


    @Transactional(rollbackFor = {Exception.class})
    public boolean failDingQi(String productId, ProductRegular regular)  throws ServiceException{
        // 条件： status = 5 && product_switch = 32
        // 1、更新status = -3
        // 2、调账户，传标ID ，loanId
        // 4、最后一步清理缓存
        // 5、给资管发流标 MQ
        // 6、如果流标使用红包，把红包退回
        // 3、更新投资记录为status=-6

        int updateCount = productRegularDao.updateProductStatus(ProductStatus.ABORTIVE, productId);
        if(1 != updateCount){
            throw new ServiceException(60004, "更新产品状态失败");
        }

        logger.info("调用账户系统流标【前】给账户系统上送的loanId:" + regular.getLoanId());
        // 调账户流标
        investService.abolishLoan(regular.getLoanId());
        logger.info("调用账户系统流标【后】给账户系统上送的loanId:" + regular.getLoanId());

        try {
            // 清除redis
            boolean clearIsSuccess = loanRedisBridge.clearProductCacheAndList(productId);
            // 成功与否只记日志
            logger.info("清理缓存,productId:" + productId + ", isSuccess" + clearIsSuccess);
        }catch (Exception e){
            logger.info("清理缓存,productId:" + productId + ", 失败", e);
        }

        // 如果标的数据（tbl_loan）来源于资产系统（source = 1）,给资产系统发 MQ
        // 以下是【定期产品】流标 MQ
        Loan loan = loanDao.findById(regular.getLoanId());
        if(null != loan){
            logger.info("流标loanId:" + loan.getId() + "资产编号portfolioNo：" + loan.getPortfolioNo() + ",资产来源source:" + loan.getSource());
        }
        if(null != loan && StringUtils.isNotBlank(loan.getPortfolioNo()) && LoanSource.ASSET == loan.getSource()){
            try {
                Map<String, String> mqMap = new HashMap<String, String>();
                mqMap.put("type", "productStatus");
                mqMap.put("id", loan.getPortfolioNo());
                mqMap.put("status", "5");
                logger.info("要发送定期流标的mq消息：" + JsonHelper.getJson(mqMap));
                String key = UUIDGenerator.generate();
                Message message = new Message("zgsystem", "baoli", key, JsonHelper.getJson(mqMap).getBytes());
                SendResult sendResult = mqProducer.send(message);
                logger.info("发送流标标的，产品productId:{},发送MQ结果：{}", productId, sendResult);
            } catch (JsonProcessingException e) {
                logger.error("发送流标标的mq时解析时出错，productId:" + productId, e);
            } catch (Exception e) {
                logger.error("发送流标【定期产品】标的mq时出错，productId:" + productId , e);
                throw new ServiceException(60012, "给资产系统发流标mq失败");
            }
        }



        // 根据产品id查询所有投资记录
        List<InvestDTO> investDTOList = tradeInvestService.findListByProductIdAndStatus(productId, InvestStatus.SUCCESS);
        if(null != investDTOList) {
            logger.info("流标productId:" + productId + ",查到的InvestStatus.SUCCESS状态的投资记录条数：" + investDTOList.size());
            for (InvestDTO invest : investDTOList) {
                // 只对使用红包的投资记录进行红包退回处理
                if(StringUtils.isNotBlank(invest.getCouponPlacememtId())) {
                    // 红包退回
                    Boolean isReturnSuccess = couponService.unusedCouponPlacementById(invest.getCouponPlacememtId(), RealmEntity.of(Realm.INVEST, invest.getId()));
                    logger.info("流标productId:" + productId + "投资订单investId:" + invest.getId() + "红包退回结果：" + isReturnSuccess);
                }
            }
        }

        // 先查询投资记录（这步骤一定在退券后面）
        int investCount = loanDao.selectInvestCountByProductId(regular.getId());
        logger.info("流标productId:" + productId + "，更新状态前查询投资条数：" + investCount);
        if(investCount > 0) {
            // 更新投资记录为status=-6
            int updateNo = loanDao.updateInvestStatusByProductId(regular.getId(), InvestStatus.ABORTIVE.getIndex(), InvestStatus.SUCCESS.getIndex());
            if (updateNo <= 0) {
                throw new ServiceException(60008, "流标更新投资记录失败");
            }
            logger.info("流标productId:" + productId + ",成功更新投资记录状态条数：" + updateNo);
        }

        logger.info("定期产品流标方法执行完成，productId:" + productId);
        return true;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean failPiaoJu(String productId, ProductBill bill)  throws ServiceException{
        // 条件： status = 5 && product_switch = 32
        // 1、更新status = -3
        // 2、调账户，传标ID ，loanId
        // 4、最后清理缓存
        // 5、给资管发流标 MQ
        // 6、如果流标使用红包，把红包退回
        // 3、更新投资记录为status=-6
        int updateCount = productBillDao.updateProductStatus(ProductStatus.ABORTIVE, productId);
        if(1 != updateCount){
            throw new ServiceException(60004, "更新产品状态失败");
        }

        logger.info("调用账户系统流标【前】给账户系统上送的loanId:" + bill.getLoanId());
        // 调账户流标
        investService.abolishLoan(bill.getLoanId());
        logger.info("调用账户系统流标【后】给账户系统上送的loanId:" + bill.getLoanId());

        try {
            // 清除redis
            boolean clearIsSuccess = loanRedisBridge.clearProductCacheAndList(productId);
            // 成功与否只记日志
            logger.info("清理缓存,productId:" + productId + ", isSuccess" + clearIsSuccess);
        }catch (Exception e){
            logger.info("清理缓存,productId:" + productId + ", 失败", e);
        }

        // 如果标的数据（tbl_loan）来源于资产系统（source = 1）,给资产系统发 MQ
        // 以下是【票据产品】流标 MQ
        Loan loan = loanDao.findById(bill.getLoanId());
        if(null != loan){
            logger.info("流标loanId:" + loan.getId() + "资产编号portfolioNo：" + loan.getPortfolioNo() + ",资产来源source:" + loan.getSource());
        }
        if(null != loan && StringUtils.isNotBlank(loan.getPortfolioNo()) && LoanSource.ASSET == loan.getSource()){
            try {
                Map<String, String> mqMap = new HashMap<String, String>();
                mqMap.put("type", "productStatus");
                mqMap.put("id", loan.getPortfolioNo());
                mqMap.put("status", "5");
                logger.info("要发送票据流标mq消息：" + JsonHelper.getJson(mqMap));
                String key = UUIDGenerator.generate();
                Message message = new Message("zgsystem", "bill", key, JsonHelper.getJson(mqMap).getBytes());
                SendResult sendResult = mqProducer.send(message);
                logger.info("发送流标标的，产品productId:{},发送MQ结果：{}", productId, sendResult);
            } catch (JsonProcessingException e) {
                logger.error("发送取流标的mq时解析时出错，productId:" + productId, e);
            } catch (Exception e) {
                logger.error("发送流标【票据产品】标的mq时出错，productId:" + productId , e);
                throw new ServiceException(60012, "给资产系统发流标mq失败");
            }
        }

        // 根据产品id查询所有投资记录
        List<InvestDTO> investDTOList = tradeInvestService.findListByProductIdAndStatus(productId, InvestStatus.SUCCESS);
        if(null != investDTOList) {
            for (InvestDTO invest : investDTOList) {
                logger.info("流标productId:" + productId + ",查到的InvestStatus.SUCCESS状态的投资记录条数：" + investDTOList.size());
                // 只对使用红包的投资记录进行红包退回处理
                if(StringUtils.isNotBlank(invest.getCouponPlacememtId())) {
                    // 红包退回
                    Boolean isReturnSuccess = couponService.unusedCouponPlacementById(invest.getCouponPlacememtId(), RealmEntity.of(Realm.INVEST, invest.getId()));
                    logger.info("流标productId:" + productId + "投资订单investId:" + invest.getId() + "红包退回结果：" + isReturnSuccess);
                }
            }
        }

        // 先查询投资记录（这步骤一定在退券后面）
        int investCount = loanDao.selectInvestCountByProductId(bill.getId());
        logger.info("流标productId:" + productId + "，投资条数：" + investCount);
        if(investCount > 0) {
            // 更新投资记录为status=-6
            int updateNo = loanDao.updateInvestStatusByProductId(bill.getId(), InvestStatus.ABORTIVE.getIndex(), InvestStatus.SUCCESS.getIndex());
            if (updateNo <= 0) {
                throw new ServiceException(60008, "流标更新投资记录失败");
            }
            logger.info("流标productId:" + productId + ",成功更新投资记录状态条数：" + updateNo);
        }

        logger.info("票据产品流标方法执行完成，productId:" + productId);
        return true;
    }

    /**
     * 区间随机数。
     *
     * @param min 最小
     * @param max 最大
     * @return
     */
    private int mtRand(int min, int max){
        int a = (int)Math.round(Math.random()*(max-min)+min);
        return a;
    }
    
    @Transactional(rollbackFor = {Exception.class})
    public Boolean modifyProductStatusAndSettleTime(String productId,String loanId, ProductStatus status, Date settleTime,Date valueTime,Date dueDate)
			throws ServiceException {
		int count = 0;
		if(productId.startsWith("dq-")){
			count = productRegularDao.updateProductStatusAndSettleTime(status, productId, settleTime,valueTime);
		}
		if(productId.startsWith("pj-")){
			count = productBillDao.updateProductStatusAndSettleTime(status, productId, settleTime,valueTime);
		}
		
		if(count <= 0 || loanDao.settleLoanValueDate(loanId,LoanStatus.SETTLED,settleTime,valueTime,dueDate) <= 0){
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),ExceptionCode.EXCEPTION.getErrMsg());
		}
		
		return true;
		
	}

}

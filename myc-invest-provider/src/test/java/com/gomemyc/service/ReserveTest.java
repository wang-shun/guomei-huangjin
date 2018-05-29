package com.gomemyc.service;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gomemyc.client.api.ClientService;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.common.utils.DateUtils;
import com.gomemyc.config.api.ConfigManager;
import com.gomemyc.invest.dao.LoanDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.enums.LoanSource;
import com.gomemyc.invest.service.LoanTypeService;
import com.gomemyc.invest.service.ProductBillService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.invest.utils.JsonHelper;
import com.gomemyc.message.MessageTemplate;
import com.gomemyc.message.api.MessageService;
import com.gomemyc.model.client.Client;
import com.gomemyc.trade.enums.InvestStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.invest.service.ReserveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 预约申请单元测试类.
 * 
 * @author Administrator
 *
 */
public class ReserveTest extends BaseFunctionalTestCase{
	
	/** 预约申请单  */
	@Reference
    private ReserveRequestService reserveRequestService;

    @Reference
    private LoanTypeService loanTypeService;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private ProductRegularDao productRegularDao;

    /** 7.MQ发送者  */
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;

/*    @Reference
    private ProductRegularDao productRegularDao;*/

    @Reference
    private ProductRegularService productRegularService;

    @Reference
    private ProductBillService productBillService;

    @Reference
    private MessageService messageService;

    @Reference
    private ClientService clientService;

    @Reference
    private ConfigManager configManager;



    @Test
    public void getLoanType() {
        List<LoanTypeDTO> list = loanTypeService.findListByTypeSwitch(2);
        System.out.println(list);

    }

    @Test
    public void getProductRegular() {
//        ProductRegular list = productRegularDao.findByTitle("测试标的1");
        ProductRegularDTO dto = productRegularService.findByTitle("测试标的2");
        System.out.println(dto);
        int a = productRegularService.updateFinshAndStatusById(new Date(),-2, dto.getId());
        int b = productRegularService.updateInvestById(new BigDecimal("5000"),100,dto.getId());
        System.out.println(a);
        System.out.println(b);
    }

    @Test
    public void getReserveInfo() {
    	// 显示预约申请单
//    	reserveRequestService.getReserveInfo("10000");

        // 发站内信
/*       messageService.sendMessageByTemplate(clientService.getClient(configManager.getClientConfig().getCode()), MessageTemplate.NOTIFICATION_LOAN_BILL_FAILED,
                "13200001002",
                "2017","4","22","8","999");*/
       messageService.sendMessageByTemplate(clientService.getClient(configManager.getClientConfig().getCode()), MessageTemplate.NOTIFICATION_LOAN_BILL_FAILED,
                "13200001002",
                "2017","4","24","8","产品名称-2017");

        /*messageService.sendMessageByTemplate(clientService.getClient(configManager.getClientConfig().getCode()), MessageTemplate.NOTIFICATION_INVITE_REGISTER_REWARD_REMIND,
                "15811443876",
                "2017",
               "4");*/
        System.out.println();
    }

    @Test
    public void reserveQueue() {

       List<String> a =  productRegularService.findWaitFailRegular(5, 32);
        List<String> b =  productBillService.findWaitFailRegular(5,32);
        System.out.println();

/*        List<Integer> investStatusList = new ArrayList<Integer>();
        investStatusList.add(InvestStatus.LOCAL_FROZEN_SUCCESS.getIndex());
        investStatusList.add(InvestStatus.BJ_FROZEN_SUCCESS.getIndex());
        investStatusList.add(InvestStatus.BJ_DF_SUCCESS.getIndex());
        investStatusList.add(InvestStatus.BJ_SYN_SUCCESS.getIndex());
        Integer count = loanDao.findInvestStatusCountByProductId("dq-8A18908B5B41DB430015B015B41DF2C0600004", investStatusList);
        if(null != count && count > 0){
            System.out.println("11111");
        }
        System.out.println();*/

/*    	List<String> list = new ArrayList<>();
    	list.add("1");*/
    	// 显示预约申请单
//    	reserveRequestService.reserveQueue("2", "2", "3", BigDecimal.ZERO, "name", "miaoshu", "描述", null);
    }

    @Test
    public void testZiGuan() {
        // 如果标的数据（tbl_loan）来源于资产系统（source = 1）,给资产系统发 MQ
        // 以下是【票据产品】取消 MQ
//        Loan loan = loanDao.findById("23232332233232");
//        if(null != loan && StringUtils.isNotBlank(loan.getPortfolioNo()) && LoanSource.ASSET == loan.getSource()){
            try {
                Map<String, Object> mqMap = new HashMap<String, Object>();

                         /* JSONObject json = new JSONObject();
          json.put("id", "PJCP20170420000007");
          json.put("status", 11);
          json.put("type", "productStatus");
                String key = UUIDGenerator.generate();
          Message msg = new Message("zgsystem",// topic
                  "bill",// tag
                  key,// key
                  JSON.toJSONString(json).getBytes());// body
          SendResult sendResult = mqProducer.send(msg);
          System.out.println("发送状态" + sendResult.getSendStatus().name());
          System.out.println("消息id：" + sendResult.getMsgId());
          System.out.println(sendResult);*/

                mqMap.put("type", "productStatus");
                mqMap.put("id", "26763");
                mqMap.put("status", 11);
                String key = UUIDGenerator.generate();
                Message message = new Message("zgsystem", "bill", key, JsonHelper.getJson(mqMap).getBytes());
                SendResult sendResult = mqProducer.send(message);
                System.out.println();
//                logger.info("发送取消标的，产品productId:{},发送MQ结果：{}", productId, sendResult);
//                logger.error("发送取消标的mq时解析时出错，productId:" + productId, e);
            } catch (Exception e) {
//                logger.error("发送取消【票据产品】标的mq时出错，productId:" + productId , e);
                throw new ServiceException(60012, "给资产系统发撤销mq失败");
            }
//        }
    }


}

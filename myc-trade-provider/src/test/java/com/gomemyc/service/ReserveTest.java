package com.gomemyc.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.FreezeResultDto;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.AppointmentService;
import com.gomemyc.account.service.LoanAccountService;
import com.gomemyc.common.constant.MQTopic;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.model.user.User;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.dao.ReserveRequestDao;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.dto.ReserveRequestShowDTO;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.entity.ReserveRequest;
import com.gomemyc.trade.enums.ExceptionCode;
import com.gomemyc.trade.enums.InvestSource;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.trade.service.ReserveRequestService;
import com.gomemyc.trade.service.ReserveService;
import com.gomemyc.trade.util.JsonHelper;
import com.gomemyc.user.api.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.math.BigDecimal;
import java.util.*;

/**
 * 预约申请单元测试类.
 * 
 * @author Administrator
 *
 */
public class ReserveTest extends BaseFunctionalTestCase {

	/** 预约申请单  */
	@Reference
    private ReserveService reserveService;

    /** 账户相关 */
    @Reference
    AccountService accountService;

	@Reference
    private UserService userService;

	@Reference
    private InvestService investService;

    @Reference
    private CouponService couponService;

    /** 账户预约投资服务 */
    @Reference
    AppointmentService appointmentService;

    /** 标的账户 */
    @Reference
    LoanAccountService loanAccountService;

    @Reference
    private ProductRegularService productRegularService;

/*    @Reference
    ProductRegularService productRegularService;*/

    /** MQ发送者  */
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;

    /** 4.订单DAO  */
    @Autowired
    private InvestDao investDao;


    @Test
    public void getReserveInfoTest() {
        // 显示预约申请单
//        reserveRequestService.getReserveInfo("10000");
    }

    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static String getJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    @Test
    public void testJson() throws JsonProcessingException {
        Map<String, Object> sc = new HashMap<String, Object>();
        sc.put("bankCode",1);
        sc.put("branchCode","999999");
        System.out.println(getJson(sc));
        System.out.println(11111);
    }


    @Test
    public void mySendMQ() throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {
        // 构建单笔投资MQ消息
        Map<String, Object> mqMap = new HashMap<String, Object>();
        // 用户id
        mqMap.put("userId", "00003BB4-BC10-486D-9CA5-16B6E00B042F");
        // 红包id
        mqMap.put("placementId", "");
        // 抵现券金额
        mqMap.put("couponCashValue", "");
        // 投资单金额（撮合资金）
        mqMap.put("investAmount", 50 + "");
        // 投资订单id
        mqMap.put("investId", "8A015E745B2966C00015B015B2966C09300000");
        // 冻结流水号
        mqMap.put("reserveRequestId", "8A015EE45B1E817E0015B015B1E817EF700000");
        // 产品编号
        mqMap.put("prodId", "8a015fac5b1ed616015b1edf6da90002");
        Message message = new Message("myc_reserve_invest","123456", getJson(mqMap).getBytes());
//        Message message2 = new Message("myc_invest","77777", "abd".getBytes());
        SendResult sendResult = mqProducer.send(message);
//        SendResult sendResult2 = mqProducer.send(message2);
        System.out.println(sendResult);
//        System.out.println(sendResult2);
        System.out.println("###############################");

    }

    @Test
    public void mySendMQLog() throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {
        // 构建单笔投资MQ消息
        Message message = new Message("myc_invest","77777", "abd".getBytes());
        SendResult sendResult = mqProducer.send(message);
        System.out.println(sendResult);
        System.out.println("#O##############################");

    }

    @Test
    public void testInvestQuery() throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {

        InvestDTO investDTO = investService.findById("8A015E745B3E716E0015B015B3E7173FA00001");
        if( InvestStatus.LOCAL_FROZEN_SUCCESS.getIndex() == investDTO.getStatus().getIndex() ){

        }

    }

    /**
     *
     * @throws InterruptedException
     * @throws RemotingException
     * @throws MQClientException
     * @throws MQBrokerException
     */
    @Test
    public void mySendMsg() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        // 测试短信
        // 数据库编辑，template 10.143.81.27 message库，表sms_template,用大括号{0}{1}{2}，表示参数，下标从0开始。对应下边数组第一个参数 wuhui 888
        Message message = new Message("myc_sms_sender","77777", "{\"templateId\":\"1000\",\"mobiles\":\"15811443876\",\"params\":[\"wuhui\",\"888\"]}".getBytes());
        SendResult sendResult = mqProducer.send(message);
        System.out.println(sendResult);
        System.out.println("###############################");

    }

    /**
     * 显示预约申请单------------------------------------>
     */
    @Test
    public void getReserveInfo() {
      /*  int a = investService.updateStatusById("8A015D0A5ACAA22A0015A015ACAA22A3400000", 6);
        System.out.println(a);*/
        // 显示预约申请单
    	System.out.println(reserveService.getReserveInfo("75044BF9-3020-4C0B-B247-D44D378B4981"));
    	System.out.println();
//        reserveService.getReserveInfo("199B94C4-1FF5-42F7-B0CD-83D755F69020");
    }

    /**
     * 显示预约申请单
     */
    @Test
    public void getReserveInfo222() {
      /*  int a = investService.updateStatusById("8A015D0A5ACAA22A0015A015ACAA22A3400000", 6);
        System.out.println(a);*/
        // 显示预约申请单
        // 张伟
        AccountDTO accountDTO = accountService.getByUserId("75044BF9-3020-4C0B-B247-D44D378B4981");
        System.out.println();
    }

    /**
     * 创建标的账户
     */
    @Test
    public void testCreateLoanAccount() {
        BigDecimal amount = new BigDecimal(100);

       // 20000203
        // 用户id
//        Boolean dto = loanAccountService.createLoanAccount("20000203", amount);

//        System.out.println(dto);
    }

    /**
     * 申请预约（北京银行）
     */
    @Test
    public void testApply() {
        BigDecimal amount = new BigDecimal(100);

        // 用户id
        FreezeResultDto dto = appointmentService.ApplyAppointment("0004", "083E6D4E-E59E-4371-A3C2-C711EBB1AED5", amount, BigDecimal.ZERO);

        System.out.println(dto);

        // 0004
        // 3df44999b4be5674c69df1fae4eed437
        // 8A1890815B28008E0015B015B283E516800009
    }



    /**
     * 北京解冻测试
     */
    @Test
    public void testJieDongBeijing() {
        BigDecimal amount = new BigDecimal(100);
        // invest id 10000104002
        // loanId: 20000203

        // 用户id
        FreezeResultDto dto = appointmentService.AllotInvestUnfreeze("10000104002", "3df44999b4be5674c69df1fae4eed437", "083E6D4E-E59E-4371-A3C2-C711EBB1AED5", "20000203", amount);

        System.out.println(dto);

    }

    /**
     * 本地转表弟
     */
    @Test
    public void testZhuanbiaodi() {
        BigDecimal amount = new BigDecimal(100);

//        String dto = appointmentService.AllotInvestSuccess("001", "e83e356255ced80f65ef8c04eac88f97", "b4c147b249762fe30f3323bef9f580fc", "investId", BigDecimal.ZERO);
//        System.out.println(dto);



       /* // 用户id
        String dto = appointmentService.AllotInvestSuccess("10000104002", "3df44999b4be5674c69df1fae4eed437", "fafcbdd2fea85976be62f66b2ca66b42", "50000001", null);

        System.out.println(dto);*/

    }

    /**
     * 生成
     */
    @Test
    public void reserveQueue() {
    	List<String> list = new ArrayList<>();
    	list.add("1");
    	// 显示预约申请单
        reserveService.reserveQueue("2", "2", "3", BigDecimal.ZERO, "name", "miaoshu", "描述", list);
    }

    /**
     * 测试预约投资
     */
    @Test
    public void submitReserveInvest() {
        // 显示预约申请单
        reserveService.submitReserveInvest("8A1890815B8E6C550015B015B8E9025E900004");


//        reserveService.submitReserveInvest("8A015FAC5B1EDE390015B015B1EDE39F200000");
       /* User user = userService.findByUserId("083E6D4E-E59E-4371-A3C2-C711EBB1AED5");
        System.out.println(user.getName());
        System.out.println(user.getMobile());*/


//        reserveService.submitReserveInvest("8A18908B5B2A3A110015B015B3E3CDB9000013");
//        reserveService.submitReserveInvest("8A015FAC5B1EDE390015B015B1EDE39F200000");
       /* User user = userService.findByUserId("083E6D4E-E59E-4371-A3C2-C711EBB1AED5");
        System.out.println(user.getName());
        System.out.println(user.getMobile());*/
    }

    /**
     * 测试产品查询
     */
    @Test
    public void queryRegular() {

        ProductRegularDTO dto = productRegularService.findByTitle("美容宝-2");
        System.out.println(dto);
    }

    @Reference
    private LoanService loanService;

    @Test
    public void createLoan() {

        loanService.openLoanAccount("dq-8A1890815B485BE90015B015B486AB60500009");
    }

    /** 申请单DAO  */
    @Autowired
    private ReserveRequestDao reserveRequestDao;

    @Test
    public void testDao() {
       ReserveRequest reserveRequest = reserveRequestDao.findByRequestId("8A1890815B7FB1BA0015B015B8052175C00014");
        System.out.println();

    }

    /**
     * 申请预约模拟前台
     */
    @Test
    public void testApplyFore() {
/*        useri_id	 id_card	 platcust:
        085E39F5-EE68-47B5-8966-E3B4ACF223A8:乐丽姿:530121198907165303  plus:2017033014290521510016
        080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8:汤古韵:410482198409244369 	plus:2017033014322086710017*/
//        String userId="085E39F5-EE68-47B5-8966-E3B4ACF223A8";


        // 18001308636
        String userId="75044BF9-3020-4C0B-B247-D44D378B4981";
        String proStr="057433E4-32BF-4226-82D6-2141C10E6323,";
        String periodIds="2c9514815aefa2c7015aefad2b8d0002";
        String rateIds = "0850B55C-C176-4109-9D78-99D255ACE298";
        BigDecimal amount = new BigDecimal(61);
        int waitPeriod = 3;
        String reserveCoupon = "23C3F1F3-DC5E-486A-9CBD-C9B340ACAB79";
        Boolean flag = reserveService.submitReserveRequest(userId, proStr, periodIds, rateIds, amount, waitPeriod,
                reserveCoupon);
        System.err.println(flag);


        // 1 执行申请
        //
    }

    /**
     * 测试预约投资
     */
    @Test
    public void sebMq() {
        // 构建单笔投资MQ消息
        Map<String, Object> mqMap = new HashMap<String, Object>();
        // 用户id
        mqMap.put("userId", "080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8");
        // 抵现券金额
        mqMap.put("couponCashValue", "0");
        // 加息券加息 eg:1% 传 0.01
        mqMap.put("interest", "8A015E745B41E5870015B015B41E59C6B00001");
        // 投资单金额（撮合资金）
        mqMap.put("investAmount",  "100.00");
        // 投资订单id
        mqMap.put("investId", "8A015E745B41E5870015B015B41E59C6B00001");
        // 调账户冻结是返回的操作流水号
        mqMap.put("applyFundOperateId", "5c5b24c4d4ab6f1e059f38462224a293");
        // 产品编号
        mqMap.put("prodId", "8A18908B5B41DB430015B015B41DF2C0000003");
        try {
            String a = "{\"queueId\":\"8A1890815B4818030015B015B4818030200000\",\"productId\":\"dq-8A1890815B4818030015B015B4819D84200004\",\"interest\":\"\",\"couponCashValue\":\"0\",\"applyFundOperateId\":\"9d9c7b6cdc4a3677e6f84395f58f78ca\",\"investAmount\":\"70.00\",\"investId\":\"8A015E745B4820DB0015B015B4820DB9000000\",\"prodId\":\"8A1890815B4818030015B015B4819D83700003\",\"userId\":\"080F1371-D8BE-4B4E-B17C-1B45D5FDB4A8\"}";
            // 通知消费端做 1、解冻北京；2、同步投资（同步）北京（转账）；3、解冻本地转账（本地）；4、更新状态
//            Message message = new Message(MQTopic.MYC_RESERVE_INVEST.getValue(), JsonHelper.getJson(mqMap).getBytes());
            Message message = new Message(MQTopic.MYC_RESERVE_INVEST.getValue(),a.getBytes());
            SendResult sendResult = mqProducer.send(message);
            System.out.println(sendResult);
//        } catch (JsonProcessingException e) {
        } catch (Exception e) {
            throw new ServiceException(ExceptionCode.SEND_INVEST_MQ_FAIL.getIndex(), ExceptionCode.SEND_INVEST_MQ_FAIL.getErrMsg());
        }
    }




    @Test
    public void testGuoQi() {
       reserveService.checkReserveRequests();
    }

    @Test
    public void testInservtInvest() {
        String investId = UUIDGenerator.generate();
        // 构建投资记录并入库。
        Invest invest = new Invest();
        invest.setId(investId);
        invest.setMobile("15811443876");
        invest.setName("aaaa");
        // 产品类型键值
        invest.setLoanTypeKey("YY");
        // 产品类型id
        invest.setLoanTypeId("12345");
        // 用户id
        invest.setUserId("aa");
        // 标的id
        invest.setLoanId("bb");
        // 产品id
        invest.setProductId("cc");
        // 投资金额
        invest.setAmount(BigDecimal.ONE);
        // 利率
        invest.setRate(1);
        // 加息利率
        invest.setPlusRate(2);
        // 来源
        invest.setSource(InvestSource.RESERVE);
        // 年月日
        invest.setYears(1);
        invest.setMonths(2);
        invest.setDays(3);
        // 还款方式
        invest.setRepaymentMethod(RepaymentMethod.BulletRepayment);
        // 状态，因为预约成功时已经冻结成功，所以入库时状态值为 LOCAL_FROZEN_SUCCESS(1, "本地资金冻结成功（待北京解冻，北京银行解冻中）")
        invest.setStatus(InvestStatus.LOCAL_FROZEN_SUCCESS);
        Date submitTime = new Date();
        // 提交时间
        invest.setSubmitTime(submitTime);

        ReserveRequest reserveRequest = reserveRequestDao.findById("8A015E745B8972B60015B015B8972B66C00000");
        // 使用的红包id
        if(null != reserveRequest && StringUtils.isNotBlank(reserveRequest.getReserveCoupon())){
            invest.setCouponPlacememtId(reserveRequest.getReserveCoupon());
        }

        // 使用的红包id
        investDao.save(invest);
    }

    @Test
    public void testCancel() {
       reserveService.userCancelReserve("8A18908D5B93B67F0015B015B93D169AC00009");
//        int a = investService.updateStatusById("8A18908B5B726FF80015B015B728F105900009", InvestStatus.BJ_DF_SUCCESS.getIndex());
//        System.out.println(a);
    }

    /**
     * 测试设置状态8A0110FF5B70B33B0015B015B70B6447700001
     */
    @Test
    public void ReserveSettle() {
//       reserveService.settleReserve("8A1890815B485BE90015B015B4869E0DC00005", true);
//        CouponPlacement findCouponPlacementbyId = couponService.findCouponPlacementbyId(null, "5D89AAE9-3E3E-4DDE-A5CE-4F0FA0E6F894");
//        System.out.println(findCouponPlacementbyId);

/*        int updateRegularCount = reserveRequestDao.updateRegularStatus("dq-8A1890815B7FEBFF0015B015B7FED07F900003", ProductStatus.FINISHED.getIndex());
        System.out.println();*/

//        System.out.println(reserveService.reserveAmountByUserId("75044BF9-3020-4C0B-B247-D44D378B4981"));

/*        Page<ReserveRequestShowDTO> dtoPage = reserveService.findReserveRequests("189FCC0D-9A33-4839-90FC-1901289C7CBE", 1, 10);

        System.out.println(dtoPage);*/

       // 查账户表
        List<String> frozenCodeList = reserveRequestDao.findFundOperate("8A01580F5B6A6A5F0015B015B6A6A5FF300000");
        // 只有一条记录
        if(null != frozenCodeList && frozenCodeList.size() == 1) {
            System.out.println(11111);
        }
    }

}

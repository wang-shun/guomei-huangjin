package com.gomemyc.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.gomemyc.account.service.AssignService;
import com.gomemyc.common.page.Page;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dto.DebtassignProductDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.enums.DebtAssignPricingMethod;
import com.gomemyc.invest.enums.DebtAssignStatus;
import com.gomemyc.invest.service.DebtAssignRequestService;
import com.gomemyc.invest.service.LoanService;

public class DebtAssignRequestTest extends BaseFunctionalTestCase {

	@Reference
	private DebtAssignRequestService debtAssignRequestService;
	@Reference
	private LoanService loanService;
	@Reference
	private AssignService assignService;
    @Autowired
    @Qualifier("producer")
    private MQProducer mqProducer;
    @Autowired
	private ProductRegularDao productRegularDao;
	/**
	 * 债转申请
	 */
	@Test
	public void applyDebtAssign(){
		System.out.println("1111");
		debtAssignRequestService.applyDebtAssign("00009D74-01BA-4098-9416-B16D02F109BA", "8A015D0A5ACA988F0015A015ACA988FB300001", BigDecimal.valueOf(3500),  DebtAssignPricingMethod.ORIGINAL, BigDecimal.valueOf(0), true, 3);
	}
	/**
	 * 撤销
	 */
	@Test
	public void cancelDebtAssign(){
		debtAssignRequestService.cancelDebtAssign("00009D74-01BA-4098-9416-B16D02F109BA", "pj-8A015FD25B1E70440015B015B1E704D2600001","00009D74-01BA-4098-9416-B16D02F109BA","ht");
	}
	/**
	 * 查询日志
	 */
	@Test
	public void findListByUserIdAndProductId(){
		debtAssignRequestService.findListByUserIdAndProductId("u-1111", "dq-558db288-0669-11e7-a905-0050569426e2");
	}
	/**
	 * 最大可转金额
	 */
	@Test
	public void queryDebtMaxAmount(){
		BigDecimal amount=debtAssignRequestService.queryDebtMaxAmount("pj-8A015FD25B18D2830015B015B18D3CD7300001", "8A015D0A5ACA988F0015A015ACA988FB300001", 3);
		System.out.println(amount);
	}
	/**
	 * 债转方案查询
	 */
	@Test
	public void getDebtPlan(){
		debtAssignRequestService.getDebtPlan("00009D74-01BA-4098-9416-B16D02F109BA", "8A015D0A5ACA988F0015A015ACA988FB300001", "pj-8A015FD25B18D2830015B015B18D3CD7300001");
	}
	/**
	 * 查询自己债转产品列表
	 */
	@Test
	public void findPageByUserIdAndStatus(){
		Page<DebtassignProductDTO> page=debtAssignRequestService.findPageByUserIdAndStatus("00009D74-01BA-4098-9416-B16D02F109BA", DebtAssignStatus.WAIT_END_CHECK, 1, 10);
		System.out.println(page.getPageSize());
	}
	@Test
	public void test(){
		Map<String, String> map=new HashMap<String, String>();
		map.put("productID", "dq-a042005f-0703-11e7-a905-0050569426e2");
		map.put("userid", "aaa1111111111");
		Message message = new Message("myc_debtassign","debtassignCancel","dq-79850086-0af6-11e7-a905-0050569426e2".getBytes());
		
		try {
			SendResult sendResult = mqProducer.send(message);
			System.out.println(sendResult);
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (MQBrokerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testaccount(){
//		String accountNo=assignService.assignApply("dq-79850086-0af6-11e7-a905-0050569426e2", "8A015D0A5ACAA22A0015A015ACAA22A3400000", BigDecimal.valueOf(2000), "00003BB4-BC10-486D-9CA5-16B6E00B042F", "8160e1bc-0859-11e7-a905-0050569426e2");
//		System.out.println(accountNo);
		//String accountNo=assignService.assignApply("dq-79850086-0af6-11e7-a905-0050569426e2", "8A015D0A5ACAA22A0015A015ACAA22A3400000", BigDecimal.valueOf(2000), "00003BB4-BC10-486D-9CA5-16B6E00B042F", "8160e1bc-0859-11e7-a905-0050569426e2");
		//System.out.println(accountNo);
	}
	
	@Test
	public void debtStatis(){
		debtAssignRequestService.debtStatistics(new Date());
	}
	
	@Test
	public void updatestatus(){
		debtAssignRequestService.updateDebtStatus("8A015D0A5ADBA7C60015A015ADBA7C69200000", DebtAssignStatus.ALREADY_TRANSFERRED);
	}
	
	@Test
	public void test1(){
		ProductDTO dto=loanService.getProduct("pj-8A015FD25B18D2830015B015B18D3CD7300001");
		System.out.println(dto.getId());
	}
}

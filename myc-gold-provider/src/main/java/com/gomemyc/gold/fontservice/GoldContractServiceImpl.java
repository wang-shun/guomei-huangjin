package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.utils.DateUtils;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dao.GoldProductDao;
import com.gomemyc.gold.dto.ContractDTO;
import com.gomemyc.gold.entity.extend.Contract;
import com.gomemyc.gold.entity.extend.ContractInvest;
import com.gomemyc.gold.service.GoldContractService;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.model.user.User;
import com.gomemyc.user.api.UserService;

/**
 * @ClassName:GoldContractServiceImpl.java
 * @Description:
 * @author zhuyunpeng
 * @date 2017年4月10日
 */
@Service(timeout = 6000)
public class GoldContractServiceImpl implements GoldContractService {

	private static final Logger logger = LoggerFactory.getLogger(GoldContractServiceImpl.class);

	@Reference
	private UserService userService;

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;

	@Autowired
	private GoldProductDao goldProductDao;

	/**
	 * 
	 * @author zhuyunpeng
	 * @date 2017年04月11日
	 * @param investId 订单ID
	 * @return ContractDTO
	 * @since JDK 1.8 getContractDTOByInvestId
	 */
	@Override
	public ContractDTO getContractDTOByInvestId(String investId)  {

		logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [investId={}]", investId);
		if (StringUtils.isBlank(investId) || investId.length() == 0) {
			return null;
		}
		// 依据investId得到contractInvest(包含userId,与购买克重)
		ContractInvest contractInvest = goldInvestOrderDao.getUserIdAndRealWeightById(investId);
		String userId = "";
		BigDecimal goldWeight = new BigDecimal(0.000);
		User user = null;
		String productId = "";
		String partiesA = "";
		String idCardNo = "";
		try{
			if (contractInvest != null) {
				logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [contractInvest={}]",
						contractInvest.toString());
				userId = contractInvest.getUserId();
				goldWeight = contractInvest.getGoldWeight();
				productId = contractInvest.getProductId();
				user = userService.findByUserId(userId);
			}else {
				logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [contractInvest={}]",contractInvest);
				throw new ServiceException(GoldInfoCode.CONTRACT_INVEST_ORDER_NOT_EXIST.getStatus(),GoldInfoCode.CONTRACT_INVEST_ORDER_NOT_EXIST.getMsg());
			}
			// 依据userId调用userService得到user
			logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [userId={}],[goldWeight={}]",userId,goldWeight);
		}catch(Exception e){
			logger.error("getContractDTOByInvestId in GoldContractServiceImpl ,error {}",e);
			throw new ServiceException(GoldInfoCode.CONTRACT_USER_NOT_EXIST.getStatus(),GoldInfoCode.CONTRACT_USER_NOT_EXIST.getMsg());
		}
			if (user != null) {
				logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [user={}]",user.toString());
				partiesA = user.getName() == null ? "" : user.getName();
				idCardNo = user.getIdNumber() == null ? "" : user.getIdNumber();
			}
		
		// 通过Id获取黄金产品与合同相关的对象
		Contract contract = goldProductDao.getContractByProductId(productId);
		if (contract == null) {
			logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [contract={}]",contract);
			throw new ServiceException(GoldInfoCode.CONTRACT_NOT_EXIST.getStatus(),GoldInfoCode.CONTRACT_NOT_EXIST.getMsg());
		}
		logger.info("getContractDTOByInvestId in GoldContractServiceImpl ,the param [contract={}]",contract.toString());
		String goldName = "黄金9999";
		BigDecimal rate = new BigDecimal(contract.getRate() ).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
		Date startTime = contract.getStartTime();
		Date finishTime = contract.getFinishTime();
		String loanId = contract.getLoanId();
		String startYear = "";
		String startMonth = "";
		String startDay = "";
		String endYear = "";
		String endMonth = "";
		String endDay = "";
		if (startTime != null && startTime.toString().length()>8) {
			startYear = DateUtils.getYear(startTime);
			startMonth = DateUtils.getMonth(startTime);
			startDay = DateUtils.getDay(startTime);
		}
		if (finishTime != null && finishTime.toString().length()>8) {
			endYear = DateUtils.getYear(finishTime);
			endMonth = DateUtils.getMonth(finishTime);
			endDay = DateUtils.getDay(finishTime);
		}
		
		String partiesB = "北京盈衍网络科技有限公司";
		String currentAdress = "北京朝阳区浦项中心B座17层6-8室";
		ContractDTO contractDTO = new ContractDTO(partiesA, partiesB, idCardNo, currentAdress, 
												  goldName,goldWeight, rate, startYear,startMonth, 
												  startDay, endYear, endMonth, endDay, loanId, productId);
		
		return contractDTO;
	}

}

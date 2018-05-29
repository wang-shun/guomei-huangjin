package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.gold.dao.GoldInvestOrderInfoDao;
import com.gomemyc.gold.dto.GoldInvestOrderInfoDTO;
import com.gomemyc.gold.entity.GoldInvestOrderInfo;
import com.gomemyc.gold.service.GoldInvestOrderInfoService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;

/**
 * GoldInvestOrderInfoService
 * Date:     2017年3月24日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
@Service(timeout=6000)
public class GoldInvestOrderInfoServiceImpl implements GoldInvestOrderInfoService {
	
	private static final Logger logger = LoggerFactory.getLogger(GoldInvestOrderInfoServiceImpl.class);

	@Autowired
	private GoldInvestOrderInfoDao goldInvestOrderInfoDao;
	
	/**
	 * 
	 * 根据订单号,查询黄金投资订单详情表
	 * 
	 * @param  reqNo (String) 订单号
	 * @return GoldInvestOrderInfoDTO
	 * @ServiceException
	 *                30000   参数错误
	 *                30003   投资订单详情信息不存在
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月24日
	 */
	@Override
	public GoldInvestOrderInfoDTO selectByReqNo(String reqNo) throws ServiceException {
		logger.info("selectByReqNo  in  GoldInvestOrderInfoServiceImpl ,the param [reqNo={}]",reqNo);
		if(StringUtils.isBlank(reqNo))
			throw new ServiceException(GoldInfoCode.WRONG_PARAMETERS.getStatus(), GoldInfoCode.WRONG_PARAMETERS.getMsg());
		GoldInvestOrderInfoDTO goldInvestOrderInfoDTO = new GoldInvestOrderInfoDTO();
		//根据订单号查询投资订单详情
		GoldInvestOrderInfo goldInvestOrderInfo = goldInvestOrderInfoDao.selectByReqNo(reqNo);
		logger.info("selectByReqNo  in  GoldInvestOrderInfoServiceImpl ,the result [goldInvestOrderInfo={}]",goldInvestOrderInfo);
		if(goldInvestOrderInfo == null)
			 throw new ServiceException(GoldInfoCode.INVEST_ORDER_DETAIL_NOT_EXIST.getStatus(), GoldInfoCode.INVEST_ORDER_DETAIL_NOT_EXIST.getMsg());
		BeanMapper.copy(goldInvestOrderInfo, goldInvestOrderInfoDTO);
		logger.info("selectByReqNo  in  GoldInvestOrderInfoServiceImpl ,the result [goldInvestOrderInfoDTO={}]",goldInvestOrderInfoDTO);
		return goldInvestOrderInfoDTO;
	}

	/**
	 * 
	 * 保存黄金投资订单详情
	 * 
	 * @param reqNo             (String) 订单号(必填)
	 * @param amount            (String) 投资金额(必填)
	 * @param remainAmount      (BigDecimal) 剩余支付金额(必填)
	 * @param balancePaidAmount (BigDecimal) 余额支付(必填)
	 * @param couponAmount      (BigDecimal) 红包金额(选填)
	 * @param couponId          (String) 奖券Id(选填)
	 * @return                  (Integer) 影响的行数
	 * @ServiceException
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月24日
	 */
	@Override
	public Integer saveGoldInvestOrderInfo(String reqNo, 
									   BigDecimal amount, 
									   BigDecimal remainAmount,
									   BigDecimal balancePaidAmount, 
									   BigDecimal couponAmount,
									   String couponId) throws ServiceException{
		logger.info("saveGoldInvestOrderInfo  in  GoldInvestOrderInfoServiceImpl ,the param [reqNo={}],[amount={}],[remainAmount={}],[balancePaidAmount={}],[couponAmount={}],[couponId={}],", reqNo, amount, remainAmount, balancePaidAmount, couponAmount, couponId);
		//将数据插入投资订单详情
		GoldInvestOrderInfo goldInvestOrderInfo = new GoldInvestOrderInfo();
		goldInvestOrderInfo.setAmount(amount);
		goldInvestOrderInfo.setBalancePaidAmount(balancePaidAmount);
		goldInvestOrderInfo.setRemainAmount(remainAmount);
		goldInvestOrderInfo.setReqNo(reqNo);
		goldInvestOrderInfo.setId(UUIDGenerator.generate());
		goldInvestOrderInfo.setCouponAmount(couponAmount);
		goldInvestOrderInfo.setCouponId(couponId);
		goldInvestOrderInfo.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
		//保存进数据库
		int insertSelective = goldInvestOrderInfoDao.insertSelective(goldInvestOrderInfo);
		logger.info("saveGoldInvestOrderInfo  in  GoldInvestOrderInfoServiceImpl ,the param [insertSelective={}],", insertSelective);
		if(insertSelective == 0) 
			throw new ServiceException(GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(), GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
		return insertSelective;
	}
}
  
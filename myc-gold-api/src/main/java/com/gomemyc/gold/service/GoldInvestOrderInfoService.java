package com.gomemyc.gold.service;

import java.math.BigDecimal;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.gold.dto.GoldInvestOrderInfoDTO;

/**
 * GoldInvestOrderInfoService
 * Date:     2017年3月24日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
public interface GoldInvestOrderInfoService {

	/**
	 * 
	 * 根据订单号,查询黄金投资订单详情表
	 * 
	 * @param  reqNo (String) 订单号(必填)
	 * @return GoldInvestOrderInfoDTO
	 * @ServiceException
	 *                30000   参数错误
	 *                30003   投资订单详情信息不存在
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月24日
	 */
    GoldInvestOrderInfoDTO selectByReqNo(String reqNo) throws ServiceException;
    
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
    Integer saveGoldInvestOrderInfo(String reqNo, BigDecimal amount,BigDecimal remainAmount, BigDecimal balancePaidAmount, BigDecimal couponAmount, String couponId) throws ServiceException;
    
}
  
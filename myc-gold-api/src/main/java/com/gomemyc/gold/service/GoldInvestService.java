/** 
 * Project Name:myc-gold-api 
 * File Name:GoldInvestService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月6日下午2:36:05 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.gold.dto.FinishOrdersDTO;
import com.gomemyc.gold.dto.GoldEarningsDTO;
import com.gomemyc.gold.dto.GoldInvestOrderDTO;

/**
 * ClassName:GoldInvestService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月6日 下午2:36:05 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
public interface GoldInvestService {
	
	/**
	 * 
	 * 用户预下单
	 * 
	 * @param amount    (String) 投资金额(必填)
	 * @param uid       (String) 用户id(必填)
	 * @param productId (String) 产品id(必填)
	 * @param couponId  (String) 红包id(选填)
	 * @return GoldInvestOrderDTO
	 * @ServiceException
	 *                30000:参数错误
	 *                50000:操作失败，请重试 
	 *                50001:预下单失败
	 *                60000:产品不存在
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月6日
	 */
	GoldInvestOrderDTO prePay(BigDecimal amount,String uid,String productId,String couponId) throws ServiceException;
	
	/**
	 * 
	 * 用户确认下单(查询用户预下单信息，产品信息，产品详情信息)
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @return HashMap<String, Object>
	 * @ServiceException
	 *                30000:参数错误
	 *                30001:预下单信息不存在
	 *                50002:确认下单失败
	 *                60000:产品不存在
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	 HashMap<String, Object> confirmBySelect(String uid,String reqNo) throws ServiceException;
	
	/**
	 * 
	 * 用户确认下单(调用黄金钱包接口，确认下单)
	 * 
	 * @param uid        (String) 用户id(必填)
	 * @param reqNo      (String) 产品id(必填)
	 * @param investId   (String) 投资id(必填)
	 * @return boolean true确认下单成功  false确认下单失败
	 * @ServiceException
	 *                30001:预下单信息不存在
	 *                30005:确认下单信息不存在
	 *                50000:操作失败，请重试 
	 *                50002:确认下单失败
	 *                50003:确认下单处理中
	 *                60000:产品不存在
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	 boolean confirmByGold(String uid,String reqNo,String investId) throws ServiceException;
	
	/**
	 * 
	 * 用户确认下单(更新预下单和确认下单信息)
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @ServiceException
	 *                50002:确认下单失败
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	 void confirmUpdateStatus(String uid,String reqNo) throws ServiceException;
	
	/**
	 * 
	 * 用户确认下单(更新订单状态，保存订单信息)
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @return  GoldInvestOrderDTO
	 * @ServiceException
	 *                50002:确认下单失败
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月7日
	 */
	 GoldInvestOrderDTO confirmSaveInvestOrder(String uid,String reqNo) throws ServiceException;
	
	/**
	 * 
	 * 查询购买记录
	 * 
	 * @param userId  用户id
	 * @param productId  产品id
	 * @param page 页码
	 * @param   pageSize  页内数据量
	 * @return  Page<GoldInvestOrderDTO>
	 * @since JDK 1.8
	 * @author TianBin 
	 * @param pageSize 
	 * @param page 
	 * @date 2017年3月6日
	 */
	Page<GoldInvestOrderDTO> listPageByUserIdLoanId(String userId,String productId, int page, int pageSize);
	
	/**
	 * 
	 * 查询交易结果
	 * 
	 * @param userId  用户id
	 * @param reqNo   请求订单号
	 * @return  GoldInvestOrderDTO
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月6日
	 */
	GoldInvestOrderDTO findResultByUserIdReqNo(String userId,String reqNo);
	
	/**
	 * 
	 * 统计黄金克数
	 * 
	 * @param userId  用户id
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author TianBin 
	 * @date 2017年3月6日
	 */
	BigDecimal statisticsGoldWeight(String userId);

	/**
	 *
	 * 查询募集中列表
	 *
	 * @param userId  用户id
	 * @param page 页码
	 * @param   pageSize  页内数据量
	 * @return Page<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author TianBin
	 * @date 2017年3月6日
	 */
	Page<FinishOrdersDTO>  listPageCollectByUserId(String userId,Integer page,Integer pageSize);

	/**
	 *
	 * 查询收益中列表
	 *
	 * @param userId  用户id
	 * @param page 页码
	 * @param   pageSize  页内数据量
	 * @return Page<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author TianBin
	 * @date 2017年3月7日
	 */
	Page<FinishOrdersDTO>  listPageEarningsByUserId(String userId,Integer page,Integer pageSize);

	/**
	 *
	 * 查询已完结列表
	 *
	 * @param userId  用户id
	 * @param page 页码
	 * @param   pageSize  页内数据量
	 * @return
	 * @since JDK 1.8
	 * @author TianBin
	 * @date 2017年3月7日
	 */

	Page<FinishOrdersDTO> listPageClearedByUserId(String  userId, Integer page,Integer pageSize);

	/**
	 *
	 *向用户表中插入收益数据
	 *
	 * @param  </GoldEarningsDTO>   对象
	 * @return  插入时影响的行数。
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	Integer saveEarnings(GoldEarningsDTO goldEarningsDTO);

	/**
	 * 
	 * 查询昨日收益
	 * 
	 * @param userId  用户id
	 * @return  GoldEarnings
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	 GoldEarningsDTO getYesterdayEarnings(String userId);
	
	/**
	 * 
	 * 按照页数查询历史收益
	 * 
	 * @param userId  用户id
	 * @param page  页码
	 * @param pageSize  每一页显示的条数
	 * @return  List<GoldEarnings>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	 List<GoldEarningsDTO> getHistoryEarnings(String userId, int page, int pageSize);
	
	
	/**
	 * 
	 * 查询历史收益总记录数
	 * 
	 * @param userId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
   	 int getHistoryEarningrEcord(String userId);
	/**
	 *
	 * 查询历史总收益
	 *
	 * @param userId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	BigDecimal getHistoryTotalEarningrs(String userId);
	/**
 	 * 
 	 * 根据产品id查询客户购买总记录数
 	 * 
 	 * @param userId  用户id
 	 * @param userId  用户id
 	 * @return  int
 	 * @since JDK 1.8
 	 * @author liuqiangbin
 	 * @date 2017年3月14日
 	 */
	Integer findRecordAmount(String userId, String productId);

	/**
	 *
	 * 根据产品id和订单号查询信息
	 *
	 * @param userId  用户id
	 * @param reqNo  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	GoldInvestOrderDTO getResultByUserIdReqNo(String userId,String reqNo);
	/**
	 *
	 * 根据产品id和订单号查询信息
	 *
	 * @param productId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
	List<GoldInvestOrderDTO> selectGoldInvestOrderByProductId(String productId);
	
	/**
	 *
	 * 根据产品id和状态，查询所有关联订单的投资总额
	 *
	 * @param productId  商品id
	 * @param orderStatus 订单状态
	 * @param orderType 交易类型
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年4月26日
	 */
	BigDecimal getSumAmountByProductId(String productId, Integer orderType, Integer orderStatus);
}
  
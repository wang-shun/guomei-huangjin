/** 
 * Project Name:myc-rest 
 * File Name:GoldInvestController.java 
 * Package Name:com.gomemyc.controller 
 * Date:2017年3月5日下午12:10:57 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/

package com.gomemyc.controller;

import java.math.BigDecimal;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.gomemyc.gold.dto.FinishOrdersDTO;
import com.gomemyc.gold.dto.GoldProductDetailsDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.annotation.Validate;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.gold.dto.GoldInvestOrderDTO;
import com.gomemyc.gold.service.GoldInvestService;
import com.gomemyc.gold.service.GoldProductService;
import com.gomemyc.http.MediaTypes;
import com.gomemyc.trade.service.GoldService;
import com.gomemyc.util.InfoCode;
import com.gomemyc.util.RestResp;

/**
 * ClassName:GoldInvestController
 * Function: TODO ADD FUNCTION.
 * Reason: TODO ADD REASON.
 * Date: 2017年3月5日 下午12:10:57
 * 
 * @author TianBin
 * @version
 * @since JDK 1.8
 * @see
 * @description
 */
@Component
@Path("/api/v1/gold/invest")
@Produces(MediaTypes.JSON_UTF_8)
public class GoldInvestController {

	@Reference
	private GoldInvestService goldInvestService;
	
	@Reference
	private GoldProductService goldProductService;
	
	@Reference
	private GoldService goldService;
	

	private static final Logger logger = LoggerFactory.getLogger(GoldInvestController.class);

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
	@POST
//	@Validate
	@Path("/prepay")
	public Object prePay(@FormParam("amount") BigDecimal amount,
						 @FormParam("uid") String uid,
						 @FormParam("productId") String productId,
						 @FormParam("couponId") String couponId) {
		logger.info("prePay in GoldInvestController, the params amount is [{}],uid is [{}],productId is [{}],couponId is [{}]",amount,uid,productId,couponId);
		if (amount.compareTo(BigDecimal.ZERO) < 1 ||
			StringUtils.isBlank(uid) ||
			StringUtils.isBlank(productId))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.prePay(amount, uid, productId, couponId);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 30000:
					throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				case 50001:
					throw new AppRuntimeException(InfoCode.PREPAY_ORDER_ERROR);
				case 51000:
					throw new AppRuntimeException(InfoCode.BALANCE_ERROR);
				case 60000:
					throw new AppRuntimeException(InfoCode.LOAN_NOT_EXITS);
				case 60001:
					throw new AppRuntimeException(InfoCode.LOAN_NOT_OPEN);
				case 70000:
					throw new AppRuntimeException(InfoCode.ACCOUNT_ERROR);
				case 70001:
					throw new AppRuntimeException(InfoCode.ACCOUNT_NOT_OPEN);
				case 80000:
					throw new AppRuntimeException(InfoCode.USER_ERROR);
				case 80001:
					throw new AppRuntimeException(InfoCode.USER_NOT_EXITS);
				case 90000:
					throw new AppRuntimeException(InfoCode.PRODUCT_ERROR);
				case 90001:
					throw new AppRuntimeException(InfoCode.PRODUCT_NOT_RULES);
				case 90002:
					throw new AppRuntimeException(InfoCode.PRODUCT_RULES_NOT_EXIST);
				case 91000:
					throw new AppRuntimeException(InfoCode.COUPON_ERROR);
				case 91001:
					throw new AppRuntimeException(InfoCode.COUPON_NOT_EXIST);
				case 91002:
					throw new AppRuntimeException(InfoCode.COUPON_NOT_RULES);
				case 91003:
					throw new AppRuntimeException(InfoCode.COUPON_FREEZE_ERROR);
				case 92000:
					throw new AppRuntimeException(InfoCode.GOLD_ERROR);
				default:
					throw new AppRuntimeException(InfoCode.PREPAY_ORDER_ERROR);
			}
		} catch(Exception e){
			logger.error("prePay in GoldInvestController, the params error is [{}]","prepayError");
			logger.error("prePay in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * 
	 * 用户确认下单
	 * 
	 * @param uid   (String) 用户id(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @return GoldInvestOrderDTO
	 * @ServiceException
	 *                30000:参数错误
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
	@POST
//	@Validate
	@Path("/confirm")
	public Object confirm(@FormParam("reqNo") String reqNo, 
						  @FormParam("uid")   String uid) {
		logger.info("confirm in GoldInvestController, the params uid is [{}],reqNo is [{}]",uid,reqNo);
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(reqNo))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldService.confirm(uid, reqNo);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 30000:
					throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
				case 30001:
					throw new AppRuntimeException(InfoCode.PREPAY_NOT_EXIST);
				case 30005:
					throw new AppRuntimeException(InfoCode.CONFIRM_NOT_EXIST);
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				case 50001:
					throw new AppRuntimeException(InfoCode.PREPAY_ORDER_ERROR);
				case 50002:
					throw new AppRuntimeException(InfoCode.AFFIRM_ORDER_ERROR);
				case 50003:
					throw new AppRuntimeException(InfoCode.AFFIRM_ORDER_PROCESSING);
				case 50004:
					throw new AppRuntimeException(InfoCode.PREPAY_ORDER_EXPIRE);
				case 50005:
					throw new AppRuntimeException(InfoCode.INVEST_INFO_ORDER_NOT_EXIST);
				case 50006:
					throw new AppRuntimeException(InfoCode.INVEST_SAVE_ERROR);
				case 50007:
					throw new AppRuntimeException(InfoCode.INVEST_NOT_EXIST);
				case 50008:
					throw new AppRuntimeException(InfoCode.REPEAT_SUBMIT_ORDERS);
				case 52000:
					throw new AppRuntimeException(InfoCode.LOCAL_FREEZE_AMOUNT_FAIL);
				case 53000:
					throw new AppRuntimeException(InfoCode.INVEST_STATUS_NOT_AVALIABLE_SYNC);
				case 53001:
					throw new AppRuntimeException(InfoCode.INVEST_INVOKE_BJ_INVEST_RESULT_NULL);
				case 53002:
					throw new AppRuntimeException(InfoCode.ORDER_QUERY_FAIL);
				case 53003:
					throw new AppRuntimeException(InfoCode.INVEST_SUCCESS_INVOKE_FAIL);
				case 53004:
					throw new AppRuntimeException(InfoCode.INVEST_SUCCESS_INVOKE_LOCAL_FAIL);
				case 53005:
					throw new AppRuntimeException(InfoCode.INVEST_LOAN_INVOKE_BJ_FAIL);
				case 54000:
					throw new AppRuntimeException(InfoCode.INVEST_SUCCESS_UPDATE_INVEST_STATUS_FAIL);
				case 54001:
					throw new AppRuntimeException(InfoCode.INVEST_UPDATE_LOCAL_FROZEN_NO_FAIL);
				case 54002:
					throw new AppRuntimeException(InfoCode.INVEST_UPDATE_FROZEN_FAILED);
				case 54003:
					throw new AppRuntimeException(InfoCode.INVEST_SUCCESS_UPDATE_BJ_STATUS);
				case 54004:
					throw new AppRuntimeException(InfoCode.INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS);
				case 60000:
					throw new AppRuntimeException(InfoCode.LOAN_NOT_EXITS);
				case 70000:
					throw new AppRuntimeException(InfoCode.ACCOUNT_ERROR);
				case 70001:
					throw new AppRuntimeException(InfoCode.ACCOUNT_NOT_OPEN);
				case 70002:
					throw new AppRuntimeException(InfoCode.ACCOUNT_BALANCE_NOT_ENOUGH);
				case 80000:
					throw new AppRuntimeException(InfoCode.USER_ERROR);
				case 80001:
					throw new AppRuntimeException(InfoCode.USER_NOT_EXITS);
				case 92000:
					throw new AppRuntimeException(InfoCode.GOLD_ERROR);
				default:
					throw new AppRuntimeException(InfoCode.AFFIRM_ORDER_ERROR);
			}
		} catch(Exception e){
			logger.error("confirm in GoldInvestController, the params error is [{}]","confirmError");
			logger.error("confirm in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
	}


	/**
	 *
	 * 查询购买记录
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @param productId 产品id
	 * @param page 页码
	 * @param pageSize 每页显示数量
	 * @return
	 * @since JDK 1.8
	 */
	@GET
	@Path("/list")
	public Object list(@QueryParam("uid") String uid,
					   @QueryParam("productId") String productId,
					   @DefaultValue("1") @QueryParam("page") int page,
					   @DefaultValue("10") @QueryParam("pageSize") int pageSize) {
		logger.info("list in GoldInvestController, the params uid is [{}],productId is [{}],page is [{}],pageSize is [{}]",uid,productId,page,pageSize);
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(productId))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.listPageByUserIdLoanId(uid,productId, page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("list in GoldInvestController, the params error is [{}]","listError");
			logger.error("list in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new Page<GoldInvestOrderDTO>(null,0,0,0L);
	}

	/**
	 *
	 * 查询交易结果 
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @param reqNo 订单号
	 * @return
	 * @since JDK 1.8
	 */

	@GET
	@Path("/result")
	public Object result(@QueryParam("uid") String uid,
						 @QueryParam("reqNo") String reqNo) {
		logger.info("result in GoldInvestController, the params uid is [{}],reqNo is [{}]",uid,reqNo);
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(reqNo))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.findResultByUserIdReqNo(uid, reqNo);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("result in GoldInvestController, the params error is [{}]","listError");
			logger.error("result in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new GoldInvestOrderDTO();
	}

	/**
	 *
	 * 查询募集中列表
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @param page 页码
	 * @param pageSize 每页显示数量
	 * @return
	 * @since JDK 1.8
	 */

	@GET
	@Path("/list/collect")
	public Object collect(@QueryParam("uid") String uid,
						  @DefaultValue("1") @QueryParam("page") int page,
						  @DefaultValue("10") @QueryParam("pageSize") int pageSize) {
		logger.info("collect in GoldInvestController, the params uid is [{}],page is [{}],pageSize is [{}]",uid,page,pageSize);
		if (StringUtils.isBlank(uid))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.listPageCollectByUserId(uid, page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("collect in GoldInvestController, the params error is [{}]","collectError");
			logger.error("collect in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new Page<FinishOrdersDTO>(null,0,0,0L);
	}

	/**
	 *
	 * 查询收益中列表
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户ud
	 * @param page 页码
	 * @param pageSize 每页显示数量
	 * @return
	 * @since JDK 1.8
	 */

	@GET
	@Path("/list/earnings")
	public Object earnings(@QueryParam("uid") String uid,
						   @DefaultValue("1") @QueryParam("page") int page,
						   @DefaultValue("10") @QueryParam("pageSize") int pageSize) {
		logger.info("earnings in GoldInvestController, the params uid is [{}],page is [{}],pageSize is [{}]",uid,page,pageSize);
		if (StringUtils.isBlank(uid))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.listPageEarningsByUserId( uid,   page,pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("earnings in GoldInvestController, the params error is [{}]","earningsError");
			logger.error("earnings in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new Page<FinishOrdersDTO>(null,0,0,0L);
	}

	/**
	 *
	 * 查询已完结列表 
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param uid 用户id
	 * @param page 页码
	 * @param pageSize 每页显示数量
	 * @return
	 * @since JDK 1.8
	 */

	@GET
	@Path("/list/cleared")
	public Object cleared(@QueryParam("uid") String uid,
						  @DefaultValue("1") @QueryParam("page") int page,
						  @DefaultValue("10") @QueryParam("pageSize") int pageSize) {
		
		logger.info("cleared in GoldInvestController, the params uid is [{}],page is [{}],pageSize is [{}]",uid,page,pageSize);
		if (StringUtils.isBlank(uid))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.listPageClearedByUserId( uid,   page,pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("cleared in GoldInvestController, the params error is [{}]","earningsError");
			logger.error("cleared in GoldInvestController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new Page<FinishOrdersDTO>(null,0,0,0L);
	}
	/**
	 *
	 * 投资预下单产品查询
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param reqNo  订单号
	 * @return RestResp
	 * @since JDK 1.8
	 */
	@GET
	@Path("/info")
	public Object info(@QueryParam("reqNo")String reqNo,@QueryParam("uid")String uid){
		logger.info("info in GoldProductController, the params reqNo is [{}],uid is [{}]",reqNo,uid);
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(reqNo))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldInvestService.getResultByUserIdReqNo(uid,reqNo);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("info in GoldProductController, the params error is [{}]","infoError");
			logger.error("info in GoldProductController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new GoldInvestOrderDTO ();
	}
}

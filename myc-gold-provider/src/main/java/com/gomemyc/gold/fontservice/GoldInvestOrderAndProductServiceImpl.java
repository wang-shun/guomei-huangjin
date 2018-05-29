package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.coupon.api.CouponService;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dao.GoldInvestOrderInfoDao;
import com.gomemyc.gold.dao.GoldProductInfoDao;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.entity.GoldInvestOrderInfo;
import com.gomemyc.gold.entity.GoldProductInfo;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.service.GoldInvestOrderAndProductService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.service.InvestService;

/** 
 * ClassName:GoldInvestOrderAndProductServiceImpl
 * Date:     2017年3月28日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 */
@Service(timeout=6000)
public class GoldInvestOrderAndProductServiceImpl implements GoldInvestOrderAndProductService{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldInvestOrderAndProductServiceImpl.class);

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;
	
	@Autowired
	private GoldProductInfoDao goldProductInfoDao;
	
	@Autowired
	private GoldInvestOrderInfoDao goldInvestOrderInfoDao;
	
	@Reference
	private InvestService investService;
	
	@Reference
	private CouponService couponService;
	
	/**
	 * 
	 * 查询订单表和产品表，更新订单状态和剩余可投资金额
	 * 
	 * @param currentTime (String) 当前时间(必填) 格式
	 * @ServiceException
	 *                30001   预下单信息不存在
	 *                50000   操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月28日
	 */
	@Override
	public void updatePrepayStatusAndBalance(String currentTime) throws ServiceException {
		logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [currentTime={}]",currentTime);
		//根据当前时间 查询所有过期的预下单订单
		List<GoldInvestOrder> listGoldInvestOrder = goldInvestOrderDao.listByCurrentTime(DateUtil.getDateTime(),  GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus());
		//判断是否有过期的预下单订单
		if(listGoldInvestOrder == null || listGoldInvestOrder.size() == 0){
			logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.PREPAY_NOT_EXIST.getStatus(),GoldInfoCode.PREPAY_NOT_EXIST.getMsg());
			return ;
		}
		logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [listGoldInvestOrder={}]",listGoldInvestOrder);
		//遍历订单
		for(GoldInvestOrder goldInvestOrder : listGoldInvestOrder)
		{
			//根据预下单信息，查询是否有成功的确认下单信息
			GoldInvestOrder goldInvestOrderConfirm = goldInvestOrderDao.selectByReqNo(goldInvestOrder.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
			if(goldInvestOrderConfirm != null){
				goldInvestOrderDao.updateByUseridAndReqNo(goldInvestOrderConfirm.getUserId(), goldInvestOrderConfirm.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_SUCCESS.getStatus());
				continue ;
			}
			//根据预下单信息，查询是否有处理中的确认下单信息
			GoldInvestOrder goldInvestOrderProcess = goldInvestOrderDao.selectByReqNo(goldInvestOrder.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_PROCESSING.getStatus());
			if(goldInvestOrderProcess != null){
				continue ;
			}
			//查询产品信息
			GoldProductInfo goldProductInfo = goldProductInfoDao.selectByPrimaryKey(goldInvestOrder.getProductId());
			logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [goldProductInfo={}]",goldProductInfo);
			//取出红包Id
			GoldInvestOrderInfo goldInvestOrderInfo = goldInvestOrderInfoDao.selectByReqNo(goldInvestOrder.getReqNo());
			logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [goldInvestOrderInfo={}]",goldInvestOrderInfo);
			if(goldInvestOrderInfo == null)
				continue ;
			String couponId = goldInvestOrderInfo.getCouponId();
			//如果有红包id，那么解冻红包
	        if(StringUtils.isNotBlank(couponId)){
	        	try {
	        		//调用红包服务，解冻红包
					CouponPlacement couponPlacement = couponService.findCouponPlacementbyId(null, couponId);
					logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the param [couponPlacement={}]",couponPlacement);
					//将红包状态改为可用
					couponPlacement.setStatus(CouponStatus.PLACED);
			        if (!couponService.updateCouponPlacement(null, couponPlacement))
					    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is unFreezeCouponError");
				} catch (Exception e) {
				    logger.info("updateOrderStatusAndAmount  in  GoldInvestOrderAndAccountServiceImpl ,the error is unFreezeCouponError");
				    continue ;
				}
	        }
			//将剩余可投资金额增加
			BigDecimal balance = goldProductInfo.getBalance().add(goldInvestOrder.getRealAmount());
			logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [balance={}]",balance);
			int updateRowsProductInfo = goldProductInfoDao.updateByProductIdAndVersion(goldProductInfo.getId(), goldProductInfo.getVersion(), balance);
			logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [updateRowsProductInfo={}]",updateRowsProductInfo);
			//剩余可投资金额增加操作失败
			if(updateRowsProductInfo == 0) {
				logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
				continue ;
			}
			//更新投资订单状态，设置为预下单过期
			int updateRowsInvestOrder = goldInvestOrderDao.updateByUseridAndReqNo(goldInvestOrder.getUserId(), goldInvestOrder.getReqNo(), goldInvestOrder.getOrderType(), goldInvestOrder.getOrderStatus(), GoldOrderStatusEnum.ORDERSTATUS_PREPAY_EXPIRE.getStatus());
			logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [updateRowsInvestOrder={}]",updateRowsInvestOrder);
			if(updateRowsInvestOrder == 0){
				//操作失败(更新投资订单状态失败)
				logger.info("updatePrepayStatusAndBalance  in  GoldInvestOrderAndProductServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
				continue ;
			}
		}
	}
}
  
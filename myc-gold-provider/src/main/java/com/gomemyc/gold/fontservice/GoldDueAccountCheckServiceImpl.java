
package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.dao.GoldDueAccountCheckDao;
import com.gomemyc.gold.dto.GoldDueAccountCheckDTO;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.service.GoldProductService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.entity.GoldDueAccountCheck;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.enums.GoldCheckType;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.service.GoldDueAccountCheckService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.CheckExpireOrderRDataDto;
import com.gomemyc.wallet.resp.CheckExpireOrderResultDto;
import com.gomemyc.wallet.walletinter.GoldCheckAccountWallet;

/** 
 * ClassName:GoldDueAccountCheckServicImpl
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:26:36
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service
public class GoldDueAccountCheckServiceImpl implements GoldDueAccountCheckService{

	private static final Logger logger = LoggerFactory.getLogger(GoldDueAccountCheckServiceImpl.class);
	@Reference
	private GoldDueAccountCheckService goldDueAccountCheckService;

	@Reference
	private GoldProductService goldProductService;

	@Autowired
	private GoldDueAccountCheckDao goldDueAccountCheckDao;

	@Autowired
	private GoldWalletConstant goldWalletConstant;

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;

	/**
	 *
	 * 保存定期金到期对账文件数据
	 *
	 * @return            (Integer) 影响的记录数
	 * @ServiceException
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                30005:确认下单信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月20日
	 */
	@Override
	public Integer saveGoldDueAccountCheck() throws ServiceException {
		//插入多少条记录
		int insertRows = 0;
		//还清时间为当前时间的后一天
		String clearTime = LocalDate.now().plusDays(1).toString();
		logger.info("execute in GoldDueAccountCheckSimpleJob,the openTime is {}",clearTime);

		//根据开标时间查询产品id和产品编码
		List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByClearTime(clearTime);
		if(findIdAndCodeByOpenTime == null || findIdAndCodeByOpenTime.size() == 0){
			logger.info("execute in GoldDueAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}",findIdAndCodeByOpenTime);
			return 0;
		}
		//遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
		for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
		{


			if(StringUtils.isBlank(goldProductIdAndCodeDTO.getGoldProductCode()) || StringUtils.isBlank(clearTime)){
				logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
				continue;
			}

			CheckExpireOrderResultDto checkExpireOrder = null;
			try {
				//调用黄金钱包接口,定期金到期对账文件
				checkExpireOrder = GoldCheckAccountWallet.checkExpireOrder(goldWalletConstant.getVersion(),
																		   goldWalletConstant.getMerchantCode(),
																		   goldProductIdAndCodeDTO.getGoldProductCode(),
																		   clearTime,
																		   0,
																		   0,
																		   goldWalletConstant.getSign(),
																		   goldWalletConstant.getIp());
			} catch (Exception e) {
				logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
				logger.error("saveDueAccountCheck in GoldDueAccountCheckServiceImpl, the params Exception is [{}]",e);
				continue;
			}
			if(checkExpireOrder == null || checkExpireOrder.getData().length == 0) {
				logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CHECK_NOT_EXIST.getStatus(),GoldInfoCode.CHECK_NOT_EXIST.getMsg());
				continue;
			}
			logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the checkExpireOrder is [{}]",checkExpireOrder);
			GoldDueAccountCheck goldDueAccountCheck = new GoldDueAccountCheck();
			//遍历返回的买定期金对账文件，插入数据库并统计插入
			for(CheckExpireOrderRDataDto checkExpireOrderRDataDto : checkExpireOrder.getData())
			{
				if (null == checkExpireOrderRDataDto)
					continue;
				//将查询回来的数据拷贝进goldInvestAccountCheck
				BeanMapper.copy(checkExpireOrderRDataDto, goldDueAccountCheck);
				//设置创建时间和检查类型
				goldDueAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOLD.getStatus());
				goldDueAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
				goldDueAccountCheck.setId(UUIDGenerator.generate());
				goldDueAccountCheck.setGoldFinishTime(DateUtil.strToDateLong(checkExpireOrderRDataDto.getFinishTime()));
				goldDueAccountCheck.setGoldOrderTime(DateUtil.strToDateLong(checkExpireOrderRDataDto.getOrderTime()));
				//插入数据库
				goldDueAccountCheckDao.insertSelective(goldDueAccountCheck);
				logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the goldDueAccountCheck is [{}]",goldDueAccountCheck);
				insertRows++;
//			//根据订单号和订单状态唯一查询确认下单订单
//			saveDueAccountCheck(goldDueAccountCheck);
//			GoldInvestOrder goldInvestOrder = goldInvestOrderDao.selectByReqNo(goldDueAccountCheck.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
//			logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the goldInvestOrder is [{}]",goldInvestOrder);
//			if(goldInvestOrder == null){
//				logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CONFIRM_NOT_EXIST.getStatus(),GoldInfoCode.CONFIRM_NOT_EXIST.getMsg());
//				return null;
//			}
//			//计算本金
//			BigDecimal orderAmount = goldInvestOrder.getRealWeight().multiply(goldDueAccountCheck.getRealPrice());
//			logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the orderAmount is [{}]",orderAmount);
//			//保存计算后的本金,更改检查状态，存入数据库
//			goldDueAccountCheck.setOrderAmount(orderAmount);
//			goldDueAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
//			goldDueAccountCheck.setId(UUIDGenerator.generate());
//			goldDueAccountCheckDao.insertSelective(goldDueAccountCheck);
//			logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the goldDueAccountCheck is [{}]",goldDueAccountCheck);
//			insertRows++;
			}
		}
		logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the insertRows is [{}]",insertRows);
		return insertRows;
	}

//	//本地数据整理
//	public Integer saveDueAccountCheck(GoldDueAccountCheck goldDueAccountCheck) throws ServiceException{
//		//根据订单号和订单状态唯一查询确认下单订单
//		GoldInvestOrder goldInvestOrder = goldInvestOrderDao.selectByReqNo(goldDueAccountCheck.getReqNo(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
//		logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the goldInvestOrder is [{}]",goldInvestOrder);
//		if(goldInvestOrder == null){
//			logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CONFIRM_NOT_EXIST.getStatus(),GoldInfoCode.CONFIRM_NOT_EXIST.getMsg());
//			return null;
//		}
//		//计算本金
//		BigDecimal orderAmount = goldInvestOrder.getRealWeight().multiply(goldDueAccountCheck.getRealPrice());
//		logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the orderAmount is [{}]",orderAmount);
//		//保存计算后的本金,更改检查状态，存入数据库
//		goldDueAccountCheck.setOrderAmount(orderAmount);
//		goldDueAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
//		goldDueAccountCheck.setId(UUIDGenerator.generate());
//		int b = goldDueAccountCheckDao.insertSelective(goldDueAccountCheck);
//		logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the goldDueAccountCheck is [{}]",goldDueAccountCheck);
//		return b;
//	}

	@Override
	public Integer saveDueAccountCheck() throws ServiceException {

		//插入多少条记录
		int insertRows = 0;
		//还清时间为当前时间的后一天
		String clearTime = LocalDate.now().plusDays(1).toString();
		logger.info("execute in GoldDueAccountCheckSimpleJob,the openTime is {}", clearTime);

		//根据开标时间查询产品id和产品编码
		List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByClearTime(clearTime);
		if (findIdAndCodeByOpenTime == null || findIdAndCodeByOpenTime.size() == 0) {
			logger.info("execute in GoldDueAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}", findIdAndCodeByOpenTime);
			return 0;
		}
		//遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
		for (GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime) {

			List<GoldInvestOrder> goldInvestOrders =  goldInvestOrderDao.getInvestOrderByProductId(goldProductIdAndCodeDTO.getId(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());

			for (GoldInvestOrder goldInvestOrder :goldInvestOrders){

				GoldDueAccountCheck goldDueAccountChecks = new GoldDueAccountCheck();
				if(goldInvestOrder != null){
					logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CONFIRM_NOT_EXIST.getStatus(),GoldInfoCode.CONFIRM_NOT_EXIST.getMsg());
					GoldDueAccountCheck goldDueAccountCheck = goldDueAccountCheckDao.getByOrderNo(goldInvestOrder.getGoldOrderNo(),GoldCheckType.CHECK_TYPE_GOLD.getStatus());
					BigDecimal orderAmount = null;
					if (null != goldDueAccountCheck&&goldDueAccountCheck.getRealPrice()!=null) {
						//计算本金
						goldDueAccountChecks.setRealPrice(goldDueAccountCheck.getRealPrice());
						 orderAmount = goldInvestOrder.getRealWeight().multiply(goldDueAccountCheck.getRealPrice());
					}else if (goldInvestOrder.getRealWeight()!=null&&goldInvestOrder.getRealPrice()!=null ){
						goldDueAccountChecks.setRealPrice(goldInvestOrder.getRealPrice());
						orderAmount = goldInvestOrder.getRealWeight().multiply(goldInvestOrder.getRealPrice());
					}
					logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the orderAmount is [{}]",orderAmount);
					//保存计算后的本金,更改检查状态，存入数据库
					goldDueAccountChecks.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
					goldDueAccountChecks.setGoldFinishTime(goldInvestOrder.getRealFinishTime());
					goldDueAccountChecks.setGoldOrderTime(goldInvestOrder.getFinishTime());
					goldDueAccountChecks.setOrderNo(goldInvestOrder.getGoldOrderNo());
					goldDueAccountChecks.setProductCode(goldProductIdAndCodeDTO.getGoldProductCode());
					goldDueAccountChecks.setProductName(goldInvestOrder.getProductName());
					goldDueAccountChecks.setReqNo(goldInvestOrder.getReqNo());
					goldDueAccountChecks.setOrderAmount(orderAmount);
					goldDueAccountChecks.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
					goldDueAccountChecks.setId(UUIDGenerator.generate());
						insertRows += goldDueAccountCheckDao.insertSelective(goldDueAccountChecks);
					logger.info("saveDueAccountCheck  in  GoldDueAccountCheckServiceImpl ,the goldDueAccountCheck is [{}]",goldDueAccountCheck);
				}
			}
		}
		return insertRows;
	}


	@Override
	public List<GoldDueAccountCheckDTO> getByCreateTime(String createTime) {
		return BeanMapper.mapList(goldDueAccountCheckDao.getByCreateTime(createTime),GoldDueAccountCheckDTO.class) ;
	}

	@Override
	public int updateComparingStatusBuOrderNo(String orderNo, Integer comparingStatus) {
		return goldDueAccountCheckDao.updateComparingStatusBuOrderNo(orderNo,comparingStatus);
	}

}
  
package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dto.GoldDueSumAccountCheckDTO;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.service.GoldProductService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dao.GoldDayInterestAccountCheckDao;
import com.gomemyc.gold.dao.GoldDueSumAccountCheckDao;
import com.gomemyc.gold.entity.GoldDayInterestAccountCheck;
import com.gomemyc.gold.entity.GoldDueSumAccountCheck;
import com.gomemyc.gold.enums.GoldCheckType;
import com.gomemyc.gold.service.GoldDueSumAccountCheckService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.CheckSumInterestRDataDto;
import com.gomemyc.wallet.resp.CheckSumInterestResultDto;
import com.gomemyc.wallet.walletinter.GoldCheckAccountWallet;
import sun.awt.ConstrainableGraphics;

/** 
 * ClassName:GoldDueSumAccountCheckServiceImpl
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:26:46
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service(timeout=6000)
public class GoldDueSumAccountCheckServiceImpl implements GoldDueSumAccountCheckService{
	
	private static final Logger logger = LoggerFactory.getLogger(GoldDueSumAccountCheckServiceImpl.class);

	@Reference
	private GoldDueSumAccountCheckService goldDueSumAccountCheckService;

	@Reference
	private GoldProductService goldProductService;


	@Autowired
	private GoldDueSumAccountCheckDao goldDueSumAccountCheckDao;

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;
	
	@Autowired
	private GoldDayInterestAccountCheckDao goldDayInterestAccountCheckDao;
	
	@Autowired
	private GoldWalletConstant goldWalletConstant;
	
	/**
	 * 
	 * 保存定期到期利息汇总对账文件数据
	 * 
	 * @return            (Integer) 影响的记录数
	 * @ServiceException
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月20日
	 */
	@Override
	public Integer saveGoldDueSumAccountCheck() throws ServiceException {
		//插入多少条记录
		int insertRows = 0;
		//还清时间为当前时间的后一天
		String clearTime = LocalDate.now().plusDays(1).toString();
		logger.info("execute in GoldDueAccountCheckSimpleJob,the openTime is {}",clearTime);

		//根据开标时间查询产品id和产品编码
		List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByClearTime(clearTime);
		if(findIdAndCodeByOpenTime == null || findIdAndCodeByOpenTime.size() == 0){
			logger.info("execute in GoldDueSumAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}",findIdAndCodeByOpenTime);
			return 0;
		}
		//遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
		for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
		{

			if(StringUtils.isBlank(goldProductIdAndCodeDTO.getGoldProductCode())){
				logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
				continue;
			}

			CheckSumInterestResultDto checkSumInterest = null;
			try {
				//调用黄金钱包接口,定期金到期对账文件
				checkSumInterest = GoldCheckAccountWallet.checkSumInterest(goldWalletConstant.getVersion(),
																		   goldWalletConstant.getMerchantCode(),
																		   goldProductIdAndCodeDTO.getGoldProductCode(),
																		   0,
																		   0,
																		   goldWalletConstant.getSign(),
																		   goldWalletConstant.getIp());
			} catch (Exception e) {
				logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
				logger.error("saveDueSumAccountCheck in GoldDueSumAccountCheckServiceImpl, the params Exception is [{}]",e);
				continue;
			}
			if(checkSumInterest == null || checkSumInterest.getData().length == 0) {
				logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CHECK_NOT_EXIST.getStatus(),GoldInfoCode.CHECK_NOT_EXIST.getMsg());
				continue;
			}
			logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the checkSumInterest is [{}]",checkSumInterest);
			GoldDueSumAccountCheck goldDueSumAccountCheck = new GoldDueSumAccountCheck();
			//遍历返回的买定期金对账文件，插入数据库并统计插入
			for(CheckSumInterestRDataDto checkSumInterestRDataDto : checkSumInterest.getData())
			{
				if (null == checkSumInterestRDataDto)
				continue;
				//将查询回来的数据拷贝进goldInvestAccountCheck
				BeanMapper.copy(checkSumInterestRDataDto, goldDueSumAccountCheck);
				//设置创建时间和检查类型
				goldDueSumAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOLD.getStatus());
				goldDueSumAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
				goldDueSumAccountCheck.setId(UUIDGenerator.generate());
				//插入数据库
				goldDueSumAccountCheckDao.insertSelective(goldDueSumAccountCheck);
				logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the goldDueSumAccountCheck is [{}]",goldDueSumAccountCheck);
				insertRows++;

				//根据订单号，查询每日利息对账
//				insertRows+=saveDueSumAccountCheck(goldDueSumAccountCheck);
//
//			//根据订单号，查询每日利息对账
//			List<GoldDayInterestAccountCheck> listGoldDayInterestAccountCheck = goldDayInterestAccountCheckDao.listByReqNo(goldDueSumAccountCheck.getReqNo());
//			logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the listGoldDayInterestAccountCheck is [{}]",listGoldDayInterestAccountCheck);
//			if(listGoldDayInterestAccountCheck == null || listGoldDayInterestAccountCheck.size() == 0) {
//				logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CHECK_NOT_EXIST.getStatus(),GoldInfoCode.CHECK_NOT_EXIST.getMsg());
//				return null;
//			}
//			//保存总利息
//			BigDecimal dayInterestMoneySum = null;
//			//遍历每日利息对账，计算总利息
//			for(GoldDayInterestAccountCheck goldDayInterestAccountCheck : listGoldDayInterestAccountCheck)
//			{
//				BigDecimal dayInterestMoney = goldDayInterestAccountCheck.getDayInterestMoney();
//				dayInterestMoneySum = dayInterestMoneySum.add(dayInterestMoney);
//			}
//			logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the dayInterestMoneySum is [{}]",dayInterestMoneySum);
//			//设置核算后的总利息，更新状态，保存进数据库
//			goldDueSumAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
//			goldDueSumAccountCheck.setInterestAmount(dayInterestMoneySum);
//			goldDueSumAccountCheck.setId(UUIDGenerator.generate());
//			goldDueSumAccountCheckDao.insertSelective(goldDueSumAccountCheck);
//			logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the goldDueSumAccountCheck is [{}]",goldDueSumAccountCheck);
//			insertRows++;
			}
		}
		logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the insertRows is [{}]",insertRows);
		return insertRows;
	}


//	//每日利息对账本地对账
//	public Integer saveDueSumAccountCheck(GoldDueSumAccountCheck goldDueSumAccountCheck){
//		//根据订单号，查询每日利息对账
//		List<GoldDayInterestAccountCheck> listGoldDayInterestAccountCheck = goldDayInterestAccountCheckDao.listByReqNo(goldDueSumAccountCheck.getReqNo());
//		logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the listGoldDayInterestAccountCheck is [{}]",listGoldDayInterestAccountCheck);
//		if(listGoldDayInterestAccountCheck == null || listGoldDayInterestAccountCheck.size() == 0) {
//			logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CHECK_NOT_EXIST.getStatus(),GoldInfoCode.CHECK_NOT_EXIST.getMsg());
//			return 0;
//		}
//		//保存总利息【
//		BigDecimal dayInterestMoneySum = null;
//		//遍历每日利息对账，计算总利息
//		for(GoldDayInterestAccountCheck goldDayInterestAccountCheck : listGoldDayInterestAccountCheck)
//		{
//			BigDecimal dayInterestMoney = goldDayInterestAccountCheck.getDayInterestMoney();
//			dayInterestMoneySum = dayInterestMoneySum.add(dayInterestMoney);
//		}
//		logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the dayInterestMoneySum is [{}]",dayInterestMoneySum);
//		//设置核算后的总利息，更新状态，保存进数据库
//		goldDueSumAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
//		goldDueSumAccountCheck.setInterestAmount(dayInterestMoneySum);
//		goldDueSumAccountCheck.setId(UUIDGenerator.generate());
//		int b =  goldDueSumAccountCheckDao.insertSelective(goldDueSumAccountCheck);
//		logger.info("saveDueSumAccountCheck  in  GoldDueSumAccountCheckServiceImpl ,the goldDueSumAccountCheck is [{}]",goldDueSumAccountCheck);
//		return b;
//	}


	public Integer saveDueSumAccountCheck()throws ServiceException{
		//插入多少条记录
		int insertRows = 0;
		//还清时间为当前时间的后一天
		String clearTime = LocalDate.now().plusDays(1).toString();
		logger.info("execute in GoldDueAccountCheckSimpleJob,the openTime is {}",clearTime);

		//根据还清时间查询产品id和产品编码
		List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByClearTime(clearTime);
		if(findIdAndCodeByOpenTime == null || findIdAndCodeByOpenTime.size() == 0){
			logger.info("execute in GoldDueSumAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}",findIdAndCodeByOpenTime);
			return 0;
		}
		//遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
		for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime){
			List<GoldInvestOrder> goldInvestOrders =  goldInvestOrderDao.getInvestOrderByProductId(goldProductIdAndCodeDTO.getId(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
			for (GoldInvestOrder goldInvestOrder :goldInvestOrders){
				if (null == goldInvestOrder)
					continue;

				BigDecimal interest = goldDayInterestAccountCheckDao.getInterestByOrderNo(goldInvestOrder.getGoldOrderNo(), GoldCheckType.CHECK_TYPE_GOLD.getStatus());
				GoldDueSumAccountCheck  GoldDueSumAccountCheck  = goldDueSumAccountCheckDao.getByOrderNoAndDataCheckType(goldInvestOrder.getGoldOrderNo(),GoldCheckType.CHECK_TYPE_GOLD.getStatus());
				if (null !=GoldDueSumAccountCheck ) {
						GoldDueSumAccountCheck.setId(UUIDGenerator.generate());
						GoldDueSumAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
						GoldDueSumAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
						GoldDueSumAccountCheck.setGoldWeight(goldInvestOrder.getRealWeight()!=null?goldInvestOrder.getRealWeight():new BigDecimal("0.00"));
						GoldDueSumAccountCheck.setReqNo(goldInvestOrder.getReqNo());
						GoldDueSumAccountCheck.setInterestAmount(interest);
					    insertRows+=goldDueSumAccountCheckDao.insertSelective(GoldDueSumAccountCheck);
				}else {
						GoldDueSumAccountCheck  dueSumAccountCheck=new GoldDueSumAccountCheck();
						dueSumAccountCheck.setReqNo(goldInvestOrder.getReqNo());
						dueSumAccountCheck.setOrderNo(goldInvestOrder.getGoldOrderNo());
						dueSumAccountCheck.setGoldWeight(goldInvestOrder.getRealWeight()!=null? goldInvestOrder.getRealWeight():new BigDecimal("0.00"));
						dueSumAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
						dueSumAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
						dueSumAccountCheck.setClearRate(new Integer("0.00"));
						dueSumAccountCheck.setId(UUIDGenerator.generate());
						dueSumAccountCheck.setProductCode(goldProductIdAndCodeDTO.getGoldProductCode());
						dueSumAccountCheck.setProductName(goldInvestOrder.getProductName());
						dueSumAccountCheck.setInterestAmount(new BigDecimal("0.00"));
						insertRows+=goldDueSumAccountCheckDao.insertSelective(GoldDueSumAccountCheck);
				}
			}
		}
		return insertRows;
	}

	@Override
	public List<GoldDueSumAccountCheckDTO> getByCreateTime(String createTime) {
		return BeanMapper.mapList(goldDueSumAccountCheckDao.getByCreateTime(createTime),GoldDueSumAccountCheckDTO.class);
	}

	@Override
	public int updateComparingStatusByOrderNo(String orderNo, Integer comparingStatus) {
		return goldDueSumAccountCheckDao.updateComparingStatusByOrderNo(orderNo,comparingStatus);
	}
}
  
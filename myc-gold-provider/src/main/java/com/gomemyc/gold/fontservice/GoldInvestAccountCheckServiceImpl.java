package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.dao.GoldInvestAccountCheckDao;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dto.GoldInvestAccountCheckDTO;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
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
import com.gomemyc.gold.entity.GoldInvestAccountCheck;
import com.gomemyc.gold.enums.GoldCheckType;
import com.gomemyc.gold.service.GoldInvestAccountCheckService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.CheckTimeOrderRDataDto;
import com.gomemyc.wallet.resp.CheckTimeOrderResultDto;
import com.gomemyc.wallet.walletinter.GoldCheckAccountWallet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** 
 * ClassName:GoldInvestAccountCheckServiceImpl
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:27:02
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description  买定期金对账文件
 */
@Service(timeout=6000)
public class GoldInvestAccountCheckServiceImpl implements GoldInvestAccountCheckService{

	private static final Logger logger = LoggerFactory.getLogger(GoldInvestAccountCheckServiceImpl.class);

	@Reference
	private GoldProductService goldProductService;

	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;

	@Autowired
	private GoldInvestAccountCheckDao goldInvestAccountCheckDao;
	
	@Autowired
	private GoldWalletConstant goldWalletConstant;
	
	/**
	 * 
	 * 保存买定期金对账文件数据
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
	public Integer saveGoldInvestAccountCheck() throws ServiceException {
		logger.info("GoldInvestAccountCheckSimpleJob begin,the time is {}", LocalDateTime.now());
		//插入多少条记录
		int insertRows = 0;
		//开标时间为当前时间的前一天
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDueSumAccountCheckSimpleJob,the openTime is {}",openTime);

		//根据开标时间查询产品id和产品编码
		List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.getProductIdCodeByOpenTime(openTime);
		if(findIdAndCodeByOpenTime == null || findIdAndCodeByOpenTime.size() == 0){
			logger.info("execute in GoldDueSumAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}",findIdAndCodeByOpenTime);
			return 0;
		}
		//遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
		for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
		{

			if(StringUtils.isBlank(goldProductIdAndCodeDTO.getGoldProductCode()) || StringUtils.isBlank(openTime)){
				logger.info("saveInvestAccountCheck  in  GoldInvestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
				continue;
			}

			CheckTimeOrderResultDto checkTimeOrder = null;
			try {
				//调用黄金钱包接口,买定期金对账文件
				checkTimeOrder = GoldCheckAccountWallet.checkTimeOrder(goldWalletConstant.getVersion(),
																	   goldWalletConstant.getMerchantCode(),
																	   goldProductIdAndCodeDTO.getGoldProductCode(),
																	   openTime,
																	   0,
																	   0,
																	   goldWalletConstant.getSign(),
																	   goldWalletConstant.getIp());
			} catch (Exception e) {
				logger.info("saveInvestAccountCheck  in  GoldInvestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
				logger.error("saveInvestAccountCheck in GoldInvestAccountCheckServiceImpl, the params Exception is [{}]",e);
				continue;
			}
			if(checkTimeOrder == null || checkTimeOrder.getData() == null || checkTimeOrder.getData().length == 0) {
				logger.info("saveInvestAccountCheck  in  GoldInvestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CHECK_NOT_EXIST.getStatus(),GoldInfoCode.CHECK_NOT_EXIST.getMsg());
				continue;
			}
			logger.info("saveInvestAccountCheck  in  GoldInvestAccountCheckServiceImpl ,the checkTimeOrder is [{}]",checkTimeOrder);
			GoldInvestAccountCheck goldInvestAccountCheck = new GoldInvestAccountCheck();
			//遍历返回的买定期金对账文件，插入数据库并统计插入
			for(CheckTimeOrderRDataDto checkTimeOrderRDataDto : checkTimeOrder.getData())
			{
				if (null ==checkTimeOrderRDataDto )
					continue;
				//将查询回来的数据拷贝进goldInvestAccountCheck
				BeanMapper.copy(checkTimeOrderRDataDto, goldInvestAccountCheck);
				//设置创建时间和检查类型
				goldInvestAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOLD.getStatus());
				goldInvestAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
				goldInvestAccountCheck.setId(UUIDGenerator.generate());
				goldInvestAccountCheck.setGoldFinishTime(DateUtil.strToDateLong(checkTimeOrderRDataDto.getFinishTime()));
				goldInvestAccountCheck.setGoldOrderTime(DateUtil.strToDateLong(checkTimeOrderRDataDto.getOrderTime()));
				//插入数据库
				goldInvestAccountCheckDao.insertSelective(goldInvestAccountCheck);
				insertRows++;
			}
		}
		logger.info("GoldInvestAccountCheckSimpleJob end,the time is {}",LocalDateTime.now());
		logger.info("saveInvestAccountCheck  in  GoldInvestAccountCheckServiceImpl ,the insertRows is [{}]",insertRows);
		return insertRows;
	}

	/**
	 *
	 * 保存买定期金对账文件数据
	 *
	 * @return            (Integer) 影响的记录数
	 * @ServiceException
	 *                30000:参数不完整
	 *                30002:对账文件信息不存在
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月20日
	 */
	@Override
	public Integer saveInvestAccountCheck() {

		//插入数据
		int insertRows = 0;
		//查询当前时间的前一天开标的商品下的所有订单
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<GoldInvestAccountCheck> goldInvestAccountChecks = goldInvestOrderDao.findtGoldInvestOrderByOpenTime(LocalDate.now().minusDays(1).format(formatter), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
		if (goldInvestAccountChecks.size() == 0)
			return 0;
		//便利所有订单并查询黄金买定期金对账表内黄金钱包返回的金价
		for (GoldInvestAccountCheck goldInvestAccountCheck : goldInvestAccountChecks){
			BigDecimal realPrice = goldInvestAccountCheckDao.getRealPriceByDataCheckType(goldInvestAccountCheck.getReqNo(),1);
			if (realPrice == null){
				realPrice = goldInvestAccountCheck.getRealPrice();
			}
			goldInvestAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
			goldInvestAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
			goldInvestAccountCheck.setId(UUIDGenerator.generate());
			goldInvestAccountCheck.setRealPrice(realPrice);
			goldInvestAccountCheck.setOrderAmount(goldInvestAccountCheck.getGoldWeight().multiply(realPrice));
			//插入数据库
			insertRows += goldInvestAccountCheckDao.insertSelective(goldInvestAccountCheck);

		}


		return insertRows;
	}

	@Override
	public List<GoldInvestAccountCheckDTO> getByCreateTime(String createTime) {
		return BeanMapper.mapList(goldInvestAccountCheckDao.getByCreateTime(createTime),GoldInvestAccountCheckDTO.class);
	}

	@Override
	public int updateComparingStatusByOrderNo(String orderNo, Integer comparingStatus) {
		return goldInvestAccountCheckDao.updateComparingStatusByOrderNo(orderNo,comparingStatus);
	}
}
  
package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.dao.GoldDayInterestAccountCheckDao;
import com.gomemyc.gold.dao.GoldEarningsDao;
import com.gomemyc.gold.dao.GoldProductDao;
import com.gomemyc.gold.dto.GoldDayInterestAccountCheckDTO;
import com.gomemyc.gold.dto.GoldEarningsDTO;
import com.gomemyc.gold.dto.GoldProductIdAndCodeDTO;
import com.gomemyc.gold.entity.GoldDayInterestAccountCheck;
import com.gomemyc.gold.entity.GoldEarnings;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.entity.GoldProduct;
import com.gomemyc.gold.enums.CheckEnums;
import com.gomemyc.gold.service.GoldProductService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.entity.extend.GoldDayInterestAccountCheckExtend;
import com.gomemyc.gold.entity.extend.GoldInvestOrderExtend;
import com.gomemyc.gold.enums.GoldCheckType;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.service.GoldDayInterestAccountCheckService;
import com.gomemyc.gold.util.DateUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.CheckDailyInterestRDataDto;
import com.gomemyc.wallet.resp.CheckDailyInterestResultDto;
import com.gomemyc.wallet.walletinter.GoldCheckAccountWallet;

/** 
 * ClassName:GoldDayInterestAccountCheckServiceImpl
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月20日 下午4:26:20
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service(timeout=6000)
public class GoldDayInterestAccountCheckServiceImpl implements GoldDayInterestAccountCheckService{

	private static final Logger logger = LoggerFactory.getLogger(GoldDayInterestAccountCheckServiceImpl.class);

	@Reference
	private GoldProductService goldProductService;

	@Reference
	private GoldDayInterestAccountCheckService goldDayInterestAccountCheckService;

	@Autowired
	private GoldDayInterestAccountCheckDao goldDayInterestAccountCheckDao;
	
	@Autowired
	private GoldWalletConstant goldWalletConstant;
	
	@Autowired
	private GoldInvestOrderDao goldInvestOrderDao;

	@Autowired
	private GoldEarningsDao goldEarningsDao;

	@Autowired
	private GoldProductDao goldProductDao;
	/**
	 * 
	 * 保存黄金钱包每天利息对账文件数据
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
	public Integer saveGoldDayInterestAccountCheck() throws ServiceException {
		//插入多少条记录
		int insertRows = 0;
		//生成当前时间
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the openTime is {}",openTime);

		//根据当前时间查询产品介于开标和还清时间的id和产品编码
		List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime = goldProductService.findIdAndCodeByOpenTime(openTime);
		if(findIdAndCodeByOpenTime == null || findIdAndCodeByOpenTime.size() == 0){
			logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}",findIdAndCodeByOpenTime);
			return 0;
		}
		//遍历，取得每一个产品编码，对每一个产品下的每一个订单进行对账
		for(GoldProductIdAndCodeDTO goldProductIdAndCodeDTO : findIdAndCodeByOpenTime)
		{


			if(StringUtils.isBlank(goldProductIdAndCodeDTO.getGoldProductCode()) || StringUtils.isBlank(openTime)){
				logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.WRONG_PARAMETERS.getStatus(),GoldInfoCode.WRONG_PARAMETERS.getMsg());
				continue;
			}

			CheckDailyInterestResultDto checkDailyInterest = null;
			try {
				//调用黄金钱包接口,每天利息对账文件
				checkDailyInterest = GoldCheckAccountWallet.checkDailyInterest(goldWalletConstant.getVersion(),
																			   goldWalletConstant.getMerchantCode(),
						                                                       goldProductIdAndCodeDTO.getGoldProductCode(),
																			   openTime,
																			   0,
																			   0,
																			   goldWalletConstant.getSign(),
																			   goldWalletConstant.getIp());
			} catch (Exception e) {
				logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
				logger.error("saveDayInterestAccountCheck in GoldDayInterestAccountCheckServiceImpl, the params Exception is [{}]",e);
				continue;
			}
			if(checkDailyInterest == null || checkDailyInterest.getData().length == 0) {
				logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.CHECK_NOT_EXIST.getStatus(),GoldInfoCode.CHECK_NOT_EXIST.getMsg());
				continue;
			}
			logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the checkDailyInterest is [{}]",checkDailyInterest);
			GoldDayInterestAccountCheck goldDayInterestAccountCheck = new GoldDayInterestAccountCheck();
			//遍历返回的买定期金对账文件，插入数据库并统计插入
			for(CheckDailyInterestRDataDto checkDailyInterestRDataDto : checkDailyInterest.getData())
			{
				if (null == checkDailyInterestRDataDto)
					continue;
				//将查询回来的数据拷贝进goldInvestAccountCheck
				logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the checkDailyInterestRDataDto is [{}]",checkDailyInterestRDataDto);
				BeanMapper.copy(checkDailyInterestRDataDto, goldDayInterestAccountCheck);
				//设置创建时间和检查类型
				goldDayInterestAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOLD.getStatus());
				goldDayInterestAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
				goldDayInterestAccountCheck.setId(UUIDGenerator.generate());
				goldDayInterestAccountCheck.setGoldInterestDate(DateUtil.strToDateLong(checkDailyInterestRDataDto.getInterestDate(),"yyyy-MM-dd"));
				//插入数据库
				goldDayInterestAccountCheckDao.insertSelective(goldDayInterestAccountCheck);
				insertRows++;
			}

		}
		logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the insertRows is [{}]",insertRows);
		return insertRows;
	}
	
	
	/**
	 * 
	 * 保存平台每天利息对账文件数据
	 * 
	 * @return (Integer) 影响的记录数
	 * @exception
	 *         50000   操作失败，请重试
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月22日
	 */
	@Override
	public Integer savePlatformDayInterestAccountCheck() throws ServiceException {
		//插入多少条记录
		int insertRows = 0;
		//根据当前时间查询投资订单
		List<GoldInvestOrderExtend> listGoldInvestOrderAndProductByCurrentTime = goldInvestOrderDao.listGoldInvestOrderAndProductByCurrentTime(DateUtil.getDateTime(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
		if(listGoldInvestOrderAndProductByCurrentTime == null || listGoldInvestOrderAndProductByCurrentTime.size() == 0) {
			logger.info("savePlatformDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
			return 0;
		}
		logger.info("savePlatformDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the listGoldInvestOrderAndProductByCurrentTime is [{}]",listGoldInvestOrderAndProductByCurrentTime);
		//根据收益日期查询每日利息对账文件
		List<GoldDayInterestAccountCheckExtend> listGoldDayInterestAccountCheckExtend = goldDayInterestAccountCheckDao.selectByInterestDate(LocalDate.now().minusDays(1).toString());
		if(listGoldDayInterestAccountCheckExtend == null || listGoldDayInterestAccountCheckExtend.size() == 0) {
			logger.info("savePlatformDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the param [errorStatus={}],[errorMsg={}",GoldInfoCode.SERVICE_UNAVAILABLE.getStatus(),GoldInfoCode.SERVICE_UNAVAILABLE.getMsg());
			return 0;
		}
		logger.info("savePlatformDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the listGoldDayInterestAccountCheckExtend is [{}]",listGoldDayInterestAccountCheckExtend);
		GoldDayInterestAccountCheck goldDayInterestAccountCheck = new GoldDayInterestAccountCheck();
		//遍历投资订单
		for(GoldInvestOrderExtend goldInvestOrderExtend : listGoldInvestOrderAndProductByCurrentTime)
		{
			for(GoldDayInterestAccountCheckExtend goldDayInterestAccountCheckExtend : listGoldDayInterestAccountCheckExtend)
			{
				if(goldInvestOrderExtend.getProductCode().equals(goldDayInterestAccountCheckExtend.getProductCode()))
				{
					BeanMapper.copy(goldDayInterestAccountCheckExtend, goldDayInterestAccountCheck);
					BeanMapper.copy(goldInvestOrderExtend, goldDayInterestAccountCheck);
					//计算利息
					BigDecimal dayInterestMoney = (goldDayInterestAccountCheckExtend.getGoldPrice().multiply(goldInvestOrderExtend.getRealWeight()).multiply(new BigDecimal(goldDayInterestAccountCheckExtend.getClearRate()))).divide(new BigDecimal(365), 2, RoundingMode.FLOOR);
					//设置核对后的每日利息对账文件
					goldDayInterestAccountCheck.setDayInterestMoney(dayInterestMoney);
					goldDayInterestAccountCheck.setDataCheckType(GoldCheckType.CHECK_TYPE_GOME.getStatus());
					goldDayInterestAccountCheck.setCreateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
					goldDayInterestAccountCheck.setId(UUIDGenerator.generate());
					goldDayInterestAccountCheck.setGoldInterestDate(DateUtil.strToDateLong(LocalDate.now().minusDays(1).toString(),"yyyy-MM-dd"));
					goldDayInterestAccountCheck.setOrderNo(goldInvestOrderExtend.getGoldOrderNo());
					goldDayInterestAccountCheck.setGoldWeight(goldInvestOrderExtend.getRealWeight());
					//插入数据库
					insertRows+=goldDayInterestAccountCheckDao.insertSelective(goldDayInterestAccountCheck);

					logger.info("saveDayInterestAccountCheck  in  GoldDayInterestAccountCheckServiceImpl ,the insertRows is [{}]",insertRows);
				}
			}
		}
		return insertRows;
	}

	@Override
	public List<GoldDayInterestAccountCheckDTO> getBycreateTime( String createTime) {
		return BeanMapper.mapList(goldDayInterestAccountCheckDao.getBycreateTime(createTime),GoldDayInterestAccountCheckDTO.class) ;
	}
	/**
	 *
	 * 根据订单号和创建时间查询当前时间创建的相应的订单
	 *
	 * @param reqNo 订单号
	 * @param createTime 创建时间
	 * @return  GoldDayInterestAccountCheckDTO
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月18日
	 */
	@Override
	public GoldDayInterestAccountCheckDTO getBycreateTimeAndReqNo( String createTime, String reqNo ) {
		return BeanMapper.map(goldDayInterestAccountCheckDao.getBycreateTimeAndReqNo(reqNo,createTime),GoldDayInterestAccountCheckDTO.class) ;
	}



	/**
	 *
	 *根据创建时间查找当前时间下所有的对账成功的记录
	 *
	 * @param
	 * @param
	 * @return  GoldDayInterestAccountCheckDTO
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月18日
	 */
	@Override
	public Integer saveEarnings() {

		//插入多少条记录
		int insertRows = 0;
		//生成当前时间
		String openTime = LocalDate.now().minusDays(1).toString();
		logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the openTime is {}",openTime);
		//根据当前时间查询产品介于开标和还清时间的id和产品编码
		List<GoldProduct> goldProducts = goldProductDao.productsByCurrentTime(openTime);
		if(goldProducts == null || goldProducts.size() == 0){
			logger.info("execute in GoldDayInterestAccountCheckSimpleJob,the ListGoldProductIdAndCodeDTO is {}",goldProducts);
			return 0;
		}
		//遍历，取得每一个产品编码，查询每一个产品下的每一个订单
		for (GoldProduct goldProduct : goldProducts) {

			List<GoldInvestOrder> goldInvestOrders = goldInvestOrderDao.getInvestOrderByProductId(goldProduct.getId(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());

			if (null == goldInvestOrders)
				continue;
			//根据订单查询当前时间创建的对账的争正确的数据
			for (GoldInvestOrder goldInvestOrder : goldInvestOrders) {
				if (null == goldInvestOrder )
					continue;
				GoldDayInterestAccountCheck goldDayInterestAccountCheck = goldDayInterestAccountCheckDao.getBycreateTimeAndReqNo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), goldInvestOrder.getReqNo());
				if (null ==goldDayInterestAccountCheck && goldDayInterestAccountCheck.getComparingStatus() != CheckEnums.COMPARING_STATUS_SUCCESS.getIndex())
					continue;
				insertRows += goldEarningsDao.insert(getGoldEarnings(goldProduct,goldInvestOrder,goldDayInterestAccountCheck));
			}
		}
		//将对账成功的数据插入数据库

		return insertRows;
	}

	@Override
	public int updateComparingStatusByOrderNo(String orderNo,Integer comparingStatus) {
		return goldDayInterestAccountCheckDao.updateComparingStatusByOrderNo(orderNo,comparingStatus);
	}

	private GoldEarnings getGoldEarnings(GoldProduct goldProduct,GoldInvestOrder goldInvestOrder,GoldDayInterestAccountCheck goldDayInterestAccountCheck) {
		GoldEarnings goldEarnings = new GoldEarnings();
		goldEarnings.setId(UUIDGenerator.generate());
		goldEarnings.setCeateTime(DateUtil.strToDateLong(DateUtil.getDateTime()));
		goldEarnings.setEarningsAmount(goldDayInterestAccountCheck.getDayInterestMoney());
		goldEarnings.setEarningsDate(goldDayInterestAccountCheck.getGoldInterestDate().toString());
		goldEarnings.setGoldWeight(goldDayInterestAccountCheck.getGoldWeight());
		goldEarnings.setInvestOrderId(goldInvestOrder.getId());
		goldEarnings.setProjectRate(goldProduct.getRate());
		goldEarnings.setUserId(goldInvestOrder.getUserId());
		goldEarnings.setUnitPrice(goldDayInterestAccountCheck.getGoldPrice());

		return  goldEarnings;
	}
}
  
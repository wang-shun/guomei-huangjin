/** 
 * Project Name:myc-gold-provider 
 * File Name:GoldProductServiceImpl.java 
 * Package Name:com.gomemyc.gold.fontservice 
 * Date:2017年3月7日上午11:26:52 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/

package com.gomemyc.gold.fontservice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.account.enums.AccountType;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.gold.dto.*;
import com.gomemyc.gold.entity.extend.GoldProductExtend;
import com.gomemyc.gold.entity.extend.GoldProductsExtend;
import com.gomemyc.gold.enums.GoldMethodStatusEnum;
import com.gomemyc.gold.enums.GoldProductStatusEnum;
import com.gomemyc.gold.service.GoldCommonService;
import com.gomemyc.gold.service.GoldProductService;
import com.gomemyc.gold.util.GetProductStatusUtil;
import com.gomemyc.gold.util.GoldInfoCode;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.QueryTimeProductListRDListDto;
import com.gomemyc.wallet.resp.QueryTimeProductListResultDto;
import com.gomemyc.wallet.walletinter.GoldQueryWallet;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dao.GoldProductDao;
import com.gomemyc.gold.dao.GoldProductInfoDao;

/**
 * ClassName:GoldProductServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年3月7日 上午11:26:52 <br/>
 * 
 * @author TianBin
 * @version
 * @since JDK 1.8
 * @see
 * @description
 */
@Service(timeout=6000)
public class GoldProductServiceImpl implements GoldProductService {
	
	private static final Logger logger = LoggerFactory.getLogger(GoldProductServiceImpl.class);

	@Autowired
	private GoldProductDao goldProductDao;
	
	@Autowired
	private GoldProductInfoDao goldProductInfoDao;
	
	@Autowired
	private GoldWalletConstant goldWalletConstant;

	@Reference
	private AccountService accountService;

	@Reference
	private GoldCommonService goldCommonService;

	/**
	 * 查询首页列表
	 * @author Liujunhan
	 * @date 2017年03月12日
	 * @param page  要查询的当前页
	 * @param pageSize  每页条数
	 * @return  Page<GoldProductDTO>
	 * @since JDK 1.8
	 */
	@Override
	public  Page<GoldProductDTO> listPageIndexList(int page, int pageSize) {
		logger.info("listPageIndexList  in  GoldProductServiceImpl ,the param [page={}],[pageSize={}]",page,pageSize);
		int pageStart = 0 ;
		String openTime ;

		List<GoldProductDTO> goldProductDTOList = new ArrayList<GoldProductDTO>();
		List<GoldProductExtend> goldProductExtendList = null;
		int count = 0;
		//再此处查询首页第一页列表和确定哪天是有产品的
		do {
			openTime = LocalDate.now().minusDays(count).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			logger.info("listPageIndexList  in  GoldProductServiceImpl ,the param [page={}]",openTime);
			goldProductExtendList = goldProductDao.getListGoldProduct(pageStart, pageSize, openTime);
			logger.info("listPageIndexList  in  GoldProductServiceImpl ,the param [goldProductExtendList={}]",goldProductExtendList);
			count++;
			logger.info("listPageIndexList  in  GoldProductServiceImpl ,the param [count={}]",count);
		}while (goldProductExtendList.size()==0 && count<10);
		//判断客户查询页数为第几页
		if (page != 1 && page !=0) {
			pageStart = (page - 1) * pageSize;
			goldProductExtendList.clear();
			goldProductExtendList = goldProductDao.getListGoldProduct(pageStart, pageSize, openTime);
			logger.info("listPageIndexList  in  GoldProductServiceImpl ,the param [goldProductExtendList={}]",goldProductExtendList);
		}
		BigDecimal investAmount ,amount;
		if (goldProductExtendList == null)
			return new Page<>(null, page, pageSize, (long) goldProductDao.getGoldProductAmount(openTime));
		
		for (GoldProductExtend goldProductExtend : goldProductExtendList) {
			double investPercent = 0;
			if (null == goldProductExtend)
				continue;
			if (null != (goldProductExtend.getBalance())&& null != (amount = goldProductExtend.getAmount())){
				investAmount = goldProductExtend.getAmount().subtract(goldProductExtend.getBalance());
				logger.info("listPageIndexList  in  GoldProductServiceImpl ,the investAmount is [{}]",investAmount);
				investPercent = investAmount.divide(amount,2).doubleValue();
				logger.info("listPageIndexList  in  GoldProductServiceImpl ,the investPercent is [{}]",investPercent);
			}
			goldProductDTOList.add(  new GoldProductDTO(
					goldProductExtend.getId(),
					goldProductExtend.getStatus() == null? "ERROR": GetProductStatusUtil.getProductStatus(goldProductExtend.getStatus()),
					goldProductExtend.getRate(),
					goldProductExtend.getRatePlus(),
					goldProductExtend.getYears()*365+goldProductExtend.getMonths()*30+goldProductExtend.getDays(),
					GoldMethodStatusEnum.METHOD_STATUS_ONCE.getKey(),
					goldProductExtend.getAmount(),
					goldProductExtend.getMinInvestAmount(),
					goldProductExtend.getTitle(),
					goldProductExtend.getOpenTime(),
					new Date(),
					goldProductExtend.getUseCoupon()==null?false:goldProductExtend.getUseCoupon()==0?false:true,
					goldProductExtend.getTypeKey(),
					investPercent
					));
		}
		return new Page<>(goldProductDTOList, page, pageSize, (long) goldProductDao.getGoldProductAmount(openTime));
	}


	/**
	 * 查询产品信息服务
	 * @author liujunhan
	 * @date  2017年03月14日
	 * @param productId  产品ID
	 * @return GoldProductDetailsDTO
	 * @since JDK 1.8 findByProductId
	 */
	@Override
	public GoldProductsExtendDTO serviceFindInfoByProductId(String productId){
		if (Strings.isNullOrEmpty(productId))
			return null ;
		return BeanMapper.map( goldProductDao.getByProductId(productId), GoldProductsExtendDTO.class);
	}
	/**
	 * 产品详细
	 * @author liujunhan
	 * @date  2017年03月14日
	 * @param productId  产品ID
	 * @return GoldProductDetailsDTO
	 * @since JDK 1.8 findByProductId
	 */
	@Override
	public GoldProductDetailsDTO findInfoByProductId(String productId){
		return getGoldProductDetailsDTO(goldProductDao.getByProductId(productId),null);
	}

	/**
	 * 投资预下单,查询产品明细
	 * @author liujunhan
	 * @date  2017年03月06日
	 * @param productId  产品ID
	 * @return GoldProductDetailsDTO
	 * @since JDK 1.8
	 */
	@Override
	public GoldProductDetailsDTO findByProductId(String productId ,String userId){
		return getGoldProductDetailsDTO(goldProductDao.getByProductId(productId),userId);
	}

	/**
	 * 根据开标时间查询产品id和code
	 * @author liujunhan
	 * @date  2017年03月22日
	 * @param  openTime 开标时间
	 * @return GoldProductDetailsDTO对象
	 * @since JDK 1.8
	 */
	@Override
	public List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime(String openTime) {
		return BeanMapper.mapList(goldProductDao.selectIdAndCodeByOpenTime(openTime),GoldProductIdAndCodeDTO.class);
	}

	/**
	 *
	 * 调用黄金钱包接口，查询所有的产品信息
	 * 
	 * @param start (Integer) 起始记录行数(选填)
	 * @param size  (Integer) 获取记录数大小(选填)
	 * @return List<GoldQueryTimeProductDTO>
	 * @ServiceException
	 *                30000:参数错误
	 *                50000:操作失败，请重试 
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date  2017年03月26日
	 */
	@Override
	public List<GoldQueryTimeProductDTO> queryTimeProduct(Integer start, Integer size) {
		if(start < 0 || size < 0)
			throw new ServiceException(GoldInfoCode.WRONG_PARAMETERS.getStatus(), GoldInfoCode.WRONG_PARAMETERS.getMsg());
		QueryTimeProductListResultDto queryTimeProduct = null;
		List<GoldQueryTimeProductDTO> listGoldQueryTimeProductDTO = new ArrayList<GoldQueryTimeProductDTO>();
		try {
			//调用黄金钱包接口,查询所有产品信息
			queryTimeProduct = GoldQueryWallet.queryTimeProduct(goldWalletConstant.getVersion(), 
														        goldWalletConstant.getMerchantCode(), 
														        start,
														        size, 
														        goldWalletConstant.getSign(),
														        goldWalletConstant.getIp());
			logger.info("QueryTimeProduct  in  GoldProductServiceImpl ,the queryTimeProduct is [{}]",queryTimeProduct);
		} catch (Exception e) {
			 logger.info("QueryTimeProduct  in  GoldProductServiceImpl ,the error is [调用黄金钱包接口失败]");
			 return listGoldQueryTimeProductDTO;
		}
		//判断查询回来的产品信息是否为空
		if(queryTimeProduct == null ||
		   queryTimeProduct.getData() == null ||
		   queryTimeProduct.getData().getList() == null ||
		   queryTimeProduct.getData().getList().size() == 0)
			 return listGoldQueryTimeProductDTO;
		//遍历查询回来的所有产品信息
		for(QueryTimeProductListRDListDto queryTimeProductListRDListDto : queryTimeProduct.getData().getList())
		{
			GoldQueryTimeProductDTO goldQueryTimeProductDTO = new GoldQueryTimeProductDTO();
			BeanMapper.copy(queryTimeProductListRDListDto, goldQueryTimeProductDTO);
			logger.info("QueryTimeProduct  in  GoldProductServiceImpl ,the goldQueryTimeProductDTO is [{}]",goldQueryTimeProductDTO);
			listGoldQueryTimeProductDTO.add(goldQueryTimeProductDTO);
		}






		logger.info("QueryTimeProduct  in  GoldProductServiceImpl ,the listGoldQueryTimeProductDTO is [{}]",listGoldQueryTimeProductDTO);
		return listGoldQueryTimeProductDTO;
	}

	/**
	 * 对象处理工具方法
	 * 对产品表查询的字段进行匹配计算并将结果set进GoldProductDetailsDTO
	 * @param goldProductsExtend 对象
	 * @return goldProductDetailsDTO对象
	 * @author liujunhan
	 * @date 2017年03月28号
	 * @since JDK 1.8
	 */
	private GoldProductDetailsDTO getGoldProductDetailsDTO(GoldProductsExtend goldProductsExtend,String userId){
		GoldProductDetailsDTO goldProductDetailsDTO;
		if (goldProductsExtend != null){

			goldProductDetailsDTO =BeanMapper.map(goldProductsExtend,GoldProductDetailsDTO.class);
			if (userId != null){
			try {
            //根据用户id查询用户可用余额
				goldProductDetailsDTO.setAvailableBalance( accountService.getAmountByUserIdAndAccountType(userId, AccountType.INVESTOR_CASH_ACCOUNT));

			}catch (Exception e){
				logger.info("getGoldProductDetailsDTO  in  GoldProductServiceImpl ,调用accountService服务失败");
				return null;
			}
			}
//			//接口不可用，暂时使用假数据
//			goldProductDetailsDTO.setAvailableBalance(new BigDecimal("50000.00"));


			//计算总天数
			goldProductDetailsDTO.setTotalDays(goldProductsExtend.getDays()+goldProductsExtend.getMonths()*30+goldProductsExtend.getYears()*365);
			//计算投资进度
			BigDecimal investAmount ,amount;
			double investPercent = 0;

			if (null != (goldProductsExtend.getBalance())&& null != (amount = goldProductsExtend.getAmount())){
				investAmount = goldProductsExtend.getAmount().subtract(goldProductsExtend.getBalance());
				logger.info("getGoldProductDetailsDTO  in  GoldProductServiceImpl ,the investAmount is [{}]",investAmount);
				investPercent = investAmount.divide(amount,2).doubleValue();
				logger.info("getGoldProductDetailsDTO  in  GoldProductServiceImpl ,the investPercent is [{}]",investPercent);
			}
			goldProductDetailsDTO.setInvestPercent(investPercent);
			//参考金价暂时无法获得
			try {
				goldProductDetailsDTO.setGoldPrice(goldCommonService.selectGoldRealPrice().getRealPrice());
			}catch (Exception e) {
				logger.info("getGoldProductDetailsDTO  in  GoldProductServiceImpl ,调用goldCommonService服务失败");
				goldProductDetailsDTO.setGoldPrice(new BigDecimal("0.00"));
			}
			//获得结束时间暂时使用还清时间

			Calendar cl = Calendar.getInstance();
			cl.setTime(goldProductsExtend.getOpenTime());cl.add(Calendar.DATE,1);
			goldProductDetailsDTO.setDueDate(goldProductsExtend.getClearTime()!=null? goldProductsExtend.getClearTime().getTime():cl.getTime().getTime());
			goldProductDetailsDTO.setMaxAmount(goldProductsExtend.getMaxAmount());
			goldProductDetailsDTO.setMinAmount(goldProductsExtend.getMinAmount());
			goldProductDetailsDTO.setMethod(GoldMethodStatusEnum.METHOD_STATUS_ONCE.getKey());
			goldProductDetailsDTO.setStatus(goldProductsExtend.getStatus() == null? "ERROR": GetProductStatusUtil.getProductStatus(goldProductsExtend.getStatus()));
			goldProductDetailsDTO.setTimeOpen(goldProductsExtend.getOpenTime());
			goldProductDetailsDTO.setLoanDescription(goldProductsExtend.getProductDetail());
			goldProductDetailsDTO.setProjectDescription(goldProductsExtend.getProjectDetail());
			goldProductDetailsDTO.setLoanTitle(goldProductsExtend.getTitle());
			goldProductDetailsDTO.setValueDate(goldProductsExtend.getValueTime()!=null?goldProductsExtend.getValueTime():cl.getTime());
			goldProductDetailsDTO.setTime(new Date());
			if (null==userId)
				goldProductDetailsDTO.setUseCoupon(goldProductsExtend.getUseCoupon()==null?false:goldProductsExtend.getUseCoupon()== 0?false:true);
		}else{
			goldProductDetailsDTO = new GoldProductDetailsDTO();
		}
		return goldProductDetailsDTO;
	}

	/**
	 * 根据还清时间查询产品id和code
	 * @author liujunhan
	 * @date  2017年03月22日
	 * @param  clearTime 开标时间
	 * @return GoldProductDetailsDTO对象
	 * @since JDK 1.8
	 */
	@Override
	public List<GoldProductIdAndCodeDTO> findIdAndCodeByClearTime(String clearTime) {
		return BeanMapper.mapList(goldProductDao.getIdAndCodeByClearTime(clearTime),GoldProductIdAndCodeDTO.class);
	}

	/**
	 *
	 * 根据产品id，更新产品状态status
	 *
	 * @param productId  （String）产品id
	 * @param status  (Integer)状态码
	 * @param settleTime 结标时
	 * @param valueTime 起息日
	 * @return Integer 影响的行数
	 * @ServiceException
	 *                30000:参数错误
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date  2017年04月14日
	 */
	@Override
	public Integer updateStstusByProductId(String productId, Integer status, Date settleTime,Date valueTime) {

		if(StringUtils.isBlank(productId)&&null== status&& null ==settleTime&& null ==valueTime){
			return 0 ;
		}
		
		return goldProductDao.updateStatusByProductId(productId,status,settleTime,valueTime);
	}

	/**
	 * 查询产品信息
	 * @author liujunhan
	 * @date  2017年04月15日
	 * @param settleTime  结标时间
	 * @return List<GoldProductsExtendDTO>
	 * @since JDK 1.8 findByProductId
	 */
	@Override
	public List<GoldProductsExtendDTO> findProductsBySettleTime(String settleTime) {

		return BeanMapper.mapList(goldProductDao.findProductsBySettleTime(settleTime),GoldProductsExtendDTO.class);
	}

	/**
	 * 根据当前时间，查询当天开标的产品
	 * 
	 * @param currentTime 当前时间  [yyyy-MM-dd]
	 * @return List<GoldProductsExtendDTO>
	 * @author LiuQiangBin
	 * @date  2017年04月15日
	 * @since JDK 1.8 
	 */
	@Override
	public List<GoldProductsExtendDTO> findProductsByCurrentTime(String currentTime) {
		return BeanMapper.mapList(goldProductDao.findProductsByCurrentTime(currentTime),GoldProductsExtendDTO.class);
	}

	/**
	 *
	 * 根据产品id，更新产品状态status
	 *
	 * @param productId  （String）产品id
 	 * @param status  (Integer)状态码
	 * @return Integer 影响的行数
	 * @author LiuQiangBin
	 * @date  2017年04月17日
	 * @since JDK 1.8
	 */
	@Override
	public Integer updateStatusById(String productId, Integer status) {
		return goldProductDao.updateStatusById(productId, status);
	}

	/**
	  *
	  * 查询产品数量
	  *
	  * @return Integer 产品数量
	  * @author LiuQiangBin
	  * @date  2017年04月18日
	  * @since JDK 1.8
	  */
	@Override
	public Integer selectProductCount() {
		String currentTime ;
		int count = 0 ;
		int rows = 0;
		do {
			currentTime = LocalDate.now().minusDays(count).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			rows = goldProductDao.selectProductCountByCurrentTimeAndStatusStartAndStatusEnd(currentTime,GoldProductStatusEnum.PRODUCTSTATUS_ARRANGED_READY.getStatus(),GoldProductStatusEnum.PRODUCTSTATUS_CLEARED.getStatus());
			count++;
		}while (rows == 0 && count<10);
		return rows;
	}

	/**
	 *
	 *
	 *
	 * @return List<String></> 产品数量
	 * @author liujunhan
	 * @date  2017年04月20日
	 * @since JDK 1.8
	 */
	@Override
	public List<String> selectIdByStatus() {
		return goldProductDao.listRegularProductIdsToSettle();
	}

	/**
	 *
	 * 查询产品信息
	 *
	 * @param openTime 当前时间
	 * @return  List<GoldProductIdAndCodeDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年04月21日
	 */
	@Override
	public List<GoldProductIdAndCodeDTO> getProductIdCodeByOpenTime(String openTime){
		return BeanMapper.mapList(goldProductDao.getIdAndCodeByOpenTime(openTime),GoldProductIdAndCodeDTO.class);
	}

	/**
	 *
	 * 查询产品id集合根据产品状态
	 *
	 * @return List<String></> 产品数量
	 * @author liujunhan
	 * @date  2017年04月24日
	 * @since JDK 1.8
	 */
	@Override
	public List<GoldProductsExtendDTO> findIdByStatus(Integer status) {
		return BeanMapper.mapList(goldProductDao.findIdByStatus(status),GoldProductsExtendDTO.class) ;
	}
}

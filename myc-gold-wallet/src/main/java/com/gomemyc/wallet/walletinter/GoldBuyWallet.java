package com.gomemyc.wallet.walletinter;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gomemyc.wallet.constant.WalletUrl;
import com.gomemyc.wallet.exception.ServiceException;
import com.gomemyc.wallet.resp.BuyTimeGoldDto;
import com.gomemyc.wallet.resp.BuyTimeGoldResultDto;
import com.gomemyc.wallet.resp.ConfirmBuyTimeGoldDto;
import com.gomemyc.wallet.resp.ConfirmBuyTimeGoldResultDto;
import com.gomemyc.wallet.util.HttpClientHelper;
import com.gomemyc.wallet.util.GoldInfoCode;
import com.gomemyc.wallet.util.JSONUtils;
import com.gomemyc.wallet.util.SignHelper;
import com.google.common.collect.Maps;

/**
 * @ClassName: BuyInterfaceWallet
 * @Description: 黄金钱包购买接口
 * @author liuqiangbin
 * @date 2017年3月9日
 *
 *
 */
public class GoldBuyWallet {
	
	
	private static final Logger logger = LoggerFactory.getLogger(GoldBuyWallet.class);

	static HttpClientHelper helper = new HttpClientHelper();

	/**
	 * @Title BuyTimeGold
	 * @Description 2.4 购买定期金
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param orderTime (String) 订单时间(必填)
	 * @param goldWeight (Integer) 订单金重(选填)
	 * @param orderAmount (Integer) 订单金额(选填)
	 * @param reqNo (String) 请求订单号(必填)
	 * @param buyType (String) 购买类型(必填)
	 * @param productCode (String) 产品编码(必填)
	 * @param depositCode (String) 国美金融的标的代码(必填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static BuyTimeGoldResultDto buyTimeGold(String version,
			                                       String merchantCode,
			                                       String orderTime,
			                                       BigDecimal goldWeight,
			                                       BigDecimal orderAmount,
			                                       String reqNo,
			                                       String buyType,
			                                       String productCode,
			                                       String depositCode,
			                                       String sign, 
			                                       String ip) throws ServiceException {
		
		
		logger.info("buyTimeGold in GoldBuyWallet the params "
			         + "[version={}],"
			         + "[merchantCode={}],"
				     + "[orderTime={}],"
				     + "[goldWeight={}],"
				     + "[orderAmount={}],"
				     + "[reqNo={}],"
				     + "[buyType={}],"
				     + "[productCode={}],"
				     + "[depositCode={}],"
				     + "[sign={}],"
				     + "[ip={}]"
				     ,version,
				     merchantCode,
				     orderTime,
				     goldWeight,
				     orderAmount,
				     reqNo,
				     buyType,
				     productCode,
				     depositCode,
				     sign,
				     ip);
		
		
		if (StringUtils.isBlank(buyType) || 
			StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) || 
			StringUtils.isBlank(orderTime) ||
			orderAmount.compareTo(BigDecimal.ZERO) < 1 ||
			StringUtils.isBlank(reqNo) || 
			StringUtils.isBlank(productCode) || 
			StringUtils.isBlank(depositCode))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
					                   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());
		
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("orderTime", orderTime);
		params.put("goldWeight", goldWeight);
		params.put("orderAmount", orderAmount);
		params.put("reqNo", reqNo);
		params.put("buyType", buyType);
		params.put("productCode", productCode);
		params.put("depositCode", depositCode);
		String reqData = JSONUtils.objectToJSONString(params);
		
		logger.info("buyTimeGold  in  GoldBuyWallet ,the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		
		logger.info("buyTimeGold  in  GoldBuyWallet ,the param [sign={}]",sign);
		params.put("sign", sign);

		// 获取返回的json字符串
		BuyTimeGoldDto dto = new BuyTimeGoldDto(version,
				                                merchantCode,
				                                orderTime,
				                                goldWeight,
				                                orderAmount,
				                                reqNo,
				                                buyType,
				                                productCode,
				                                depositCode,
				                                sign);
		
		logger.info("buyTimeGold  in  GoldBuyWallet ,the param [dto={}]",dto);
		String body = JSONUtils.objectToJSONString(dto);
		
		logger.info("buyTimeGold  in  GoldBuyWallet ,the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.BUY_REGULAR_GOLD, body, "");
		// 转换成Dto
		BuyTimeGoldResultDto resultdto = JSONUtils.jsonStringToObject(BuyTimeGoldResultDto.class, result);
		
		logger.info("buyTimeGold  in  GoldBuyWallet ,the param [resultdto={}]",resultdto);
		return resultdto;
	}

	/**
	 * @Title BuyTimeGold
	 * @Description 2.4 购买定期金
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param orderTime (String) 订单时间(必填)
	 * @param reqNo (String) 请求订单号(必填)
	 * @param buyType (String) 购买类型(必填)
	 * @param productCode (String) 产品编码(必填)
	 * @param depositCode (String) 国美金融的标的代码(必填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static BuyTimeGoldResultDto buyTimeGold(String version,
			                                       String merchantCode,
			                                       String orderTime,
			                                       BigDecimal orderAmount,
			                                       String reqNo, 
			                                       String buyType, 
			                                       String productCode, 
			                                       String depositCode, 
	
			                                       String sign, String ip) throws ServiceException {
		logger.info("buyTimeGold in GoldBuyWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[orderTime={}],"
			     + "[orderAmount={}],"
			     + "[reqNo={}],"
			     + "[buyType={}],"
			     + "[productCode={}],"
			     + "[depositCode={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     orderTime,
			     orderAmount,
			     reqNo,
			     buyType,
			     productCode,
			     depositCode,
			     sign,
			     ip);
		return GoldBuyWallet.buyTimeGold(version, 
				                         merchantCode, 
				                         orderTime, 
				                         BigDecimal.ZERO, 
				                         orderAmount, 
				                         reqNo, 
				                         buyType, 
				                         productCode,
				                         depositCode,
				                         sign, 
				                         ip);
	}

	/**
	 * @Title confirmBuyTimeGold
	 * @Description 2.5 购买定期金确认
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param orderTime (String) 订单时间(必填)
	 * @param confirmWeight (Integer) 确认金重(选填)
	 * @param confirmAmount (Integer) 确认金额(选填)
	 * @param confirmPrice (Integer) 确认金价(选填)
	 * @param productCode (String) 产品编码(必填)
	 * @param reqNo (String) 请求订单号(必填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static ConfirmBuyTimeGoldResultDto confirmBuyTime(String version, 
			                                                 String merchantCode,
			                                                 String orderTime, 
			                                                 BigDecimal confirmWeight,
			                                                 BigDecimal confirmAmount, 
			                                                 BigDecimal confirmPrice, 
			                                                 String productCode, 
			                                                 String reqNo, 
			                                                 String sign, 
			                                                 String ip) throws ServiceException {

		logger.info("confirmBuyTime in GoldBuyWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[orderTime={}],"
			     + "[confirmWeight={}],"
			     + "[confirmAmount={}],"
			     + "[confirmPrice={}],"
			     + "[productCode={}],"
			     + "[reqNo={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     orderTime,
			     confirmWeight,
			     confirmAmount,
			     confirmPrice,
			     productCode,
			     reqNo,
			     sign,
			     ip);
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) || 
			StringUtils.isBlank(orderTime) || 
			
			StringUtils.isBlank(productCode) || 
			StringUtils.isBlank(reqNo))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());
		// 签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("orderTime", orderTime);
		params.put("confirmWeight", confirmWeight);
		params.put("confirmAmount", confirmAmount);
		params.put("confirmPrice", confirmPrice);
		params.put("productCode", productCode);
		params.put("reqNo", reqNo);
		String reqData = JSONUtils.objectToJSONString(params);
		
		logger.info("confirmBuyTime in GoldBuyWallet the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		
		logger.info("buyTimeGold  in  GoldBuyWallet ,the param [sign={}]",sign);
		params.put("sign", sign);

		// 获取返回的json字符串
		ConfirmBuyTimeGoldDto dto = new ConfirmBuyTimeGoldDto(version, 
				                                              merchantCode, orderTime, 
				                                              confirmWeight, 
				                                              confirmAmount, 
				                                              confirmPrice, 
				                                              productCode,
				                                              reqNo, 
				                                              sign);
		String body = JSONUtils.objectToJSONString(dto);
		
		logger.info("confirmBuyTime in GoldBuyWallet the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.CONFIRM_BUY_REGULAR_GOLD, body, "");
		// 转换成Dto
		ConfirmBuyTimeGoldResultDto resultdto = JSONUtils.jsonStringToObject(ConfirmBuyTimeGoldResultDto.class, result);
		
		logger.info("confirmBuyTime in GoldBuyWallet the param [resultdto={}]",resultdto);
		return resultdto;
	}
	
}
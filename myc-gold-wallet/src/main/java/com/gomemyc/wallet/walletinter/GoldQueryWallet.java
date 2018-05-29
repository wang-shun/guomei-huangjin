package com.gomemyc.wallet.walletinter;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gomemyc.wallet.constant.WalletUrl;
import com.gomemyc.wallet.exception.ServiceException;
import com.gomemyc.wallet.resp.QueryBuyTimeOrderDto;
import com.gomemyc.wallet.resp.QueryBuyTimeOrderResultDto;
import com.gomemyc.wallet.resp.QueryMyAccountDto;
import com.gomemyc.wallet.resp.QueryMyAccountResultDto;
import com.gomemyc.wallet.resp.QueryPriceDto;
import com.gomemyc.wallet.resp.QueryPriceResultDto;
import com.gomemyc.wallet.resp.QueryTimeProductListDto;
import com.gomemyc.wallet.resp.QueryTimeProductListResultDto;
import com.gomemyc.wallet.util.HttpClientHelper;
import com.gomemyc.wallet.util.GoldInfoCode;
import com.gomemyc.wallet.util.JSONUtils;
import com.gomemyc.wallet.util.SignHelper;
import com.google.common.collect.Maps;

/**
 * @ClassName: QueryInterfaceWallet
 * @Description: 黄金钱包查询接口
 * @author zhuyunpeng
 * @date 2017年3月8日
 *
 */
public class GoldQueryWallet {
	

	private static final Logger logger = LoggerFactory.getLogger(GoldQueryWallet.class);
	static HttpClientHelper helper = new HttpClientHelper();
	/**
	 * @Title QueryPrice
	 * @Description 2.1 查询实时金价
	 * @author zhuyunpeng
	 * @date 2017年3月8日
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static QueryPriceResultDto queryPrice(String version, 
												 String merchantCode, 
												 String ip) throws ServiceException{
		if (StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());
		// 获取返回的json字符串
		QueryPriceDto dto = new QueryPriceDto(version, merchantCode);
		logger.info("queryPrice  in  GoldQueryWallet ,the param [dto={}]",dto);
		String body = JSONUtils.objectToJSONString(dto);
		logger.info("queryPrice  in  GoldQueryWallet ,the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.QUERY_THE_REAL_GOLD_PRICE, body, "");
		logger.info("queryPrice  in  GoldQueryWallet ,the param [result={}]",result);
		QueryPriceResultDto resultdto = JSONUtils.jsonStringToObject(QueryPriceResultDto.class, result);
		logger.info("queryPrice  in  GoldQueryWallet ,the param [resultdto={}]",resultdto);
		return resultdto;
	}

	/**
	 * @Title QueryMyAccount
	 * @Description 2.2 账户信息查询
	 * @author zhuyunpeng
	 * @date 2017年3月8日
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static QueryMyAccountResultDto queryMyAccount(String version, 
														 String merchantCode, 
														 String sign, 
														 String ip) throws ServiceException {
		if (StringUtils.isBlank(sign) ||
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(), 
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());
		//签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [params={}]",params);
		String reqData = JSONUtils.objectToJSONString(params);
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [sign={}]",sign);
		params.put("sign", sign);
		// 获取返回的json字符串
		QueryMyAccountDto dto = new QueryMyAccountDto(version, merchantCode, sign);
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [dto={}]",dto);
		String body = JSONUtils.objectToJSONString(dto);
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.QUERY_ACCOUNT_INFORMATION, body, "");
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [result={}]",result);
		QueryMyAccountResultDto resultdto = JSONUtils.jsonStringToObject(QueryMyAccountResultDto.class, result);
		logger.info("queryMyAccount  in  GoldQueryWallet ,the param [resultdto={}]",resultdto);
		return resultdto;
	}

	/**
	 * @Title QueryTimeProductList
	 * @Description 2.3 定期金产品查询
	 * @date 2017年3月8日
	 * @author zhuyunpeng
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param start (Integer) 起始记录行数(选填)
	 * @param size (Integer) 获取记录数大小(选填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static QueryTimeProductListResultDto queryTimeProduct(String version, 
																 String merchantCode,
																 Integer start, 
																 Integer size, 
																 String sign, 
																 String ip) throws ServiceException {
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) ||
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) ||
			StringUtils.isBlank(start.toString()) || 
			StringUtils.isBlank(size.toString()))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());
		//签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("start", start);
		params.put("size", size);
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [params={}]",params);
		String reqData = JSONUtils.objectToJSONString(params);
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [sign={}]",sign);
		params.put("sign", sign);
		//获取返回的json字符串
		QueryTimeProductListDto dto = new QueryTimeProductListDto(version, 
																  merchantCode, 
																  start, 
																  size, 
																  sign);
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [dto={}]",dto);
		String body = JSONUtils.objectToJSONString(dto);
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.QUERY_REGULAR_GOLD_PRODUCT, body, "");
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [result={}]",result);
		QueryTimeProductListResultDto resultdto = JSONUtils.jsonStringToObject(QueryTimeProductListResultDto.class,
				result);
		logger.info("queryTimeProduct  in  GoldQueryWallet ,the param [resultdto={}]",resultdto);
		return resultdto;
	}
	
	/**
	 * @Title QueryTimeProductList
	 * @Description 2.3 定期金产品查询 (只传必填)
	 * @date 2017年3月8日
	 * @author zhuyunpeng
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static QueryTimeProductListResultDto queryTimeProduct(String version, 
																 String merchantCode, 
																 String sign, 
																 String ip) throws ServiceException {
		//start,size 默认为0
		return GoldQueryWallet.queryTimeProduct(version, 
												merchantCode,
												0, 
												0,
												sign, 
												ip);
	}
	/**
	 * @Title QueryBuyTimeOrder
	 * @Description 2.6 购买定期金交易结果查询
	 * @author liuqiangbin
	 * @date 2017年3月8日
	 * @param version (String) 版本号(必填)
	 * @param merchantCode (String) 商户编码(必填)
	 * @param reqNo (String) 订单号(必填)
	 * @param sign (String) 签名结果(必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 * 
	 */
	public static QueryBuyTimeOrderResultDto queryBuyTimeOrder(String version, 
															   String merchantCode, 
															   String reqNo, 
															   String sign, 
															   String ip) throws ServiceException {
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) || 
			StringUtils.isBlank(reqNo))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());
		//签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("reqNo", reqNo);
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [params={}]",params);
		String reqData = JSONUtils.objectToJSONString(params);
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [sign={}]",sign);
		params.put("sign", sign);
		//获取返回的json字符串
		QueryBuyTimeOrderDto dto = new QueryBuyTimeOrderDto(version,
															merchantCode, 
															reqNo, 
															sign);
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [dto={}]",dto);
		String body = JSONUtils.objectToJSONString(dto);
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.QUERY_BUY_REGULAR_GOLD_ORDER, body, "");
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [result={}]",result);
		QueryBuyTimeOrderResultDto resultdto = JSONUtils.jsonStringToObject(QueryBuyTimeOrderResultDto.class,
				result);
		logger.info("queryBuyTimeOrder  in  GoldQueryWallet ,the param [resultdto={}]",resultdto);
		return resultdto;
	}
	
}

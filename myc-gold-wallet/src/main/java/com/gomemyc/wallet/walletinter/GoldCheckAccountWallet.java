package com.gomemyc.wallet.walletinter;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gomemyc.wallet.constant.WalletUrl;
import com.gomemyc.wallet.exception.ServiceException;
import com.gomemyc.wallet.resp.CheckDailyInterestDto;
import com.gomemyc.wallet.resp.CheckDailyInterestResultDto;
import com.gomemyc.wallet.resp.CheckExpireOrderDto;
import com.gomemyc.wallet.resp.CheckExpireOrderResultDto;
import com.gomemyc.wallet.resp.CheckSumInterestDto;
import com.gomemyc.wallet.resp.CheckSumInterestResultDto;
import com.gomemyc.wallet.resp.CheckTimeOrderDto;
import com.gomemyc.wallet.resp.CheckTimeOrderResultDto;
import com.gomemyc.wallet.util.HttpClientHelper;
import com.gomemyc.wallet.util.GoldInfoCode;
import com.gomemyc.wallet.util.JSONUtils;
import com.gomemyc.wallet.util.SignHelper;
import com.google.common.collect.Maps;

public class GoldCheckAccountWallet {
	
	
	private static final Logger logger = LoggerFactory.getLogger(GoldCheckAccountWallet.class);
	
	static HttpClientHelper helper = new HttpClientHelper();

	/**
	 * @Title CheckTimeOrder
	 * @Description 2.7.1 买定期金对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param orderDate (String) 订单日期 (必填)【格式yyyy-MM-dd】
	 * @param start (integer) 起始记录行数 (选填)
	 * @param size (integer) 获取记录数大小 (选填)
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static CheckTimeOrderResultDto checkTimeOrder(String version, 
														 String merchantCode,
														 String productCode, 
														 String orderDate,
														 Integer start, 
														 Integer size,
														 String sign, 
														 String ip) throws ServiceException{
		
		logger.info("checkTimeOrder in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[orderDate={}],"
			     + "[start={}],"
			     + "[size={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     orderDate,
			     start,
			     size,
			     sign,
			     ip);
	
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) ||
			StringUtils.isBlank(orderDate) ||
			StringUtils.isBlank(start.toString()) || 
			StringUtils.isBlank(size.toString()))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());

		//签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("productCode",productCode);
		params.put("orderDate",orderDate);
		params.put("start",start);
		params.put("size",size);
		String reqData = JSONUtils.objectToJSONString(params);
		
		logger.info("checkTimeOrder in GoldCheckAccountWallet the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData,sign);
		
		logger.info("checkTimeOrder in GoldCheckAccountWallet the param [sign={}]",sign);
		params.put("sign", sign);
        
        CheckTimeOrderDto dto = new CheckTimeOrderDto(version, 
        											  merchantCode, 
        											  productCode,
        											  orderDate, 
        											  start, 
        											  size, 
        											  sign);
        
		logger.info("checkTimeOrder in GoldCheckAccountWallet the param [dto={}]",dto);
        helper = new HttpClientHelper();
        String body = JSONUtils.objectToJSONString(dto);
        
		logger.info("checkTimeOrder in GoldCheckAccountWallet the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.CHECK_TIME_ORDER, body, "");
		
		logger.info("checkTimeOrder in GoldCheckAccountWallet the param [result={}]",result);
        CheckTimeOrderResultDto resultdto = JSONUtils.jsonStringToObject(CheckTimeOrderResultDto.class, result);
		
        logger.info("checkTimeOrder in GoldCheckAccountWallet the param [resultdto={}]",resultdto);
        return resultdto;
    }

	/**
     * @Title CheckTimeOrder
     * @Description 2.7.1 买定期金对账文件
     * @author liuqiangbin
     * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param orderDate (String) 订单日期 (必填)【格式yyyy-MM-dd】
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
     * @throws ServiceException
     */
    public static CheckTimeOrderResultDto checkTimeOrder(String version, 
    													 String merchantCode,
    													 String productCode, 
    													 String orderDate, 
    													 String sign, 
    													 String ip) throws ServiceException{
    	logger.info("checkTimeOrder in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[orderDate={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     orderDate,
			     sign,
			     ip);
        return GoldCheckAccountWallet.checkTimeOrder(version, 
        											 merchantCode, 
        											 productCode,
        											 orderDate,
        											 0,
        											 0,
        											 sign, 
        											 ip);

    }

	/**
	 * @Title CheckExpireOrder
	 * @Description 2.7.2定期金到期对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param orderDate (String) 订单日期 (必填)【格式yyyy-MM-dd】
	 * @param start (integer) 起始记录行数 (选填)
	 * @param size (integer) 获取记录数大小 (选填)
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static CheckExpireOrderResultDto checkExpireOrder(String version, 
															 String merchantCode, 
															 String productCode, 
															 String orderDate, 
															 Integer start, 
															 Integer size, 
															 String sign,
															 String ip) throws ServiceException{
		
		logger.info("checkExpireOrder in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[orderDate={}],"
			     + "[start={}],"
			     + "[size={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     orderDate,
			     start,
			     size,
			     sign,
			     ip);
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) ||
			StringUtils.isBlank(orderDate) || 
			StringUtils.isBlank(start.toString()) || 
			StringUtils.isBlank(size.toString()))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());

		//签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("productCode",productCode);
        params.put("orderDate",orderDate);
        params.put("start",start);
        params.put("size",size);
		String reqData = JSONUtils.objectToJSONString(params);

		logger.info("checkExpireOrder in GoldCheckAccountWallet the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		
		logger.info("checkExpireOrder in GoldCheckAccountWallet the param [sign={}]",sign);
		params.put("sign", sign);
        
		CheckExpireOrderDto dto = new CheckExpireOrderDto(version, 
														  merchantCode, 
														  productCode, 
														  orderDate, 
														  start, 
														  size, 
														  sign);
		
		logger.info("checkExpireOrder in GoldCheckAccountWallet the param [dto={}]",dto);
        helper = new HttpClientHelper();

        String body = JSONUtils.objectToJSONString(dto);
        
		logger.info("checkExpireOrder in GoldCheckAccountWallet the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.CHECK_EXPIRE_ORDER, body, "");
		
		logger.info("checkExpireOrder in GoldCheckAccountWallet the param [result={}]",result);
		CheckExpireOrderResultDto resultdto = JSONUtils.jsonStringToObject(CheckExpireOrderResultDto.class, result);
		logger.info("checkExpireOrder in GoldCheckAccountWallet the param [resultdto={}]",resultdto);
        return resultdto;
    }

	/**
	 * @Title CheckExpireOrder
	 * @Description 2.7.2定期金到期对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param orderDate (String) 订单日期 (必填)【格式yyyy-MM-dd】
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
    public static CheckExpireOrderResultDto checkExpireOrder(String version, 
    														 String merchantCode,
    														 String productCode, 
    														 String orderDate,
    														 String sign,
    														 String ip) throws ServiceException{
    	       
    	logger.info("checkExpireOrder in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[orderDate={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     orderDate,
			     sign,
			     ip);
        return GoldCheckAccountWallet.checkExpireOrder(version, 
        											   merchantCode, 
        											   productCode, 
        											   orderDate,
        											   0,
        											   0,
        											   sign,
        											   ip);

    }

	/**
	 * @Title CheckDailyInterest
	 * @Description 2.7.3每天利息对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param orderDate (String) 订单日期 (必填)【格式yyyy-MM-dd】
	 * @param start (integer) 起始记录行数 (选填)
	 * @param size (integer) 获取记录数大小 (选填)
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
    public static CheckDailyInterestResultDto checkDailyInterest(String version, 
    															 String merchantCode,
    															 String productCode,
    															 String orderDate,
    															 Integer start, 
    															 Integer size,
    															 String sign, 
    															 String ip) throws ServiceException{
    	
    	logger.info("checkDailyInterest in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[orderDate={}],"
			     + "[start={}],"
			     + "[size={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     orderDate,
			     start,
			     size,
			     sign,
			     ip);
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) || 
			StringUtils.isBlank(productCode) || 
			StringUtils.isBlank(orderDate) || 
			StringUtils.isBlank(start.toString()) || 
			StringUtils.isBlank(size.toString()))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());

		// 签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("productCode", productCode);
		params.put("orderDate", orderDate);
		params.put("start", start);
		params.put("size", size);
		String reqData = JSONUtils.objectToJSONString(params);
		
		logger.info("checkDailyInterest in GoldCheckAccountWallet the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		
		logger.info("checkDailyInterest in GoldCheckAccountWallet the param [sign={}]",sign);
		params.put("sign", sign);

		CheckDailyInterestDto dto = new CheckDailyInterestDto(version, 
				                                              merchantCode,
				                                              productCode, 
				                                              orderDate, 
				                                              start,
				                                              size, 
				                                              sign);
		
		logger.info("checkDailyInterest in GoldCheckAccountWallet the param [dto={}]",dto);
		helper = new HttpClientHelper();

		String body = JSONUtils.objectToJSONString(dto);

		logger.info("checkDailyInterest in GoldCheckAccountWallet the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.CHECK_DAILY_INTEREST, body, "");
		
		logger.info("checkDailyInterest in GoldCheckAccountWallet the param [result={}]",result);
		CheckDailyInterestResultDto resultdto = JSONUtils.jsonStringToObject(CheckDailyInterestResultDto.class, result);
		
		logger.info("checkDailyInterest in GoldCheckAccountWallet the param [resultdto={}]",resultdto);
		return resultdto;
	}

	/**
	 * @Title CheckExpireOrder
	 * @Description 2.7.2定期金到期对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param orderDate (String) 订单日期 (必填)【格式yyyy-MM-dd】
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static CheckDailyInterestResultDto checkDailyInterest(String version, 
																 String merchantCode, 
																 String productCode,
																 String orderDate,
																 String sign,
																 String ip) throws ServiceException {
		logger.info("checkDailyInterest in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[orderDate={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     orderDate,
			     sign,
			     ip);
		return GoldCheckAccountWallet.checkDailyInterest(version, 
														 merchantCode,
														 productCode, 
														 orderDate, 
														 0,
														 0,
														 sign,
														 ip);
	}

	/**
	 * @Title CheckSumInterest
	 * @Description 2.7.4定期到期利息汇总对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param start (integer) 起始记录行数 (选填)
	 * @param size (integer) 获取记录数大小 (选填)
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static CheckSumInterestResultDto checkSumInterest(String version,
															 String merchantCode,
			                                                 String productCode,
			                                                 Integer start, 
			                                                 Integer size, 
			                                                 String sign,
			                                                 String ip)throws ServiceException {
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[start={}],"
			     + "[size={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     start,
			     size,
			     sign,
			     ip);
		if (StringUtils.isBlank(sign) || 
			StringUtils.isBlank(ip) || 
			StringUtils.isBlank(version) || 
			StringUtils.isBlank(merchantCode) || 
			StringUtils.isBlank(productCode) || 
			StringUtils.isBlank(start.toString()) || 
			StringUtils.isBlank(size.toString()))
			throw new ServiceException(GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetCode(),
									   GoldInfoCode.REQUEST_PARAMETER_ERROR.getRetMsg());

		// 签名
		Map<String, Object> params = Maps.newHashMap();
		params.put("version", version);
		params.put("merchantCode", merchantCode);
		params.put("productCode", productCode);
		params.put("start", start);
		params.put("size", size);
		String reqData = JSONUtils.objectToJSONString(params);
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the param [reqData={}]",reqData);
		sign = SignHelper.sign(reqData, sign);
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the param [sign={}]",sign);
		params.put("sign", sign);

		CheckSumInterestDto dto = new CheckSumInterestDto(version, 
														  merchantCode, 
														  productCode, 
														  start,
														  size,
														  sign);
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the param [dto={}]",dto);
		helper = new HttpClientHelper();

		String body = JSONUtils.objectToJSONString(dto);
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the param [body={}]",body);
		String result = helper.postMsg(ip + WalletUrl.CHECK_SUM_INTEREST, body, "");
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the param [result={}]",result);
		CheckSumInterestResultDto resultdto = JSONUtils.jsonStringToObject(CheckSumInterestResultDto.class, result);
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the param [resultdto={}]",resultdto);

		return resultdto;
	}

	/**
	 * @Title CheckSumInterest
	 * @Description 2.7.4定期到期利息汇总对账文件
	 * @author liuqiangbin
	 * @date 2017年3月9日
	 * @param version (String) 版本号 (必填)
	 * @param merchantCode (String) 商户编码 (必填)
	 * @param productCode (String) 产品编码 (必填)
	 * @param sign (String) 签名结果 (必填)
	 * @param ip (String) 黄金钱包ip地址(必填)
	 * @throws ServiceException
	 */
	public static CheckSumInterestResultDto checkSumInterest(String version, 
															 String merchantCode, 
															 String productCode,
															 String sign,
															 String ip) throws ServiceException {
		
		logger.info("checkSumInterest in GoldCheckAccountWallet the params "
		         + "[version={}],"
		         + "[merchantCode={}],"
			     + "[productCode={}],"
			     + "[sign={}],"
			     + "[ip={}]"
			     ,version,
			     merchantCode,
			     productCode,
			     sign,
			     ip);

		return GoldCheckAccountWallet.checkSumInterest(version, 
													   merchantCode, 
													   productCode, 
													   0, 
													   0, 
													   sign, 
													   ip);

	}

}

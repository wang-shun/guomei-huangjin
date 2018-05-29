package com.gomemyc.wallet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;

public class SignHelper {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(SignHelper.class);
	
	/**
	 * 加签
	 * 
	 * @param value
	 * @param signKey
	 * @return
	 */
	public static String sign(Object value, String signKey) {
		return sign(JSONUtils.objectToJSONString(value), signKey);
	}
	
	/**
	 * 验签
	 * 
	 * @param json
	 * @param signKey
	 * @param sign
	 * @return
	 */
	public static boolean validSign(String json, String signKey, String sign) {
		return sign(json, signKey).equals(sign);
	}
	
	/**
	 * 加签
	 * 
	 * @param json json字符串
	 * @param signKey
	 * @return
	 */
	public static String sign(String json, String signKey) {
		JSONObject params = JSONUtils.jsonStringToJsonMap(json);
		List<String> keyList = new ArrayList<String>(params.keySet());
		Collections.sort(keyList);
		
		StringBuffer sbuffer = new StringBuffer();
		for (String key : keyList) {
			Object val = params.get(key);
			if (!"sign".equals(key) && val != null) {
				sbuffer.append(key);
				sbuffer.append("=");
				sbuffer.append(val);
				sbuffer.append("&");
			}
		}
		
		// 待签名原始字符串
		String signTemp = sbuffer + signKey;
		LOG.debug("ori sign string. [signTemp = {}]", signTemp);
		
		// MD5签名
		String sign = MD5Utils.md5(signTemp);
		LOG.debug("sign string. [sign = {}]", sign);
		return sign;
	}
	
}

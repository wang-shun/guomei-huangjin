package com.gomemyc.wallet.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * JSON数据格式转换工具类
 * 
 * @author HuangSheng
 * 
 */
public class JSONUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(JSONUtils.class);
	
	private static final ObjectMapper DATE_FORMAT_NOT_NULL_MAPPER = new ObjectMapper();
	
	static {
		DATE_FORMAT_NOT_NULL_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		DATE_FORMAT_NOT_NULL_MAPPER.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
		DATE_FORMAT_NOT_NULL_MAPPER.getSerializationConfig()
				.withDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	
	private JSONUtils() {
	}
	
	/**
	 * java对象转为json字符串<br>
	 * null不输出并且日期类型按照yyyy-MM-dd HH:mm:ss格式化
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static String objectToJSONString(Object value) {
		try {
			return DATE_FORMAT_NOT_NULL_MAPPER.writeValueAsString(value);
		} catch (IOException e) {
			LOG.error("json字符串转为java对象出错. value = " + value, e);
		}
		return "";
	}
	
	/**
	 * java对象转为json输出流<br>
	 * null不输出并且日期类型按照yyyy-MM-dd HH:mm:ss格式化
	 * 
	 * @param value
	 * @param out
	 */
	public static void objectToJSONStream(Object value, OutputStream out) {
		try {
			DATE_FORMAT_NOT_NULL_MAPPER.writeValue(out, value);
		} catch (IOException e) {
			LOG.error("json字符串转为java对象出错. [value = " + value + "]", e);
		}
	}
	
	/**
	 * Json字符串转换为java对象
	 * 
	 * @param type
	 * @param jsonStr
	 * @return
	 * @throws IOException
	 */
	public static <T> T jsonStringToObject(Class<T> type, String jsonStr) {
		try {
			return JSONObject.parseObject(jsonStr, type);
		} catch (Exception e) {
			LOG.error("json字符串转为java对象出错. [jsonStr = " + jsonStr + "]", e);
		}
		return null;
	}
	
	/**
	 * json字符串转为自定义java对象<br>
	 * 日期类型按照yyyy-MM-dd HH:mm:ss格式化为Date
	 * 
	 * @param type
	 * @param jsonStr
	 * @return
	 */
	public static <T> T jsonStringToObject(TypeReference<T> type, String jsonStr) {
		try {
			return JSONObject.parseObject(jsonStr, type);
		} catch (Exception e) {
			LOG.error("json字符串转为自定义java对象出错. [jsonStr = " + jsonStr + "]", e);
		}
		return null;
	}
	
	/**
	 * json字符串转为jsonMap
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static JSONObject jsonStringToJsonMap(String jsonStr) {
		try {
			return JSONObject.parseObject(jsonStr);
		} catch (Exception e) {
			LOG.error("json字符串转为JSONObject对象出错. [jsonStr = " + jsonStr + "]", e);
		}
		return null;
	}
	
}

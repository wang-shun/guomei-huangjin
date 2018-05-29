package com.gomemyc.gold.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

public class DateUtil {

	private static Logger logger = Logger.getLogger(DateUtil.class);
	private static String defaultDatePattern = null;
	private static Calendar cale = Calendar.getInstance();
	public static final String TS_FORMAT = DateUtil.getDatePattern() + " HH:mm:ss.S";
	/** 日期格式yyyy-MM-dd HH:mm:ss字符串常量 */
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** 某天开始时分秒字符串常量 00:00:00 */
	/** 某天结束时分秒字符串常量 23:59:59 */
	public static final String DAY_END_STRING_HHMMSS = " 23:59:59";
	private static SimpleDateFormat sdf_datetime_format = new SimpleDateFormat(DATETIME_FORMAT);

	
	 /** 
     * 获取SimpleDateFormat 
     * @param parttern 日期格式 
     * @return SimpleDateFormat对象 
     * @throws RuntimeException 异常：非法日期格式 
     */  
    private static SimpleDateFormat getDateFormat(String parttern) throws RuntimeException {  
        return new SimpleDateFormat(parttern);  
    } 
    
    
	public static String getDateTime() {
		try {
			return sdf_datetime_format.format(cale.getTime());
		} catch (Exception e) {
			logger.debug("DateUtil.getDateTime():" + e.getMessage());
			return "";
		}
	}

	public static synchronized String getDatePattern() {
		defaultDatePattern = "yyyy-MM-dd";
		return defaultDatePattern;
	}
	
	/**
	   * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	   * 
	   * @param strDate
	   * @return
	   */
	public static Date strToDateLong(String strDate) {
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   ParsePosition pos = new ParsePosition(0);
		   Date strtodate = formatter.parse(strDate, pos);
		   return strtodate;
	}
	
	/**
	   * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	   * 
	   * @param strDate
	   * @return
	   */
	public static Date strToDateLong(String strDate,String dateFormat) {
		   SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		   ParsePosition pos = new ParsePosition(0);
		   Date strtodate = formatter.parse(strDate, pos);
		   return strtodate;
	}
	
    /** 
     * 增加日期的秒钟
     * @param date 日期 
     * @param second 增加的秒数
     * @return 增加秒钟后的日期 
     */  
    public static Date addSecond(Date date, int second) {  
    	Long time = date.getTime() + second*1000;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return DateUtil.strToDateLong(sdf.format(new Date(time)));
    }
    
    
    
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @param parttern 日期格式 
     * @return 日期 
     */  
    public static Date StringToDate(String date, String parttern) {  
        Date myDate = null;  
        if (date != null) {  
            try {  
                myDate = getDateFormat(parttern).parse(date);  
            } catch (Exception e) {  
            }  
        }  
        return myDate;  
    }  
    
    
    
    
    
    
    
}

package com.gomemyc.invest.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;

/**
 * 日期工具类
 * lujixiang
 */
public class DateUtil {
    
    public static SimpleDateFormat FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    
    public static int YEAR_PER_DAYS = 365;
    
    public static int MONTH_PER_DAYS = 30;
    
    /**
     * 转换日期
     * @param date
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    public static String toYYYYMMDDString(Date date) {
        if (null == date) {
            return "";
        }
        try {
            return FORMAT_YYYY_MM_DD.format(date);

        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * 计算日期推迟日期
     * @param startDate
     * @param days
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    public static Date offset(Date startDate, int days){
        if (null == startDate) {
            return null;
        }
        
        return new LocalDate(startDate).plusDays(days).toDate();
    }
    
    /**
     * 返回总天数
     * @param years
     * @param months
     * @param days
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    public static int getTotalDays(int years, int months, int days){
        
        return YEAR_PER_DAYS * years + MONTH_PER_DAYS * months + days;
    }

}

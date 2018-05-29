package com.gomemyc.wallet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
	
	
    public static final String DFS_MMdd = "MMdd";

    public static final String DFS_yyyyMMdd = "yyyyMMdd";

    public static final String DFS_yyMMdd = "yyMMdd";

    public static final String DF_MMdd = "MM-dd";

    public static final String DF_HHmm = "HH:mm";

    public static final String DF_MMddHH = "MM-dd HH";

    public static final String DF_yyyyMM = "yyyy-MM";
    public static final String DF_MM = "MM";

    public static final String DF_yyyyMMdd = "yyyy-MM-dd";

    public static final String DF_yyyyMMddHH = "yyyy-MM-dd HH";

    public static final String DF_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";

    public static final String DF_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    public static final String DF_yyyyMMddHHmmssS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DF_yyyyMMddHHmmssFile = "yyyyMMddHHmmss";

    public static final String DF_HHmmss="HH:mm:ss";
    
    private static String[] availableDF = { DFS_MMdd, DFS_yyyyMMdd, DFS_yyMMdd,
            DF_MMdd, DF_MMddHH, DF_HHmm, DF_yyyyMMdd, DF_yyyyMM, DF_yyyyMMddHH,
            DF_yyyyMMddHHmm, DF_yyyyMMddHHmmss, DF_yyyyMMddHHmmssS ,DF_HHmmss};

    public static final Date FREE_PACKAGE_CHECK_TIME = DateUtil.setFullDate(2100,0,1);

    private DateUtil() {
    }

    public static Date strToDate(String strDate) throws ParseException {
        if (strDate==null||strDate.length()==0) {
            return null;
        }
        int _L = strDate.trim().length();
        String format = "";
        switch (_L) {
            case 4:
                format = DFS_MMdd;
                break;
            case 5:
                if (strDate.indexOf("-") != -1) {
                    format = DF_MMdd;
                } else if (strDate.indexOf(":") != -1) {
                    format = DF_HHmm;
                }
                break;
            case 6:
                format = DFS_yyMMdd;
                break;
            case 7:
                format = DF_yyyyMM;
                break;
            case 8:
                if (strDate.indexOf("-") != -1) {
                    format = DF_MMddHH;
                } else {
                    format = DFS_yyyyMMdd;
                }
                break;
            case 10:
                format = DF_yyyyMMdd;
                break;
            case 13:
                format = DF_yyyyMMddHH;
                break;
            case 16:
                format = DF_yyyyMMddHHmm;
                break;
            case 19:
                format = DF_yyyyMMddHHmmss;
                break;
            case 21:
                format = DF_yyyyMMddHHmmssS;
                break;
            default:
                throw new ParseException("", 0);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.parse(strDate);
    }
    public static String dateToStr(Date date, String format) {
        if (date == null) {
            return "";
        }
        for (int i = 0; i < availableDF.length; i++) {
            if (availableDF[i].equals(format)) {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.format(date);
            }
        }
        return "";
    }

    public static Date getDayStart(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDayEnd(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, calendar
                .getActualMaximum(Calendar.MILLISECOND)-1);
        return calendar.getTime();
    }

    public static Date getMonthStart(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMonthEnd(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, calendar
                .getActualMaximum(Calendar.MILLISECOND)-1);
        return calendar.getTime();
    }

    public static Date getHourStart(Date date)
    {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMinuteStart(Date date)
    {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDateMOPMonth(Date date,int num)
    {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        return calendar.getTime();
    }

    public static Date getDateMOPHour(Date date,int num)
    {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, num);
        return calendar.getTime();
    }

    public static Date getDateMOPMinute(Date date,int num)
    {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, num);
        return calendar.getTime();
    }


    public static Date addDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    public static String formatTime(long time) {
        long hour = time / 3600;
        long tempTime = time % 3600;
        long min = tempTime / 60;
        long sec = tempTime % 60;
        return hour + " H" + min + " M" + sec + " S";
    }

    public static Date strToDate(String strDate, String formatStr,
                                 String zoneStr) {
        Locale locale = new Locale(zoneStr);
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr, locale);
        Date strtodate = null;
        try {
            strtodate = formatter.parse(strDate);
        } catch (ParseException e) {

        }
        return strtodate;
    }

    public static String getLogTime() {
        return dateToStr(new Date(), DateUtil.DF_yyyyMMddHHmmssS);
    }

    /**
     * 计算两个时间点之间的相差的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getIntervalDaysByTwoDate(Date startDate, Date endDate) {
        int recoupDays = 0;
        if (startDate != null && endDate != null) {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(startDate);
            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
            calendarStart.set(Calendar.MINUTE, 0);
            calendarStart.set(Calendar.SECOND, 0);
            calendarStart.set(Calendar.MILLISECOND, 0);

            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(endDate);
            calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
            calendarEnd.set(Calendar.MINUTE, 0);
            calendarEnd.set(Calendar.SECOND, 0);
            calendarEnd.set(Calendar.MILLISECOND, 0);
            long time = calendarEnd.getTime().getTime() - calendarStart.getTime().getTime();
            recoupDays = (int) (time / (24 * 3600 * 1000));
        }
        return recoupDays;
    }

    public static Date getFileStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    public static Date getFileEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getFirstFileStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    public static Date getFirstFileEndTime(Date date,int peroid) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.add(Calendar.HOUR_OF_DAY, peroid);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }

    public static Date getNextFileStartTime(Date date,int peroid) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, peroid);
        return calendar.getTime();
    }
    public static Date getNextFileEndTime(Date date,int peroid) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR_OF_DAY, peroid);
        return calendar.getTime();
    }

    public static Date getTimeEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }


    public static Date getTimeStart(Date date, int period) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR, period);
        return calendar.getTime();
    }
    public static String getFileNameByTwoDate(Date startTime,Date endTime) {
        String fileStartName = DateUtil.dateToStr(startTime,DateUtil.DF_yyyyMMddHHmmss).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        String fileEndName = DateUtil.dateToStr(endTime,DateUtil.DF_yyyyMMddHHmmss).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        return fileStartName+"~"+fileEndName;
    }


    public static String getNowTime() {
        return dateToStr(new Date(), DateUtil.DF_yyyyMMddHHmmss);
    }

    public static String getDuration(long startTime,long endTime){
        long durTime = endTime - startTime;
        durTime = durTime/1000;
        int hour = (int)durTime/3600;
        int minTmp = (int)durTime%3600;
        int min = (int)minTmp/60;
        int sec = (int)minTmp%60;
        return hour+" H "+min+" M "+sec+" S";
    }

    public static String getStartTimeStr(Date date,String format){

        return dateToStr(getStartTime(date),format);
    }
    
    public static String dateToStr(Date date){
        String str = dateToStr(date, DF_yyyyMMdd);
        String[] t = str.split("-");
        str = t[0] + "Y" + t[1] + "M" + t[2] + "D";
        return str;
    }

    public static Date getStartTime(Date date){
    	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));  
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);  
    	calendar.set(Calendar.MINUTE, 0);  
    	calendar.set(Calendar.SECOND, 0);  
    	calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String getEndTimeStr(Date date,String format){
        return dateToStr(getTodayEndDate(date), format);
    }
    
    /**
	 * 获取今天23:59:59时间
	 * @return
	 */
	public static Date getTodayEndDate(Date date){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));  
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, 23);  
    	calendar.set(Calendar.MINUTE, 59);  
    	calendar.set(Calendar.SECOND, 59);  
    	calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
	}
    
    public static Date getEndTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.SECOND, 1);

        return calendar.getTime();
    }
    public static Date getEndTimeOfPeriod(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.SECOND, -1);

        return calendar.getTime();
    }

    public static boolean  is1970(Date date){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 1970);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);


        int year2 = calendar2.get(Calendar.YEAR);

        return year2 == 1970;

    }

    /**
     * 根据一个日期，获取一周的第一天的时间
     *
     * @param date
     * @return
     */
    public static Date getWeekStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取上个月的第一天的时间
     */
    public static Date getLastMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取date所在季度的第一天
     */
    public static Date getThisSeasonStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = getQuarterInMonth(calendar.get(Calendar.MONTH), true);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    // isQuarterStart true返回几月
    // 季度一年四季， 第一季度：2月-4月， 第二季度：5月-7月， 第三季度：8月-10月， 第四季度：11月-1月
    private static int getQuarterInMonth(int month, boolean isQuarterStart) {
        int months[] = {1, 4, 7, 10};
        if (!isQuarterStart) {
            months = new int[]{3, 6, 9, 12};
        }
        if (month >= 2 && month <= 4)
            return months[0];
        else if (month >= 5 && month <= 7)
            return months[1];
        else if (month >= 8 && month <= 10)
            return months[2];
        else
            return months[3];
    }
    
    /**
     * 设置时间的时分秒毫秒
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    public synchronized static Date setDateNumber(Date date,int hour,int minute,int second,int millisecond){
  	  	Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND,millisecond);
        return calendar.getTime();
    }
    
    /**
     * 对日期添加月数
     * @param date
     * @param addNumber
     * @return
     */
    public synchronized static Date addDateMonth(Date date,int addMonth){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+addMonth);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    /**
     * 对日期添加天数
     * @param date
     * @param addNumber
     * @return
     */
    public synchronized static Date addDateDay(Date date,int addDay){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+addDay);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    public synchronized static Date setFullDate(int year,int month,int day){
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR, year);
    	calendar.set(Calendar.MONTH, month);
    	calendar.set(Calendar.DAY_OF_MONTH, day);
    	calendar.add(Calendar.HOUR, 0);
    	calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
    	return calendar.getTime();
    }
    
    
    /**
     * 时间相减得到天数
     * @param beginDate
     * @param endDate
     * @return long
     *  
     */
    public synchronized static long getDaySub(Date beginDate,Date endDate)
    {
    	GregorianCalendar startTime = new GregorianCalendar();
    	startTime.setTime(beginDate);
    	startTime.set(GregorianCalendar.HOUR, 0);
    	startTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	startTime.set(GregorianCalendar.MINUTE, 0);
    	startTime.set(GregorianCalendar.SECOND, 0);
    	startTime.set(GregorianCalendar.MILLISECOND, 0);
    	
    	GregorianCalendar endTime = new GregorianCalendar();
    	endTime.setTime(endDate);
    	endTime.set(GregorianCalendar.HOUR, 0);
    	endTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	endTime.set(GregorianCalendar.MINUTE, 0);
    	endTime.set(GregorianCalendar.SECOND, 0);
    	endTime.set(GregorianCalendar.MILLISECOND, 0);
    	
    	long diff = endTime.getTime().getTime()-startTime.getTime().getTime(); //相减的毫秒数
    	
        long day=diff/(24*60*60*1000);
        return day;
    }
    
    
    /**
     * 获取一个时间数 
     * @param date
     * @return
     */
    public synchronized static long getDayTime(Date date){
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(date);
    	calendar.set(GregorianCalendar.HOUR, 0);
    	calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	calendar.set(GregorianCalendar.MINUTE, 0);
    	calendar.set(GregorianCalendar.SECOND, 0);
    	calendar.set(GregorianCalendar.MILLISECOND, 0);
    	return calendar.getTime().getTime();
    }


    /**
     * 计算两天之间的小时数
     *
     * @param formDate
     * @param toDate
     * @return
     */
    public static int getIntervalHoursByTwoDate(Date formDate, Date toDate) {
        int days = getIntervalDaysByTwoDate(formDate, toDate);
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(formDate);
        int hourStart = calStart.get(Calendar.HOUR_OF_DAY);
        calStart.setTime(toDate);
        int hourEnd = calStart.get(Calendar.HOUR_OF_DAY);
        int length = hourEnd - hourStart;
        return days * 24 + length;
    }

    /**
	 * 获取今天0点时间
	 * @return
	 */
	public static Date getTodayStartDate(){
		Calendar todayStart = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));  
        todayStart.set(Calendar.HOUR_OF_DAY, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
	}
	
	/**
     * 获取今天的时间 
     * 小时 分钟 秒 为0
     * @param date
     * @return
     */
    public static Date getTodayDate(Date date) {
    	GregorianCalendar startTime = new GregorianCalendar();
    	startTime.setTime(date);
    	startTime.set(GregorianCalendar.HOUR, 0);
    	startTime.set(GregorianCalendar.HOUR_OF_DAY, 0);
    	startTime.set(GregorianCalendar.MINUTE, 0);
    	startTime.set(GregorianCalendar.SECOND, 0);
    	startTime.set(GregorianCalendar.MILLISECOND, 0);
    	return startTime.getTime();
    }
    

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public static Long getIntervalSecondsBetweenTwoDay(Date startDate, Date endDate) {
        long a = startDate.getTime();
        long b = endDate.getTime();
        return ((b - a) / 1000);
    }

    /**
     * 当前季度的开始时间
     *
     * @return
     */
    public static Date getCurrentQuarterStartTime(Date currentDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 6);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            now = c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime(Date currentDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            now = c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    
    //获取当天开始时间
    public static Date getDayStartTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    
  //获取当天结束时间
    public static Date getDayEndTime(Date date){
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 计算某个小时的开始时间
     * @param date
     * @return
     */
    public static Date getHourStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }
    
    /**
     * 将毫秒转成date
     * @param time
     * @return
     */
    public static Date convertTimeToDate(long time){
    	Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(time);
    	return c.getTime();
    }
    
    
    /**
     * 获取一个日期的天
     * @param date
     * @return
     */
    public static int getDateOfDay(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 获取一个日期的月
     * @param date
     * @return
     */
    public static int getMonthOfYear(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	return calendar.get(Calendar.MONTH)+1;
    }
    
    public static int getYear(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	return calendar.get(Calendar.YEAR);
    }
    
    public static int getQuarter(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	int month =calendar.get(Calendar.MONTH)+1;
    	if(month>=1&&month<=3) {
    		return 1;
    	} else if(month>=4&&month<=6) {
    		return 2;
    	} else if(month>=7&&month<=9) {
    		return 3;
    	} else {
    		return 4;
    	}
    }
    public static void main(String[] args) {
		Date d=addDateMonth(new Date(), 4);
		System.out.println(getQuarter(d));
	}
    
}

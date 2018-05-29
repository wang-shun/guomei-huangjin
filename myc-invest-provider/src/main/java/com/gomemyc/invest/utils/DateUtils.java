package com.gomemyc.invest.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;

import static com.gomemyc.invest.constant.TimeConstant.DAYS_PER_MONTH;
import static com.gomemyc.invest.constant.TimeConstant.DAYS_PER_YEAR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 *
 * @author sobranie
 */
public class DateUtils {

    static org.slf4j.Logger log = LoggerFactory.getLogger(DateUtils.class);

    private static final GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();

    public static final Date FIRST_DATE = new Date(0);

    public static final int SECONDS_OF_ONE_HOUR = 1 * 60 * 60;

    public static final int SECONDS_OF_ONE_DAY = SECONDS_OF_ONE_HOUR * 24;
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String HHmmSS = "HHmmSS";
    public static final String dateTimeFormatter = "yyyy年MM月dd日 HH:mm:ss";
    


    /**
     * list all dates between start date and end date, both day included
     *
     * @param start
     * @param end
     * @return
     */
    public static List<Date> listDates(Date start, Date end) {
        List<Date> dates = new ArrayList<>();

        Date date = start;
        calendar.setTime(start);
        while (date.before(end)) {
            dates.add(date);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            date = calendar.getTime();
        }
        if (date.equals(end)) {
            dates.add(end);
        }

        return dates;
    }

    /**
     * count days between start date and end date, both day included
     *
     * @param start
     * @param end
     * @return
     */
    public static int countDays(Date start, Date end) {
        int result = 0;
        Date date = start;
        calendar.setTime(start);
        while (date.before(end)) {
            result++;
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            date = calendar.getTime();
        }
        if (getYear(date).equals(getYear(end))
                && getMonth(date).equals(getMonth(end))
                && getDay(date).equals(getDay(end))) {
            result++;
        }
        return result;
    }
    /**
     * count hours between start date and end date
     *
     * @param start
     * @param end
     * @return
     */
    public static int countHours(Date start, Date end) {
    	long beginTime = start.getTime(); 
    	long endTime = end.getTime(); 
    	long endTime_beginTime=endTime-beginTime;
    	if(endTime_beginTime<0)
    		 return 0;
        return (int) (endTime_beginTime/(1000 * 60 * 60));
    }
    
    //计算两个日期相减
	public static Long twoDateSubtract(Date start, Date end) {
		long intervalMilli = start.getTime() - end.getTime();
		return intervalMilli / (24 * 60 * 60 * 1000);
	}

    /**
     * return the 0'clock time for a date, like 2013/8/1 0:0:0
     *
     * @param date
     * @return
     */
    public static Date get0OClock(Date date) {
        if (date == null) {
            return null;
        }

        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }



    /**
     * 获取某天的最后一秒对应的时间:当天的23:59.000
     *
     * @param day
     * @return
     */
    public static Date dayEnd(Date day) {
        if (day == null) {
            return null;
        }
        long theDay = dayStart(day).getTime();
        final long ONE_DAY_MILLIS = 1 * 24 * 60 * 60 * 1000;

        return new Date(theDay + ONE_DAY_MILLIS - 1 * 1000);
    }

    /**
     * 获取某天的第一秒对应的时间:当天的00:00.000
     *
     * @param date
     * @return
     */
    public static Date dayStart(Date date) {
        if (date == null) {
            return null;
        }

        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public static Date monthStart(Date date) {
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year, month, 1, 0, 0, 0);
        return calendar.getTime();
    }

    public static Date monthEnd(Date date) {
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year, month, calendar.getActualMaximum(Calendar.DATE), 23, 59, 59);
        return calendar.getTime();
    }

    /**
     * 从当前时间增加秒 seconds 得到另外一个时间
     *
     * @param day
     * @param seconds 可以为负数
     * @return
     */
    public static Date offsetSeconds(Date day, int seconds) {
    	if(day==null){
    		day=new Date();
    	}
        long nowMillis = dayStart(day).getTime();
        final long OFFSET_MILLIS = seconds * 1000L;
        return new Date(nowMillis + OFFSET_MILLIS);
    }
    /**
     * 从当前时间增加秒 seconds 得到另外一个时间
     *
     * //@param day
     * @param seconds 可以为负数
     * @author TianBin
     * @return
     */
    public static Date offsetSecondsByCurrent(Date date, int seconds) {
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        calendar.add (Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * 从当前时间增加天数 days 得到另外一个时间
     *
     * @param day
     * @param days 可以为负数
     * @return
     */
    public static Date offsetDays(Date day, int days) {
        return offsetSeconds(day, days * SECONDS_OF_ONE_DAY);
    }
    //当前日期加一个整数获取一个新的一天日期（时分秒和原时间保持一致）
    public static Date offsetDaysByInt(Date day, int days) {
    	  long nowMillis = day.getTime();
          final long OFFSET_MILLIS = days * SECONDS_OF_ONE_DAY * 1000L;
          return new Date(nowMillis + OFFSET_MILLIS);
    }

    public static Date offsetHours(Date day, int hours) {
        return offsetSeconds(day, hours * SECONDS_OF_ONE_HOUR);
    }

    public static Date yesterday(Date day) {
        return offsetSeconds(day, -SECONDS_OF_ONE_DAY);
    }

    public static Date tomorrow(Date day) {
        return offsetSeconds(day, SECONDS_OF_ONE_DAY);
    }

    /**
     * 时间格式化为字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String toString(Date date, String format) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat(format);
            String ctime = formatter.format(date);

            return ctime;
        } catch (Exception e) {
            return "";
        }
    }
    
	   /**
     * 凌晨
     * @param date
     * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
     *       1 返回yyyy-MM-dd 23:59:59日期
     * @return
     */
    public static Date weeHours(Date date, int flag) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //时分秒（毫秒数）
        long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
        //凌晨00:00:00
        cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);
         
        if (flag == 0) {
            return cal.getTime();
        } else if (flag == 1) {
            //凌晨23:59:59
            cal.setTimeInMillis(cal.getTimeInMillis()+23*60*60*1000 + 59*60*1000 + 0*1000);
        }
        return cal.getTime();
    }

    public static String yyyymmdd(Date date) {
        return toString(date, "yyyyMMdd");
    }

    public static String hhmmss(Date date) {
        return toString(date, "HHmmss");
    }

    public static String getYear(Date date){
        return yyyymmdd(date).substring(0, 4);
    }

    public static String getMonth(Date date){
        return yyyymmdd(date).substring(4, 6);
    }

    public static String getDay(Date date){
        return yyyymmdd(date).substring(6, 8);
    }

    /**
     * thus是否处于start和end之间
     * @param start
     * @param end
     * @param thus
     * @return
     */
    public static boolean isBetween(Date start, Date end, Date thus) {
        if (start == null || end == null) {
            return false;
        }
        return start.before(thus) && end.after(thus);
    }
    
    /**
     * time转字符串
     * @param time
     * @return
     * @author weiyinbin
     * @date 2016年6月23日
     */
    public static String timeToString(long time){
        if (time < 0) {
        	log.warn("Illegal time.[time={}]", time);
            return "";
        }
        int seconds, minuts = 0, hours = 0, days = 0;
        StringBuilder sb = new StringBuilder();
        
        time = time - time % 1000;
        seconds = (int) (time / 1000);
        if (seconds > 60) {
            minuts = (seconds - seconds % 60) / 60;
            seconds = seconds % 60;
            if (minuts > 60) {
                hours = (minuts - minuts % 60) / 60;
                minuts = minuts % 60;
                if (hours > 24) {
                    days = (hours - hours % 24) / 24;
                    hours = hours % 24;
                }
            }
        }
        
        if (days > 0) {
            sb.append(days).append("天 ");
        }
        if (hours > 0) {
            sb.append(hours).append("小时 ");
        }
        if (minuts > 0) {
            sb.append(minuts).append("分 ");
        }
        if (seconds > 0) {
            sb.append(seconds).append("秒");
        }
        
        return sb.toString();
    }
    
    /**
     * 根据有多少年、月、日算出具体天数
     * @param years
     * @param months
     * @param days
     * @return
     */
    public static int getTotalDays(Integer years,Integer months,Integer days){
    	if(years==null){
    		years=0;
    	}
    	if(months==null){
    		months=0;
    	}
    	if(days==null){
    		days=0;
    	}
    	return years*DAYS_PER_YEAR+months*DAYS_PER_MONTH+days;
    }
    /**
     * 根据天数算出有多少年、月、日
     * @param totalDays
     * @return map<"y",y>,map<"m",m>,map<"d",d>
     */
    public static Map<String,Integer> getYearMonthsDays(int totalDays){
    	int y = totalDays / DAYS_PER_YEAR;
        int m = (totalDays % DAYS_PER_YEAR) / DAYS_PER_MONTH;
        int d = totalDays - y * DAYS_PER_YEAR - m * DAYS_PER_MONTH;
        Map<String,Integer> map=new HashMap<String,Integer>();
        map.put("y", y);
        map.put("m", m);
        map.put("d", d);
    	return map;
    }
    
    public static String format(Date d, String format) {
		if (d == null)
			return "";
		SimpleDateFormat myFormatter = new SimpleDateFormat(format);
		return myFormatter.format(d);
	}
    
    public static String getStartTime(Date date){  
        Calendar todayStart =  Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        return format(todayStart.getTime(), "yyyy-MM-dd HH:mm:ss");
    } 
    public static String getEndTime(Date date){  
        Calendar todayEnd = Calendar.getInstance();  
        todayEnd.setTime(date);
        todayEnd.set(Calendar.HOUR, 23);  
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        return format(todayEnd.getTime(), "yyyy-MM-dd HH:mm:ss");  
    }  
    
    public static void main(String args[]){
    	SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
    	try {
    		System.out.println(getStartTime(myFormatter.parse("2010-05-27")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}

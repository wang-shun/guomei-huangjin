package com.gomemyc.invest.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class JodaDateUtils {

    private static final Logger logger = LoggerFactory.getLogger(JodaDateUtils.class);
    public static final DateTimeFormatter shortDateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
    public static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter longDateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
    public static final DateTimeFormatter timeFormatter2 = DateTimeFormat.forPattern("HHmmss");
    public static final DateTimeFormatter dateYear = DateTimeFormat.forPattern("yyyy");
    public static final DateTimeFormatter dateMonth = DateTimeFormat.forPattern("MM");
    public static final DateTimeFormatter dateDay = DateTimeFormat.forPattern("dd");

    public static final long ONE_DAY = 24L * 60 * 60 * 1000;
    public static final long ONE_HOUR = 60L * 60 * 1000;
    public static final long ONE_MINUTE = 60L * 1000;



    public static String formatshortDate(Date date) {
        return formatDate(date, shortDateFormatter);
    }
    public static String formatTime(Date date) {
        return formatDate(date, timeFormatter2);
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(formatDate(date,dateYear));
        System.out.println(formatDate(date,dateMonth));
        System.out.println(formatDate(date,dateDay));
        /*System.out.println(formatshortDate(date));
        System.out.println(formatTime(date));*/
//        System.out.println((32 & 32) == 32);
    }


    public static String formatDate(Date date, DateTimeFormatter formatter) {
        if(null == date) return "";
        DateTime datetime = new DateTime(date.getTime());
        return formatDate(datetime, formatter);
    }


    public static String formatDate(DateTime datetime, DateTimeFormatter dateFormatter){
        return dateFormatter.print(datetime);
    }


}

package com.gomemyc.invest.utils;
import java.util.Date;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;
import static com.gomemyc.invest.constant.TimeConstant.DAYS_PER_MONTH;
import static com.gomemyc.invest.constant.TimeConstant.DAYS_PER_YEAR;
import static com.gomemyc.invest.constant.TimeConstant.MONTHS_PER_YEAR;

/**
 * Object represent a certain duration including years/months and days
 *
 * @author sobranie
 */
public class Duration  implements Comparable<Duration> {

    private static final String YEAR = "年";
    
    private static final String MONTH = "个月";
    
    private static final String DAY = "天";
    
    private static final String AND = "零";
    
    private int years;

    private int months;

    /**
     * 票据类产品没有年、月，只有天
     * @author weiyinbin
     */
    private int days;

    public Duration() {
    }

    public Duration(final int years,
                    final int months,
                    final int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    /**
     * @deprecated use the other constructor
     * @param duration
     */
    public Duration(final int duration) {
        years = duration / DAYS_PER_YEAR;
        months = (duration % DAYS_PER_YEAR) / DAYS_PER_MONTH;
        days = duration - years * DAYS_PER_YEAR - months * DAYS_PER_MONTH;
    }
    
    /**
     * 票据类产品，以天为单位，没有年、月
     * @param days		天	
     * @param onlyDay	仅保留天,无意义,可传任意布尔值
     * @author weiyinbin
     */
    public Duration(final int days,Boolean onlyDay) {
        this.days = days;
    }
    
    /**
     * @根据天数生成duration
     * @param duration
     * @return 
     */
    public static Duration getDurationByDays(final int duration) {
        int y = duration / DAYS_PER_YEAR;
        int m = (duration % DAYS_PER_YEAR) / DAYS_PER_MONTH;
        int d = duration - y * DAYS_PER_YEAR - m * DAYS_PER_MONTH;
        return   new Duration(y, m, d);
    }

    public static Duration fromMonths(int months) {
        final int y = months / 12;
        final int m = months % 12;
        final int d = 0;
        return new Duration(y, m, d);
    }

    public static Duration fromYears(int years) {
        final int y = years;
        final int m = 0;
        final int d = 0;
        return new Duration(y, m, d);
    }

    /**
     * 活期产品专用 Duration (0,0,1)
     *
     * @return
     */
    public static Duration CURRENT() {
        Duration d = new Duration(0, 0, 1);
        return d;
    }

    public int getYears() {
        return years;
    }

    public int getMonths() {
        return months;
    }

    public int getDays() {
        return days;
    }

    public int getTotalMonths() {
        return years * MONTHS_PER_YEAR + months;
    }

    /**
     *  The return value is WRONG ! ONLY FOR VALIDATION
     * @return
     */
    public int getTotalDays() {
        return getTotalMonths() * DAYS_PER_MONTH + days;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(years).append(months).append(days).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Duration other = (Duration) obj;
        if (this.years != other.years) {
            return false;
        }
        if (this.months != other.months) {
            return false;
        }
        if (this.days != other.days) {
            return false;
        }       
        return true;
    }

    @Override
    public int compareTo(Duration o) {
        if (!(years == o.years)) {
            return years > o.years ? 1 : -1;
        }
        if (!(months == o.months)) {
            return months > o.months ? 1 : -1;
        }
        if (!(days == o.days)) {
            return days > o.days ? 1 : -1;
        }
        return 0;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setDays(int days) {
        this.days = days;
    }

    /**
     * return copy of Duration minus specified number of months
     *
     * @param months
     * @return
     */
    public Duration minusMonths(int months) {
        if (months < 0) {
            return null;
        }
        int totalMonths = getTotalMonths() - months;
        if (totalMonths < 0) {
            return null;
        }
        int years_ = totalMonths / 12;
        int months_ = totalMonths - 12 * years_;
        int days_ = days;
        return new Duration(years_, months_, days_);
    }

    /**
     * return copy of Duration plus specified number of months
     *
     * @param months
     * @return
     */
    public Duration plusMonths(int months) {
        if (months < 0) {
            return null;
        }
        int totalMonths = getTotalMonths() + months;
        int years_ = totalMonths / 12;
        int months_ = totalMonths - 12 * years_;
        int days_ = days;
        return new Duration(years_, months_, days_);
    }

    /**
     * 根据起止日期生成Duration，包含起始止日，不包括到期日
     *
     * <p>
     * 每个月天数按照自然月来计算</p>
     *
     * @param start
     * @param end
     * @return
     */
    public static Duration getDuration(Date start, Date end) {
        //异常参数
        if (start.after(end)) {
            return null;
        }
        //计算
        int totalMonths = -1;
        Date temp = start;
        do {
            temp = DateUtils.addMonths(temp, 1);
            totalMonths++;
        } while (!temp.after(end));
        temp = DateUtils.addMonths(temp, -1);
        //生成结果
        int years_ = totalMonths / 12;
        int months_ = totalMonths - 12 * years_;
        int days_ = 0;
        while (temp.before(end)) {
            temp = DateUtils.addDays(temp, 1);
            days_++;
        }
        return new Duration(years_, months_, days_);
    }
    
        @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE, false);
    }
    
    // 用于导出文件是显示期限
    public String getShowDuration(){
        StringBuilder sb = new StringBuilder();
        if (years != 0) {
            sb.append(years).append(YEAR);
            if (months != 0 || days != 0) {
                sb.append(AND);
            }
        }
        if (months != 0) {
            sb.append(months).append(MONTH);
            if (days != 0) {
                sb.append(AND);
            }
        }
        if (days != 0) {
            sb.append(days).append(DAY);
        }
        
        return sb.toString();
    }
}

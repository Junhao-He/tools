package sam.znv.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期时间工具
 * @author 章云
 * @date 2019/7/27 14:58
 */
public class DateUtils {

    private DateUtils() {}

    public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DATE = "yyyyMMdd";
    public static final String DATE_FORMAT_TIME = "HH:mm:ss";

    /**
     * 获得当前日期时间
     * @return
     */
    public static String getCurrentDateTime() {
        return getDay(DATE_FORMAT_DATETIME, 0);
    }

    /**
     * 获得当前日期时间
     * @param format 自定义时间格式
     * @return
     */
    public static String getCurrentDateTime(String format) {
        return getDay(format, 0);
    }

    /**
     * 获取自定义时间（按日加减）
     * @param day
     * @return
     */
    public static String getDay(int day) {
        return getDay(DATE_FORMAT_DATE, day);
    }

    /**
     * 获得时间，格式自定义（日）
     * @param format 自定义时间格式
     * @param day
     * @return
     */
    public static String getDay(String format, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return sdf.format(calendar.getTime());
    }

    /**
     * 相对时间加减
     * @param fromFormat 相对时间格式
     * @param fromDate   相对时间
     * @param toFormat   相对时间加减后的格式
     * @param day
     * @return
     */
    public static String getDay(String fromFormat, String fromDate, String toFormat, int day) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
        Date date = getDateOfString(fromDate, fromFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获得时间，格式自定义（月）
     * @param format
     * @return
     */
    public static String getMonth(String format, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获得时间，格式自定义（年）
     * @param format
     * @return
     */
    public static String getYear(String format, int year) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, year);
        return sdf.format(calendar.getTime());
    }

    /**
     * 计算时间差
     * @param datetime1
     * @param datetime2
     * @return
     * @throws ParseException
     */
    public static long diffSeconds(String datetime1, String datetime2) throws ParseException {
        long diffSeconds;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DATETIME);
        Date date1 = sdf.parse(datetime1);
        Date date2 = sdf.parse(datetime2);
        long dateLong1 = date1.getTime();
        long dateLong2 = date2.getTime();
        long diff = 0;
        diff = dateLong2 - dateLong1;
        diffSeconds = diff / 1000;
        return diffSeconds;
    }

    /**
     * 获取每个月的第一天时间
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1, 0, 0, 0);
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DATETIME);
        return sdf.format(c.getTime());
    }

    /**
     * 获取每个月的最后一天时间
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1, 23, 59, 59);
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, c.getMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DATETIME);
        return sdf.format(c.getTime());
    }

    /**
     * 字符串时间格式相互转换
     * @param fromFormat
     * @param dateStr
     * @param toFormat
     * @return
     */
    public static String getStringOfString(String fromFormat, String dateStr, String toFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
        SimpleDateFormat sdf2 = new SimpleDateFormat(fromFormat);
        Date date = sdf2.parse(dateStr);
        return sdf.format(date);
    }

    /**
     * Date转成String
     * @param date
     * @param format
     * @return
     */
    public static String getStringOfDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * long转成String
     * @param time
     * @param format
     * @return
     */
    public static String getStringOfLong(long time, String format) {
        Date date = getDateOfLong(time);
        return getStringOfDate(date, format);
    }

    /**
     * String转成Date
     * @param date
     * @param format
     * @return
     */
    public static Date getDateOfString(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * long转成Date
     * @param time
     * @return
     */
    public static Date getDateOfLong(long time) {
        return new Date(time);
    }

    /**
     * Date转成long
     * @param date
     * @return
     */
    public static long getLongOfDate(Date date) {
        return date.getTime();
    }

    /**
     * String转成long
     * @param dateStr
     * @param format
     * @return
     */
    public static long getLongOfString(String dateStr, String format) throws ParseException {
        Date date = getDateOfString(dateStr, format);
        return date.getTime();
    }

    /**
     * 转换指定时区的时间
     * @param date
     * @param formate
     * @param timeZoneId
     * @return
     */
    public static String getTimeZoneDate(Date date, String formate, String timeZoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return sdf.format(date);
    }

    public static Date getTimeZoneDate(String date, String formate, String timeZoneId) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return sdf.parse(date);
    }

}

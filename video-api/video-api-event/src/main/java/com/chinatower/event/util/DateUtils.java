package com.chinatower.event.util;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @Package : com.chinatower.product.util
 * @FileName : DateUtil
 * @Description : 时间转换工具
 * @author : yxb
 * @version : V1.0
 * @UpateLog: Name Date Reason/Contents --------------------------------------- *** **** ****
 */
public class DateUtils {

    /** 日期：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_TIME_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间（毫秒）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_TIME_MS_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间（毫秒）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_TIME_CHINA_MS_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间（时分秒）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_HMS_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间（年月）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_YM_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间格式化 **/
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_TIME_TWO_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /** 日期时间格式化 **/
    private static final String DATE_TIME_HMS_FORMAT = "HH:mm:ss";

    /** 日期时间格式化 **/
    private static final String DATE_TIME_YM_FORMAT = "yyyyMM";

    /** 日期时间格式化 **/
    private static final String DATE_TIME_FORMAT_YMD = "yyyy-MM-dd";

    /** 日期时间格式化-到毫秒 **/
    private static final String DATE_TIME_FORMAT_MS = "yyyy-MM-dd HH:mm:ss SSS";

    private static final long HOUR = 60 * 60 * 1000;

    private static final long MIN = 60 * 1000;

    private static final long SEC = 1000;

    private static final long DAY = 24 * 60 * 60 * 1000;

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();


    /**
     * @Method: dateToString
     * @Description: (日期转换为字符串：精确到毫秒)
     * @param date 日期
     * @return String (转换好的字符串日期)
     * @author : yxb
     */
    public static String dateToString(Date date) {
        SimpleDateFormat sdf = DATE_TIME_MS_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_MS);
            DATE_TIME_MS_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * @Method: currentDateToString
     * @Description: 当前时间转为 "yyyy-MM-dd HH:mm:ss"格式时间
     * @param
     * @return java.lang.String (返回类型描述)
     * @throws @author : yxb
     */
    public static String currentDateToString() {
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        return sdf.format(new Date());
    }

    /**
     * @Method : dateLaterString
     * @Description : 获取当前时间点前面几小时的时间
     * @param date : 日期
     * @param hour : 小时
     * @return : java.util.Date 时间
     * @author : yxb
     */
    public static Date dateLaterString(String date, int hour) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(DATE_TIME_FORMAT).parse(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }

    /**
     * @Method: getDateAfter
     * @Description: (得到几天后的时间)
     * @param d 日期
     * @param day 天数
     * @return java.util.Date (返回类型描述)
     * @throws @author : yxb
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * @Method : dateToStringNoMS
     * @Description : 日期转化为字符串，没有毫秒
     * @param date : 日期
     * @return : java.lang.String 字符串
     * @author : yxb
     */
    public static String dateToStringNoMS(Date date) {
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * @Method : dateToStringYMD
     * @Description :日期转化为字符串，只有年月日
     * @Description :日期转化为字符串，只有年月日
     * @param date :日期
     * @return : java.lang.String
     * @author : yxb
     */
    public static String dateToStringYMD(Date date) {
        SimpleDateFormat sdf = DATE_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_YMD);
            DATE_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * @Method: str2DateTime
     * @Description: 将日期格式化为字符串
     * @param date 日期对象
     * @param pattern 格式模板
     * @return java.util.Date
     * @throws @author : yxb
     */
    public static String date2Str(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        if (null == pattern) {
            pattern = DATE_TIME_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(pattern);
        return format.format(date);
    }

    /**
     * @Method : stringToDateYMDHMS
     * @Description : 根据字符串获取当前日期-年月日时分秒
     * @param dateStr :
     * @return : java.util.Date
     * @author : yxb
     */
    public static Date stringToDateYMDHMS(String dateStr) {
        Date date = null;
        SimpleDateFormat sdf = DATE_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_YMD);
            DATE_LOCAL.set(sdf);
        }
        try {
            date = sdf.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 根据字符串获取当前日期-年月日时分秒
     * @param dateStr 年月 格式：yyyyMM
     * @return 日期
     */
    public static Date stringToDateYYMMdd(String dateStr) {
        Date date = null;
        SimpleDateFormat sdf = DATE_YM_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_YM_FORMAT);
            DATE_YM_LOCAL.set(sdf);
        }
        try {
            date = sdf.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * @Method: sameMouth
     * @Description: 判断两个月是否在一个月内
     * @param date1 日期1
     * @param date2 日期2
     * @return boolean (返回类型描述)
     */
    public static boolean sameMouth(Date date1, Date date2) {
        Calendar now1 = Calendar.getInstance();
        Calendar now2 = Calendar.getInstance();
        now1.setTime(date1);
        now2.setTime(date2);
        int month1 = now1.get(Calendar.MONTH) + 1;
        int year1 = now1.get(Calendar.YEAR);
        int month2 = now2.get(Calendar.MONTH) + 1;
        int year2 = now2.get(Calendar.YEAR);
        return month1 == month2 && year1 == year2;
    }

    /**
     * @Method: dateToStringYM
     * @Description: 日期转化为字符串，只有年月
     * @param date 日期转化为字符串，只有年月
     * @return String 日期
     * @author : yxb
     */
    public static String dateToStringYM(Date date) {
        SimpleDateFormat sdf = DATE_YM_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_YM_FORMAT);
            DATE_YM_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * @Method: getlastMonth
     * @Description: 获取当前月的的上个月
     * @param datetime 传入的时间
     * @return String 操作后的时间
     */
    public static String getlastMonth(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_YM_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, -1);
        date = cl.getTime();
        return sdf.format(date);
    }

    /**
     * @Method: getForeSixMonth
     * @Description: 获取当前月的前6个月（半年）
     * @param datetime 传入的时间
     * @return Date 操作后的时间
     */
    public static Date getForeSixMonth(Date datetime) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(datetime);
        cl.add(Calendar.MONTH, -6);
        datetime = cl.getTime();
        return datetime;
    }

    /**
     * @Method: getCurrentMonthFirstTime
     * @Description: 获取当月第一天的日期 返回年月日时分秒
     * @return Date 操作后的时间
     */
    public static Date getCurrentMonthFirstTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * @Method: getCurrentMonthEndTime
     * @Description: 获取当月最后一天的日期 返回年月日时分秒
     * @return Date 操作后的时间
     */
    public static Date getCurrentMonthEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * @Method : stringToDateYM
     * @Description : 根据传入的字符串获得时间-年月yyyyMM
     * @param dateStr : 日期字符串
     * @return : java.util.Date
     */
    public static Date stringToDateYM(String dateStr) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_YM_FORMAT);
        try {
            date = sdf.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取指定日期的前 n 天
     *
     * @param date 指定日期
     * @param days 天数
     * @return 前 n 天的日期
     */
    public static Date getDaysBefore(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置当前日期
        calendar.add(Calendar.DAY_OF_YEAR, -days); // 向前移动 n 天
        return calendar.getTime();
    }

    /**
     * @Method : getTimeDifference
     * @Description : 返回时间差，小于一小时展示分，小于一天到小时，大于一天显示天
     * @param startTime : 被减数
     * @param targetTime : 减数
     * @return : java.util.Date
     */
    public static String getTimeDifference(Date startTime, Date targetTime) {
        long difference = startTime.getTime() - targetTime.getTime();
        if (difference < DAY) {
            long hour = difference / (HOUR);
            long minute = (difference - hour * HOUR) / (MIN);
            long second = (difference - hour * HOUR - minute * MIN) / (SEC);
            return String.format("%d天 %02d:%02d:%02d", 0, hour, minute, second);
        }
        if (difference > DAY) {
            long day = difference / DAY;
            long hour = (difference - day * DAY) / HOUR;
            long minute = (difference - day * DAY - hour * HOUR) / (MIN);
            long second = (difference - day * DAY - hour * HOUR - minute * MIN) / (SEC);
            return String.format("%d天 %02d:%02d:%02d", day, hour, minute, second);
        }
        return "0天0小时0分0秒";
    }

    public static String getTimeDifference2(Date startTime, Date targetTime) {
        long difference = startTime.getTime() - targetTime.getTime();
        if (difference < DAY) {
            long hour = difference / (HOUR);
            long minute = (difference - hour * HOUR) / (MIN);
            long second = (difference - hour * HOUR - minute * MIN) / (SEC);
            return hour + "小时" + minute + "分" + second + "秒";
        }
        if (difference > DAY) {
            long day = difference / DAY;
            long hour = (difference - day * DAY) / HOUR;
            long minute = (difference - day * DAY - hour * HOUR) / (MIN);
            long second = (difference - day * DAY - hour * HOUR - minute * MIN) / (SEC);
            return day + "天" + hour + "小时" + minute + "分" + second + "秒";
        }
        return "0天0小时0分0秒";
    }

    /**
     * 功能描述: 判断是否超期
     *
     * @param:
     * @return:
     * @auther: yuexuebin
     * @date: 2021/11/3 10:49
     */
    public static int checkIfTimeOut(Integer timeLimit, Date flowNodeTime) {
        long nowDate = System.currentTimeMillis();
        long start = flowNodeTime.getTime();
        if (new BigDecimal(nowDate - start).compareTo(new BigDecimal(timeLimit).multiply(new BigDecimal(1000))) >0) {
            return 1;
        }
        return 0;
    }

    /**
     * 加减对应时间后的日期
     *
     * @param date 需要加减时间的日期
     * @param amount 加减的时间(毫秒)
     * @return 加减对应时间后的日期
     */
    public static Date subtractTime(Date date, int amount) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            String strTime = sdf.format(date.getTime() + amount);
            Date time = sdf.parse(strTime);
            return time;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加减对应分钟后的日期
     *
     * @param date 需要加减时间的日期
     * @param minutes 加减的时间(分钟)
     * @return 加减对应时间后的日期
     */
    public static Date subtractTimeMinutes(Date date, int minutes) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.SECOND, minutes);
            return cal.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Method : string2Date
     * @Description : 将yyyy-MM-dd HH:mm:ss格式字符串时间转为Date类型
     * @param time : 日期
     * @return : java.util.Date 时间
     * @author : hued
     */
    public static Date string2Date(String time) {
        Date date = null;
        try {
            if(time.contains("/")){
                date = new SimpleDateFormat(DATE_TIME_TWO_FORMAT).parse(time);
            }else{
                date = new SimpleDateFormat(DATE_TIME_FORMAT).parse(time);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 获取当前时间
     * @param timeLimit
     * @return
     */
    public static String getExceedTime(Date date,String timeLimit) {
        SimpleDateFormat sdf = DATE_TIME_MS_LOCAL.get();
        if(!StringUtils.isEmpty(timeLimit)){
            long second =date.getTime()/1000+Long.parseLong(timeLimit);
            return new SimpleDateFormat(DATE_TIME_FORMAT).format(second*1000);
        }
        return null;
    }

    /**
     * @Method: getNextMonth
     * @Description: 获取传入事件的下个月
     * @param datetime 传入的时间
     * @return String 操作后的时间
     */
    public static String getNextMonth(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_YM_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, 1);
        date = cl.getTime();
        return sdf.format(date);
    }

    /**
     * 返回经计算修改后的时间， 举例：如现在时间为2004-10-14 那么dateAdd("d",2,new Date())返回的时间为2004-10-16 2004-11-4 修正月份相减时(12月份、月份天数不一致时)问题.
     * <br>
     *
     * @param timeInterval 时间单位(年，月，日..)，取值分别为("y","m","d")
     * @param number 时间间隔值
     * @param sd 时间始点
     * @return Date 日期
     * @author: luo.zhenghong <br>
     * <br>
     */
    public static Date dateAdd(final String timeInterval, final int number, final Date sd) {

        Calendar now = Calendar.getInstance();
        if (timeInterval.toLowerCase().equals(Constants.DAY)) {
            now.setTime(sd);
            now.set(Calendar.DATE, now.get(Calendar.DATE) + number);
        }
        else if (timeInterval.toLowerCase().equals(Constants.HOUR)) {
            now.setTime(sd);
            now.add(Calendar.HOUR, number);
        }
        else if(timeInterval.toLowerCase().equals(Constants.MINUTE)){
            now.setTime(sd);
            now.add(Calendar.MINUTE,number);
        }
        else if (timeInterval.toLowerCase().equals(Constants.SECOND)) {
            now.setTime(sd);
            now.add(Calendar.SECOND, number);
        }
        else {
            return null;
        }
        return now.getTime();
    }

    public static boolean compareStrTime(String dateTimeStr1, String dateTimeStr2, String pattern){
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // 解析字符串为 LocalDateTime 对象
        LocalDateTime dateTime1 = LocalDateTime.parse(dateTimeStr1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(dateTimeStr2, formatter);

        if (dateTime1.isBefore(dateTime2)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取指定 Date 对应的当天 00:00:00 的 Date 对象
     * @param date 输入日期（可为 null，null 则取当前日期）
     * @return 当天 00:00:00 的 Date
     */
    public static Date getStartTimeOfDay(Date date) {
        LocalDate localDate = date == null
                ? LocalDate.now(DEFAULT_ZONE)  // 无输入则取当前日期
                : date.toInstant().atZone(DEFAULT_ZONE).toLocalDate();

        // 构造当天 00:00:00 的 LocalDateTime，转回 Date
        LocalDateTime startOfDay = localDate.atStartOfDay();
        return Date.from(startOfDay.atZone(DEFAULT_ZONE).toInstant());
    }

    /**
     * 获取指定 Date 对应的下一天 00:00:00 的 Date 对象
     * @param date 输入日期（可为 null，null 则取当前日期）
     * @return 下一天 00:00:00 的 Date
     */
    public static Date getStartTimeOfNextDay(Date date) {
        LocalDate localDate = date == null
                ? LocalDate.now(DEFAULT_ZONE)
                : date.toInstant().atZone(DEFAULT_ZONE).toLocalDate();

        // 构造次日 00:00:00 的 LocalDateTime，转回 Date
        LocalDateTime startOfNextDay = localDate.plusDays(1).atStartOfDay();
        return Date.from(startOfNextDay.atZone(DEFAULT_ZONE).toInstant());
    }
}

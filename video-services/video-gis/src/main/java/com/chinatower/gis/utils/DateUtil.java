package com.chinatower.gis.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Package : com.chinatower.product.util
 * @FileName : DateUtil
 * @Description : 时间转换工具
 * @author : yxb
 * @version : V1.0
 * @UpateLog:	Name	Date	Reason/Contents
 * 			---------------------------------------
 * 				***		****	****
 *
 */
public class DateUtil {

    /** 日期：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_LOCAL = new ThreadLocal<SimpleDateFormat>();
    /** 日期时间：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_TIME_LOCAL = new ThreadLocal<SimpleDateFormat>();
    /** 日期时间（毫秒）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_TIME_MS_LOCAL = new ThreadLocal<SimpleDateFormat>();
    /** 日期时间（时分秒）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_HMS_LOCAL = new ThreadLocal<SimpleDateFormat>();
    /** 日期时间（年月）：线程变量 **/
    private static ThreadLocal<SimpleDateFormat> DATE_YM_LOCAL = new ThreadLocal<SimpleDateFormat>();

    /** 日期时间格式化 **/
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 日期时间格式化 **/
    private static final String DATE_TIME_HMS_FORMAT = "HH:mm:ss";
    /** 日期时间格式化 **/
    private static final String DATE_TIME_YM_FORMAT = "yyyyMM";
    /** 日期时间格式化 **/
    private static final String DATE_TIME_FORMAT_YMD = "yyyy-MM-dd";

    /** 日期时间格式化-到毫秒 **/
    private static final String DATE_TIME_FORMAT_MS = "yyyy-MM-dd HH:mm:ss SSS";

    private static final long HOUR = 60*60*1000;
    private static final long MIN = 60*1000;
    private static final long SEC = 1000;
    private static final long DAY = 24*60*60*1000;
    /**
     *
     * @Method: dateToString
     * @Description: (日期转换为字符串：精确到毫秒)
     * @param date 日期
     * @return String (转换好的字符串日期)
     * @author : yxb
     */
    public static String dateToString(Date date){
        SimpleDateFormat sdf = DATE_TIME_MS_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_MS);
            DATE_TIME_MS_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * @Method: dateToStamp
     * @Description: 将"yyyy-MM-dd HH:mm:ss"格式时间转换为时间戳
     * @param s 格式化的时间
     * @return   Long
     * @throws ParseException ParseException
     * @author : yxb
     */
    public static Long dateToStamp(String s) throws ParseException {
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        Date date = sdf.parse(s);
        return date.getTime();
    }
    /**
     * @Method: dateToStamp
     * @Description: 将时间戳转换为"yyyy-MM-dd HH:mm:ss"格式时间
     * @param s 时间戳
     * @return   String
     * @throws  ParseException ParseException
     * @author : yxb
     */
    public static String stampToDate(String s){
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        Date date = new Date(Long.parseLong(s));
        return sdf.format(date);
    }
    /**
     * @Method: getStringCurrentDateTime
     * @Description: (获取字符串类型当前的时间)
     * @return String
     * @throws
     * @author : yxb
     */
    public static String getStringCurrentDateTime(){
        SimpleDateFormat sdf = DATE_TIME_MS_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_MS);
            DATE_TIME_MS_LOCAL.set(sdf);
        }
        return sdf.format(new Date());
    }

    /**
     * @Method: currentDateToString
     * @Description: 当前时间转为 "yyyy-MM-dd HH:mm:ss"格式时间
     * @param
     * @return java.lang.String (返回类型描述)
     * @throws
     * @author : yxb
     */
    public static String currentDateToString(){
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        return sdf.format(new Date());
    }

    /**
     * @Method:  dateToString
     * @Description: 获取当前时间点前面几小时的时间
     * @param hour 小时
     * @return  时间字符串
     * @throws
     * @author : yxb
     */
    public static String dateToString(int hour){
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        return sdf.format(new Date(System.currentTimeMillis()-hour*60*60*1000));
    }
    /**
     *
     * @Method : dateLaterString
     * @Description : 获取当前时间点前面几小时的时间
     * @param date : 日期
     * @param hour : 小时
     * @return : java.util.Date 时间
     * @author : yxb
     *
     */
    public static Date dateLaterString(String date ,int hour){
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(DATE_TIME_FORMAT).parse(date));
        } catch (ParseException e) {
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
     * @throws
     * @author : yxb
     */
    public static Date getDateAfter(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }

    /**
     *
     * @Method : dateToStringNoMS
     * @Description : 日期转化为字符串，没有毫秒
     * @param date : 日期
     * @return : java.lang.String 字符串
     * @author : yxb
     *
     */
    public static String dateToStringNoMS(Date date){
        SimpleDateFormat sdf = DATE_TIME_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            DATE_TIME_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     *
     * @Method : dateToStringYMD
     * @Description :日期转化为字符串，只有年月日
     * @Description :日期转化为字符串，只有年月日
     * @param date :日期
     * @return : java.lang.String
     * @author : yxb
     *
     */
    public static String dateToStringYMD(Date date){
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
     * @throws
     * @author : yxb
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
     *
     * @Method : stringToDateYMDHMS
     * @Description : 根据字符串获取当前日期-年月日时分秒
     * @param dateStr :
     * @return : java.util.Date
     * @author : yxb
     *
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     *
     * @Method : dateToStringSFM
     * @Description : 日期转换为时分秒
     * @param date : 转换日期
     * @return : java.lang.String
     * @author : yxb
     *
     */
    public static String dateToStringSFM(Date date){
        SimpleDateFormat sdf = DATE_HMS_LOCAL.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_HMS_FORMAT);
            DATE_HMS_LOCAL.set(sdf);
        }
        return sdf.format(date);
    }

    /**
     * @Method: sameYear
     * @Description: 判断是否是同一年
     * @param date1 日期1
     * @param date2 日期2
     * @return boolean (返回类型描述)
     */
    public static boolean sameYear(Date date1,Date date2){
        Calendar now1 =Calendar.getInstance();
        Calendar now2 =Calendar.getInstance();
        now1.setTime(date1);
        now2.setTime(date2);
        int year1 = now1.get(Calendar.YEAR);
        int year2 = now2.get(Calendar.YEAR);
        return  year1 == year2;
    }

    /**
     * @Method: sameMouth
     * @Description: 判断两个月是否在一个月内
     * @param date1 日期1
     * @param date2 日期2
     * @return boolean (返回类型描述)
     */
    public static boolean sameMouth(Date date1,Date date2){
        Calendar now1 =Calendar.getInstance();
        Calendar now2 =Calendar.getInstance();
        now1.setTime(date1);
        now2.setTime(date2);
        int month1 = now1.get(Calendar.MONTH) + 1;
        int year1 = now1.get(Calendar.YEAR);
        int month2 = now2.get(Calendar.MONTH) +1;
        int year2 = now2.get(Calendar.YEAR);
        return  month1 == month2 && year1 == year2;
    }

    /**
     * @Method: sameMouth
     * @Description: 判断是否是同一天
     * @param date1 日期1
     * @param date2 日期2
     * @return boolean (返回类型描述)
     */
    public static boolean sameMouthDay(Date date1,Date date2){
        Calendar now1 =Calendar.getInstance();
        Calendar now2 =Calendar.getInstance();
        now1.setTime(date1);
        now2.setTime(date2);
        int month1 = now1.get(Calendar.MONTH) + 1;
        int year1 = now1.get(Calendar.YEAR);
        int day1 = now1.get(Calendar.DAY_OF_MONTH);
        int month2 = now2.get(Calendar.MONTH) +1;
        int year2 = now2.get(Calendar.YEAR);
        int day2 = now2.get(Calendar.DAY_OF_MONTH);
        return  month1 == month2 && year1 == year2 && day1 == day2;
    }

    /**
     *
     * @Method: dateToStringYM
     * @Description: 日期转化为字符串，只有年月
     * @param date 日期转化为字符串，只有年月
     * @return String 日期
     * @author : yxb
     */
    public static String dateToStringYM(Date date){
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, -1);
        date = cl.getTime();
        return sdf.format(date);
    }

    /**
     * @Method: getlastMonth
     * @Description: 获取当前月的的上个月
     * @param datetime 传入的时间
     * @return String 操作后的时间
     */
    public static Date getlastMonthDate(Date datetime) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(datetime);
        cl.add(Calendar.MONTH, -1);
        datetime = cl.getTime();
        return datetime;
    }

    /**
     *
     * @Method : stringToDateYM
     * @Description : 根据传入的字符串获得时间-年月yyyyMM
     * @param dateStr : 日期字符串
     * @return : java.util.Date
     *
     */
    public static Date stringToDateYM(String dateStr) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_YM_FORMAT);
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * @Method : getTimeDifference
     * @Description : 返回时间差，小于一小时展示分，小于一天到小时，大于一天显示天
     * @param startTime : 被减数
     * @param targetTime : 减数
     * @return : java.util.Date
     *
     */
    public static String getTimeDifference(Date startTime, Date targetTime) {
        long difference = startTime.getTime()-targetTime.getTime();
        if (difference < DAY) {
            long hour = difference/(HOUR);
            long minute = (difference - hour*HOUR)/(MIN);
            long second = (difference - hour*HOUR - minute*MIN)/(SEC);
            return hour+ "小时" + minute + "分" + second + "秒";
        }
        if (difference > DAY) {
            long day = difference / DAY;
            long hour = (difference - day*DAY)/HOUR;
            long minute = (difference - day*DAY - hour*HOUR)/(MIN);
            long second = (difference - day*DAY - hour*HOUR - minute*MIN)/(SEC);
            return day + "天" + hour+ "小时" + minute + "分" + second + "秒";
        }
        return "0天0小时0分0秒";
    }


    /**
     *
     * 功能描述:
     *  判断是否超期
     * @param:
     * @return:
     * @auther: yuexuebin
     * @date: 2021/11/3 10:49
     */
    public static int checkIfTimeOut(Integer timeLimit, Date flowNodeTime) {
        long nowDate = System.currentTimeMillis();
        long start = flowNodeTime.getTime();
        if (nowDate-start > timeLimit*60*1000) {
            return 1;
        }
        return 0;
    }

    /**
     *  加减对应时间后的日期
     * @param date  需要加减时间的日期
     * @param amount    加减的时间(毫秒)
     * @return  加减对应时间后的日期
     */
    public static Date subtractTime(Date date, int amount) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            String strTime = sdf.format(date.getTime() + amount);
            Date time = sdf.parse(strTime);
            return time;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @Method : string2Date
     * @Description : 将yyyy-MM-dd HH:mm:ss格式字符串时间转为Date类型
     * @param time : 日期
     * @return : java.util.Date 时间
     * @author : hued
     *
     */
    public static Date string2Date(String time){
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_TIME_FORMAT).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

package com.chinatower.platform.utils;

import cn.hutool.core.date.DateUtil;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.platform.modules.statistics.entity.CheckpointDateVO;
import com.xxl.job.core.biz.model.ReturnT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DataUtil {

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
    private static final String DATE_TIME_TWO_FORMAT = "yyyy/MM/dd HH:mm:ss";
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
     * 封装返回值
     * @param flag
     * @return
     */
    public static ReturnT<String> returnString(boolean flag){
        if(flag){
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * 封装入参对象
     * @return
     */
    public static DateVO setDateVO(){
        DateVO dateVO = new DateVO();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateVO.setYear(String.valueOf(year));
        dateVO.setMonth(String.valueOf(month));
        dateVO.setDay(String.valueOf(day));
        dateVO.setHandleDate(DateUtils.getDate());
        dateVO.setHandleDateTime(DateUtils.getTime());
        dateVO.setHandleMonth(DateUtils.getCurrentMonth());
        dateVO.setHandleYear(String.valueOf(year));
        dateVO.setNowYear(String.valueOf(nowYear));
        dateVO.setDate(date);
        return dateVO;
    }

    /**
     * 封装入参对象
     * @return
     */
    public static DateVO setDateVO(String dateStr){
        DateVO dateVO = new DateVO();
        dateVO.setYear(dateStr.substring(0,4));
        String month=dateStr.substring(5, 7);
        if(month.startsWith("0")) {
            month = month.substring(1,2);
        }
        dateVO.setMonth(month);
        if (dateStr.length()>=10) {
            String day = dateStr.substring(8, 10);
            if(day.startsWith("0")) {
                day = day.substring(1,2);
            }
            dateVO.setDay(day);
        }
        dateVO.setHandleDate(dateStr);

        dateVO.setHandleDateTime(DateUtils.getTime());
        dateVO.setHandleMonth(DateUtils.getCurrentMonth());
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        int year = calendar.get(Calendar.YEAR);
        dateVO.setHandleYear(String.valueOf(year));
        dateVO.setNowYear(String.valueOf(nowYear));
        return dateVO;
    }


    public static DateVO setDateVOS(String dateStr){
        DateVO dateVO = new DateVO();
        String year= dateStr.substring(0, 4);
        dateVO.setYear(year);
        String month=dateStr.substring(5, 7);
        if(month.startsWith("0")) {
            month = month.substring(1,2);
        }
        dateVO.setMonth(month);
        if (dateStr.length()>=10) {
            String day = dateStr.substring(8, 10);
            if(day.startsWith("0")) {
                day = day.substring(1,2);
            }
            dateVO.setDay(day);
        }
        dateVO.setHandleDate(dateStr);
        dateVO.setHandleDateTime(DateUtils.getTime());
        dateVO.setHandleMonth(dateStr);
        dateVO.setHandleYear(year);
        return dateVO;
    }



    /**
     * 封装入参对象
     * @return
     */
    public static CheckpointDateVO setCheckpointDateVO(){
        CheckpointDateVO dateVO = new CheckpointDateVO();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        dateVO.setYear(String.valueOf(year));
        dateVO.setMonth(String.valueOf(month));
        dateVO.setDay(String.valueOf(day));
        dateVO.setWarningHour(String.valueOf(hour));
        dateVO.setHandleDate(DateUtils.getDate());
        return dateVO;
    }

    /**
     * 封装入参对象
     * @return
     */
    public static CheckpointDateVO setCheckpointDateVOS(String dateStr){
        CheckpointDateVO dateVO = new CheckpointDateVO();
        dateVO.setYear(dateStr.substring(0,4));
        String month=dateStr.substring(5, 7);
        if(month.startsWith("0")) {
            month = month.substring(1,2);
        }
        dateVO.setMonth(month);
        if (dateStr.length()>=10) {
            String day = dateStr.substring(8, 10);
            if(day.startsWith("0")) {
                day = day.substring(1,2);
            }
            dateVO.setDay(day);
        }
        dateVO.setHandleDate(dateStr);
        Calendar calendar = Calendar.getInstance();
        return dateVO;
    }

    /**
     * 封装入参对象
     * @return
     */
    public static CheckpointDateVO setCheckpointDateVOYear(String dateStr){
        CheckpointDateVO dateVO = new CheckpointDateVO();
        dateVO.setYear(dateStr.substring(0,4));
        dateVO.setHandleDate(dateStr);
        Calendar calendar = Calendar.getInstance();
        return dateVO;
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
     * 获取分钟的时间戳（13位）
     *
     * @return
     */
    public static long getTimeMills() {
        LocalDate localDate= LocalDate.now();
        LocalTime localTime= LocalTime.now();
        return LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute(), localTime.getSecond()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * “yyyy-MM-dd HH:mm:ss”格式转换为精确到分钟的时间戳（13位）
     *
     * @return
     */
    public static long formatTimeMills(String compareTime) {
        Date date = DateUtil.parse(compareTime);
        return LocalDateTime.of(DateUtil.year(date), DateUtil.month(date)+1, DateUtil.dayOfMonth(date), DateUtil.hour(date,true), DateUtil.minute(date), DateUtil.second(date)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取某年某月的第一天
     */
    public static String getFisrtDayOfMonth(int year,int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_YMD);
        return  sdf.format(cal.getTime());
    }
    /**
     * 获取某月的最后一天
     */
    public static String getLastDayOfMonth(int year,int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_YMD);
        return  sdf.format(cal.getTime());
    }


}

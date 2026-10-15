package com.chinatower.platform.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间工具类
 *
 * @author chinatower
 */
@SuppressWarnings("all")
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /** 日期时间格式化 **/
    public static final String DATE_TIME_YM_FORMAT = "yyyyMM";
    private static ThreadLocal<SimpleDateFormat> DATE_YM_LOCAL = new ThreadLocal<SimpleDateFormat>();

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getCurrentMonth() {
        return dateTimeNow(YYYY_MM);
    }


    public static String getLastMonth() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.MONTH, instance.get(Calendar.MONTH) - 1);
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM, instance.getTime());
    }


    /**
     * 获取本月的所有日期
     * @param descFlag 判断是否正序 <br/>
     *                 true：正序；false：倒叙
     *
     * @return list
     */
    public static List<String> getAllDaysWithCurrentMonth(boolean descFlag) {
        String currentMonth = DateUtils.getCurrentMonth();
        String currentDay = DateUtils.getDate();
        int currentDayNum = Integer.parseInt(currentDay.substring(8, 10)) ;

        return getTimeArr(descFlag, currentMonth, currentDayNum);
    }

    /**
     * 获取本年的所有月份
     * @param descFlag 判断是否正序 <br/>
     *                 true：正序；false：倒叙
     *
     * @return list
     */
    public static List<String> getAllMonthWithCurrentYear(boolean descFlag) {
        String currentMonth = DateUtils.getCurrentMonth();
        String currentYearStr = currentMonth.substring(0, 4);
        int currentMonthNum = Integer.parseInt(currentMonth.substring(5, 7)) ;

        return getTimeArr(descFlag, currentYearStr, currentMonthNum);
    }



    private static List<String> getTimeArr(boolean descFlag, String currentYearStr, int currentMonthNum) {
        String tempInt;
        List<String> list = new ArrayList<>();
        if (descFlag) {
            for (int i = 1; i <= currentMonthNum; i ++) {
                if (i < 10) {
                    tempInt = "0" + i;
                } else {
                    tempInt = i + "";
                }
                list.add(currentYearStr + "-" + tempInt);
            }
        } else {
            for (int i = currentMonthNum; i >= 1; i --) {
                if (i < 10) {
                    tempInt = "0" + i;
                } else {
                    tempInt = i + "";
                }
                list.add(currentYearStr + "-" + tempInt);
            }
        }
        return list;
    }


    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60L;
        long nh = 1000 * 60 * 60L;
        long nm = 1000 * 60L;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
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
}

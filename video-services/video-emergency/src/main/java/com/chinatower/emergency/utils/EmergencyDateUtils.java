package com.chinatower.emergency.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: video-platform
 * @ClassName: EmergencyDateUtils
 * @description: 应急相关时间工具类
 * @author: fengty
 * @create: 2024-11-01 10:40
 */
public class EmergencyDateUtils {
    /**
     * 根据日期获得起始时间
     * @param startDate, endDate
     * @return
     */
    public static String[] getDateRange(String startDate, String endDate) {
        try {
            // 定义输入和输出的日期格式
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            // 解析输入的开始日期和结束日期字符串
            Date start = inputFormat.parse(startDate);
            Date end = inputFormat.parse(endDate);

            // 拼接开始时间和结束时间
            String startTime = outputFormat.format(start) + " 00:00:00";
            String endTime = outputFormat.format(end) + " 23:59:59";

            return new String[]{startTime, endTime};
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String completionDateString (String date, String time) {
        return date + " " + time;
    }
}

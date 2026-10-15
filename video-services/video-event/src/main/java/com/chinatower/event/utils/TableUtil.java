package com.chinatower.event.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TableUtil.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/24 10:45
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public class TableUtil {

    /**
     *数据库分表时间格式化
     */
    private static final String TABLE_DATE_FORMAT = "yyyyMM";

    /**
     * @Method: getTableName
     * @Description: 根据时间获取分表名
     * @param date 时间
     * @param tableModule 分表前缀
     * @return java.lang.String 存在返回表名，不存在返回nil
     * @author : gaoziying
     * @CreateDate : 2017/12/1 下午2:40
     */
    public static String getTableName(Date date, String tableModule) {
        ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>();
        SimpleDateFormat sdf = dateFormat.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(TABLE_DATE_FORMAT);
            dateFormat.set(sdf);
        }
        String tableName = tableModule + sdf.format(date);
        return tableName;
    }

    /**
     * @Method: getTableName
     * @Description: 根据时间获取分表名
     * @param yyyyMM 时间
     * @param tableModule 分表前缀
     * @return java.lang.String 存在返回表名，不存在返回nil
     * @author : yxb
     * @CreateDate : 2021/11/3 09:32:12
     */
    public static String getTableNameByString(String yyyyMM, String tableModule) {
        String tableName = tableModule + yyyyMM;
        return tableName;
    }

    /**
     * 功能描述：获取最小表时间,取当前时间半年数据
     * @params []
     * @return java.lang.String
     * @author su.tingning
     * @date 2022/6/16 13:46
     */
    public static String getMinTableDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -5);
        SimpleDateFormat dateFormat = new SimpleDateFormat(TABLE_DATE_FORMAT);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前时间前14天的月份
     * @return
     */
    public static String getMinTableDateForDay(long day) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 减去14天
        LocalDate fourteenDaysAgo = currentDate.minusDays(day);
        // 设置日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TABLE_DATE_FORMAT);
        // 格式化日期为字符串
        return fourteenDaysAgo.format(formatter);
    }


}

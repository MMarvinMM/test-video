package com.chinatower.common.core.utils;

import java.text.SimpleDateFormat;
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
     * 分表id字段的长度
     */
    private static final int ID_STR_LENGTH = 14;


    public static boolean checkTableDate(String tableName, String minTableName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TABLE_DATE_FORMAT);
        String tableDate = tableName.substring(tableName.length() - 6);
        String minTableDate = minTableName.substring(minTableName.length() - 6);
        String nowTableDate = dateFormat.format(new Date());
        return minTableDate.compareTo(tableDate) <= 0 && nowTableDate.compareTo(tableDate) >= 0;
    }



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
     * @Method: getTableNameByTablePrefixAndId
     * @Description: 拼接表名称
     * @param tablePrefix 表名称的前缀
     * @param id id
     * @return String 表名称
     * @author :  weichaoxu
     * @CreateDate : 2017年12月6日 上午11:42:43
     */
    public static String getTableNameByTablePrefixAndId(String tablePrefix, String id) {
        return tablePrefix + id.substring(0, 6);
    }



    public static String findYearMonth(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String date = year + "" + (month < 10 ? "0" + month : month);
        return date;
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
}

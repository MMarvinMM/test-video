package com.chinatower.platform.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 大数据科学记数法
 *
 * @author: root
 * @create: 2018-07-13 17:11
 **/
@SuppressWarnings("all")
public class BigDecimalUtils {

    public static final long DATA_100 = 100;
    public static final long DATA_10000 = 10000;

    /**
     *
     * @param money 数字
     * @param num 保留小数
     * @return
     */
    public static String longToString100(long money, int num) {
        String format_str = "0";
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                if (i == 0) format_str += ".0";
                if (i > 0) format_str += "0";
            }
        }
        DecimalFormat formater = new DecimalFormat(format_str);
        return formater.format(round(money, DATA_100));
    }



    /**
     * long和long相加
     *
     * @param v1
     * @param v2
     * @return double
     */
    public static long add(long v1, long v2) {
        BigDecimal b1 = new BigDecimal(Long.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.add(b2).longValue();
    }


    /**
     * double和double相加
     *
     * @param v1
     * @param v2
     * @return double
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * double和double相减
     *
     * @param v1
     * @param v2
     * @return double
     **/
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * double和double相减
     *
     * @param v1
     * @param v2
     * @return double
     **/
    public static long sub(long v1, long v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).longValue();
    }

    /**
     * double和double相减
     *
     * @param v1
     * @param v2
     * @return double
     **/
    public static int sub(int v1, int v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).intValue();
    }

    /**
     * double和long相乘
     *
     * @param v1
     * @param v2
     * @return long
     */
    public static long multiply(float v1, long v2) {
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.multiply(b2).longValue();
    }

    /**
     * double和long相乘
     *
     * @param v1
     * @param v2
     * @return long
     */
    public static long multiply(long v1, int v2) {
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.multiply(b2).longValue();
    }

    /**
     * double和long相乘
     *
     * @param v1
     * @param v2
     * @return long
     */
    public static long multiply(double v1, long v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.multiply(b2).longValue();
    }

    /**
     * double和long相乘
     *
     * @param v1
     * @param v2
     * @return long
     */
    public static long multiply(long v1, long v2) {
        BigDecimal b1 = new BigDecimal(Long.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.multiply(b2).longValue();
    }

    /**
     * double和int相乘
     *
     * @param v1
     * @param v2
     * @return long
     */
    public static long multiply(double v1, int v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Integer.toString(v2));
        return b1.multiply(b2).longValue();
    }


    /**
     * float和double相乘
     *
     * @param v1
     * @param v2
     * @return long
     */
    public static double multiply(float v1, double v2) {
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }


    /**
     * double 相乘
     *
     * @param v1
     * @param v2
     * @return double
     */
    public static double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }


    /**
     * 相除,做除法的时候相对的麻烦一点,涉及的多一些
     * 提供精确的小数位四舍五入处理。
     *
     * @param v1    需要四舍五入的数字
     * @param v2    需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static long round(long v1, long v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Long.toString(v1));
        BigDecimal two = new BigDecimal(Long.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).longValue();
    }


    /**
     * 相除,做除法的时候相对的麻烦一点,涉及的多一些
     * 提供精确的小数位四舍五入处理。
     *
     * @param v1    需要四舍五入的数字
     * @param v2    需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static Double roundDouble(long v1, long v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Long.toString(v1));
        BigDecimal two = new BigDecimal(Long.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 相除,做除法的时候相对的麻烦一点,涉及的多一些
     * 提供精确的小数位四舍五入处理。
     *
     * @param v1    需要四舍五入的数字
     * @param v2    需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Double.toString(v1));
        BigDecimal two = new BigDecimal(Double.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }



    /**
     * 相除,做除法的时候相对的麻烦一点,涉及的多一些
     * 提供精确的小数位四舍五入处理。
     *
     * @param v1    需要四舍五入的数字
     * @param v2    需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v1, long v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("此参数错误");
        }
        BigDecimal one = new BigDecimal(Double.toString(v1));
        BigDecimal two = new BigDecimal(Double.toString(v2));
        return one.divide(two, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * double和double相除 保留2位小数
     *
     * @param v1
     * @param v2
     * @return double
     */
    public static double round(double v1, double v2) {
        return round(v1, v2, 4);
    }

    /**
     * double和double相除 保留2位小数
     *
     * @param v1
     * @param v2
     * @return double
     */
    public static double round(double v1, long v2) {
        return round(v1, v2, 4);
    }

}

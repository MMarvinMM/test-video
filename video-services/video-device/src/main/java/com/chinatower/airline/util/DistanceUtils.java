package com.chinatower.airline.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DistanceUtils {

    // 地球平均半径(公里)
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 计算两个经纬度点之间的距离(单位：米)
     * @param longitude1 第一个点的经度
     * @param latitude1 第一个点的纬度
     * @param longitude2 第二个点的经度
     * @param latitude2 第二个点的纬度
     * @return 两点之间的距离，保留两位小数
     */
    public static double calculateDistance(String longitude1, String latitude1,
                                           String longitude2, String latitude2) {
        try {
            // 将字符串经纬度转换为double类型
            double lon1 = Double.parseDouble(longitude1);
            double lat1 = Double.parseDouble(latitude1);
            double lon2 = Double.parseDouble(longitude2);
            double lat2 = Double.parseDouble(latitude2);

            // 转换为弧度
            double lat1Rad = Math.toRadians(lat1);
            double lon1Rad = Math.toRadians(lon1);
            double lat2Rad = Math.toRadians(lat2);
            double lon2Rad = Math.toRadians(lon2);

            // 计算纬度差和经度差
            double deltaLat = lat2Rad - lat1Rad;
            double deltaLon = lon2Rad - lon1Rad;

            // 应用哈维辛公式
            double a = Math.pow(Math.sin(deltaLat / 2), 2)
                    + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                    * Math.pow(Math.sin(deltaLon / 2), 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            // 计算距离（米）并保留两位小数
            double distance = EARTH_RADIUS * c;
            return new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (NumberFormatException e) {
            // 处理经纬度格式错误
            throw new IllegalArgumentException("经纬度格式错误，无法转换为数字", e);
        }
    }

}

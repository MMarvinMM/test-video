package com.chinatower.video.util;

import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.video.modules.query.entity.GeoPointVO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SectorCalculatorUtil {
    private static final double EARTH_RADIUS = 6371.0; // 地球半径，单位km

    public static void main(String[] args) {
        // 输入参数
        double centerLat = 40.069465753;   // 中心点纬度(示例：北京)
        double centerLon = 116.329942926;  // 中心点经度
        double centerBearing = 56.05;    // 扇形中心线相对于正北的偏移(56度)
        double sectorWidth = 27.00;      // 扇形展开宽度(27度)
        double radius = 3;            // 3公里半径

        // 计算扇形区域
        List<GeoPointVO> sector = calculateSector(
                centerLat, centerLon,
                centerBearing,
                sectorWidth,
                radius);

        // 输出结果
        System.out.println("扇形闭合区域坐标(顺时针方向):");
        for (GeoPointVO point : sector) {
            System.out.printf("纬度: %.6f, 经度: %.6f%n", point.getLatitude(), point.getLongitude());
        }
    }
    public static List<GeoPointVO> calculateSector(DeviceModelDTO deviceModel){
        double longitude = Double.parseDouble(deviceModel.getLongitude());
        double latitude = Double.parseDouble(deviceModel.getLatitude());
        double horizRange = Double.parseDouble(deviceModel.getHorizRange());
        double horizViewRange = Double.parseDouble(deviceModel.getHorizViewRange());
        double visualRange = Double.parseDouble(deviceModel.getVisualRange());
        log.info("经度：{}，纬度：{}，水平角范围：{}，水平视场角：{}，可视距离：{}",longitude,latitude,horizRange,horizViewRange,visualRange);
        return calculateSector(latitude, longitude, horizViewRange, horizRange, visualRange);
    }

    /**
     * 计算扇形闭合区域坐标
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param centerBearing 扇形中心线方位角(正北为0，顺时针增加)
     * @param sectorWidth 扇形展开宽度(度)
     * @param radius 半径(km)
     * @return 扇形边界点列表(闭合多边形，首尾点相同)
     */
    public static List<GeoPointVO> calculateSector(
            double centerLat, double centerLon,
            double centerBearing,
            double sectorWidth,
            double radius) {

        List<GeoPointVO> points = new ArrayList<>();

        // 1. 添加中心点(扇形顶点)
        points.add(new GeoPointVO(centerLat, centerLon));

        // 2. 计算扇形角度范围
        double startAngle = centerBearing - sectorWidth/2;
        double endAngle = centerBearing + sectorWidth/2;
        double step = 1.0; // 每1度计算一个点

        // 标准化角度到0-360范围
        startAngle = (startAngle + 360) % 360;
        endAngle = (endAngle + 360) % 360;

        // 3. 计算弧线点(从endAngle到startAngle，顺时针方向)
        if (startAngle > endAngle) {
            // 情况1: 跨越0度(如startAngle=350, endAngle=10)
            for (double angle = endAngle; angle >= 0; angle -= step) {
                addPoint(points, centerLat, centerLon, angle, radius);
            }
            for (double angle = 360; angle >= startAngle; angle -= step) {
                addPoint(points, centerLat, centerLon, angle, radius);
            }
        } else {
            // 情况2: 正常范围(如startAngle=20, endAngle=50)
            for (double angle = endAngle; angle >= startAngle; angle -= step) {
                addPoint(points, centerLat, centerLon, angle, radius);
            }
        }

        // 4. 闭合多边形
        points.add(new GeoPointVO(centerLat, centerLon));

        return points;
    }

    private static void addPoint(List<GeoPointVO> points, double lat, double lon, double angle, double distance) {
        double[] dest = calculateDestination(lat, lon, angle, distance);
        points.add(new GeoPointVO(dest[0], dest[1]));
    }

    /**
     * 使用Haversine公式计算目标点坐标
     */
    private static double[] calculateDestination(double lat, double lon,
                                                 double bearing, double distance) {
        double latRad = Math.toRadians(lat);
        double lonRad = Math.toRadians(lon);
        double bearingRad = Math.toRadians(bearing);
        double angularDist = distance / EARTH_RADIUS;

        double newLatRad = Math.asin(Math.sin(latRad) * Math.cos(angularDist) +
                Math.cos(latRad) * Math.sin(angularDist) * Math.cos(bearingRad));

        double newLonRad = lonRad + Math.atan2(
                Math.sin(bearingRad) * Math.sin(angularDist) * Math.cos(latRad),
                Math.cos(angularDist) - Math.sin(latRad) * Math.sin(newLatRad));

        // 确保经度在-180到180范围内
        newLonRad = (newLonRad + 3*Math.PI) % (2*Math.PI) - Math.PI;

        return new double[]{
                Math.toDegrees(newLatRad),
                Math.toDegrees(newLonRad)
        };
    }

    /**
     * 经纬度点类
     */
//    static class GeoPointVO {
//        double latitude;
//        double longitude;
//
//        public GeoPointVO(double lat, double lon) {
//            this.latitude = lat;
//            this.longitude = lon;
//        }
//    }
}

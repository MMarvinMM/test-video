package com.chinatower.gis.utils;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;

public class WKTSimplifier {

    /**
     * 对WKT格式的几何图形进行抽稀
     * @param wkt 原始WKT字符串
     * @param tolerance 抽稀容差（值越大抽稀程度越高）
     * @return 抽稀后的WKT字符串
     */
    public static String simplifyWKT(String wkt, double tolerance) {
        try {
            // 读取WKT字符串为几何对象
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(wkt);

            // 使用Douglas-Peucker算法进行抽稀
//            Geometry simplifiedGeometry = DouglasPeuckerSimplifier.simplify(geometry, tolerance);
            Geometry simplifiedGeometry = TopologyPreservingSimplifier.simplify(geometry, tolerance);
            // 将结果写回WKT格式
            WKTWriter writer = new WKTWriter();
            return writer.write(simplifiedGeometry);
        } catch (Exception e) {
            throw new RuntimeException("WKT抽稀失败", e);
        }
    }
}
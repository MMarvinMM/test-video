package com.chinatower.gis.utils;

public class EqualityCheck {
    private static final double tolerance = 0.0000001;
    // 判断两个点是否在一个容差范围内相等
    public static boolean arePointsEqual(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) < tolerance && Math.abs(p1.y - p2.y) < tolerance;
    }
}

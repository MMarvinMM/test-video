package com.chinatower.bdm.common;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * 坐标点与坐标面关系判断工具类（待完善）
 * @author: guochao.bj@fang.com
 * @createDate: 2021/7/22 13:52
 */
public class CoordinateUtil {

    /**
     * 将字符串多坐标点转换为坐标数组
     * @param latLonString "116.616634,40.272665|116.644733,40.280371|116.636181,40.264352|116.616634,40.272665"
     * @return org.locationtech.jts.geom.Coordinate[]
     * @author guochao.bj@fang.com
     * @date 2021/7/22
     */
    public static Coordinate[] getCoordinateArray(String latLonString){
        String[] split = latLonString.split("\\|");
        Coordinate[] coordinateArray = new Coordinate[split.length];
        for (int i = 0; i < split.length; i++) {
            String[] LatLng = split[i].split(",");
            Coordinate coordinate = new Coordinate(Double.valueOf(LatLng[0]),Double.valueOf(LatLng[1]));
            coordinateArray[i]=coordinate;
        }
        return coordinateArray;
    }


    /**
     * 判断坐标点是否在多坐标点组成的多边形面内
     * @param coordinateArray
     * @param coordinate
     * @return boolean
     * @author guochao.bj@fang.com
     * @date 2021/7/22
     * within 判断是否在内部，边缘点返回false
     * intersects 判断是否相交，弥补 within边缘点缺陷，
     */
    public static boolean withinAndIntersects(Coordinate[] coordinateArray,Coordinate coordinate){

        boolean result=false;
        //小于3个点无法组成多边形
        if (coordinateArray.length<3){
            return result;
        }

        //必须首尾坐标点相同组成闭合多边形
        if (!(coordinateArray[0].equals2D(coordinateArray[coordinateArray.length-1]))){
            return result;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(coordinate);
        Polygon polygon = geometryFactory.createPolygon(coordinateArray);

        if (point.within(polygon)||point.intersects(polygon)){
            result=true;
        }

        return result;
    }




}


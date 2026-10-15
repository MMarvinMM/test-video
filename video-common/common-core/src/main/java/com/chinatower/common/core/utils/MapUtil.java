package com.chinatower.common.core.utils;

import java.net.URL;
import java.util.ArrayList;

/**
 * 判断某个点是否在某个区域范围内,主要是为根据定位的经纬度查询在哪个网格内服务
 * @author Huada
 * 2014-07-29
 */
public class MapUtil {

	/**
	 * 根据某点坐标判断该坐标是否在某区域坐标范围内
	 * @param px 目标点x坐标
	 * @param py 目标点y坐标
	 * @param polygon 目标范围xy坐标集合
	 * @return
	 */
	public static boolean rayCasting(double px, double py, String polygon) {
		boolean flag = false;
		String[] points = polygon.split(",");
		for (int i = 0, j = points.length - 2; i < points.length - 1; j = i, i = i + 2) {
			double sx = Double.parseDouble(points[j]);// 从倒数第二依次读取;
			double sy = Double.parseDouble(points[j + 1]);// 从倒第一依次读取;
			double tx = Double.parseDouble(points[i]);// 从第一个依次读取;
			double ty = Double.parseDouble(points[i + 1]);// 从第二个依次读取;
			// 点与多边形顶点重合
			if ((sx == px && sy == py) || (tx == px && ty == py)) {
				return true;
			}
			// 判断线段两端点是否在射线两侧,射线为y轴;
			if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
				// 线段上与射线 Y 坐标相同的点的 X 坐标
				double x = sx + (py - sy) * ((tx - sx) / (ty - sy));
				// 点在多边形的边上
				if (x == px) {
					return true;
				}
				// 射线穿过多边形的边界
				if (x > px) {
					flag = !flag;
				}
			}
		}
		// 射线穿过多边形边界的次数为奇数时点在多边形内
		return flag;
	}

	private static double EARTH_RADIUS = 6378.137;  
	  
    private static double rad(double d) {  
        return d * Math.PI / 180.0;  
    }  
  
    /** 
     * 通过经纬度获取距离(单位：米) 
     * @param lat1 
     * @param lng1 
     * @param lat2 
     * @param lng2 
     * @author dzc
     * @return double
     */  
    public static double getDistance(double lat1, double lng1, double lat2,  
                                     double lng2) {  
        double radLat1 = rad(lat1);  
        double radLat2 = rad(lat2);  
        double a = radLat1 - radLat2;  
        double b = rad(lng1) - rad(lng2);  
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)  
                + Math.cos(radLat1) * Math.cos(radLat2)  
                * Math.pow(Math.sin(b / 2), 2)));  
        s = s * EARTH_RADIUS;  
        s = Math.round(s * 10000d) / 10000d;  
        s = s*1000;  
        return s;  
    }  
    /** 
     * 通过经纬度获取距离(单位：米) 
     * @param log 经度 
     * @param lat 纬度
     * @author dzc
     * @return String
     */  
    public static String getAdd(String log, String lat ){  
        //lat 小  log  大  
        //参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)  
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l="+lat+","+log+"&type=010";  
        String res = "";     
        try {     
            URL url = new URL(urlString);    
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();    
            conn.setDoOutput(true);    
            conn.setRequestMethod("POST");    
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));    
            String line;    
           while ((line = in.readLine()) != null) {    
               res += line+"\n";    
         }    
            in.close();    
        } catch (Exception e) {    
            System.out.println("error in wapaction,and e is " + e.getMessage());    
        }   
        return res;    
    }  
	
	/**
	 * 根据某点坐标判断该坐标是否在某区域坐标范围内
	 * @param px 目标点x坐标
	 * @param py 目标点y坐标
	 * @param polygonXA 目标范围x坐标集合
	 * @param polygonYA 目标范围y坐标集合
	 * @author Huada copy
	 * 2014-07-29
	 * @return
	 */
	public boolean isPointInPolygon(double px, double py,ArrayList<Double> polygonXA, ArrayList<Double> polygonYA) {
		boolean isInside = false;
		double ESP = 1e-9;
		int count = 0;
		double linePoint1x;
		double linePoint1y;
		double linePoint2x = 180;
		double linePoint2y;
		linePoint1x = px;
		linePoint1y = py;
		linePoint2y = py;
		for (int i = 0; i < polygonXA.size() - 1; i++) {
			double cx1 = polygonXA.get(i);
			double cy1 = polygonYA.get(i);
			double cx2 = polygonXA.get(i + 1);
			double cy2 = polygonYA.get(i + 1);
			if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) {
				return true;
			}
			if (Math.abs(cy2 - cy1) < ESP) {
				continue;
			}

			if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x,
					linePoint2y)) {
				if (cy1 > cy2){
					count++;
				}	
			} else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y,linePoint2x, linePoint2y)) {
				if (cy2 > cy1){
					count++;
				}	
			} else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x,linePoint1y, linePoint2x, linePoint2y)) {
				count++;
			}
		}
		if (count % 2 == 1) {
			isInside = true;
		}
		return isInside;
	}
	
	
	public double Multiply(double px0, double py0, double px1, double py1,double px2, double py2) {
		return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
	}

	/**
	 * 判断对应点是否在多边形的边上，判断目标点是否在所有相邻坐标点的连线上
	 * @param px0 目标点x坐标
	 * @param py0 目标点y坐标
	 * @param px1 目标范围某点x坐标
	 * @param py1 目标范围某点y坐标
	 * @param px2 目标范围另一点x坐标
	 * @param py2 目标范围另一点y坐标
	 * @return
	 */
	public boolean isPointOnLine(double px0, double py0, double px1,
			double py1, double px2, double py2) {
		boolean flag = false;
		double ESP = 1e-9;
		if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP)
				&& ((px0 - px1) * (px0 - px2) <= 0)
				&& ((py0 - py1) * (py0 - py2) <= 0)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 从目标点向向右
	 *  //每个线段的两点都在另一个线段的左右不同侧，则能断定线段相交                                                                               
	 * 公式对于向量(x1,y1)->(x2,y2),判断点(x3,y3)在向量的左边,右边,还是线上.                                                                    
	 * p=x1(y3-y2)+x2(y1-y3)+x3(y2-y1).p<0 左侧, p=0 线上, p>0 右侧            
	 * 参考http://blog.sina.com.cn/s/blog_4c4a15950100rhjn.html
	 * @param px1
	 * @param py1
	 * @param px2
	 * @param py2
	 * @param px3
	 * @param py3
	 * @param px4
	 * @param py4
	 * @return
	 */
	public boolean isIntersect(double px1, double py1, double px2, double py2,
			double px3, double py3, double px4, double py4) {
		boolean flag = false;
		double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
		if (d != 0) {
			double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3))
					/ d;
			double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1))
					/ d;
			if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
				flag = true;
			}
		}
		return flag;
	}

	private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	/// <summary>
	/// 中国正常坐标系GCJ02协议的坐标，转到 百度地图对应的 BD09 协议坐标
	/// </summary>
	/// <param name="lat">维度</param>
	/// <param name="lng">经度</param>
	public static String Convert_GCJ02_To_BD09(double lat, double lng) {
		double x = lng, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		lng = z * Math.cos(theta) + 0.0065;
		lat = z * Math.sin(theta) + 0.006;
		System.out.print("lnglng=" + lng);
		System.out.print("latlat=" + lat);
		return lng +"," + lat;
	}

	/// <summary>
	/// 百度地图对应的 BD09 协议坐标，转到 中国正常坐标系GCJ02协议的坐标
	/// </summary>
	/// <param name="lat">维度</param>
	/// <param name="lng">经度</param>
	public static String Convert_BD09_To_GCJ02(double lat, double lng) {
		double x = lng - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		lng = z * Math.cos(theta);
		lat = z * Math.sin(theta);
		System.out.println("lnglng=" + lng);
		System.out.println("latlat=" + lat);
		return lng +"," + lat;
	}
}

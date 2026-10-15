package com.chinatower.outdata.api.entity;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

/**
 * 监测因子
 * Created by zhujun on 2022/3/30.
 */
@Data
public class Monitor extends BaseMongoDTO {

    private String pm1;

    private String tsp;

    private String soundPressureA;

    // 温度
    private String temperature;
    //水温
    private String waterTemperature;
    //高锰酸盐指数
    private String imn;
    //总磷
    private String totalPhosphorus;
    //总氮
    private String tnb;
    //铜
    private String cu;
    //锌
    private String zn;

    // 湿度
    private String humidity;
    // 气压
    private String pressure;
    // 风速
    private String windSpeed;
    // 风向
    private String windDir;
    // 降水量
    private String precip;
    // 光照度
    private String illumination;
    // 水位
    private String waterLevel;
    // 雨量
    private String rainfall;
    // 表面位移
    private String displacement;
    // 渗流
    private String seepage;
    // 渗压
    private String infiltPressure;
    // PM2.5
    private String pm25;
    // PM10
    private String pm10;
    // SO2
    private String so2;
    // NO2
    private String no2;
    // O3
    private String o3;
    // CO
    private String co;
    // 总挥发性有机化合物TVOC
    private String tvoc;
    // β射线颗粒物
    private String particle;
    // 硫化氢
    private String h2s;
    // 氯化氢
    private String hcl;
    // 氯气
    private String cl2;
    // 氨气
    private String nh3;
    // 二硫化碳
    private String cs2;
    // 甲醛固态聚合物
    private String formaldehyde;
    // 三甲胺
    private String c3h9n;
    // 甲硫醇
    private String ch4s;
    // 甲硫醚
    private String c2sh6;
    // 二甲二硫醚
    private String c2h6s2;
    // 苯乙烯
    private String c8h8;
    // 电导率
    private String conductivity;
    // 浊度
    private String turbidity;
    // 溶解氧
    private String dissolvedox;
    // PH
    private String ph;
    // 氨氮
    private String ammoniaNig;
    // 化学需氧量
    private String cod;
    // 透明度
    private String transparency;
    // 盐度
    private String salinity;
    // 总溶解固体
    private String totalSolids;
    // 海水比重
    private String seawater;
    // 深度
    private String depth;

    // 设备编码
    private String deviceCode;
    // 监测时间 格式yyyy-MM-dd HH:mm:ss
    private String monitorTime;
    //平面坐标x  目标椭球平面坐标x方向差值，单位毫米
    private String planeX;
    //平面坐标y 目标椭球平面坐标y方向差值，单位毫米
    private String planeY;
    //平面坐标h  水利行业，目标椭球平面坐标h方向差值，单位毫米
    private String planeH;
    //水位高程 单位米
    private String waterH;
    //浸润线值 单位米
    private String soakage;
    //流速 流速（L/s）
    private String flux;
    //液位高程 单位米
    private String elevationH;



    public Monitor() {
        this.collectionName = "sensor_attribute_report";
    }

    //新大陆要求返回设备信息字段
    private String deviceName;

    private String modelCode;

    private String longitude;

    private String latitude;

    private String deviceModelCode;

    private String location;
}

package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RadarParam
 * @Deacription 雷达扩展字段属性
 * @Author chenmf
 * @Date 2022/3/19 10:30
 * @Version 1.0
 **/
@Data
public class RadarParam implements Serializable {

    //目标ID
    private String targetId;
    //告警区域名称
    private String alarmAreaName;
    //目标类型
    private String targetType;
    //目标MMSI
    private String mmsi;
    //目标名称
    private String targetName;
    //目标速度
    private float speed;
    //目标航向
    private float course;
    //目标长度
    private float targetLength;
    //租户编码
    private float tenantCode;
    //雷达编码
    private String[] radarCodes;

    // 2022-04-24 lc 车辆卡口摄像机识别拓展字段 start
    /** 车牌号 */
    private String vehicleNumber;
    /** 车牌类型 */
    private String vehicleType;
    /** 车牌颜⾊ */
    private String vehicleNumColor;
    /** 车辆颜⾊ */
    private String vehicleColor;

    /**
     * 小图
     */
    private String vehicleLitimg;
    // 2022-04-24 lc 车辆卡口摄像机识别拓展字段 end

}

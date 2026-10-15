package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Liqi
 * @description 大屏数据配置VO
 */
@Data
@ToString
public class ScreenDataVO implements Serializable {

    private static final long serialVersionUID = 1L;


    //大屏数据配置数量表id
    private String countId;
    //摄像机数量
    private String cameraNum;
    //物联设备数量
    private String iotNum;
    //雷达数量
    private String radarNum;
    //喇叭数量
    private String speakerNum;
    //无人机数量
    private String uavNum;
    //执法记录仪数量
    private String recorderNum;
    //告警类型数量
    private String alarmTypeNum;
    //数据编码
    private String dataCode;
    //告警来源
    private String dataSource;
    //数据类型0-摄像机1-物联设备2-雷达3-喇叭4-无人机5-执法记录仪6-网格7-网格员8-资源9-告警类型
    private String dataType;




}

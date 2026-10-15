package com.chinatower.sync.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 属地化-物联设备上报
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalizationSensorStatusReport extends BaseMongoDTO {
    // 设备编码
    private String deviceCode;
    //设备名称
    private String deviceName;
    // 在线状态 在线状态: 0在线 1离线
    private int onlineStatus;
    // 点位id 平台编码+自增序列
    private String pointId;
    //点位名称
    private String pointName;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    //更新时间 格式yyyy-MM-dd HH:mm:ss
    private String lastTime;
    //挂高
    private String height;
    // 操作类型 操作类型：0新增 1更新 2删除
    private String optType;
    //设备类型，运管录入，各系统与运管同步
    private String deviceType;
    //传感器监测因子状态列表
    private List<MonitorStatusReprot> monitorStatusList;


    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;



}

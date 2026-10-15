package com.chinatower.device.entity.dto;


import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/12/4 9:40
 */
@Data
public class UavFlyReq {
    private String uavFlyId;
    private String taskRecordId;
    private String deviceCode; // 设备编号
    private String lng; // 经度
    private String lat; // 纬度
    private String height; // 高度
    private String horizontalSpeed; // 水平速度
    private String verticalSpeed; // 垂直速度
    private String capacityPercent; // 电池剩余电量百分比
    private String attitudePitch; // 俯仰轴角度
    private String attitudeRoll; // 横滚轴角度
    private String attitudeHead; // 偏航轴角度
    private List<UavCameraAttitudeReq> cameraAttitude; // 相机云台姿态
    private String windSpeed; // 风速
    private int uploadSignal; // 上传信号
    private int downloadSignal; // 下载信号
    private String remainFlightTime; // 剩余飞行时间
    private String flightStatus; // 飞行状态
    private String flightRouteId; //航线id
    private String taskType; // 航线类型
    private List<UavPayloadStatusReq> payloadStatus; // 挂载状态
    private String accessNode; // 接入节点
    private String createTime; // 创建时间
    private String timestamp;  // 时间戳
    private String industryCode;
    private String uavName;
    private WebSocketMsgType webSocketMsgType = WebSocketMsgType.UFCP_UAV_FLY;
    /**
     * 飞行距离(单位：米)
     * 从轨迹起点到当前点的累计飞行距离
     */
    private Double flightDistance;

    /**
     * 累计里程米
     */
    private String accumulateMileage;
    /**
     * 预估总里程米
     */
    private String airMileage;

    /**
     * 累计返程里程米
     */
    private String accumulateMileageReturn;
}



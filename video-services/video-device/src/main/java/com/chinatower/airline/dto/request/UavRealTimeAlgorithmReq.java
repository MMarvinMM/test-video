package com.chinatower.airline.dto.request;

import com.chinatower.device.entity.dto.ExtraInfo;
import com.chinatower.device.entity.dto.UavMonitorGeoJson;
import lombok.Data;

/**
 * @author 90372
 */
@Data
public class UavRealTimeAlgorithmReq {
    /**
     * 航线id
     */
    private String flightRouteId;
    /**
     * 设备编码，优先机巢编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 设备接入节点，默认本节点
     */
    private String accessNode;
    /**
     * 算法编码
     */
    private String algorithmCode;
    /**
     * 分析类型 默认1
     */
    private String analyType;
    /**
     * 操作类型 1-新增，3-停止。 3=停止必填参数：taskId、flightRouteId、algorithmCode
     */
    private String optType;
    /**
     * 任务版本号
     */
    private Integer taskVersion;
    /**
     * 告警间隔时间
     */
    private Integer alarmInterval;
    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 接收节点
     */
    private String acceptNode;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 算法告警电子围栏
     */
    private ExtraInfo extraInfo;
    /**
     * 电子围栏参数
     */
    private UavMonitorGeoJson uavMonitorGeoJson;
}

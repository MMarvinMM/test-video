package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirLineVo {
    private String id;
    /**
     * 航线id
     */
    private String flightRouteId;
    /**
     * 航线名称
     */
    private String flightRouteName;
    /**
     *  // kmz文件地址
     */
    private String kmzUrl;
    /**
     *  // kmz文件名称
     */
    private String kmzFileName;
    /**
     * kmz可访问地址
     */
    private String kmzAccessUrl;
    /**
     * 航线类型
     */
    private String type;
    /**
     * 设备编码(优先机巢)
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 修改时间
     */
    private String modifiedTime;
    /**
     * 告警类型编码
     */
    private String warningTypeId;
    /**
     * 飞行kmz——json
     */
    private String kmzJson;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 收藏状态
     */
    private String colStatus;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 更新时间
     */
    private String updateTime;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 机巢编码
     */
    private String uavNestName;
    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 机巢坐标经度
     */
    private String uavNestLongitude;
    /**
     * 机巢坐标纬度
     */
    private String uavNestLatitude;
    /**
     * 无人机经度
     */
    private String uavLongitude;
    /**
     * 无人机纬度
     */
    private String uavLatitude;
    /**
     * 算法信息
     */
    private String algorithms;
    /**
     * 算法任务集合,分割
     */
    private String algorithmsTaskIds;
    /**
     * 禁飞标识
     */
    private String uavAllowed;
    /**
     * 是否精准复拍标记位（1：可以  0：不可以）
     */
    private String retakeFlag;
    /**
     * 航线备份版本
     */
    private String snapshotId;
    /**
     * 无人机名称
     */
    private String uavName;
    /**
     * 无人机编码
     */
    private String uavCode;
    /**
     * 无人机机巢编码
     */
    private String uavNestCode;
    /**
     * 无人机机巢海拔
     */
    private String uavNestAltitude;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 用户id集合
     */
    private List<String> userIds;
    /**
     * 无人机信息 包含镜头信息
     */
    private List<UavInfo> uavInfo;
    /**
     * 是否开启扩展水印（0-否、1-是）
     */
    private String isExpandWatermark;
    /**
     * 飞行里程
     */
    private String airMileage;
    /**
     * 喇叭配置
     */
    private String hornInfo;
    /**
     * 起飞机巢
     */
    private String startNestCode;

    /**
     * 降落机巢
     */
    private String endNestCode;

    /**
     * 是否蛙跳
     */
    private String frogLeap;

    /**
     * 无人机型号  1.8.10 改造
     */
    private String uavModelCode;
}

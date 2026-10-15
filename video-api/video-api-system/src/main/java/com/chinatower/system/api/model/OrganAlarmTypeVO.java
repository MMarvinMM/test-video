package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author liuxt
 * @date 2024/01/06 15:30
 * @description 组织与告警类型关系表
 */
@Data
@ToString
public class OrganAlarmTypeVO {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String orgId;
    private String creator;
    private Date creatorDate;
    private String modifier;
    private Date modifierDate;
    private String deleteFlag;//删除状态：0正常 1删除
    private String industryCode;
    private String appVerCode;
    private String deptId;
    /**
     * 告警来源1、AI告警2、人工上报3、一键告警4.摄像机类型5、车辆卡口6、人脸卡口8、物联设备告警9、卫星告警10、雷达告警
     */
    private String warningSource;
    /**
     * 告警类型编码
     */
    private String typeValue;

    /**
     * 个数
     */
    private Integer num;

    /**
     * 告警类型来源列表
     */
    private List<String> alarmSources;

    /**
     *摄像机告警来源 1有
     */
    private String cameraAlarmFlag;

    /**
     *无人机告警来源 1有
     */
    private String uavDeviceAlarmFlag;

    /**
     *物联设备告警来源 1有
     */
    private String IotAlarmFlag;

    /**
     *雷达设备告警来源 1有
     */
    private String radarAlarmFlag;

}

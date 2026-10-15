package com.chinatower.platform.project.custom.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author pei.g
 * @date 2021/6/18 15:30
 * @description 组织与设备关系表
 */
@Data
@ToString
public class OrganDeviceVO {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String orgId;
    private String deviceCode;
    private String devName;//设备名称
    private String siteName;//站址名称
    private String location;//站址地址
    private String creator;
    private Date creatorDate;
    private String modifier;
    private Date modifierDate;
    private String deleteFlag;//删除状态：0正常 1删除
    private String provinceId;
    private String cityId;
    private String countyId;
    private String parentOrgId;//父级组织ID
    private String industryCode;
    private String appVerCode;
    /** 摄像机 */
    private List<String> cameraCodeList;
    /** 物联设备 */
    private List<String> sensorCodeList;
    /** 卫星设备 */
    private List<String> satelliteList;
    /** 雷达设备 */
    private List<String> radarList;
    /** 无人机设备 */
    private List<String> uavList;
    /** 大喇叭设备 */
    private List<String> hornList;
    /** 执法记录仪设备 */
    private List<String> LERList;

    /** 图层 */
    private List<String> layerList;
    /**
     * 无人机机场
     */
    private List<String> uavNestList;

    private List<String> deviceCodeList;
    private List<SatelliteInfoVO> satelliteResultList;
    private List<String> orgIdList;
    private String orgName;//组织名称
    private String level;//层级
    private String admCode;//层级
    private List<String> applyScope;
    private int categoryCode;
    /**
     * 告警类型权限
     */
    private List<OrganAlarmTypeVO> alarmTypeList;


}

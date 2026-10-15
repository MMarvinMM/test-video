package com.chinatower.system.api.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @desription: 组织设备视图
 * @data 创建时间：2023/8/22 9:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ApiModel(value = "组织设备视图")
public class SysDeptDeviceVO implements Serializable {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 省
     */
    private String provinceId;

    /**
     * 城市
     */
    private String  cityId;

    /**
     * 街道
     */
    private String townCode;

    /**
     * 区县
     */
    private String countyId;

    /**
     * 区县
     */
    private List<String> countyIds;
    /**
     * 部门
     */
    private String deptId;

    private String pid;

    /**
     * 机巢设备编码
     */
    private String uavNestName;

    /**
     * 设备状态
     */
    private Integer deviceStatus;
}

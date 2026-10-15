package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @desription: 部门设备
 * @data 创建时间：2023/8/22 9:47
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysDeptDetailVO implements Serializable {

    /**
     * 雷达
     */
    private List<SysDeptDeviceVO> radars;

    /**
     * 摄像机
     */
    private List<SysDeptDeviceVO> cameras;

    /**
     * 传感器
     */
    private List<SysDeptDeviceVO> sensors;

    /**
     * 卫星
     */
    private List<SysDeptDeviceVO> satellites;

    /**
     * 部门
     */
    private List<SysDeptStreetVO> deptStreets;


    private List<SysDeptDeviceVO> uavs;

    private List<SysDeptDeviceVO> horns;

    private List<SysDeptDeviceVO> LERs;

    /**
     * 告警类型权限
     */
    private List<OrganAlarmTypeVO> alarmTypeList;

    private List<SysDeptDeviceVO> layers;

}

package com.chinatower.system.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author zhaol
 * @date 2022/4/26 17:37
 * @description
 */
@Data
public class AuthObjDataVO implements Serializable {

    /**
     * 组织列表
     */
    private Set<DeptVO> deptList;

    /**
     * 雷达
     */
    private List<DeviceVO> radars;

    /**
     * 摄像机
     */
    private List<DeviceVO> cameras;

    /**
     * 传感器
     */
    private List<DeviceVO> sensors;

    /**
     * 卫星列表
     */
    private List<DeviceVO> satellites;


    /**
     * 管理区域
     */
    private List<AreaDataVO> areaList;

    private List<SysDeptDeviceVO> uavs;

    private List<SysDeptDeviceVO> layars;

    private List<SysDeptDeviceVO> horns;

    @JsonProperty("LERs")
    private List<SysDeptDeviceVO> LERs;

    private List<SysDeptDeviceVO> lawEnforce;
}

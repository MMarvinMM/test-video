package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysDeptDetailVO implements Serializable {

    private List<SysDeptDeviceVO> radars;

    private List<SysDeptDeviceVO> cameras;

    private List<SysDeptDeviceVO> sensors;

    private List<SysDeptDeviceVO> satellites;

    private List<SysDeptStreetVO> deptStreets;

    private List<SysDeptDeviceVO> uavs;

    private List<SysDeptDeviceVO> uavNest;

    private List<SysDeptDeviceVO> horns;

    private List<SysDeptDeviceVO> LERs;

    /**
     * 告警类型权限
     */
    private List<OrganAlarmTypeVO> alarmTypeList;

    private List<SysDeptDeviceVO> layers;

}

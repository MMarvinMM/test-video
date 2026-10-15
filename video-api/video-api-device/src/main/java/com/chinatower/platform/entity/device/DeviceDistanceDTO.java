package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceDistanceDTO extends BaseDataScopeEntity {

    /**
     * 经度
     * @required true
     */
    private String longitude;
    /**
     * 纬度
     * @required true
     */
    private String latitude;
    /**
     * 检索半径，单位：米
     * @required true
     */
    private String radius;

    /**
     * 查询名称
     */
    private String searchName;

    /**
     * 查询条数
     */
    private Integer limitSize;

    /**
     * 是否包含所有状态
     */
    private Integer withAllStatus;

    /**
     * 查询类型 1.无人机 2.执法记录仪 3.雷达  4.物联设备  5.大喇叭
     */
    private Integer queryType;

    /**
     * 是否查询无人机飞行状态
     */
    private Boolean withFlightStatus;

    /**
     * 不包含的设备编码
     */
    private String notIncludeDeviceCode;
}

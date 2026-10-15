package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 无人机管理导出类
 */
@Data
@ToString
public class UavDeviceVoExport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "机场编号")
    private String uavNestCode;

    @Excel(name = "机场名称")
    private String uavNestName;

    @Excel(name = "机场厂家")
    private String uavNestManufacturerName;

    @Excel(name = "机场型号")
    private String uavNestModelName;

    @Excel(name = "机场状态")
    private String uavNestStatusName;

    @Excel(name = "无人机编号")
    private String deviceCode;

    @Excel(name = "无人机名称")
    private String deviceName;

    @Excel(name = "无人机厂家")
    private String manufacturerName;

    @Excel(name = "无人机型号")
    private String modelName;

    @Excel(name = "无人机状态")
    private String deviceStatusName;

    @Excel(name = "无人机地址")
    private String location;

    @Excel(name = "相机型号")
    private String cameraModelName;

    @Excel(name = "所属组织")
    private String orgName;

    @Excel(name = "订购状态")
    private String orderStatusName;

    @Excel(name = "省份")
    private String provinceName;

    @Excel(name = "地市")
    private String cityName;

    @Excel(name = "区县")
    private String countyName;

    @Excel(name = "乡镇")
    private String townName;
}

package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 喇叭设备导出类
 */
@Data
@ToString
public class HornDeviceVoExport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "设备编号")
    private String deviceCode;

    @Excel(name = "设备名称")
    private String deviceName;

    @Excel(name = "设备状态")
    private String deviceStatusName;

    @Excel(name = "设备厂家")
    private String manufacturerName;

    @Excel(name = "设备型号")
    private String modelCode;

    @Excel(name = "设备类型")
    private String deviceTypeName;

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

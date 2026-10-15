package com.chinatower.device.entity.vo;

import com.chinatower.common.annotation.Excel;
import lombok.Data;

/**
 * 铁塔视角导出
 */
@Data
public class RecorderTTExportVO {

    @Excel(name = "节点编号")
    private String locationCode;

    @Excel(name = "节点名称")
    private String locationName;

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

    @Excel(name = "设备地址")
    private String location;

    @Excel(name = "客户数")
    private String custCount;

    @Excel(name = "省公司")
    private String provinceDeptName;

    @Excel(name = "市公司")
    private String cityDeptName;
}

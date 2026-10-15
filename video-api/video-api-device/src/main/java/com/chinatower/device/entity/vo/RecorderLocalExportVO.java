package com.chinatower.device.entity.vo;

import com.chinatower.common.annotation.Excel;
import lombok.Data;

/**
 * 私有化导出
 */
@Data
public class RecorderLocalExportVO {

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

    @Excel(name = "经纬度")
    private String lngLati;

    @Excel(name = "接入方式")
    private String cascadeName;

    @Excel(name = "接入类型")
    private String collectTypeName;

    @Excel(name = "接入节点")
    private String collectNodeName;

    @Excel(name = "接入协议")
    private String accessProtocolsName;

    @Excel(name = "用户名")
    private String devUserName;

    @Excel(name = "密码")
    private String devPassword;
}

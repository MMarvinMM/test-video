package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 导出卡口设备数据VO
 */
@Data
@ToString
public class CheckpointDeviceExportVO implements Serializable {
    private static final long serialVersionUID = -1771199413525860923L;

    @Excel(name = "摄像机名称")
    private String deviceName;
    @Excel(name = "摄像机编号")
    private String deviceCode;
    @Excel(name = "通道名称")
    private String channelName;
    @Excel(name = "通道编号")
    private String channelCode;
    @Excel(name = "摄像机状态",readConverterExp = "0=在线,1=离线",type=Excel.Type.EXPORT)
    private String deviceStatus;
    @Excel(name = "省份")
    private String provinceName;
    @Excel(name = "地市")
    private String cityName;
    @Excel(name = "区县")
    private String countyName;

}

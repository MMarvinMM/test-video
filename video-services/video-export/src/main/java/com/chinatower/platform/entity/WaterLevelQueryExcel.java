package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;

/**
 * @ClassName: WaterLevelQueryExcel
 * @Author: wangyt
 * @CreateTime: 2024-12-15  14:25
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class WaterLevelQueryExcel {
    @Excel(name = "水位（cm）")
    private String waterLevel;
    @Excel(name = "采集时间")
    private String gatherTime;
    @Excel(name = "摄像机编号")
    private String deviceCode;
    @Excel(name = "摄像机名称")
    private String deviceName;
    @Excel(name = "摄像机状态",readConverterExp = "0=在线,1=离线",type=Excel.Type.EXPORT)
    private String deviceStatusName;
    @Excel(name = "摄像机地址")
    private String deviceAddress;
}

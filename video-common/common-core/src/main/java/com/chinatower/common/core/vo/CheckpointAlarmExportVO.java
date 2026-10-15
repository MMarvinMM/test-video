package com.chinatower.common.core.vo;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckpointAlarmExportVO extends BaseEntity {

    private static final long serialVersionUID = -3525457947128124277L;

    @Excel(name = "车牌号码")
    private String vehicleNumber;

    @Excel(name = "车辆类型")
    private String carType;

    @Excel(name = "区域")
    private String areaName;

    @Excel(name = "摄像机名称")
    private String deviceName;

    @Excel(name = "摄像机编号")
    private String deviceCode;

    @Excel(name = "通过时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

}

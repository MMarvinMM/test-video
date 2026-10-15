package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EnergyDeviceMonitorVO {

    private Date monitorTime;

    private List<EnergyDeviceMonitorSignalDataVO> signalDatas;
}

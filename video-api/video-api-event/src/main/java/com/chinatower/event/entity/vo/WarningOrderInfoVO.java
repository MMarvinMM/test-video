package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderInfoVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/9 10:00
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningOrderInfoVO{

    private OrderInfoVO order;

    private List<Map> imgs;

    private Map video;

    private List<Map> records;

    /**物联设备的检测因子*/
    private List<SensorMonitorVo> monitors;
    /**物联设备关联的摄像机*/
    private List<IotRelDeviceVo> reldevice;

}

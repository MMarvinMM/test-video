package com.chinatower.event.dataobject;

import com.chinatower.event.vo.IotFactorVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class IotAlarmConfigGroupsDo implements Serializable {

    /**
     * 主键
     */
    private String id;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date updateTime;

    private Integer deleteFlag;

    /**告警防抖类型 1:触发次数 2:持续超标 3:突然超标*/
    private String antiShakeType;

    /**配置组名称*/
    private String groupsName;

    /**告警防抖次数*/
    private String antiShakeNum;

    /**告警防抖时间*/
    private String antiShakeTime;

    /**告警防抖时间单位  时分秒*/
    private String antiShakeTimeUnit;

    /**物联因子关系 1:且  2:或*/
    private String relation;

    /**物联告警配置id(vm_iot_alarm_config_t主键 )*/
    private String iotConfigId;

    /**
     * 物联设备类型
     */
    private String deviceType;

    /**
     * 物联因子
     */
    private List<IotFactorVo> iotFactorVoList;

    /**
     * 物联设备
     */
    private List<IotAlarmDeviceDo> iotAlarmDeviceDoList;
}

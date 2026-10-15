package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import com.chinatower.common.core.entity.BasePlatformEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_sensor_monitor_config")
public class SenSorMonitorConfigDO extends BasePlatformEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 监测因子编码
     */
    private String monitorCode;

    /***
     * 监测因子名称
     */
    private String monitorName;

    /**
     * 监测因子单位
     */
    private String monitorUnits;

    /**
     * 监测因子对应告警类型
     */
    private Integer alarmType;


}

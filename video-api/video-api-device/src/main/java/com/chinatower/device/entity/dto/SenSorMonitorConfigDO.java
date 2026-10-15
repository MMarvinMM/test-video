package com.chinatower.device.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BasePlatformEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class SenSorMonitorConfigDO {

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

}

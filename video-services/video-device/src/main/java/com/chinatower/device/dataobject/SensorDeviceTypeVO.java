package com.chinatower.device.dataobject;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 传感器设备类型实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorDeviceTypeVO extends BaseDataScopeEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 设备类型编码
     */
    private String typeCode;

    /**
     * 设备类型
     */
    private String typeName;

    /**
     * 创建开始时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeBegin;
    /**
     * 创建结束时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    /**
     * 修改开始时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeBegin;
    /**
     * 修改结束时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;
}

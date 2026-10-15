package com.chinatower.device.entity.vo;

import lombok.Data;

/**
 * 传感器监测事件实体类
 */
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SensorEventVO implements Serializable {


    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 事件id
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;


    /**
     * 文件列表
     */
    private List<FileInfoVO> file;

    /**
     * 拓展参数
     */
    private List<Map<String,String>> extraAttribute;

    /**
     * 拓展参数名称
     */
    private String extraAttributeName;


    /**
     * 事件类型名称
     */
    private String eventTypeName;


    /**
     * 监测时间
     */
    private String monitorTime;


}

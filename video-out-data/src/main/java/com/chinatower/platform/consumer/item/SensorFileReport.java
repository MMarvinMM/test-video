package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

import java.util.List;

@Data
public class SensorFileReport extends BaseMongoDTO {
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 监测时间 格式yyyy-MM-dd HH:mm:ss
     */
    private String monitorTime;

    /**
     * 事件id
     */
    private String relationId;

    /**
     * 关联的事件id
     */
    private List<SensorFileItem> files;
}

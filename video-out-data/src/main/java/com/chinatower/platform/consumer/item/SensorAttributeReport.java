package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

import java.util.Map;

/**
 * 传感器监测数据对象
 * Created by zhujun on 2022/3/30.
 */
@Data
public class SensorAttributeReport extends BaseMongoDTO {
    // 设备编码
    private String deviceCode;
    // 监测时间 格式yyyy-MM-dd HH:mm:ss
    private String monitorTime;
    //时间频次 null-实时 1-分 2-时 3-天
    private String timeFrequency;
    /**
     * 接入节点
     */
    private String accessNode;
    // 监测因子
    private Map<String, Object> monitor;

    public SensorAttributeReport() {
        this.collectionName = "sensor_attribute_report";
    }
}

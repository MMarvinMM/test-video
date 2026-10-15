package com.chinatower.device.mongodb.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * 监测因子
 */
@Data
@Document(collection = "sensor_attribute_report")
public class SensorAttrValueDTO extends BaseMongoDTO {

    // 设备编码
    private String deviceCode;
    // 监测时间 格式yyyy-MM-dd HH:mm:ss
    private String monitorTime;
    // 监测因子
    private Map<String, Object> monitor;

    public SensorAttrValueDTO() {
        this.collectionName = "sensor_attribute_report";
    }

}

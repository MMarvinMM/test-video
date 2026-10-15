package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SensorEventReport extends BaseMongoDTO {

    /**
     * 事件唯一ID(全国平台、节点唯一标识)
     */
    private String eventId;

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
     * 格式 yyyy-MM-dd HH:mm:ss
     */
    private String monitorTime;

    /**
     * 事件类型 1-噪声超上限 2-噪声超下线 3-虫情算法识别
     */
    private Integer eventType;

    /**
     * 额外属性值
     */
    private List<Map<String, String>> extraAttribute;
}

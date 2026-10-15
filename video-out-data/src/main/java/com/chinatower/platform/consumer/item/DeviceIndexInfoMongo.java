package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lzt
 */
@Data
@Document(collection="device_index_info")
public class DeviceIndexInfoMongo extends BaseMongoDTO implements Serializable {

    private String id;

    /**
     * 设备编码
     */
    private String deviceCode;
    
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 上报时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportTime;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 水质扩展字段
     */
    private String extendField;

    /**
     * 1：水质指标数据  2：水位指标数据
     */
    private String type;

    private String msgId;

}

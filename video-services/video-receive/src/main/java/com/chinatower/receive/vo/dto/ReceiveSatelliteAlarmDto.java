package com.chinatower.receive.vo.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName ReceiveSatelliteAlarmVo
 * @Deacription 卫星火情报警接收参数
 * @Author guyuefei
 * @Date 2022-05-12 09:49:20
 * @Version 1.0
 */
@Data
@ApiModel(value = "卫星告警对象",description = "卫星告警对象")
@Document(collection="vm_order_satellite_t")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiveSatelliteAlarmDto extends BaseMongoDTO {

    /** 告警类型 */
    private String alarmType;
    /** 告警时间 */
    private String alarmTime;
    /** 事件标题 */
    private String alarmBody;
    /** 设备编码 */
    private String deviceCode;
    /** 设备名称 */
    private String deviceName;
    /** 通道编码 */
    private String channelCode;
    /** 通道名称 */
    private String channelName;
    /** 告警编码 */
    private String alarmCode;
    /** 事件源经度 */
    private String longitude;
    /** 事件源纬度 */
    private String latitude;
    /** 火点位置 */
    private String address;
    /** 省 */
    private String province;
    /** 市 */
    private String city;
    /** 县 */
    private String county;
    /** 区 */
    private String street;
    /** 告警来源 */
    private String alarmSource;
    /** 置信度数组 */
    private String confidenceLevel;
    /** 告警等级 */
    private String importanceLevel;
    /**
     * 拓展字段
     */
    private String extendField;

    /**
     * 上报时间
     */
    private String reportingTime;

    /**
     * 推送时间
     */
    private String pushTime;

    /**
     * 是否推送客户（0：不推送  1：推送）
     */
    private String pushCustomerFlag;

}
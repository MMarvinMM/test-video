package com.chinatower.event.entity.vo;

import com.chinatower.common.core.vo.BaseAlarmVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ReceiveSatelliteAlarmVo
 * @Deacription 卫星火情报警接收参数
 * @Author guyuefei
 * @Date 2022-05-12 09:49:20
 * @Version 1.0
 */
@Data
public class ReceiveSatelliteAlarmApiVo extends BaseAlarmVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 告警类型 */
   /* private String alarmType;
    *//** 告警时间 *//*
    private String alarmTime;
    *//** 事件标题 *//*
    private String alarmBody;
    *//** 设备编码 *//*
    private String deviceCode;
    *//** 设备名称 *//*
    private String deviceName;
    *//** 通道编码 *//*
    private String channelCode;
    *//** 通道名称 *//*
    private String channelName;
    *//** 告警编码 *//*
    private String alarmCode;
    *//** 事件源经度 *//*
    private String longitude;
    *//** 事件源纬度 *//*
    private String latitude;*/
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
    private String dataSource;
    /** 扩展字段属性*/
    private SatelliteExtendFieldApiVo extendField;
    /** 告警来源 */
    private String alarmSource;
    /** 置信度数组 */
    private String confidenceLevel;

    /**
     * 是否推送客户（0：不推送  1：推送）
     */
    private String pushCustomerFlag;

    private String localNode;

}

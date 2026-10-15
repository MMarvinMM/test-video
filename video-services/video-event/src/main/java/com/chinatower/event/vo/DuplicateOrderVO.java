package com.chinatower.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: DuplicateOrderVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/6/8 13:55
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class DuplicateOrderVO implements Serializable {

    /**
     * 告警内容
     */
    private String alarmBody;
    /**
     * 事件ID
     */
    private String orderId;
    /**
     * 告警类型编码
     */
    private String warningTypeId;
    /**
     * 形成告警时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;
    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatusName;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;

    private String warningSourceName;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 事件描述
     */
    private String description;

    /**
     * 备注
     */
    private String remark;
    /**
     * 事件发现人姓名
     */
    private String findName;

    /**
     * 事件发现人电话
     */
    private String findMobile;
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;
    /**
     * 原图
     */
    private String imgUrl;
    /**
     * 事件等级
     */
    private String emergencyLevel;

    /**
     * 事件等级名称
     */
    private String emergencyLevelName;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String createDate;
    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String reciveTime;

    /**
     * 处理人名称
     */
    private String processName;
    /**
     * 环节时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String flowNodeTime;

    /**
     * 时限
     */
    private String timeLimit;

    /**
     * 处理人ID
     */
    private String processId;

    /**
     *
     */
    private String groupId;
    /**
     * 省份编码
     */
    private String provinceId;

    /**
     * 地市编码
     */
    private String cityId;
    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 所在市名称
     */
    private String cityName;
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 重复告警ID
     */
    private String duplicateOrderId;

    private String checkTime;

    private String checkMan;
}

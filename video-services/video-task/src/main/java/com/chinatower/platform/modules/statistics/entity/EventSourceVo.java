package com.chinatower.platform.modules.statistics.entity;

import lombok.Data;

/**
 * @className: EventSourceVo
 * @description:
 * @author: yongqi.han
 * @date: 2022/6/23
 * @requestID 需求单号及需求名称
 */
@Data
public class EventSourceVo {
    private static final long serialVersionUID = 2704796666613104862L;

    //主键
    private String id;
    //省编码
    private String provinceId;
    //市编码
    private String cityId;
    //区县编码
    private String countyId;
    //乡镇编码
    private String townId;
    //客户ID
    private String tenantId;
    //小组ID
    private String groupId;
    //设备编号
    private String deviceCode;
    //告警时间
    private String alarmTime;
    //告警来源
    private String warningSource;
    //告警来源名称
    private String warningSourceName;
    //设备名称
    private String deviceName;
    //告警状态
    private String orderStatus;
    //工单数量
    private String orderNum;
    private String date;

    private String yearStr;
    private String monthStr;
    private String dayStr;
    private String handleDateTime;
    //应用版本
    private String appVerCode;

    //组织ID
    private String orgId;
    //行业编码
    private String industryCode;
    /**
     * 真警数量
     */
    private int realNum;

    /**
     * 重复告警数量
     */
    private int repeatCompleteNum;

    /**
     * 误报数量
     */
    private int falseNum;

    /**
     * 网格ID
     */
    private String gridId;
}

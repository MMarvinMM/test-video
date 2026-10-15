package com.chinatower.event.entity.vo;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;

/**
 * 导出回收站
 */
@Data
@ToString
public class AlarmInterceptExport {
    private static final long serialVersionUID = 1L;

    //@Excel(name = "客户ID")
    private String tenantId;

    //@Excel(name = "客户名称")
    private String tenantName;

    @Excel(name = "摄像机编号")
    private String deviceCode;

    @Excel(name = "摄像机名称")
    private String deviceName;

    @Excel(name = "摄像机标签")
    private String deviceLabelString;

    @Excel(name = "通道编号")
    private String channelCode;

    @Excel(name = "通道名称")
    private String channelName;

    @Excel(name = " 预置位名称")
    private String presetName;

    @Excel(name = " 预置位ID")
    private String presetId;

    @Excel(name = " PTZ(原始)")
    private String ptzInitial;

    @Excel(name = " PTZ(转义)")
    private String ptzTrans;

    @Excel(name = "事件编号")
    private String warningOrderId;

    @Excel(name = "事件标题")
    private String alarmBody;

    @Excel(name = "告警类型")
    private String warningTypeName;

    @Excel(name = "告警原因")
    private String alarmReason;

    //@Excel(name = "事件等级")
    private String emergencyLevelName;

    private String emergencyLevel;

    @Excel(name = "告警等级")
    private String importanceLevelName;

    @Excel(name = "告警来源")
    private String warningSourceName;

    private String warningSource;

    @Excel(name = "告警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    @Excel(name = "事发地点")
    private String address;

    @Excel(name = "经纬度")
    private String longitudeAndLatitude;

    @Excel(name = "省份")
    private String provinceName;

    @Excel(name = "地市")
    private String cityName;

    @Excel(name = "区县")
    private String countyName;

    @Excel(name = "乡镇")
    private String townName;

    @Excel(name = "所属网格")
    private String gridName;

    @Excel(name = "所属组织")
    private String orgName;

    @Excel(name = "事件描述")
    private String description;

    @Excel(name = "告警压制原因")
    private String interceptName;

    /**
     * AI告警来源（设备告警/设备告警（二次研判为假）/设备告警（二次研判为真）/算法告警） 通知类型 1-设备告警；2-算法告警
     */
    //@Excel(name = "AI告警来源")
    private String noticeResourceName;

    /**
     * 是否二次研判 1-是；0-否
     */
    @Excel(name = " 是否二次研判")
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
    @Excel(name = " 二次研判结果")
    private String secondJudgeResult;

    /** 原图 */
    //@Excel(name = "可见光原图url")
    private String imgUrl;

    /** 框图 */
    //@Excel(name = "可见光框图url")
    private String fileImgUrlIcon;

    /** 热成像原图 */
    //@Excel(name = "热成像原图url")
    private String frontAlarmPicUrl;

    /*@Excel(name = "事件描述")
    private String description;*/

    @Excel(name = "办理说明")
    private String remark;

}

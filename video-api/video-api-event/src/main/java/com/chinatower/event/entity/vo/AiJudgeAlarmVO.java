package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI研判告警卡片VO
 * 用于展示单个告警信息
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Data
public class AiJudgeAlarmVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警工单ID
     */
    private String warningOrderId;

    /**
     * 告警图片URL
     */
    private String imgUrl;

    /**
     * 告警视频URL
     */
    private String videoUrl;

    /**
     * 告警位置/区域
     */
    private String provinceName;
    private String cityName;
    private String countyName;

    /**
     * 告警时间
     */
    private String alarmTime;

    /**
     * AI研判分数
     */
    private Double aiJudgeScore;

    /**
     * AI研判结果
     */
    private String aiJudgeResult;

    /**
     * AI研判结果描述
     */
    private String aiJudgeDesc;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 告警类型ID
     */
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    private String warningTypeName;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 任务执行记录
     */
    private Long taskExecutionId;

    /**
     * 创建时间
     */
    private Date createTime;

}

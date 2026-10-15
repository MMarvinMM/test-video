package com.chinatower.event.entity.dto;

import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import lombok.Data;

import java.util.List;

/**
 * 无人机告警上报请求实体
 * @Author: lyy
 * @Date: 2024/5/6 15:45
 * @version v1.7.00
 */

@Data
public class ReceiveUavAlarmVO {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 告警时间
     */
    private String alarmTime;

    /**
     * 航线ID
     */
    private String flightRouteId;

    /**
     * 飞行任务ID
     */
    private String taskRecordId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 二次分析结果
     */
    private String secondJudgeResult;

    /**
     * 二次分析图片
     */
    private String originalPictureUrl;

    /**
     * 二次分析框图Url
     */
    private String alarmPictureUrl;

    /**
     * 二次分析视频
     */
    private String alarmVideoUrl;

    /**
     * 报警区域
     */
    private List<AlarmOriginalAreaVO> alarmArea;

    /**
     * 扩展字段
     */
    private Object extendField;

    /**
     * 字段透传
     */
    private String extraFieldInfo;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 告警等级
     */
    private String importanceLevel;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    //接入节点
    private String accessNode;

    //受理节点
    private String acceptNode;
}

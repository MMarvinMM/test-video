package com.chinatower.video.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class QueryRecordPlanData {
    /**
     * 是否全程录像
     * @required true
     */
    private int isAllDay;
    /**
     * 通道录像计划时间段
     * @required false
     */
    private List<QueryRecordTimeData> timeSections;
    /**
     * 码流类型
     * @required true
     */
    private int streamType;
    /**
     * 存储类型
     * @required true
     */
    private int storageType;
    /**
     * 存储时长
     * @required true
     */
    private int storageDay;
    /**
     * 是否开启音频
     * @required true
     */
    private int isAudio;
    /**
     * 录像计划id
     * @required true
     */
    private String recordPlanId;
    /**
     * 录像计划名称
     * @required true
     */
    private String recordPlanName;
    /**
     * 录像计划状态
     * @required true
     */
    private int recordPlanStatus;
    /**
     * 生效开始日期
     * @required true
     */
    private String startDate;
    /**
     * 生效结束日期
     * @required true
     */
    private String endDate;

    /**
     * 受理节点
     * @required true
     */
    private String acceptNode;

    /**
     * 接入节点
     * @required true
     */
    private String accessNode;


    @Data
    public static class QueryRecordPlanDataA extends CommonResultEntiry {
        private List<QueryRecordPlanData> data;
    }
}

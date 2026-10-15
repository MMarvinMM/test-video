package com.chinatower.video.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class QueryPatrolData {
    /**
     * 是否正在巡航
     * @required true
     */
    private int isCruise;
    /**
     * 预置点列表
     * @required true
     */
    private List<QueryPatrolListData> presets;
    /**
     * 巡航名称
     * @required true
     */
    private String patrolName;
    /**
     * 巡航路径Id
     * @required true
     */
    private long patrolId;
    private String creator;
    private String creatorDate;
    private String startStop;
    /**
     * 1.7.20之前的版本巡航时段
     */
    private String[] startTimePeriodArr;
    /**
     * 1.7.20版本巡航时段
     */
    private String[] startTimePeriodArrNew;
    private String startTimePeriod;
    private String modifier;
    private String updateTime;
    private String channelName;
    private String deviceName;
    private String deviceCode;
    private String channelCode;
    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;

    private String operator;

    private String operationTime;

    @Data
    public static class QueryPatrolDataEntity extends CommonResultEntiry {
        private List<QueryPatrolData> data;
    }

}

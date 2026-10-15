package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class VideoQueryPatrolData extends VideoBaseReq{
    private Long patrolId;

    //开始时间(格式：yyyy-MM-dd）
    private String startTime;

    //结束时间(格式：yyyy-MM-dd）
    private String endTime;

    //开始时间(格式：yyyy-MM-dd）
    private String createStartTime;

    //结束时间(格式：yyyy-MM-dd）
    private String createEndTime;

    //开始时间(格式：yyyy-MM-dd）
    private String updateStartTime;

    //结束时间(格式：yyyy-MM-dd）
    private String updateEndTime;

    //巡航计划名称
    private String patrolName;

    //摄像机名称
    private String deviceName;

    //通道名称
    private String channelName;

    //启动状态（启动中/未启动）
    private String startStop;

    //执行状态（执行中/未执行）
    private String isCruise;
    
    private int pageNum; //页码

    private int pageSize; //每页条数

    //更新开始日期
    private String updateStartDate;

    //更新结束日期
    private String updateEndDate;

    //createTime updateTime
    private String orderBy;

    //asc desc
    private String sort;

    /**
     * 是否兼容app旧版本 0：否，1：是
     */
    private String ifAppOld;
}

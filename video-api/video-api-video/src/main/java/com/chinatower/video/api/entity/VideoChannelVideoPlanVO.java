package com.chinatower.video.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class VideoChannelVideoPlanVO extends VideoBaseReq{

    //是否全程录像 默认为0；1-是；0-否；如果为1，timeSections省略；
    private int isAllDay;

    //通道录像计划时间段 当isAllDay字段为1时必填；
    private List<VideoTimeSectionVO> timeSections;

    //码流类型 1-主码流；0-子码流；
    private int streamType;

    //存储类型 1-中心存储；0-前端存储；
    private int storageType;

    //存储时长 录像存储的最长周期 单位:天
    private int storageDay;

    //是否开启音频 1-是；0-否；
    private int isAudio;

    //录像计划id
    private String recordPlanId;

    //录像计划名称
    private String recordPlanName;

    //录像计划状态
    private int recordPlanStatus;

    //生效开始日期
    private String startDate;

    //生效结束日期
    private String endDate;


    //客户唯一编码
    private String customerCode;

    //用户编码
    private String userCode;

    private String packageCode;

    //租户id
    private String tenantId;


    //日期，取值1-7(对应周一周日）
    private int dayOfWeek;

    //录像开始时间，格式：HHmmss，范围[000000~235959]
    private String startTime;

    //录像结束时间，格式：HHmmss，范围[000000~235959]
    private String endTime;

    private String matchFlag;

    private int[] recordPlanIds;


    private String creator;

    private String creatorDate;

    private String modifier;

    private String modifierDate;

    private String queryFlag;//flag为1 查询该租户下所有通道计划列表

    //设备名称
    private String devName;

    //通道名称
    private String channelName;

    //铁塔视角
    private String source;

    private List<String> tenantList;//租户ID列表

    private String provinceId;

    private String provinceName;

    private String cityId;

    private String cityName;

    private String tenantName;

    private String keyword;

    private int ordStatus;

    private List<String> devIdList;

    private int pageNum; //页码

    private int pageSize; //每页条数

    private String creatorName;

    private String modifierName;

    private String createTimeStart;

    private String createTimeEnd;

    private String updateTimeStart;

    private String updateTimeEnd;

    private String recordPlanStatusStr;

}
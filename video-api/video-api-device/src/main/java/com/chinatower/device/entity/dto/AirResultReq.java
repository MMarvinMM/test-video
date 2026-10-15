package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/4 14:37
 */
@Data
public class AirResultReq extends PageDTO {
    /**
     * 成果物ID
     * */
    private List<String> id;

    private List<CollectReq> collectIds;

    private List<String> picType;

    private String startTime;

    private String endTime;

    /**
     * 任务ID
     * */
    private String taskRecordId;
    /**
     * 任务ID列表
     */
    private List<CollectReq> taskRecordIds;
    /**
     * 视频ID列表
     */
    private List<CollectReq> videoIds;
    /**
     * 图片ID列表
     */
    private List<CollectReq> photoIds;
    /**
     * 授理节点
     * */
    private String acceptNode;

    private String accessNode;

    /**
     * photo-图片
     * video-视频
     * */
    private String dataType;

    private String userId;

    private String isReFly;

    private String reFlyName;

    private String secretJson;

    private String isSecret;

    private String photoId;

    private String uavCode;

    private String queryPhotoId;

    private String deviceCode;

    private String createStartTime;

    private String createEndTime;

    private String originalTaskRecordId;

    private String queryName;

    private String longitude;
    private String latitude;
    private String altitude;
    private String height;
    private List<String> algorithmCodes;
    private List<String> alarmCodes;
    private List<String> picSeIds;

    private String isWatermark;

    private String remark;

    /**
     * 查询类型
     * NEST - 机巣
     * UAV - 无人机
     */
    private String deviceCodeType = "NEST";
}

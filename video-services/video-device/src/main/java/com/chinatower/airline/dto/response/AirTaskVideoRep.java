package com.chinatower.airline.dto.response;

import com.chinatower.event.constants.Constants;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 17:32
 */
@Data
public class AirTaskVideoRep {
    // 飞行任务id
    private String taskRecordId;

    //航线ID
    private String flightRouteId;

    // 视频id
    private String id;

    // 视频名称
    private String name;

    // 视频别名
    private String alias;

    // 视频链接
    private String videoUrl;

    // 视频访问地址
    private String videoAccessUrl;

    // 视频大小
    private Long videoSize;

    // 视频类型
    private Integer type;

    // 架次id
    private Integer missionId;

    // 任务架次记录id
    private Integer missionRecordsId;

    // 创建时间
    private String createTime;

    // 修改时间
    private String modifyTime;

    private List<AirTaskVideoPointRep> airTaskVideoPointReps;

    private String taskName;

    private String deviceName;

    private String deviceCode;

    private String planId;

    //0 1 2 未识别 无告警 有告警
    private Integer eventStatus;

    // 纬度
    private Double latitude;

    // 经度
    private Double longitude;

    private Double altitude;

    /**
     * 重复飞行状态：0-未飞行，1-已飞行
     */
    private String reFlyStatus = Constants.ZERO;
    /**
     * 采集状态：0-未收藏，1-已收藏
     */
    private String collectStatus = Constants.ZERO;
    /**
     * 是否主任务：0-主任务，1-复飞任务
     */
    private String isMainTaskStatus = Constants.ZERO;

    /**
     * 视频第一帧图片地址
     */
    private String videoShotImageUrl;
}

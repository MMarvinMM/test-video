package com.chinatower.message.station.entity;

import com.chinatower.message.api.model.WebSocketMsgType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author WLF
 * @description
 * @date 2024/12/2 16:01
 */
@Data
public class UavVideoFrameMsg implements Serializable {
    private String id; // 抽帧任务id
    private String extractTime; // 抽帧时间
    private Integer count; // 已抽帧数量
    private Integer totalCount; // 总抽帧数量
    private Integer state; // 状态
    private String videoId; // 视频id
    private String videoName; // 视频名称
    private long timestamp; // 上报时间
    private String acceptNode; //
    private final WebSocketMsgType webSocketMsgType = WebSocketMsgType.UAV_VIDEO_TASK;


    /**
     * 行业
     */
    private String industryCode;

    /**
     * 租户ID
     */
    private String tenantId;
}

package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 喊话相关接口入参实体类
 */
@Data
public class TalkV2Req implements Serializable {

    /**
     * 广播类型 固定值：WEB_STREAM
     */
    private String broadcastType;

    /**
     * 是否录音
     */
    private Integer isRecord;

    /**
     * 优先级 广播优选级，数字越大，优选级越小
     */
    private Integer priority;

    /**
     * 音量 0~10数值越大，音量越大
     */
    private Integer volume;

    /**
     * 音频数据编码类型
     */
    private Integer streamCodeId;

    /**
     * 是否录音
     */
    private List<HornRangeInfoDTO> rangeInfoList;

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 应急标识
     */
    private Boolean emergency;
}

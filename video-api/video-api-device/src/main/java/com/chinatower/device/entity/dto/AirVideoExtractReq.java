package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author lyy
 * @description 视频抽帧请求入参
 * @date 2024/12/02 17:51
 */
@Data
public class AirVideoExtractReq {
    /**
     * 抽帧的视频ID
     * */
    private String id;

    /**
     * 抽帧的视频名称
     */
    private String videoName;

    /**
     * 抽帧频次（秒/帧，默认展示1）
     * */
    private String frequency;

    /**
     * 抽帧任务名称
     * */
    private String taskName;

    /**
     * 视频时长
     */
    private String videoDuration;

    /**
     * 预计的抽帧图片数量
     */
    private Integer extractCount;

    /**
     * 受理节点
     * */
    private String acceptNode;

    private String accessNode;
}

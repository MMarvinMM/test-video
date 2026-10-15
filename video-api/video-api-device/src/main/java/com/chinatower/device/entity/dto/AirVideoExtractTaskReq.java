package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author lyy
 * @description 抽帧任务请求入参
 * @date 2024/12/03 16:11
 */
@Data
public class AirVideoExtractTaskReq {

    /**
     * 飞行任务ID
     */
    private String taskRecordId;

    /**
     * 抽帧任务ID
     */
    private String taskId;

    /**
     * 抽帧的视频ID
     * */
    private String videoId;

    /**
     * 抽帧任务ID集合
     */
    private List<String> taskIds;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 受理节点
     * */
    private String acceptNode;
}

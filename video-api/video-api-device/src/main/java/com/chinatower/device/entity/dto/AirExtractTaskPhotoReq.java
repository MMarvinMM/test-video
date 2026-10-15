package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author lyy
 * @description 删除抽帧任务图片请求入参
 * @date 2024/12/03 19:11
 */
@Data
public class AirExtractTaskPhotoReq {

    /**
     * 抽帧任务图片id集合
     */
    private List<String> extractionPhotoIds;

    /**
     * 抽帧任务ID集合
     */
    private List<String> extractionTaskIds;

    /**
     * 受理节点
     * */
    private String acceptNode;

    private String accessNode;
}

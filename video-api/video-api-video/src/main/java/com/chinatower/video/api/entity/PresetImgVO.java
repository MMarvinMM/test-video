package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class PresetImgVO {
    //单次识别任务id
    private String taskId;
    //单次识别告警编码
    private String warningOrderId;
}

package com.chinatower.bdm.api.entity;

import lombok.Data;

@Data
public class AlgorithmPicResultVO {
    private Long taskInfoId;
    private String layerId;
    private String baselinePicUrl;
    /**
     * 二次识别结果
     */
    private String secondRecogResult;

}

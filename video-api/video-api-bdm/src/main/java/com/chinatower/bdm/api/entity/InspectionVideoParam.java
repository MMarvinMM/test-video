package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionVideoParam implements Serializable {
    private String fileCode;
    private String fileName;
    private String startTime;
    private String endTime;
    private String recordType;
    private String fileVideoUrl;
    private String coverUrl;//app用到 缩略图
}

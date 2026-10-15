package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class QueryVideoListData {
    private String fileName;
    private String startTime;
    private String endTime;
    private int recordType;
    private String fileCode;
    private String fileImageUrl;
    private String fileSize;
}

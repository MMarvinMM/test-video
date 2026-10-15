package com.chinatower.airline.dto.response;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/3/18 19:10
 */
@Data
public class QueryTaskPhotoVideoRep{
    private List<String> taskRecordIds;
    private String taskRecordsId;
    private String originalTaskRecordId;
    private Integer type; //0:照片，1：视频
    private String fileUrl;
    private String mediaId;
    private String timeCreated;
    private String deviceCode;
    private String accessNode;
    private String createStartTime;
    private String createEndTime;
    private String flightRouteId;
    private String name;
    private String taskRecordName;
    private String photoType;
    private List<String> pointList;
}

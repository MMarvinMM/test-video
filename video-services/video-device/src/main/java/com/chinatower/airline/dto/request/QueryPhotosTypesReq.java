package com.chinatower.airline.dto.request;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/7/15 14:52
 */
@Data
public class QueryPhotosTypesReq extends PageDTO {
    private String type;
    private String deviceCode;
    private String isCollect;
    private String timeCreatedStartTime;
    private String timeCreatedEndTime;
    private String taskRecordId;
    private String correlationId;
    private Integer pageNum;
    private Integer pageSize;
    private String presetId;
    private String channelCode;
    private String userId;
    private String industryCode;
    private String sort;
    private String accessNode;
}

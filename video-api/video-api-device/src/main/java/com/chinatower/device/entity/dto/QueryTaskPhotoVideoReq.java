package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/3/18 19:10
 */
@Data
public class QueryTaskPhotoVideoReq extends PageDTO {
    private List<String> taskRecordIds;
    private String taskRecordId;
    private List<Integer> type; //0:照片，1：视频
    private String deviceCode;
    private String accessNode;
    private String createStartTime;
    private String createEndTime;
    private String sort; //0:倒序，1：正序
    private String planStartTime;
    private String planEndTime;
}

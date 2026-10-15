package com.chinatower.device.entity.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

import java.util.Date;

/**
 * @author WLF
 * @description
 * @date 2024/11/19 18:05
 */
@Data
public class AirAlTaskLogReq extends BaseMongoDTO {
    private String id;
    private String taskId;
    private String taskJson;
    private String returnCode;
    private String returnMsg;
    private String createTime;
    private String errMsg;
    private String accessNode;
    private String acceptNode;
}

package com.chinatower.sync.consumer.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author WLF
 * @description
 * @date 2024/11/8 16:54
 */
@Data
public class UavStatusRequest {
    private String taskRecordId;

    private String deviceCode;

    private String uavDeviceCode;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    /**
     * 执行状态  0待执行 1执行成功 2执行失败 3执行中 4航线断点(业务状态) 5返航中(挂载逻辑处理需要，不入库)
     */
    private String execStatus;

    private String mediaSyncStatus;

    private String acceptNode;

    private String flyTime;

    private String errorReason;

    private BreakPointReq breakPoint;

    private String breakPointReqJson;

    private String waypointIndex;

    private String waypointState;


}

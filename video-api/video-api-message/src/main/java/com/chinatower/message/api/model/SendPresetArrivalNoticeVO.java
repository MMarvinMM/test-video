package com.chinatower.message.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendPresetArrivalNoticeVO implements Serializable {
    private String channelCode;

    private String deviceCode;

    private String tenantId;

    private String userId;

    private String industryCode;

    private Integer presetId;

    /**
     * 3：预置位到达通知
     */
    private Integer type;
}
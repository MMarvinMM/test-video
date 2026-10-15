package com.chinatower.outdata.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExtendInfo {
    private String algorithmCode;

    private String batchId;

    private String msgId;

    private String taskId;
}

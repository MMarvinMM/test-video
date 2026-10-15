package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelStorageTimeReq extends VideoBaseReq{

    private String recordPlanId;

    private String packageCode;

    private String storageTime;

    private String orderCode;

    private String storageDay;
}

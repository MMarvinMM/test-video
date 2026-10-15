package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CasCadeVO {
    /**
     * 当前节点编码
     */
    private String deviceCode;

    /**
     * 当前节点编码
     */
    private String channelCode;

    /**
     * 当前节点编码
     */
    private String platformCode;

    /**
     * 目标节点编码
     */
    private String cascadeToCode;
}

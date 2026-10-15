package com.chinatower.video.modules.videorecordplan.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoScreenConfigChannelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;

    private Long screenConfigId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    private Integer deleteFlag;

    private Date createTime;

    private Date updateTime;



}

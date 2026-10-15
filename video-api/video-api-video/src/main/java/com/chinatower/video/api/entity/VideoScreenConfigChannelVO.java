package com.chinatower.video.api.entity;

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

    /**
     * 删除标识 正常0  删除1
     */
    private Integer deleteFlag;

    private Date createTime;

    private Date updateTime;

}

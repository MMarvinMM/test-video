package com.chinatower.video.api.entity;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: VideoRequestDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/18 11:35
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
@ToString
@Alias("VideoRequestDTOVideo")
public class VideoRequestDTO {
    //设备编码
    @NotNull(message = "设备编码不能为空")
    private String deviceCode;

    //通道编码
    @NotNull(message = "通道编码不能为空")
    private String channelCode;

    //告警事件id
    @NotNull(message = "alarmId不能为空")
    private String alarmId;
}

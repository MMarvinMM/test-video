package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerGetVideoRealtimeUrlReq extends VideoBaseReq{
    /**
     * 协议类型
     *  1-rtsp；2-rtmp；3-hls；4-http-flv；5-wss； 6-铁塔私协
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_PROTOCOL_TYPE)
    private int protocolType;
    /**
     * 码流类型
     *  1-主码流；2-子码流
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_STREAM_TYPE)
    private int streamType;
    /**
     * 网域类型
     *  拉流url的网域类型：1-公网(默认值为1)；0-局域内网；
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_NET_TYPE)
    private String netType;
    /**
     * 视频流的唯一编码，对流进中断/限制带宽等操作
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_STREAM_UUID)
    private String streamUuid;
    /**
     * 插件id
     *  播放插件唯一标识，32位uuid；当protocolType=6，即使用铁塔私协获取url时，此项必填；
     * */
    private String plugId;
}

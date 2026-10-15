package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerGetAudioChannelReq extends VideoBaseReq{

    /**
     * 播放插件唯一标识，32位uuid
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_PLUG_ID)
    private String plugId;
    /**
     * 网域类型
     *  拉流url的网域类型：1-公网(默认值为1)；0-局域内网；
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_NET_TYPE)
    private int netType;

    // 请求来源 0:pc端  1:app端
    private int reqFrom;
    
    // 协议类型 5-ws； 6-铁塔私协；10-rtp（tcp）
    private int protocolType;
}

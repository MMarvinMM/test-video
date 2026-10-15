package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author lyy
 * @description 无人机机巢通道信息
 * @date 2025/8/27 18:50
 */
@Data
public class UavNestChannelResp {
    private String deviceCode;
    private List<ChannelDTO> channelList;

    @Data
    public static class ChannelDTO {
        private String channelCode;
        //通道类型（0巢内、1巢外）
        private String channelType;
        //流地址
        private String streamUrl;
        //视频类型（0-原始流、1-AI识别流，默认为0）
        private String videoType;
    }
}

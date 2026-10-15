package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class GetAudioChannelData {
    private String streamUrl;

    @Data
    public static class PGetAudioChannelDataEntity extends CommonResultEntiry {
        private GetAudioChannelData data;
    }
}

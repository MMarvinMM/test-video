package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class GetVideoPlaybackUrlData {
    /**
     * 流url地址
     * @required true
     */
    private String streamUrl;
    /**
     * 本次流的UUID
     * @required false
     */
    private String streamUuid;

    private String streamControlId;

    @Data
    public static class GetVideoPlaybackUrlDataEntity extends CommonResultEntiry {
        private GetVideoPlaybackUrlData data;
    }
}

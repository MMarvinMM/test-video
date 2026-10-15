package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DoSetChannelParamReq extends VideoBaseControlReq{
    private String channelName;
    private int streamType;
    private int stream;
    private int interFrameSpqcing;
    private int intelligentCodec;
    private int resolution;
}

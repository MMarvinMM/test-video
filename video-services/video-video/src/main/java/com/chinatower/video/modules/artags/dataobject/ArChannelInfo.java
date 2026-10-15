package com.chinatower.video.modules.artags.dataobject;

import lombok.Data;

@Data
public class ArChannelInfo {
    private String deviceCode;
    private String channelCode;
    private String channelName;
    //外型 1-球机；2-半球；3-枪机
    private String facade;
}

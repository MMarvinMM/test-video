package com.chinatower.sync.consumer.entity;

import lombok.Data;

/**
 * Created by zheng.yan on 2022/1/27.
 */
@Data
public class ChannelStatusReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道状态，0在线，1离线
     */
    private Integer channelStatus;

}

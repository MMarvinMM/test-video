package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: PlayerGetVideoPlayBackUrlData
 * @Description: 获取通道实时放流URL地址返回实体
 * @Author: ldl
 * @Date: 2022/1/18
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerGetVideoRealtimeUrlData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 回放流url地址（公网地址）
     * @required true
     * */
    private String streamUrl;
    /**
     * 本次流ID , 长度限制32字符 , 不传或传空平台自动生成
     * */
    private String streamUuid;
    /**
     * 码流类型 1-主码流；2-子码流
     */
    private int streamType;
    /**
     * 切换主子码流 2-不可切换；
     */
    private int streamSwitchFlag;

    private String deviceCode;

    private String channelCode;

    private String returnMsg;

    private int protocolType;

    private String netType;

    private String plugId;

    @Data
    public static class PlayerGetVideoRealtimeUrlDataA extends CommonResultEntiry {
        private PlayerGetVideoRealtimeUrlData data;
    }
}

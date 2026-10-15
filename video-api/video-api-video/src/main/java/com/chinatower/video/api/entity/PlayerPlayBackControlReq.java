package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerPlayBackControlReq extends VideoBaseReq{

    /**
     * 倍速回放流控制标识
     *  倍速回放流控制标识,在获取录像回放地址时返回
     *  @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_STREAM_CONTROL_ID)
    private String streamControlId;
    /**
     * 控制类型
     *  1：暂停，2：播放，3:倍速
     *  @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_CONTROL_TYPE)
    private int controlType;
    /**
     * 播放速度
     *  当controlType为3时，必传，取之范围为[-2,2]整数范围，即-2代表1/4倍速播放，-1代表1/2倍速播放，0代表正常倍速播放，1代表2倍速播放，2代表4倍速播放
     * */
    private int speed;


    @Data
    public static class PlayerPlayBackControlReqA extends CommonResultEntiry {
        private PlayerPlayBackControlReq data;
    }
}

package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: PlayerGetVideoPlayBackUrlReq
 * @Description: 视频获取通道的录像回放流URL地址实体
 * @Author: ldl
 * @Date: 2022/1/18
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerGetVideoPlayBackUrlReq extends VideoBaseReq{
    /**
     * 开始时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_START_TIME)
    private String startTime;
    /**
     * 结束时间(格式：yyyy-MM-dd HH:mm:ss）
     * @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_END_TIME)
    private String endTime;

    /**
     * 协议类型
     *  1-rtsp；2-rtmp；3-hls；4-http-flv；5-wss； 6-铁塔私协
     *  @required true
     * */
    @NotBlank(message = VideoConstants.NOT_EMPTY_PROTOCOL_TYPE)
    private int protocolType;
    /**
     * 视频流的唯一编码，对流进中断/限制带宽等操作
     * @required false
     * */
    private String streamUuid;
    /**
     * 插件id
     *  播放插件唯一标识，32位uuid；当protocolType=6，即使用铁塔私协获取url时，此项必填；
     *  @required false
     * */
    private String plugId;

    /**
     * 网域类型
     *  拉流url的网域类型：1-公网(默认值为1)；0-局域内网；
     * */
    private String netType;

    /**
     *  前端录像唯一标识
     *  @required false
     * */
    private String frontStreamUuid;

}

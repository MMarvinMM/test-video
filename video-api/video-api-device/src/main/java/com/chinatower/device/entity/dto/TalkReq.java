package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 喊话相关接口入参实体类
 */
@Data
public class TalkReq implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 音量
     */
    private String volume;

    /**
     * 实时流协议类型
     */
    private String protocolType;

    /**
     * 网域类型
     */
    private String netType;

    /**
     * 锁ID
     */
    private String lockId;

    /**
     * 广播消息编码
     */
    private String messageId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     *推流的编码类型：
     * 3-G711a
     * 4-G11U
     * 5-mp3
     * 6-AAC
     */
    private String streamCodeId = "3";

    /**
     *推流的音频采样频率，单位为KHz
     */
    private String sampleRate = "44.1";
}

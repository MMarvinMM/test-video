package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class TalkV2VO implements Serializable {

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 音量
     */
    private Integer volume;

    /**
     * 流地址
     */
    private String streamUrl;

    /**
     * websocket地址
     */
    private String websocketUrl;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 设备名称列表
     */
    private List<String> deviceNameList;
}

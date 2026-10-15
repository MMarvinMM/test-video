package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TalkResultVO<R extends Serializable> implements Serializable {

    /**
     * 返回编码
     */
    private String returnCode;

    /**
     * 返回消息
     */
    private String returnMsg;

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误时间
     */
    private String errorMsg;

    /**
     * 返回内容
     */
    private R data;

}

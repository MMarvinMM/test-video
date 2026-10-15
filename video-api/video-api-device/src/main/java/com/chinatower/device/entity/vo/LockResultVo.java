package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class LockResultVo implements Serializable {

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
     * 错误消息
     */
    private String errorMsg;

    /**
     * 返回内容
     */
    private LockVo data;
}

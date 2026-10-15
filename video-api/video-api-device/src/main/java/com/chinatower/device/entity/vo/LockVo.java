package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 业务报文数据--加锁
 */
@Data
public class LockVo implements Serializable {

    /**
     * 锁ID
     */
    private String lockId;

    /**
     * 流媒体服务推流地址
     */
    private String pushStreamAddress;

    /**
     * 广播消息编码
     */
    private String messageId;
}

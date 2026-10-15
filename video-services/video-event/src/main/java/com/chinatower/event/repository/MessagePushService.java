package com.chinatower.event.repository;

import com.chinatower.platform.entity.IVmMessagePushDTO;

/**
 * 功能说明: 事件流转-告警消息推送service
 * @author: lyy
 * @date: 2024/08/08
 * @requestID 告警接收业务代码迁移
 */
public interface MessagePushService {

    void messagePush(IVmMessagePushDTO dto);
}

package com.chinatower.platform.service;

import com.chinatower.platform.entity.IVmMessagePushDTO;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/7 14:58
 */
public interface MessageSendService {

    void sendMessagePush(IVmMessagePushDTO dto);
}

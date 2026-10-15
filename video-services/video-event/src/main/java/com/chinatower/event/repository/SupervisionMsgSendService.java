package com.chinatower.event.repository;

import com.chinatower.event.dto.MessagePushDTO;

/**
 * @Description 督办消息推送
 * @Author lyy
 * @Date 2024/06/27 19:28
 * @Version release_v1.6.80
 **/
public interface SupervisionMsgSendService {

    void supervisionSendMsg(MessagePushDTO dto) throws Exception;
}

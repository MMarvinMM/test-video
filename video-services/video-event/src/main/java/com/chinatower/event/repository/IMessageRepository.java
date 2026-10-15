package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.MessagePushDTO;

/**
 * <p>
 * 消息推送 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
public interface IMessageRepository {

    /**
     * 消息推送
     * @param dto
     */
    void messagePush(MessagePushDTO dto);

}

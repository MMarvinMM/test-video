package com.chinatower.event.service;

import com.chinatower.event.entity.dto.DelayMessageDto;
import com.chinatower.event.entity.dto.DelayMsgTypeDto;

import javax.xml.transform.Result;
import java.util.List;

public interface OrderDelayMessageService {


    /**
     * 批量修改延迟消息状态
     * @param delayMessageDtoList
     * @return
     */
    void updateOrderDelayList(List<DelayMessageDto> delayMessageDtoList);

    /**
     * 批量修改延迟消息类型
     * @param
     * @return
     */
    void updateOrderDelayMsgType( DelayMsgTypeDto delayMsgTypeDto);
}

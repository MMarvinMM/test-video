package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.DelayMessageDto;
import com.chinatower.event.entity.dto.DelayMsgTypeDto;
import com.chinatower.event.mapper.MessageSendInfoMapper;
import com.chinatower.event.mongodb.OrderDelayMessageMapper;
import com.chinatower.event.service.OrderDelayMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-event
 * 延迟消息数据操作
 *
 * @author csj
 * @date 2022-10-08
 */
@RequestMapping("/alarm/dealyMessage")
@RestController
public class DealyMessageController {
    @Autowired
    private OrderDelayMessageService orderDelayMessageService;

    /**
     * 功能描述：批量修改延迟消息状态
     * @param delayMessageDtoList
     * @return
     */
    @PostMapping("/updateDealyMessageList")
    public Result updateDealyMessageList(@RequestBody List<DelayMessageDto> delayMessageDtoList) {
        orderDelayMessageService.updateOrderDelayList(delayMessageDtoList);
        return Result.data(null);
    }

    /**
     * 功能描述：批量修改延迟消息类型
     * @param delayMsgTypeDto
     * @return
     */
    @PostMapping("/updateDealyMessageMsgType")
    public Result updateDealyMessageMsgType(@RequestBody DelayMsgTypeDto delayMsgTypeDto) {
        orderDelayMessageService.updateOrderDelayMsgType(delayMsgTypeDto);
        return Result.data(null);
    }
}

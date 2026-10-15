package com.chinatower.event.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.factory.ReceiveAlarmFallbackFactory;
import com.chinatower.event.entity.dto.DelayMessageDto;
import com.chinatower.event.entity.dto.DelayMsgTypeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "messageOperateService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = ReceiveAlarmFallbackFactory.class)
public interface IMessageOperateService {

    /**
     * 批量更改延迟消息状态为已处理
     */
    @PostMapping(value = "/alarm/dealyMessage/updateDealyMessageList")
    Result updateDealyMessageList(@RequestBody List<DelayMessageDto> delayMessageDtoList);

    /**
     * 批量更改延迟消息类型
     */
    @PostMapping(value = "/alarm/dealyMessage/updateDealyMessageMsgType")
    Result updateDealyMessageMsgType(@RequestBody DelayMsgTypeDto delayMsgTypeDto);
}

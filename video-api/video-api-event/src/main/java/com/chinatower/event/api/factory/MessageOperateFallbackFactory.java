package com.chinatower.event.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.event.api.IMessageOperateService;

import com.chinatower.event.entity.dto.DelayMessageDto;
import com.chinatower.event.entity.dto.DelayMsgTypeDto;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: ReceiveAlarmFallbackFactory.java
 *
 * @author luo.zhenghong
 * @date 2022/7/25 10:23
 * <p>
 */
@Component
public class MessageOperateFallbackFactory implements FallbackFactory<IMessageOperateService> {

    @Override
    public IMessageOperateService create(Throwable throwable) {

        return new IMessageOperateService() {
            @Override
            public Result updateDealyMessageList(List<DelayMessageDto> delayMessageDtoList) {
                return Result.fail("修改延迟消息状态异常:" + throwable.getMessage());
            }

            @Override
            public Result updateDealyMessageMsgType(DelayMsgTypeDto delayMsgTypeDto) {
                return Result.fail("修改延迟消息msgType异常:" + throwable.getMessage());
            }
        };
    }
}

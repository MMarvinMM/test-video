package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IReceiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class BatchMessagePushHandler extends AbstractHandler {

    @Autowired
    private IReceiveRepository receiveRepository;

    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        try{
            log.info("批量推送数据信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
            //发送消息推送
            receiveRepository.batchPushMessage(receiveAlarmDTO.getList());
            receiveRepository.batchPushMessage(receiveAlarmDTO.getOperations());
        }catch(Exception e){
            log.error("批量推送数据信息异常：{}", e);
        }
        return this.excuteNextHandler(receiveAlarmDTO);
    }
}

package com.chinatower.platform.project.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.custom.entity.ThirdPartyAlarmTypeSyncDataVO;
import com.chinatower.platform.project.system.service.IAlarmTypeSyncService;
import com.chinatower.system.api.request.alarmType.ThirdPartyAlarmTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * video-system
 * @ClassName AlarmTypeSyncController
 * @Description 告警类型kafka同步
 * @Author liuxt
 * @Date 2025年1月10日15:12:22
 */
@RestController
@Slf4j
public class AlarmTypeSyncController {

    @Resource
    private IAlarmTypeSyncService iAlarmTypeSyncService;

    @KafkaListener(topics = {"slyy_sys_alarm_type"},containerFactory = "kafkaListenerContainerFactoryK2Batch")
    public void mathFactoryConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        long start = System.currentTimeMillis();
        log.info(start + "alarmTypeMathFactoryFromConsumerKafka=" + consumerRecords.size());
        try {
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()) {
                consumerRecords.forEach(item -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(item.value());
                    if (kafkaMessage.isPresent()) {
                        String message = (String)kafkaMessage.get();
                        log.info(start + "接受告警类型kafka消息:"+message);
                        messages.add(message);
                    }
                });
            }
            iAlarmTypeSyncService.alarmTypeMathFactoryConsumerList(messages);
        } catch (Exception e) {
            log.error("alarmTypeMathFactoryFromConsumerKafka异常",e);
        }
        log.info(start + "alarmTypeMathFactoryFromConsumerKafka整体耗时=" + (System.currentTimeMillis() - start));
    }



//    @KafkaListener(topics = {"slyy_vm_third_party_alarm"},containerFactory = "kafkaListenerContainerFactoryK2Batch")
//    public void thirdPartyAlarmMathFactoryConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
//        long start = System.currentTimeMillis();
//        log.info(start + "thirdPartyAlarmMathFactoryConsumer=" + consumerRecords.size());
//        try {
//            List<String> messages = new ArrayList<>();
//            if (!consumerRecords.isEmpty()) {
//                consumerRecords.forEach(item -> {
//                    //判断是否为null
//                    Optional<?> kafkaMessage = Optional.ofNullable(item.value());
//                    if (kafkaMessage.isPresent()) {
//                        String message = (String)kafkaMessage.get();
//                        log.info(start + "接受第三方告警类型kafka消息:"+message);
//                        messages.add(message);
//                    }
//                });
//            }
//            iAlarmTypeSyncService.thirdPartyAlarmMathFactoryConsumerList(messages);
//        } catch (Exception e) {
//            log.error("thirdPartyAlarmMathFactoryConsumerKafka异常",e);
//        }
//        log.info(start + "thirdPartyAlarmMathFactoryConsumerKafka整体耗时=" + (System.currentTimeMillis() - start));
//    }


    @PostMapping("/alarmTypeSync/thirdPartyAlarmSync")
    public Result<String> thirdPartyAlarmSync(@Validated @RequestBody ThirdPartyAlarmTypeVO vo) {
        long start = System.currentTimeMillis();
        try {
            Result<String>  result = iAlarmTypeSyncService.thirdPartyAlarmSync(vo);
            log.info(start + "thirdPartyAlarmSync整体耗时=" + (System.currentTimeMillis() - start));
            return result;
        } catch (Exception e) {
            log.error("thirdPartyAlarmSync异常",e);
            return Result.fail("同步失败");
        }
    }


}

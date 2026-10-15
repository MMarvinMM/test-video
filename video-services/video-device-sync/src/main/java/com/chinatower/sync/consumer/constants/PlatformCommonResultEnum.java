package com.chinatower.sync.consumer.constants;


import com.chinatower.platform.common.utils.spring.SpringUtils;
import com.chinatower.platform.enums.AIotPlatformCommonEnum;
import com.chinatower.sync.consumer.entity.MessageProcessor;
import com.chinatower.sync.consumer.service.HornContentConfServiceImpl;
import com.chinatower.sync.consumer.service.HornTaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface PlatformCommonResultEnum {

    // 消息处理器枚举类，集中管理所有处理器配置
    enum MessageProcessorEnum {
        //音频文件上传状态推送
        AUDIO_PROCESSOR(AIotPlatformCommonEnum.Command.HORN_UPLOAD_AUDIO.getFnId()),
        //文转语状态推送
        TXT_PROCESSOR(AIotPlatformCommonEnum.Command.HORN_ADD_TEXT.getFnId()),
        //广播任务状态推送
        TASK_PROCESSOR(AIotPlatformCommonEnum.Command.HORN_ADD_NODE_TIMING.getFnId());

        // fnId标识
        private final String fnId;
        // 消息列表
        private final List<String> messages;
        // 消息处理器
        private final MessageProcessor messageProcessor;

        // 枚举构造器
        MessageProcessorEnum(String fnId) {
            this.fnId = fnId;
            // 处理器描述
            this.messages = new ArrayList<>();
            // 根据枚举实例创建对应的处理器
            this.messageProcessor = createMessageProcessor();
        }

        // 创建消息处理器
        private MessageProcessor createMessageProcessor() {
            switch (this) {
                case AUDIO_PROCESSOR:
                    return new MessageProcessor(() ->
                            SpringUtils.getBean(HornContentConfServiceImpl.class)
                                    .updateFileUrlByAudioId(messages));
                case TXT_PROCESSOR:
                    return new MessageProcessor(() ->
                            SpringUtils.getBean(HornContentConfServiceImpl.class)
                                    .updateFileUrlByTxtId(messages));
                case TASK_PROCESSOR:
                    return new MessageProcessor(() ->
                            SpringUtils.getBean(HornTaskService.class)
                                    .updateHornTaskStatus(messages));
                default:
                    return null;
            }
        }

        // 获取处理器映射
        public static Map<String, MessageProcessor> getProcessorMap() {
            Map<String, MessageProcessor> map = new HashMap<>();
            for (MessageProcessorEnum enumItem : values()) {
                if (Objects.nonNull(enumItem.messageProcessor)) {
                    map.put(enumItem.fnId, enumItem.messageProcessor);
                }
            }
            return map;
        }
    }
}

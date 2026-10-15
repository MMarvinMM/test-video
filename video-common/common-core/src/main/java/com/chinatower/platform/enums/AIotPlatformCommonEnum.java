package com.chinatower.platform.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public interface AIotPlatformCommonEnum {

    String SMART_BC = "smartbc";

    String CODE_SUCCESS_CODE = "200";

    String RESULT_SUCCESS_CODE = "1";

    /**
     * 设备中心设备在线状态
     */
    int AIOT_ONLINE_STATUS = 1;

    /**
     * 设备中心设备离线状态
     */
    int AIOT_OFFLINE_STATUS = 0;

    /**
     * 通用物联设备在线状态
     */
    int PLATFORM_SENSOR_ONLINE_STATUS = 0;

    /**
     * 通用物联设备离线状态
     */
    int PLATFORM_SENSOR_OFFLINE_STATUS = 1;

    @Getter
    @AllArgsConstructor
    enum Platform {
        AIOT("AIOT平台", "aiot"),
        RADAR("雷达平台", "radar"),
        SMARTBC("广播平台", SMART_BC),
        UAV("无人机平台", "uav");

        private final String platformName;
        private final String platformCode;
    }



    /**
     * 平台能力
     * fnType 平台能力 = 0 设备能力 = 1
     * reqType 0：同步请求 1：异步请求 同步阻塞等待设备响应，异步接收侧同步返回接收结果，设备执行完异步返回执行结果。
     */
    @Getter
    @AllArgsConstructor
    enum Command {
        HORN_UPLOAD_AUDIO("上传音频", "uploadAudioFile", "0", SMART_BC, "0", "audioUpload"),
        HORN_CREATE_WEB_LIVE("创建web口播", "createWebBroadcast", "1", SMART_BC, "0", "startPlayV2"),
        HORN_STOP_WEB_LIVE("结束web口播", "endWebBroadcast", "1", SMART_BC, "0", "stopPlayV2"),
        HORN_CREATE_APP_LIVE("创建app口播", "createAppBroadcast", "1", SMART_BC, "0", "startPlayAPPV2"),
        HORN_STOP_APP_LIVE("结束app口播", "endAppBroadcast", "1", SMART_BC, "0", "stopPlayAPPV2"),
        HORN_HEARTBEAT("口播心跳", "heartbeatBroadcastService", "0", SMART_BC, "0", "heartbeatV2"),
        HORN_ADD_TEXT("创建文本", "createText", "0", SMART_BC, "0", "addText"),
        HORN_DELETE_TEXT("删除文本", "deleteText", "0", SMART_BC, "0", "deleteText"),
        HORN_DELETE_AUDIO("删除音频", "deleteAudio", "0", SMART_BC, "0", "deleteAudio"),
        HORN_AUDITION_TEXT("文本试听", "textAudition", "0", SMART_BC, "0", "audition"),
        HORN_AUDIO_GET("查询音频详情", "queryAudioDetails", "0", SMART_BC, "0", "audioGet"),
        HORN_TEXT_GET("查询文本详情", "queryTextDetails", "0", SMART_BC, "0", "textGet"),
        HORN_ADD_NODE_TIMING("创建节点广播任务", "createNodeBroadcastTask", "1", SMART_BC, "0", "addNodeTiming"),
        HORN_STOP_TASK("停止广播任务", "stopBroadcastTask", "1", SMART_BC, "0", "stopTask"),
        HORN_BATCH_DELETE("删除广播任务", "deleteBroadcastTask", "1", SMART_BC, "0", "batchDelete"),
        ;

        private final String fnName;
        private final String fnId;
        private final String fnType;
        private final String platformCode;
        private final String reqType;
        private final String oldMapping;

        public static Stream<Command> stream() {
            return Stream.of(Command.values());
        }

        public static Command getCommand(String oldMapping) {
            return Command.stream().filter(command -> StringUtils.equals(oldMapping, command.getOldMapping())).findFirst().orElse(null);
        }
    }
}

package com.chinatower.message.constant;

public class WebSocketConstant {

    public final static String WEB_SOCKET_KEY_PRE = "WEBSOCKET_";

    public final static String LINE = "-";
    public final static String KAFKA_TOPIC_WEBSOCKET_MSG = "websocket_message";

    public final static String VIDEO_FRAME_MSG = "UFCP-VIDEO-EXTRACTION-PROCESS";

    public final static Integer MSG_KAFKA_TYPE = 1;
    public final static Integer MSG_RADAR_TYPE = 2;

    /**
     * 预置位到达通知
     */
    public final static Integer MSG_MARK_TYPE = 3;

    /**
     * 告警声音开关
     */
    public final static Integer SOUND_CONFIG_TYPE = 4;

    /**
     * 低空轨迹消息推送
     */
    public final static Integer LOW_ALT_CONFIG_TYPE = 5;
    /**
     * 智能研判消息推送
     */
    public final static Integer OPERATION_JUDGE_TYPE = 6;

}

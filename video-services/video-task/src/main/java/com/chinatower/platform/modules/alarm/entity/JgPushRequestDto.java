package com.chinatower.platform.modules.alarm.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.system.api.model.NoticeExtendVO;
import lombok.Data;

import java.util.List;

/**
 * 告警消息推送请求实体
 */
@Data
public class JgPushRequestDto extends CommonFeignEntity {
    /**
     * 极光别名 可以为单个  也可以为集合  集合每次最多5000
     */
    private String alias;
    /**
     * 通知内容标题
     */
    private String notificationTitle;
    /**
     * 消息内容标题
     */
    private String msgTitle;
    /**
     * 消息内容
     */
    private String msgContent;
    /**
     * 扩展字段
     */
    private String extrasparam;

    /**
     * 推送类型
     * 1- 通知类
     * 0- 消息类
     */
    private String  msgType;

    /**
     * 推送声音文件名称
     */
    private String sound;

    /**
     * 工单号
     */
    private String orderId;

    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警类型ID
     */
    private String warningTypeId;
    /**
     * 告警时间
     */
    private String alarmTime;
    /**
     * 研判时间
     */
    private String operateTime;
    /**
     * 所匹配所有需要稍后推送的时间段以及规则信息
     */
    private List<NoticeExtendVO> noticeExtendVO;
}

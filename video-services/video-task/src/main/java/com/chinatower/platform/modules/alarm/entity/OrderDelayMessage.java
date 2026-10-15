package com.chinatower.platform.modules.alarm.entity;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.system.api.model.NoticeExtendVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 推送日志记录对象
 */
@Data
@ApiModel(value = "推送日志记录对象",description = "推送日志记录对象")
@Document(collection="order_delay_message_t")
public class OrderDelayMessage extends BaseMongoDTO implements Serializable {


    /**
     * 发送来源(0:pc  1:app）
     */
    private String source;

    /**
     * 是否处理(0:未处理  1:已处理)
     */
    private String operateStatus;

    /**
     * 工单号
     */
    private String orderId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 消息报文
     */
    private String messageJson;

    /**
     * 行业版本
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 流程id
     */
    private String flowId;

    /**
     * 环节id
     */
    private String linkId;

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
     * 消息类型
     */
    private String msgType;

    /**
     * 所匹配所有需要稍后推送的时间段以及规则信息
     */
    private List<NoticeExtendVO> noticeExtendVO;

}

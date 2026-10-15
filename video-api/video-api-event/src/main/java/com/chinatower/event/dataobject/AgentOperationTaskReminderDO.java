package com.chinatower.event.dataobject;

import lombok.Data;
import java.util.Date;

/**
 * 智能研判任务提醒推送记录实体类
 */
@Data
public class AgentOperationTaskReminderDO {

    /**
     * 提醒记录ID
     */
    private Long notifyId;

    /**
     * 关联执行记录ID
     */
    private Long executionId;

    /**
     * 关联任务ID
     */
    private Long taskId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 通知类型
     */
    private String notifyType;

    /**
     * 提醒类型（3h,6h,12h,24h,1d,2d）
     */
    private String overdueNotifyType;

    /**
     * 提醒内容
     */
    private String notifyContent;

    /**
     * 发送状态 0-未发送, 1-发送成功, 2-发送失败
     */
    private Integer sendStatus;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}

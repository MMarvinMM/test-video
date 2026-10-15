package com.chinatower.platform.entity.msg;

import com.chinatower.common.entity.BaseEntity;
import com.chinatower.platform.model.dto.WarningOrderMsgDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author
 * 极光记录表
 */
@Data
@ApiModel(value = "bs_jpush_record",description = "极光记录表实体")
public class BsJpushRecord extends BaseEntity
{
    private String id;

    /**
       * 极光别名 可以为单个
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
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送结果（0失败 1成功）
     */
    private String sendResult;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 删除标志（0未删除 2已删除）
     */
    private String delFlag;

    /**
     * 创建者所属部门ID
     */
    private String deptId;

    /**
     * 是否已读
     */
    private String isRead;

    private WarningOrderMsgDto warningOrder;

}

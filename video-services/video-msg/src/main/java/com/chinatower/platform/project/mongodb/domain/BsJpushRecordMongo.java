package com.chinatower.platform.project.mongodb.domain;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.platform.model.dto.WarningOrderMsgDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="bs_jpush_record")
public class BsJpushRecordMongo extends BaseMongoDTO implements Serializable {

    private String id;

    /**
     * 极光别名 可以为单个
     */
    private String alias;

    /**
     * 消息类型
     * 1- 告警、超期
     * 2- 通知公告/行业资讯
     * 3- 其他消息 (巡查等)
     */
    private String type;
    /**
     * 通知内容标题
     */
    private String notificationTitle;
    /**
     * 消息内容标题
     */
    private String msgTitle;

    /**
     * 事件状态名称
     */
    private String orderStatusName;

    /**
     * 事件地点
     */
    private String address;

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

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
}

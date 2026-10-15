package com.chinatower.platform.project.system.domain;

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

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String type;

    private String orderStatusName;

    private String address;
}

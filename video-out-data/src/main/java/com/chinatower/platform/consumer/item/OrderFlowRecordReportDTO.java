package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.util.Date;

/**
 * 工单环节流程数据对象
 * Created by lyy on 2022/10/20.
 */
@Data
@ApiModel(value = "告警工单环节流程数据对象",description = "告警工单环节流程数据对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderFlowRecordReportDTO extends BaseMongoDTO {
    /**
     * 工单流程ID
     */
    private String orderFlowId;

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 环节ID
     */
    private String linkId;

    /**
     * 环节名称
     */
    private String linkName;

    /**
     * 流程记录名称
     */
    private String flowRecordName;

    /**
     * 环节开始时间
     */
    private String flowNodeTime;

    /**
     * 环节结束时间 格式yyyy-MM-dd HH:mm:ss
     */
    private String flowEndTime;

    /**
     * 是否当前环节  1:是 0:否
     */
    private String ifCurrentLink;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 小组id
     */
    private String groupId;

    /**
     * 0否  1是
     */
    private String ifTimeout;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    /**
     * 处理单ID
     */
    private String processOrderId;

    /**
     * 审批单ID
     */
    private String approveOrderId;

    /**
     * 操作备注
     */
    private String remark;

    /**
     * 完成时限，单位：分钟
     */
    private Integer timeLimit;

    /**
     * 语音
     */
    private String voice;

    /**
     * 指定处置人账户集
     */
    private String processNameList;

    /**
     * 行业应用编码
     */
    private String industryCode;
}

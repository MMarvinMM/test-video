package com.chinatower.bdm.api.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class InspectionTaskParamVO implements Serializable {
    /**
     * 巡查任务ID
     */
    private String taskId;

    /**
     * 配置编码
     */
    private String confId;
    /**
     * 任务名称
     */
    private String cnofName;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格姓名
     */
    private String gridName;

    /**
     * 完成状态
     */
    private String taskStatus;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;

    private String keeperId;

    private String keeperName;

    /**
     * 反馈
     */
    private String retroaction;

    /**
     * 附件id
     */
    private List<InspectionFjldParam> fjId;

    /**
     * 视频地址数组
     */
    private List<InspectionVideoParam> videoUrlList;

    /**
     * 视频地址
     */
    private InspectionVideoParam videoUrl;

    /**
     * 语音地址
     */
    private String voiceUrl;

    /**
     * 完成时间
     */
    private String inspectionTime;

    private String promoter;

    private String industryCode;

    /**
     * 反馈人网格员id
     */
    private String feedbackKeeperId;

    /**
     * 反馈人网格员名称
     */
    private String feedbackKeeperName;

    /**
     * 反馈端（1.pc 2.app）
     */
    private String feedbackEnd;


    private static final long serialVersionUID = 1L;
}

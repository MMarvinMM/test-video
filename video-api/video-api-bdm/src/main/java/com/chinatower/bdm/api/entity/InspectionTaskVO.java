package com.chinatower.bdm.api.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@Data
@ToString
public class InspectionTaskVO implements Serializable {
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
     * 当前网格名称
     */
    private String curGridName;

    /**
     * 完成状态: 1-已完成；2-未完成
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
    private String fjId;

    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 缩略图地址 app用到
     */
    private String coverUrl;

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
     * 描述
     */
    private String confDesc;

    /**
     * 描述
     */
    private String taskDesc;

    /**
     * 任务完成状态名称
     */
    private String taskStatusName;

    private String taskName;

    private String UserId;

    /**
     * 创建日期（对应APP任务详情的巡查日期）
     */
    private String createDate;

    /**
     * 巡查时段开始时间
     */
    private String inspectionStartTime;

    /**
     * 巡查时段结束时间
     */
    private String inspectionEndTime;

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

    /**
     * 反馈端名称
     */
    private String feedbackEndName;

    /**
     * 网格范围
     */
    private String gridRange;

    /**
     * 网格范围
     */
    private String gridCenter;

    /**
     * 轨迹开始时间
     */
    private String trailStartTime;

    //APP消息列表跳转URL
    private String appUrl;

    /**
     * 轨迹结束时间
     */
    private String trailEndTime;
    //下发状态 0-未下发 1-已下发
    private Integer sendFlag;

    private List<String> keeperIdList;

    private String keeperPhoneNumber;

    private String isLine;

    private String avatar;

    private static final long serialVersionUID = 1L;

    /**
     * 网格填充颜色
     * @required false
     */
    private String color;


    /**
     * 网格边框颜色
     * @required false
     */
    private String borderColor;

    /**
     * 网格边框颜色透明度 单位% 取值0-1 默认0.5
     * @required false
     */
    private String borderColorTransparency;

    /**
     * 网格边框线条粗细 取值1 2 3 4 5 默认1
     * @required false
     */
    private String borderColorLineThickness;

    /**
     * 网格填充颜色透明度 单位% 取值0-1 默认0.5
     * @required false
     */
    private String colorTransparency;

    /**
     * 巡查人网格长列表
     */
    private List<String> isGridManagerList;

    /**
     * 巡查人网格员列表
     */
    private List<String> noGridManagerList;

    /**
     * 反馈人员类型 1.网格长 否则网格员
     */
    private String feedbackKeeperType;

    /**
     * 待办标识
     */
    private String toDoFlag;

}

package com.chinatower.event.entity.vo;

import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: LeaderAuidtListVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/26 16:09
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class LeaderAuidtListVO implements Serializable {
    /**
     * 主键
     */
    private String auditId;

    /**
     * 事件ID
     */
    private String orderId;

    /**
     * 申请内容
     */
    private String content;

    /**
     * 申请人
     */
    private String userId;

    /**
     * 领导ID
     */
    private String leaderId;

    /**
     * 审批意见：0-待审批；1-同意；2-不同意
     */
    private String status;

    /**
     * 批示内容
     */
    private String auditContent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 批示时间
     */
    private String auditTime;

    /**
     * 0-有效；1-删除
     */
    private String deleteFlag;

    /**
     * 流程节点ID
     */
    private String orderFlowId;

    /**
     * 语音
     */
    private String voice;

    /**
     * 完成时限;单位：分钟
     */
    private Integer timeLimit;

    /**
     * 当前环节开始时间
     */
    private String flowNodeTime;

    /**
     * 事件等级
     */
    private String emergencyLevel;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;
    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatusName;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 县名称
     */
    private String countyCode;

    /**
     * 县编码
     */
    private String countyName;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 形成告警时间
     */
    private String alarmTime;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 地址
     */
    private String address;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    private String fileImgUrlIcon;

    private String warningName;

    private String warningSource;
    private String warningSourceName;

    private String description;

    private String judgeResultName;

    /**
     * 研判人
     * @required false
     */
    private String checkMan;

    /**
     * 研判人账号
     * @required false
     */
    private String checkManUserName;

    private String checkTime;

    private String deviceCode;

    private String channelCode;

    private String provinceId;
    private String cityId;
    private String countyId;
    private String applyTime;
    private String applyUserId;
    private String applyUserName;
    private String leaderName;
    private String imgUrl;
    private String deviceName;
    private String channelName;

    private String isSecondJudge;

    private String secondJudgeResult;

    //告警类型图标
    private String icon;

    /**
     * 重要等级
     */
    private String importanceLevel;

    /**
     * 重要等级名称
     */
    private String importanceLevelName;

    /**
     * 综合置信度
     */
    private String compConfidenceLevel;
    /**
     * 置信度
     */
    private String confidenceLevel;

    private String showTime;
    private String duration;
    /**
     * 是否超时 0否1是
     */
    private int ifTimeout;

    private String remark;

    /**
     * 告警标签集合
     */
    private List<AlarmLableRelDTO> alarmLableRelDTOList;

    private String enableAcceptanceMode;

    /**
     * P位置
     */
    private String ptzP;

    /**
     * T位置
     */
    private String ptzT;

    /**
     * Z位置
     */
    private String ptzZ;

    /**
     * 预置位名称ID
     */
    private String presetId;

    /**
     * 透传字段
     */
    private String extraField;

    /**
     * 专题图
     */
    private String relativePathThem;

    /**
     * 红外图
     */
    private String relativePathMir;

    /**
     * 用户距离事发地距离
     */
    private String userDistance;

    /**
     * 网格名称
     */
    private String gridName;

    private String alarmReason;

    /**
     * 是否显示督办提示图标（0-否，1-是）
     */
    private String isHandleDisplay;

    /**
     * 事件上报人
     */
    private String findName;

    /**
     * 所属组织
     */
    private String orgName;
}

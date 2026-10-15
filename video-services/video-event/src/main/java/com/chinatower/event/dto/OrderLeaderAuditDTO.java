package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.List;

import com.chinatower.event.dataobject.OrderLeaderAuditDO;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrderLeaderAuditDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/15 10:25
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderLeaderAuditDTO extends OrderLeaderAuditDO implements Serializable {

    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;

    private String source;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 用户类型，1用户，2角色
     */
    private String userType;

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String msgNotifyType;

    /**
     * 角色ID，中间用逗号隔开
     */
    private String roleId;

    private String countyId;

    private String cityId;

    private String provinceId;

    /**
     * 领导批示ID
     */
    private List<String> leaderIdList;

    private String importanceLevel;
    private String warningSource;

    private String alarmParam;
    private String alarmParamValue;
    private String extraField;

    /**
     * 行业编码
     */
    private String industryCode;
    private String appVerCode;
    private String horiAzimuthAngle;
    private String frontAlarmPicUrl;
    private String frontAlarmVedioUrl;

    private String confidenceLevel;
    private String compConfidenceLevel;
    private String confidenceLevelMin;

    private String noticeResource;
    private String msgType;
    /**
     * 前端告警类型
     */
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     */
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
    private String secondJudgeResult;

    /**
     * 是否二次识别 1-是；0-否
     * @required false
     */
    private String isSecondIdentify;

    /**
     * 二次识别结果 1-真，0-假，2-算法未分析
     * @required false
     */
    private String secondIdentifyResult;

    /**
     * 框图
     */
    private String fileImgUrlIcon;


    /**
     * 完成时限;单位：分钟
     */
    private Integer timeLimit;

    /**
     * 当前环节开始时间
     */
    private String flowNodeTime;

    /**
     *  置信度最大值
     */
    private String confidenceLevelMax;
    /**
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    /**
     * 预置位ID
     */
    private String presetId;

    /**
     * 事件发现人姓名
     */
    private String findName;

    /**
     * 俯仰角
     */
    private String pitchAngle;

    /**
     * 水平视场角
     */
    private String horiFieldAngle;

    /**
     * 垂直视场角
     */
    private String verticalFieldAngle;

}

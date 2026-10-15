package com.chinatower.event.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: InterceptDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/6/8 10:59
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class InterceptDTO implements Serializable {

    private String id;
    /**
     * 当前环节
     */
    private String curLink;
    /**
     * 流程ID
     */
    private String flowId;
    /**
     * 是否流转下个环节
     */
    private String isNextLink;
    /**
     * 下个环节ID
     */
    private String linkId;
    /**
     * 下个环节名称
     */
    private String linkName;
    /**
     * 通知类型
     */
    private String msgNotifyType;
    /**
     * 告警事件ID
     */
    private String orderId;

    /**
     * 告警事件ID
     */
    private List<String> orderIds;
    /**
     * 备注
     */
    private String remark;
    /**
     * 角色ID
     */
    private String roleId;
    /**
     * 时限
     */
    private String timeLimit;
    /**
     * 类型
     */
    private String userType;

    /**
     * 研判结果
     */
    private String judgeResult;

    /**
     * 告警类型ID
     */
    private String warningTypeId;

    /**
     * 事件等级
     */
    private String emergencyLevel;

    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 省份编码
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 地市编码
     */
    private String cityCode;
    /**
     * 地市名称
     */
    private String cityName;
    /**
     * 区县编码
     */
    private String countyCode;
    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 水平方位角
     */
    private String horiAzimuthAngle;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

    /**
     * 流转类型（0默认、 1转派、2回退）
     */
    private String flowLinkType;

    /**
     * 操作之前的linkkId
     */
    private String beforLinkId;

    private String templateId;

    /**
     * 1：测试本地调用
     */
    private String taskFlag;

    /**
     * 网格id
     */
    private String gridId;
    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 是否修改网格信息
     */
    private String checkGridFlag;

    private String importanceLevel;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;

    /**
     * 受理超期提醒
     */
    private String timeLimitAcceptance;

    /**
     * 流程环节id
     */
    private String attrId;

    private String videoUrl;

    private String fileImgUrlSrc;

    private String fileImgUrlIcon;

    private String extraField;

    private String alarmReason;

    private String devicePitch;

    private String deviceHorizontal;

    //是否二次识别
    private String isSecondIdentify;
    //二次识别结果
    private String secondIdentifyResult;
    //联动算法
    private String linkageAlgorithm;
}

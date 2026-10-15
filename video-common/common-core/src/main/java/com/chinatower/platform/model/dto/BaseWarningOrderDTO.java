package com.chinatower.platform.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseWarningOrderDTO implements Serializable {
    /**
     * 告警单ID(yyyyMM+租户id+)
     */
    private String orderId;

    /**
     * 告警事件编码
     * @required true
     */
    private String warningOrderId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     * @required true
     */
    private String warningSource;

    /**
     * 告警类型编码
     * @required true
     */
    private String warningTypeId;

    /**
     * 形成告警时间
     * @required true
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 租户名称
     * @required true
     */
    private String tenantName;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;

    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 告警内容
     * @required true
     */
    private String alarmBody;

    /**
     * 备注
     * @required false
     */
    private String remark;

    /**
     * 事件描述
     * @required false
     */
    private String description;

    /**
     * 详细地址
     * @required false
     */
    private String address;

    /**
     * 处理人
     * @required false
     */
    private String processId;

    /**
     * 处理人姓名
     * @required false
     */
    private String processName;

    /**
     * 事件等级，关联枚举值表
     * @required false
     */
    private String emergencyLevel;

    /**
     * 事件状态：0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     * @required true
     */
    private String orderStatus;

    /**
     * 事件发现人姓名
     * @required false
     */
    private String findName;

    /**
     * 事件发现人电话
     * @required false
     */
    private String findMobile;

    /**
     * 原图
     * @required false
     */
    private String imgUrl;

    /**
     * 最后更新时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifierDate;

    /**
     * 创建时间
     * @required true
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 处理时长
     * @required false
     */
    private String duration;

    /**
     * 超期时长
     * @required false
     */
    private String showTime;

    /**
     * 是否可编辑  0否 1是
     * @required false
     */
    private int ifEdit;

    /**
     * 是否超时 0否1是
     * @required false
     */
    private int ifTimeout;

    /**
     * 完成时限
     * @required false
     */
    private Integer timeLimit;

    /**
     * 当前环节开始时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowNodeTime;

    /**
     * 领导批示数量
     * @required false
     */
    private int leaderNum;

    /**
     * 图标
     * @required false
     */
    private String icon;

    /**
     * 告警类型名称
     * @required false
     */
    private String warningTypeName;

    /**
     * 告警等级
     * @required false
     */
    private String emergencyLevelName;

    /**
     * 工单状态名称
     * @required true
     */
    private String orderStatusName;

    /**
     * 网格名称
     * @required false
     */
    private String gridName;

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

    /**
     * 研判时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /**
     * 接收时间
     * @required true
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reciveTime;

    /**
     * 重复告警编码
     * @required false
     */
    private String duplicateOrderId;

    /**
     * 小组id，组织编码
     * @required false
     */
    private String groupId;

    private static final long serialVersionUID = 1L;

    /**
     * 是否收藏 1:收藏 0：未收藏
     * @required true
     */
    private String isCollection;

    /**
     * 告警来源名称: 1-AI上报； 2-人工上报；3-一键告警
     * @required true
     */
    private String warningSourceName;

    /**
     * 省份名称
     * @required true
     */
    private String provinceName;

    /**
     * 地市名称
     * @required true
     */
    private String cityName;

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 设备名称
     * @required true
     */
    private String deviceName;

    /**
     * 通道编码
     * @required true
     */
    private String channelCode;

    /**
     * 通道名称
     * @required true
     */
    private String channelName;

    /**
     * 研判结果
     * @required true
     */
    private String judgeResult;

    /**
     * 是否有处理权限 0：否  1：是
     * @required true
     */
    private String canEdit;

    /**
     * 流程ID
     * @required true
     */
    private String flowId;

    /**
     * 环节ID
     * @required true
     */
    private String linkId;

    /**
     * 模板ID
     * @required true
     */
    private String flowTemplate;

    /**
     * 领导批示表主键Id
     * @required true
     */
    private String auditId;

    /**
     * 省份编码
     * @required true
     */
    private String provinceId;

    /**
     * 地市编码
     * @required true
     */
    private String cityId;

    /**
     * 网格ID
     * @required true
     */
    private String gridId;

    /**
     * 区县编码
     * @required true
     */
    private String countyId;

    /**
     * 区县名称
     * @required true
     */
    private String countyName;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 网格员名称
     * @required false
     */
    private String keeperName;

    /**
     * 原图
     * @required false
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     * @required false
     */
    private String fileImgUrlIcon;

    /**
     * 视频路径
     * @required false
     */
    private String videoUrl;

    /**
     * 站址地址
     * @required false
     */
    private String location;

    /**
     * 设备厂家code
     * @required false
     */
    private String modelCode;

    /**
     * 设备挂高，单位：米
     * @required false
     */
    private String height;

    /**
     * 距离
     * @required false
     */
    private String distance;

    /**
     * 报警参数
     * @required false
     */
    private String alarmParam;

    /**
     * 报警参数值
     * @required false
     */
    private String alarmParamValue;

    /**
     * 行业扩展字段
     * @required false
     */
    private String extraField;

    /**
     * 重要等级
     * @required false
     */
    private String importanceLevel;

    /**
     * 重要等级名称
     * @required false
     */
    private String importanceLevelName;

    /**
     * AI前端告警 1后端 2前端
     * @required false
     */
    private String noticeResource;

    /**
     * AI告警名称
     * @required false
     */
    private String noticeResourceName;

    /**
     * 摄像机厂家
     * @required false
     */
    private String modelId;

    /**
     * 水平方位角
     * @required false
     */
    private String horiAzimuthAngle;

    /**
     * 热成像原图
     * @required false
     */
    private String frontAlarmPicUrl;

    /**
     * 热成像原视频
     * @required false
     */
    private String frontAlarmVideoUrl;

    /**
     * 前端告警类型
     * @required false
     */
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     * @required false
     */
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     * @required false
     */
    private String secondJudgeResult;

    /**
     * 最小置信度
     * @required false
     */
    private String confidenceLevelMin;

    /**
     * 综合置信度
     * @required false
     */
    private String compConfidenceLevel;

    /**
     * 置信度
     * @required false
     */
    private String confidenceLevel;

    /**
     * 上个环节是否调度（0：不是 1：是）
     * @required false
     */
    private String beforeDispatchFlag;

    /**
     * 真实环节id(1研判2调度4处置5核实6办结)
     * @required true
     */
    private String realLinkType;

    /**
     * 删除标识
     * @required true
     */
    private Integer deleteFlag;

    private String alarmReason;

    /**
     * 是否显示督办提示图标（0-否，1-是）
     */
    private String isHandleDisplay;

    /**
     * 所属组织
     */
    private String orgName;

    /**
     * AI风险等级（高风险/中风险/低风险）
     */
    private String riskLevel;

    /**
     * 场景编码（Dify 回调映射：smoke/fire/smokeFire）
     */
    private String sceneCode;

    /**
     * 场景模板（来自配置表）
     */
    private String sceneTemplate;

    /**
     * AI研判状态：0未研判/1研判中/2研判完成/3研判失败
     */
    private Integer aiJudgeStatus;

    /**
     * 智能体扩展字段(库内JSON，接口出参前会拆分并清空)
     */
    private String agentExtendField;

    /**
     * 湿度
     */
    private String humidity;

    /**
     * 温度
     */
    private String temperature;

    /**
     * 风速
     */
    private String windSpeed;

    /**
     * 风向
     */
    private String windDir;

    /**
     * 火险等级
     */
    private Integer fireLevel;

    /**
     * 周边环境
     */
    private String surroundings;

    /**
     * 研判依据（AI风险等级分析）
     */
    private String judgmentBasis;
}


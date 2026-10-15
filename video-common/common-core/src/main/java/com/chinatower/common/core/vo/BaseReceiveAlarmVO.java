package com.chinatower.common.core.vo;

import com.chinatower.common.core.dto.ProcessDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: hued
 * @Date: 2021年11月3日11:24:33
 * @Description:
 * 告警接收请求实体
 */
@Data
public class BaseReceiveAlarmVO extends BaseAlarmVO {

    /**
     * 详细地址
     * @required true
     */
    private String address;
    /** 市编码 */
    private String cityCode;
    /** 县编码 */
    private String countyCode;
    /** 省编码 */
    private String provinceCode;
    /**
     * 街道编码
     * @required true
     */
    private String townCode;

    /**
     * 市编码
     * @required true
     */
    private String cityId;

    /**
     * 县编码
     * @required true
     */
    private String countyId;

    /**
     * 省编码
     * @required true
     */
    private String provinceId;

    /** 乡镇名称 */
    private String townName;
    /** 市名称 */
    private String cityName;
    /** 县名称*/
    private String countyName;
    /** 省名称 */
    private String provinceName;
    /**
     * 告警类型
     * @required true
     */
    private String warningTypeId;
    /** 告警类型名称 */
    private String warningTypeName;
    /** 客户编码 */
    private String tenantId;
    /** 客户名称 */
    private String tenantName;
    /** 是否开启运营研判 0:否 1:串 2：并 */
    private String isOperation;
    /** 工单编号 */
    private String warningOrderId;
    /**
     * 通知类型:枚举值类型72
     * 1	告警消息
     * 2	运营研判消息
     * 3	领导批示消息
     * 4	巡查任务消息
     */
    private String notifyType;
    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     * @required true
     */
    private String warningSource;

    private String warningSourceName;
    /** 网格编码 */
    private String gridId;
    /** 网格名称 */
    private String gridName;
    /** 上报人姓名*/
    private String findName;
    /** 上报人电话 */
    private String findMobile;
    /** 上报人编码 */
    private String createUser;
    /** 告警等级 */
    private String emergencyLevel;
    /** 告警状态 */
    private String orderStatus;
    /** 告警模板 */
    private String flowTemplate;
    /** 上报类型：1-PC上报；2-APP上报 */
    private String reportType;

    /**
     * 备注
     * @required false
     */
    private String remark;

    /**
     * 事件描述
     * @required true
     */
    private String description;
    /** 当前环节id */
    private String linkId;
    /** 当前环节名称 */
    private String linkName;
    /** 当前环节开始时间 */
    private String flowNodeTime;
    /** 处理人 */
    private String processId;
    /** 处理人姓名 */
    private String processName;
    /** 小组id，组织编码 */
    private String groupId;
    /** 完成时限;单位：分钟 */
    private String timeLimit;
    /**
     * 受理时限（秒）
     */
    private String timeLimitAcceptance;
    /**
     * 是否开启受理模式(0:默认，不开启；1:开启)
     */
    private String enableAcceptanceMode;
    /** 语音 */
    private String voice;
    /** 环节名称 */
    private String flowName;
    /** 组织id */
    private String orgId;
    /** 用户id */
    private String userId;
    /** 用户名称*/
    private String userName;
    /** 创建时间*/
    private String createTime;
    /** 视频*/
    private String videoUrl;
    /** 框图*/
    private String fileImgUrlIcon;
    /** 原图*/
    private String fileImgUrlSrc;
    /**
     * 1-1级，需应用在告警列表展示，默认值为1；2-2级，需在工作台内静默展示；
     * @required true
     */
    private String importanceLevel;
    /** 置信度*/
    private Float thresh;
    private String nowThresh;
    /** 告警时间间隔 */
    private Integer alarmInterval;
    /** 告警时间间隔单位 */
    private String alarmIntervalType;
    /** 运行时间 */
    private String algorithmTime;
    /** 上次告警时间 */
    private String prevAlarmTime;
    /** 拦截原因：1.置信度阈值，2.告警间隔，3.告警运行时间* 4.列表告警全部拦截*/
    String intercept;
    /** 拦截备注*/
    String interceptRemarks;
    /** 月份表名*/
    String dateTable;
    /** 上个月份表名*/
    String onDateTable;
    /** 客户集合*/
    private List<String> tenantList;
    /** 告警来源 1-算法AI告警；2-人工上报；3-一键告警；4、摄像机算法告警 5-车辆卡口； 6-人脸卡口；7-行业上报；8-物联设备告警；9-卫星告警；10-雷达告警*/
    private String alarmSource;
    /** 扩展字段属性*/
    private String judgeResult;
    /** 扩展字段属性*/
    private String judgeType;
    /** 算法透传字段*/
    private ExtraInfoVO extraInfo;
    /** 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.*/
    private String noticeResource;
    /** 置信度数组 */
    private String confidenceLevel;
    /** 置信度最小值 */
    private String confidenceLevelMin;
    /** 置信度最大值 */
    private String confidenceLevelMax;
    /** 消息类型 */
    private String msgType;

    private String streetCode;

    private String streetName;

    /** 水平方位角 */
    private String horiAzimuthAngle;

    /** 综合置信度 */
    private String compConfidenceLevel;

    /**
     * 流程ID
     */
    private String flowId;

    /**
     * 角色ID，多个逗号分隔
     */
    private String roleId;

    /**
     * 用户类型，1用户，2角色
     */
    private String userType;

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String msgNotifyType;

    /**
     * 下一环节处理人
     * 会签时，会有多个processId、processName
     */
    private List<ProcessDTO> processDTOList;

    /**
     * 是否会签0:不会签  1:会签
     */
    private String isSign;

    /**
     * 当前流程环节ID
     */
    private String currentLinkId;
    /**
     * 当前流程环节名称
     */
    private String currentLinkName;
    /**
     * 行业信息集合
     */
    private String tradeInfo;

    /**
     * 行业应用版本编码
     */
    private String applicationCode;

    /**
     * 报警参数
     */
    private String alarmParam;
    /**
     * 报警参数值
     */
    private String alarmParamValue;

    /**
     * 行业扩展字段
     */
    private String extraField;

    /**
     * 实际触发的下游子任务编码数组（JSON 文本，对应月表列 sub_scenes）
     * 与 extraField 一致以 String 绑定 MyBatis JSON 列，例如 ["10313124","10212138"]
     */
    private String subScenes;

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
     * 流转类型（0默认、 1转派、2回退）
     */
    private String flowLinkType;

    /**
     * 真实环节id
     */
    private String realLinkType;

    /**
     * 3D放大标识(1-是，0-否)
     */
    private String enlargeFlag;

    /**
     * 预置位Id
     */
    private String presetId;

    /**
     * 网格员userId集合
     */
    private List<String> gridUserList;

    /**
     * 是否指派人（0：否 1：是）
     */
    private String beforeDispatchFlag;

    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 告警原因
     */
    private String alarmReason;

    private String deviceHorizontal;
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

    /**
     * 设备地址
     */
    private String deviceAddress;

    /**
     * 摄像机类型
     */
    private String deviceType;
    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机子类型
     */
    private String deviceSubType;
    /**
     * 摄像机子类型名称
     */
    private String deviceSubTypeName;

    /**
     * AI风险等级
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
     * 研判依据
     */
    private String judgmentBasis;
}

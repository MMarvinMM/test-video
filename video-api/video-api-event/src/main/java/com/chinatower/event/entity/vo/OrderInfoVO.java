package com.chinatower.event.entity.vo;

import com.chinatower.common.core.dto.BasePageDTO;
import com.chinatower.event.entity.dto.AlarmLableRelAPIDTO;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.platform.entity.admin.SysUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrderInfoVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/9 10:16
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderInfoVO extends BasePageDTO{

    private String orderId;
    /**
     * 事件ID
     */
    private String warningOrderId;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    private String warningTypeName;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;

    private String warningSourceName;

    /**
     * 形成告警时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    /**
     * 省份编码
     */
    private String provinceId;

    /**
     * 地市编码
     */
    private String cityId;

    /**
     * 县编码
     */
    private String countyId;

    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 县名称
     */
    private String countyName;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 事件等级
     */
    private String emergencyLevel;

    /**
     * 事件等级名称
     */
    private String emergencyLevelName;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 摄像机标签
     * */
    private List<String> deviceLabel;

    private String localDept;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatusName;

    /**
     * 事件发现人姓名
     */
    private String findName;
    /**

     * 事件发现人电话
     */
    private String findMobile;

    /**
     * 事件发现人电话(加密)
     */
    private String encryptionMobilePhone;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除说明
     */
    private String deleteRemark;

    /**
     * 语音
     */
    private String voice;

    /**
     * 视频路径
     */
    private String videoUrl;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    private String fileImgUrlIcon;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格名称
     */
    private String gridName;

    /**
     * 研判结果 0：误判 1：真实 2：重复报警
     */
    private String judgeResult;

    /**
     * 是否收藏
     */
    private String isCollection;

    /**
     *
     */
    private String ifSend;

    /**
     *
     */
    private String keeperName;

    /**
     *设备地址
     */
    private String deviceAddress;

    /**
     * 流程ID
     */
    private String flowId;

    /**
     * 环节ID
     */
    private String linkId;

    /**
     * 设备经度
     */
    private String deviceLongitude;

    /**
     * 设备维度
     */
    private String deviceLatitude;

    /**
     * 设备挂高
     */
    private String height;

    /**
     * 设备厂商
     */
    private String modelCode;

    /**
     * 重复告警ID
     */
    private String duplicateOrderId;

    /**
     * 行业应用编码
     */
    private String tradeId;

    /**
     * 行业应用名称
     */
    private String tradeName;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 重复告警信息
     */
    private WarningOrderInfoVO duplicateOrder;

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
     * 事件重要等级
     */
    private String importanceLevel;
    /** 水平方位角 */
    private String horiAzimuthAngle;

    /**
     * 摄像机厂家
     */
    private String modelId;

    /**
     * 重要等级名称
     */
    private String importanceLevelName;
    private String modelFactoryName;

    /**
     * 物联设备型号
     */
    private String deviceModelCode;

    /**
     * 置信度
     */
    private String confidenceLevel;
    /**
     * 最小置信度
     */
    private String confidenceLevelMin;
    /**
     * 综合置信度
     */
    private String compConfidenceLevel;
    /**
     * 设备维度
     */
    private String videoLatitude;
    /**
     * 设备经度
     */
    private String videoLongitude;

    /**
     * AI告警来源
     * 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
     */
    private String noticeResource;

    private String noticeResourceName;

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
     * 联动算法
     */
    private String linkageAlgorithm;

    /**
     *前端告警图片
     */
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    private String frontAlarmVedioUrl;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息类型名称
     */
    private String msgTypeName;

    /**
     * P位置
     */
    private String pan;

    /**
     * T位置
     */
    private String tilt;

    /**
     * Z位置
     */
    private String zoom;

    /**
     * 转义后的P位置
     */
    private String transPan;

    /**
     * 转义后的T位置
     */
    private String transTitle;

    /**
     * 转义后的Z位置
     */
    private String transZoom;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 环节名称
     */
    private String linkName;

    /**
     * 告警压制
     */
    private String intercept;

    /**
     *  告警压制原因
     */
    private String interceptName;

    /**
     * 事发地点距离 单位: 米
     */
    private String incidentDistance;

    /**
     * 火区面积
     */
    private String fireArea;
    /**
     * 下垫面类型
     */
    private String underlyingSurfaceType;

    /**
     * 转换后的水平方位角
     */
    private String transHoriAzimuthAngle;

    /**
     * 下一环节处理人
     */
    private String nextProcessName;

    /**
     * 设备收藏标记
     */
    private String deviceCodeCollect;

    /** 算法仓特征点 p值 */
    private String featurePan;
    /** 算法仓特征点 t值 */
    private String featureTilt;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    /**
     * 告警定位（1:开启 0关闭）
     */
    private String alarmLocation;

    /**
     * 是否开启基础平台视场角水平角（0-默认ptz逻辑、1-调用基础平台接口）
     */
    private String isOpenBasicFlag;

    /**
     * 物联设备关联的摄像机信息
     */
    private IotRelDeviceVo relDevice;

    private List<IotRelDeviceVo> iotDeviceList;

    /**
     * 多组物联设备信息以及关联的摄像机信息
     */
    private List<IotDeviceInfoVO> iotDevInfoList;

    /**
     * 卫星关联的摄像机信息
     */
    private SatelliteRelDeviceVo satelliteDevice;

    private List<SatelliteRelDeviceVo> satelliteDeviceList;

    /**
     * 雷达关联的摄像机信息
     */
    private SatelliteRelDeviceVo radarDevice;

    /**
     * 告警类型图标
     */
    private String icon;

    /**
     * 行业应用名称
     */
    private String industryName;

    /**
     * 流程模板
     */
    private String flowTemplate;

    /**
     * 雷达告警信息
     */
    private List<DeviceSiteInfoVO> deviceSiteList;

    /**
     * 完成时限;单位：分钟
     */
    private String timeLimit;

    /**
     * 当前环节开始时间
     */
    private String flowNodeTime;

    /**
     * 上个环节是否调度（0：不是 1：是）
     */
    private String beforeDispatchFlag;

    /**
     * 预置位名称
     */
    private String presetName;

    /**
     * 真实环节id
     */
    private String realLinkType;

    /**
     *  置信度最大值
     */
    private String confidenceLevelMax;

    /**
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    /**
     * 预置位Id
     */
    private String presetId;

    /**
     * 预置位对应告警数量
     */
    private String presetAlarmCount;


    private List<AlarmLableRelAPIDTO> alarmLableRelDTOS;

    /**
     * 网格对象信息
     */
    private GridInfo gridInfo;

    /**
     * 是否可操作（1：可操作  0：不可操作）
     */
    private int ifEdit;

    /**
     * 指定人
     */
    private String processId;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;

    private List<Map> polygonOperate;

    /**
     * 环节停留时间
     */
    private String waitTime;

    /**
     * 是否超期
     */
    private int ifTimeout;

    /**
     * 超期时间
     */
    private String showTime;

    /**
     * 接收时间
     */
    private String reciveTime;
    /**
     * 用户距离事发点位置
     */
    private String userDistance;

    private String modifierDate;

    private String deleteUser;

    private String userRecord;

    private SysUser deleteUserInfo;

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
     * 所有带水印的附件
     */
    private List<Map<String, Object>> files;

    /**
     * 属地标记位（1：表示属地运营研判列表）
     */
    private String localFlag;

    private String minChar;
    private String maxChar;

    /**
     * 误报标签
     */
    private String resultTab;

    /**
     * 节点编号名称
     */
    private String operationNode;

    /**
     * 算法编码
     */
    private List<String> algorithmCodeList;


    /**
     * 我的研判模式
     */
    private String myOperateType;

    /**
     * 卫片执法生成告警
     */
    private ReceiveRegionAlarmDto spot;

    /**
     * 摄像机图标地址
     * */
    private String deviceImageUrl;

    /**
     * 是否显示督办提示图标（0-否，1-是）
     */
    private String isHandleDisplay;

    /**
     * 所属组织
     */
    private String orgName;

    /**
     * 无人机告警来源
     */
    private UavAlarmSourceVo uavDevInfoList;

    /**
     * 第三方告警实际告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String realAlarmSource;

    /**
     * 图片、视频展示顺序标识
     */
    private String firstOrder;

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
     * 研判依据（AI风险等级分析）
     */
    private String judgmentBasis;
}

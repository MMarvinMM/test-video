package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警信息收藏表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@Getter
@Setter
@TableName("vm_warning_order_collection_t")
public class WarningOrderCollectionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "collection_id", type = IdType.AUTO)
    private Integer collectionId;

    /**
     * 告警单ID(yyyyMM+租户id+)
     */
    private String warningOrderId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;

    /**
     * 上报类型：1-PC上报；2-APP上报
     */
    private String reportType;

    /**
     * 第三方任务ID
     */
    private String taskId;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 实际触发的下游任务编码数组(JSON)，与月表 sub_scenes 一致
     */
    private String subScenes;

    /**
     * 形成告警时间
     */
    private String alarmTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 县编码
     */
    private String countyCode;

    /**
     * 县名称
     */
    private String countyName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

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
     * 备注
     */
    private String remark;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 语音
     */
    private String voice;

    /**
     * 当前环节ID
     */
    private String linkId;

    /**
     * 当前环节名称
     */
    private String linkName;

    /**
     * 当前环节开始时间
     */
    private Date flowNodeTime;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 小组id，组织编码
     */
    private String groupId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 完成时限;单位：分钟
     */
    private Integer timeLimit;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 事件等级，关联枚举值表
     */
    private String emergencyLevel;

    /**
     * 0误报、1研判环节 、2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;

    /**
     * 重复报警事件ID
     */
    private String duplicateOrderId;

    /**
     * 虚假告警说明
     */
    private String falseAlarmRemark;

    /**
     * 流程模板配置
     */
    private String flowTemplate;

    /**
     * 事件发现人姓名
     */
    private String findName;

    /**
     * 事件发现人电话
     */
    private String findMobile;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 网格编码
     */
    private String gridCode;

    /**
     * 网格名称
     */
    private String gridName;

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
     * 研判结果 0：误判 1：真实 2：重复报警
     */
    private String judgeResult;

    /**
     * 最后更新时间
     */
    private Date modifierDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    /**
     * 用户id流转记录，用','拼接
     */
    private String userRecord;

    /**
     * 组织/部门id流转记录，用','拼接
     */
    private String orgRecord;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;

    /**
     * 收藏人
     */
    private String collectionCreator;

    /**
     * 收藏创建时间
     */
    private Date collectionCreateTime;

    /**
     * 最小置信度
     */
    private String confidenceLevelMin;
    /** 综合置信度 */
    private String compConfidenceLevel;
    /**
     * 置信度
     */
    private String confidenceLevel;

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
     * 乡镇编码
     */
    private String townCode;

    /**
     * 乡镇名称
     */
    private String townName;

    /**
     * 前端告警图片
     */
    private String frontAlarmPicUrl;

    /**
     * 应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 研判时间
     */
    private Date judgeTime;

    /**
     *  置信度最大值
     */
    private String confidenceLevelMax;

    /**
     * 是否3D放大 1 是
     */
    private String enlargeFlag;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;

    private String timeLimitAcceptance;

    /**
     * 预置位ID
     */
    private String presetId;
    private String alarmReason;
    private String deviceHorizontal;
    private String devicePitch;

    /**
     * 是否属地运营研判
     */
    private String isLocalFlag;

    /**
     * 航线ID
     */
    private String flightRouteId;

    /**
     * 飞行任务ID
     */
    private String taskRecordId;

    /**
     * AI风险等级
     */
    private Integer riskLevel;

    /**
     * 研判依据
     */
    private String judgmentBasis;

    /**
     * 场景编码（场景类型）
     */
    private String sceneCode;

    /**
     * 场景模板
     */
    private String sceneTemplate;

    /**
     * AI研判状态：0未研判/1研判中/2研判完成/3研判失败
     */
    private Integer aiJudgeStatus;

    /**
     * 智能体扩展字段（JSON驼峰）
     */
    private String agentExtendField;

}

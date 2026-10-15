package com.chinatower.platform.consumer.item;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 告警工单数据对象
 * Created by lyy on 2022/10/20.
 */
@Data
@ApiModel(value = "告警工单数据对象",description = "告警工单数据对象")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarningOrderReportDTO extends BaseMongoDTO {
    /**
     * 告警单ID(yyyyMM+租户id+)
     */
    @TableId
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
     * 形成告警时间 格式yyyy-MM-dd HH:mm:ss
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
    private String flowNodeTime;
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
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
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
     * 告警研判类型 0:为默认告警 1:串行告警 2:并行告警
     */
    private String judgeType;
    /**
     * 研判时间
     */
    private String judgeTime;
    /**
     * 最后更新时间
     */
    private String modifierDate;
    /**
     * 创建时间
     */
    private String createTime;
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
     * 流程ID
     */
    private String flowId;
    /**
     * 行业信息
     */
    private String industryCode;
    /**
     * 角色ID
     */
    private String roleId;
    /**
     * 行业应用版本编码
     */
    private String appVerCode;
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
     * 消息类型
     */
    private String msgType;
    /**
     * 通知类型
     */
    private String noticeResource;
    /**
     * 最小置信度
     */
    private String confidenceLevelMin;
    /**
     * 综合置信度
     */
    private String compConfidenceLevel;
    /**
     *前端告警图片
     */
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    private String frontAlarmVedioUrl;
    /** 水平方位角 */
    private String horiAzimuthAngle;
    /**
     * 请求唯一标识
     */
    private String msgId;
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
     * 乡镇编码
     */
    private String townCode;
    /**
     * 乡镇名称
     */
    private String townName;
    /** 算法仓特征点 p值 */
    private String featurePan;
    /** 算法仓特征点 t值 */
    private String featureTilt;

    /**
     * 上个环节是否调度（0：不是 1：是）
     */
    private String beforeDispatchFlag;
}

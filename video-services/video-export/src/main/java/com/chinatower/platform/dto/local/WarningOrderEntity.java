package com.chinatower.platform.dto.local;

import com.chinatower.common.core.annotation.Excel;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2023/10/30 14:44
 */

@Data
public class WarningOrderEntity extends BaseMongoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 事件ID
     */
    @Excel(name = "工单号")
    private String warningOrderId;

    /**
     * 告警类型编码
     */
    @Excel(name = "告警类型")
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    @Excel(name = "告警类型名称")
    private String warningTypeName;

    /**
     * 告警内容
     */
    @Excel(name = "告警内容")
    private String alarmBody;

    @Excel(name = "接入节点")
    private String accessNode;

    @Excel(name = "受理节点")
    private String acceptNode;

    /**
     * 任务id
     */
    @Excel(name = "任务id")
    private String taskId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    @Excel(name = "告警来源")
    private String warningSource;

    /**
     * 告警来源名称
     */
    @Excel(name = "告警来源名称")
    private String warningSourceName;

    /**
     * 形成告警时间
     */
    @Excel(name = "告警时间")
    private String alarmTime;

    /**
     * 省份编码
     */
    @Excel(name = "省份编码")
    private String provinceId;

    /**
     * 地市编码
     */
    @Excel(name = "地市编码")
    private String cityId;

    /**
     * 县编码
     */
    @Excel(name = "县编码")
    private String countyId;

    /**
     * 乡镇编码
     */
    @Excel(name = "乡镇编码")
    private String townCode;

    /**
     * 所在省名称
     */
    @Excel(name = "省份名称")
    private String provinceName;

    /**
     * 所在市名称
     */
    @Excel(name = "市名称")
    private String cityName;

    /**
     * 县名称
     */
    @Excel(name = "县名称")
    private String countyName;

    /**
     * 乡镇名称
     */
    @Excel(name = "乡镇名称")
    private String townName;

    /**
     * 详细地址
     */
    @Excel(name = "详细地址")
    private String address;

    /**
     * 经度
     */
    @Excel(name = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度")
    private String latitude;

    /**
     * 事件等级
     */
    @Excel(name = "事件等级")
    private String emergencyLevel;

    /**
     * 事件等级名称
     */
    @Excel(name = "事件等级名称")
    private String emergencyLevelName;
    /**
     * 租户ID
     */
    @Excel(name = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @Excel(name = "租户名称")
    private String tenantName;

    /**
     * 设备编码
     */
    @Excel(name = "设备编码")
    private String deviceCode;

    /**
     * 通道编码
     */
    @Excel(name = "通道编码")
    private String channelCode;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 通道名称
     */
    @Excel(name = "通道名称")
    private String channelName;

    /**
     * 告警编码
     */
    @Excel(name = "告警编码")
    private String alarmCode;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @Excel(name = "工单状态")
    private String orderStatus;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    @Excel(name = "工单状态名称")
    private String orderStatusName;

    /**
     * 事件发现人姓名
     */
    @Excel(name = "发现人姓名")
    private String findName;

    /**
     * 事件发现人电话
     */
    @Excel(name = "发现人电话")
    private String findMobile;


    /**
     * 事件描述
     */
    @Excel(name = "事件描述")
    private String description;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 语音
     */
    @Excel(name = "语音")
    private String voice;

    /**
     * 视频路径
     */
    @Excel(name = "视频路径")
    private String videoUrl;

    /**
     * 原图
     */
    @Excel(name = "原图")
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    @Excel(name = "框图")
    private String fileImgUrlIcon;

    /**
     * 网格id
     */
    @Excel(name = "网格id")
    private String gridId;

    /**
     * 网格名称
     */
    @Excel(name = "网格名称")
    private String gridName;

    /**
     * 网格中心坐标
     */
    @Excel(name = "网格中心坐标")
    private String gridCenter;

    /**
     * 网格范围
     */
    @Excel(name = "网格绘制点位")
    private String gridRange;

    /**
     * 网格长姓名
     */
    @Excel(name = "网格长姓名")
    private String keeperName;

    /**
     * 网格长id
     */
    @Excel(name = "网格长id")
    private String keeperId;

    /**
     * 网格长联系电话
     */
    @Excel(name = "网格长联系电话")
    private String keeperPhoneNumber;

    /**
     * 研判结果 0：误判 1：真实 2：重复报警
     */
    @Excel(name = "研判结果")
    private String judgeResult;

    /**
     * 修改时间
     */
    @Excel(name = "修改时间")
    private String modifierDate;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间")
    private String createTime;

    /**
     * 删除标记
     */
    @Excel(name = "删除标记")
    private String deleteFlag;

    /**
     * 所有流转人
     */
    @Excel(name = "流转记录人")
    private String userRecord;

    @Excel(name = "组织/部门id流转记录")
    private String orgRecord;

    /**
     * 研判时间
     */
    @Excel(name = "研判时间")
    private String judgeTime;

    /**
     *设备地址
     */
    @Excel(name = "设备地址")
    private String deviceAddress;

    /**
     * 流程ID
     */
    @Excel(name = "流程ID")
    private String flowId;

    /**
     * 行业编码
     */
    @Excel(name = "行业编码")
    private String industryCode;

    /**
     * 行业应用名称
     */
    @Excel(name = "行业应用名称")
    private String industryName;

    /**
     * 角色id
     */
    @Excel(name = "角色id")
    private String roleId;

    /**
     * 环节ID
     */
    @Excel(name = "环节id")
    private String linkId;

    /**
     * 设备经度
     */
    @Excel(name = "设备经度")
    private String deviceLongitude;

    /**
     * 设备维度
     */
    @Excel(name = "设备维度")
    private String deviceLatitude;

    /**
     * 设备挂高
     */
    @Excel(name = "设备挂高")
    private String height;

    /**
     * 设备厂商
     */
    @Excel(name = "设备厂商")
    private String modelCode;

    /**
     * 重复告警ID
     */
    @Excel(name = "重复告警ID")
    private String duplicateOrderId;

    /**
     * 行业版本编码
     */
    @Excel(name = "行业版本编码")
    private String appVerCode;


    /**
     * 报警参数
     */
    @Excel(name = "报警参数")
    private String alarmParam;

    /**
     * 报警参数值
     */
    @Excel(name = "报警参数值")
    private String alarmParamValue;

    /**
     * 行业扩展字段
     */
    @Excel(name = "行业拓展字段")
    private String extraField;

    /**
     * 事件重要等级
     */
    @Excel(name = "事件重要等级")
    private String importanceLevel;

    /** 水平方位角 */
    @Excel(name = "水平方位角")
    private String horiAzimuthAngle;

    /**
     * 摄像机厂家
     */
    @Excel(name = "摄像机厂家")
    private String modelId;

    /**
     * 重要等级名称
     */
    @Excel(name = "重要等级名称")
    private String importanceLevelName;

    /**
     * 摄像机厂家名称
     */
    @Excel(name = "摄像机厂家名称")
    private String modelFactoryName;

    /**
     * 物联设备型号
     */
    @Excel(name = "物联设备型号")
    private String deviceModelCode;

    /**
     * 置信度
     */
    @Excel(name = "置信度")
    private String confidenceLevel;

    /**
     * 最小置信度
     */
    @Excel(name = "最小置信度")
    private String confidenceLevelMin;

    /**
     * 综合置信度
     */
    @Excel(name = "综合置信度")
    private String compConfidenceLevel;

    /**
     * 设备维度
     */
    @Excel(name = "设备维度")
    private String videoLatitude;

    /**
     * 设备经度
     */
    @Excel(name = "设备经度")
    private String videoLongitude;

    /**
     * AI告警来源
     * 通知类型 1-调度通知；2-设备端通知；（热成像告警、雷达平台按2方式通知）；算法需要将此字段透传回算法仓.
     */
    @Excel(name = "AI告警来源")
    private String noticeResource;

    /**
     * AI告警来源名称
     */
    @Excel(name = "AI告警来源名称")
    private String noticeResourceName;

    /**
     * 前端告警类型
     */
    @Excel(name = "前端告警类型")
    private String cameraAlarmType;

    /**
     * 是否二次研判 1-是；0-否
     */
    @Excel(name = "是否二次研判")
    private String isSecondJudge;

    /**
     * 二次研判结果 1-真，0-假
     */
    @Excel(name = "二次研判结果")
    private String secondJudgeResult;

    /**
     *前端告警图片
     */
    @Excel(name = "前端告警图片")
    private String frontAlarmPicUrl;

    /**
     * 前端告警视频
     */
    @Excel(name = "前端告警视频")
    private String frontAlarmVedioUrl;

    /**
     * 特征点P
     */
    @Excel(name = "特征点T")
    private String featurePan;

    /**
     * 特征点T
     */
    @Excel(name = "特征点T")
    private String featureTilt;

    /**
     * 上个环节是否指派（0：不是 1：是）
     */
    @Excel(name = "上个环节是否指派")
    private String beforedispatchflag;

    /**
     * 真实环节类型
     */
    @Excel(name = "真实环节类型")
    private String realLinkType;

    /**
     * 预置位id
     */
    @Excel(name = "预置位id")
    private String presetId;

    /**
     * 预置位名称
     */
    @Excel(name = "预置位名称")
    private String presetName;

    /**
     * 3D放大标识（0-否，1-是）
     */
    @Excel(name = "3D放大标识")
    private String enlargeFlag;

    @Excel(name = "最大置信度")
    private String confidenceLevelMax;

    @Excel(name = "受理状态")
    private String enableAcceptanceMode;

    @Excel(name = "受理时限")
    private String timelimitacceptance;

    /**
     * 请求唯一标识
     */
    @Excel(name = "请求唯一标识")
    private String msgId;

    /**
     * 消息类型
     */
    @Excel(name = "消息类型")
    private String msgType;

    /**
     * 消息类型名称
     */
    @Excel(name = "消息类型名称")
    private String msgTypeName;

    /**
     * 转义后的P位置
     */
    @Excel(name = "转义后P位置")
    private String transPan;

    /**
     * 转义后的T位置
     */
    @Excel(name = "转义后T位置")
    private String transTitle;

    /**
     * 转义后的Z位置
     */
    @Excel(name = "转义后Z位置")
    private String transZoom;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    /**
     * 算法编码
     */
    @Excel(name = "算法编码")
    private String algorithmCode;

    /**
     * 环节名称
     */
    @Excel(name = "环节名称")
    private String linkName;

    /**
     * 告警压制
     */
    @Excel(name = "告警压制")
    private String intercept;

    /**
     *  告警压制原因
     */
    @Excel(name = "告警压制原因")
    private String interceptName;

    /**
     * 火区面积
     */
    @Excel(name = "火区面积")
    private String fireArea;
    /**
     * 下垫面类型
     */
    @Excel(name = "下垫面类型")
    private String underlyingSurfaceType;

    /**
     * 转换后的水平方位角
     */
    @Excel(name = "转换后的水平方位角")
    private String transHoriAzimuthAngle;

    @Excel(name = "P位置")
    private String ptzP;

    @Excel(name = "T位置")
    private String ptzT;

    @Excel(name = "Z位置")
    private String ptzZ;

    @Excel(name = "节点时间")
    private String flowNodeTime;

    @Excel(name = "处理人")
    private String processId;

    @Excel(name = "处理人名称")
    private String processName;

    @Excel(name = "部门id")
    private String groupId;

    @Excel(name = "创建人")
    private String creator;

    @Excel(name = "环节处理时限")
    private String timeLimit;

    @Excel(name = "虚假告警说明")
    private String falseAlarmRemark;

    @Excel(name = "流程模板")
    private String flowTemplate;

    /**
     * 节点id
     */
    @Excel(name = "节点id")
    private String subSideId;

    /**
     * 设备厂家名称
     */
    @Excel(name = "设备厂家名称")
    private String deviceFactoryName;

    /**
     * 图斑信息
     */
    @Excel(name = "图斑信息")
    private String polygonOperate;

    /**
     * 设备序列
     */
    @Excel(name = "设备序列")
    private String deviceSerialNumber;

    /**
     * 设备型号
     */
    @Excel(name = "设备型号")
    private String deviceModel;

    /**
     * 告警对应的流转信息
     */
    @Excel(name = "流程信息")
    private String orderFlowRecordJson;

    private Integer pageNum;

    private Integer pageSize;

    private List<String> orderStatusList;

    private List<String> deviceCodeList;

}
